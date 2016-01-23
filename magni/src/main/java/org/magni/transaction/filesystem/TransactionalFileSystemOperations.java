package org.magni.transaction.filesystem;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

import org.apache.commons.io.FileUtils;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.transaction.support.TransactionSynchronizationManager;

public class TransactionalFileSystemOperations {

	// actions are to be executed executed *AFTER* JDBC operations
	static final int FILE_SYSTEM_OPERATION_ORDER = DataSourceUtils.CONNECTION_SYNCHRONIZATION_ORDER + 1000;
	
	private final TransactionalActionService transactionalActionService = new TransactionalActionService(FILE_SYSTEM_OPERATION_ORDER); 

	public void createFile(final File file, byte[] data) throws IOException {
		if (file.exists()) {
			throw new IllegalStateException("File already exists:" + file);
		}

		writeByteArrayToFileInternal(file, data);

		if (TransactionSynchronizationManager.isActualTransactionActive()) {
			transactionalActionService.executeAfterRollback(new DeleteFileRunnable(file));
		}
	}

	public void replaceFileContent(final File file, byte[] data) throws IOException {
		if (TransactionSynchronizationManager.isActualTransactionActive()) {
			final String uuid = UUID.randomUUID().toString();

			String tempFileName = String.format("originalContentOf-%s-%s.tmp", file.getName(), uuid);

			final File tempFile = new File(file.getParent(), tempFileName);

			moveFileInternal(file, tempFile);

			writeByteArrayToFileInternal(file, data);

			transactionalActionService.executeAfterCommit(new DeleteFileRunnable(tempFile));
			transactionalActionService.executeAfterRollback(new OverwriteFileRunnable(tempFile, file));

		} else {
			file.delete();
			writeByteArrayToFileInternal(file, data);
		}
	}

	public void deleteFile(File file) throws IOException {
		if (TransactionSynchronizationManager.isActualTransactionActive()) {
			final String uuid = UUID.randomUUID().toString();

			String tempFileName = String.format("originalContentOf-%s-%s.tmp", file.getName(), uuid);

			final File tempFile = new File(file.getParent(), tempFileName);

			moveFileInternal(file, tempFile);

			transactionalActionService.executeAfterCommit(new DeleteFileRunnable(tempFile));
			transactionalActionService.executeAfterRollback(new MoveFileRunnable(tempFile, file));
		} else {
			file.delete();
		}
	}

	public void moveFile(final File sourceFile, final File destinationFile) throws IOException {

		moveFileInternal(sourceFile, destinationFile);

		if (TransactionSynchronizationManager.isActualTransactionActive()) {
			transactionalActionService.executeAfterRollback(new MoveFileRunnable(destinationFile, sourceFile));
		}
	}

	public void copyFile(final File sourceFile, final File destinationFile) throws IOException {

		copyFileInternal(sourceFile, destinationFile);

		if (TransactionSynchronizationManager.isActualTransactionActive()) {
			transactionalActionService.executeAfterRollback(new DeleteFileRunnable(destinationFile));
		}
	}

	protected void writeByteArrayToFileInternal(final File file, byte[] data) throws IOException {
		FileUtils.writeByteArrayToFile(file, data);
	}
	
	protected void copyFileInternal(final File sourceFile, final File destinationFile) throws IOException {
		FileUtils.copyFile(sourceFile, destinationFile);
	}

	protected void moveFileInternal(File file, File destinationFile) {

		File sourceDirectory = file.getParentFile();
		File destinationDirectory = destinationFile.getParentFile();

		if (sourceDirectory != null && destinationDirectory != null && !sourceDirectory.equals(destinationDirectory)
				&& !destinationDirectory.exists()) {
			if (destinationDirectory.mkdirs()) {
				throw new IllegalStateException("Failed to create destination directory [" + destinationFile + "]");
			}
		}

		if (!file.renameTo(destinationFile)) {
			throw new IllegalStateException("Failed to move [" + file + "] to [" + destinationFile + "]");
		}
	}

	private static class DeleteFileRunnable implements Runnable {
		private final File fileToCreate;

		public DeleteFileRunnable(File fileToCreate) {
			this.fileToCreate = fileToCreate;
		}

		@Override
		public void run() {
			if (fileToCreate.exists()) {
				fileToCreate.delete();
			}
		}
	}

	private class OverwriteFileRunnable implements Runnable {
		private final File fileToOverwrite;
		private final File sourceFile;

		public OverwriteFileRunnable(File sourceFile, File fileToOverwrite) {
			this.sourceFile = sourceFile;
			this.fileToOverwrite = fileToOverwrite;
		}

		@Override
		public void run() {
			fileToOverwrite.delete();

			moveFileInternal(sourceFile, fileToOverwrite);
		}
	}

	private class MoveFileRunnable implements Runnable {
		private final File sourceFile;
		private final File destinationFile;

		public MoveFileRunnable(File srcFile, File dstFile) {
			this.sourceFile = srcFile;
			this.destinationFile = dstFile;
		}

		@Override
		public void run() {
			moveFileInternal(sourceFile, destinationFile);
		}
	}
}
