package org.magni.transaction.filesystem;

import java.util.Deque;
import java.util.LinkedList;
import java.util.Stack;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.transaction.support.ResourceHolderSupport;
import org.springframework.transaction.support.ResourceHolderSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

class TransactionalActionService {
	private static final Logger logger = LoggerFactory.getLogger(TransactionalActionService.class);
	
	private final int executionOrder;

	TransactionalActionService(int executionOrder) {
		this.executionOrder = executionOrder;
	}
	
	void executeAfterCommit(Runnable runnable) {
		checkTransactionState();

		
		CommandHolder commandHolderBoundToCurrentlyActiveTransaction = getOrCreateTransactionBoundCommandHolder();
		
		commandHolderBoundToCurrentlyActiveTransaction.addCommitAction(runnable);
	}

	void executeAfterRollback(Runnable runnable) {
		checkTransactionState();

		
		CommandHolder commandHolderBoundToCurrentlyActiveTransaction = getOrCreateTransactionBoundCommandHolder();
		
		commandHolderBoundToCurrentlyActiveTransaction.addRollbackAction(runnable);
	}

	private static void checkTransactionState() {
		if (!TransactionSynchronizationManager.isActualTransactionActive()) {
			throw new IllegalStateException("No active transaction found");
		}
	}

	private CommandHolder getOrCreateTransactionBoundCommandHolder() {
		final TransactionalActionService resourceKey = this;

		CommandHolder commandHolder = (CommandHolder) TransactionSynchronizationManager.getResource(resourceKey);

		if (commandHolder == null) {
			commandHolder = new CommandHolder();
			commandHolder.setSynchronizedWithTransaction(true);

			TransactionSynchronizationManager
					.registerSynchronization(new CommandHolderSynchronization(commandHolder, resourceKey));
			TransactionSynchronizationManager.bindResource(resourceKey, commandHolder);
		}

		return commandHolder;
	}

	private static class CommandHolder extends ResourceHolderSupport {
		private final Deque<Runnable> onCommitActions = new LinkedList<Runnable>();
		private final Stack<Runnable> onRollbackActions = new Stack<Runnable>();

		private void addCommitAction(Runnable runnable) {
			onCommitActions.addLast(runnable);
		}

		private void addRollbackAction(Runnable runnable) {
			onRollbackActions.push(runnable);
		}
	}

	private class CommandHolderSynchronization
			extends ResourceHolderSynchronization<CommandHolder, TransactionalActionService> implements Ordered {
		private final CommandHolder commandHolder;

		public CommandHolderSynchronization(CommandHolder commandHolder, TransactionalActionService resourceKey) {
			super(commandHolder, resourceKey);
			this.commandHolder = commandHolder;
		}

		@Override
		public void afterCompletion(int status) {
			super.afterCompletion(status);

			switch (status) {
			case STATUS_COMMITTED:
				processCommitActions(commandHolder.onCommitActions);
				break;

			case STATUS_ROLLED_BACK:
			case STATUS_UNKNOWN: 	
				// our best guess for heuristic mixed completion is rollback
				processRollbackActions(commandHolder.onRollbackActions);
				break;

			default:
				// should not happen
				throw new IllegalStateException("Unknown status: " + status);
			}
		}

		@Override
		public int getOrder() {
			return executionOrder;
		}
	}

	private void processCommitActions(Deque<Runnable> onCommit) {
		while (!onCommit.isEmpty()) {
			Runnable r = onCommit.removeFirst();

			try {
				r.run();
			} catch (Throwable t) {
				logger.error("Failed to execute commit action: " + r, t);
			}
		}
	}

	private void processRollbackActions(Stack<Runnable> onRollback) {
		while (!onRollback.isEmpty()) {
			Runnable r = onRollback.pop();

			try {
				r.run();
			} catch (Throwable t) {
				logger.error("Failed to execute rollback action: " + r, t);
			}
		}
	}
}
