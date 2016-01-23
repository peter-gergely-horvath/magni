package org.magni.transaction.filesystem;

import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.Deque;
import java.util.LinkedList;
import java.util.concurrent.atomic.AtomicInteger;

public class TransactionalActionServiceTest
{
    private TransactionalActionService transactionalActionService;
    private TransactionTemplate transactionRequiredTransactionTemplate;
    private TransactionTemplate newTransactionRequiredTransactionTemplate;
    private TransactionTemplate nestedTransactionRequiredTransactionTemplate;

    private AtomicInteger commitCalledCount;
    private AtomicInteger rollbackCalledCount;

    @BeforeClass
    public void beforeClass()
    {
        transactionalActionService = new TransactionalActionService(TransactionalFileSystemOperations.FILE_SYSTEM_OPERATION_ORDER);

        SingleConnectionDataSource dataSource = new SingleConnectionDataSource();
        dataSource.setUrl("jdbc:h2:mem:test;");

        DataSourceTransactionManager transactionManager = new DataSourceTransactionManager();
        transactionManager.setDataSource(dataSource);
        transactionManager.afterPropertiesSet();

        transactionRequiredTransactionTemplate = new TransactionTemplate(transactionManager);
        transactionRequiredTransactionTemplate.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);

        newTransactionRequiredTransactionTemplate = new TransactionTemplate(transactionManager);
        newTransactionRequiredTransactionTemplate.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);

        nestedTransactionRequiredTransactionTemplate = new TransactionTemplate(transactionManager);
        nestedTransactionRequiredTransactionTemplate.setPropagationBehavior(TransactionDefinition.PROPAGATION_NESTED);

    }

    @BeforeMethod
    public void beforeMethod() throws IOException
    {
        commitCalledCount = new AtomicInteger(0);
        rollbackCalledCount = new AtomicInteger(0);
    }


    @Test(expectedExceptions = IllegalStateException.class)
    public void testExecuteAfterCommitWithNoTransactionFails()
    {
        transactionalActionService.executeAfterCommit(
                new IncreaseAtomicIntegerRunnable(commitCalledCount));

    }

    @Test(expectedExceptions = IllegalStateException.class)
    public void testExecuteAfterRollbackWithNoTransactionFails()
    {
        transactionalActionService.executeAfterRollback(
                new IncreaseAtomicIntegerRunnable(rollbackCalledCount));

    }

    @Test
    public void testCommit()
    {
        executeInTransaction(new Runnable()
        {
            @Override
            public void run()
            {
                transactionalActionService.executeAfterCommit(
                        new IncreaseAtomicIntegerRunnable(commitCalledCount));
                transactionalActionService.executeAfterRollback(
                        new IncreaseAtomicIntegerRunnable(rollbackCalledCount));

                Assert.assertEquals(commitCalledCount.get(), 0);
                Assert.assertEquals(rollbackCalledCount.get(), 0);

            }
        });


        Assert.assertEquals(commitCalledCount.get(), 1);
        Assert.assertEquals(rollbackCalledCount.get(), 0);
    }

    @Test
    public void testCommitActionOrder()
    {
        Deque<Runnable> executionRegistry = new LinkedList<Runnable>();

        final ExecutionRegisteringRunnable first = new ExecutionRegisteringRunnable("first", executionRegistry);
        final ExecutionRegisteringRunnable second = new ExecutionRegisteringRunnable("second", executionRegistry);
        final ExecutionRegisteringRunnable third = new ExecutionRegisteringRunnable("third", executionRegistry);

        executeInTransaction(new Runnable()
        {
            @Override
            public void run()
            {
                transactionalActionService.executeAfterCommit(first);
                transactionalActionService.executeAfterCommit(second);
                transactionalActionService.executeAfterCommit(third);
            }
        });


        Assert.assertEquals(executionRegistry.removeFirst(), first);
        Assert.assertEquals(executionRegistry.removeFirst(), second);
        Assert.assertEquals(executionRegistry.removeFirst(), third);
    }

    @Test
    public void testRollbackActionOrder()
    {
        Deque<Runnable> executionRegistry = new LinkedList<Runnable>();

        final ExecutionRegisteringRunnable first = new ExecutionRegisteringRunnable("first", executionRegistry);
        final ExecutionRegisteringRunnable second = new ExecutionRegisteringRunnable("second", executionRegistry);
        final ExecutionRegisteringRunnable third = new ExecutionRegisteringRunnable("third", executionRegistry);

        executeInTransactionThenRollback(new Runnable()
        {
            @Override
            public void run()
            {
                transactionalActionService.executeAfterRollback(first);
                transactionalActionService.executeAfterRollback(second);
                transactionalActionService.executeAfterRollback(third);
            }
        });


        Assert.assertEquals(executionRegistry.removeFirst(), third);
        Assert.assertEquals(executionRegistry.removeFirst(), second);
        Assert.assertEquals(executionRegistry.removeFirst(), first);
    }

    @Test
    public void testRollback()
    {
        executeInTransactionThenRollback(new Runnable()
        {
            @Override
            public void run()
            {
                transactionalActionService.executeAfterCommit(
                        new IncreaseAtomicIntegerRunnable(commitCalledCount));
                transactionalActionService.executeAfterRollback(
                        new IncreaseAtomicIntegerRunnable(rollbackCalledCount));

                Assert.assertEquals(commitCalledCount.get(), 0);
                Assert.assertEquals(rollbackCalledCount.get(), 0);

            }
        });


        Assert.assertEquals(commitCalledCount.get(), 0);
        Assert.assertEquals(rollbackCalledCount.get(), 1);
    }

    @Test
    public void testCommitWithMultipleActions()
    {
        executeInTransaction(new Runnable()
        {
            @Override
            public void run()
            {
                transactionalActionService.executeAfterCommit(
                        new IncreaseAtomicIntegerRunnable(commitCalledCount));
                transactionalActionService.executeAfterCommit(
                        new IncreaseAtomicIntegerRunnable(commitCalledCount));

                transactionalActionService.executeAfterRollback(
                        new IncreaseAtomicIntegerRunnable(rollbackCalledCount));

                transactionalActionService.executeAfterRollback(
                        new IncreaseAtomicIntegerRunnable(rollbackCalledCount));

                Assert.assertEquals(commitCalledCount.get(), 0);
                Assert.assertEquals(rollbackCalledCount.get(), 0);

            }
        });


        Assert.assertEquals(commitCalledCount.get(), 2);
        Assert.assertEquals(rollbackCalledCount.get(), 0);
    }


    @Test
    public void testRollbackWithMultipleActions()
    {
        executeInTransactionThenRollback(new Runnable()
        {
            @Override
            public void run()
            {
                transactionalActionService.executeAfterCommit(
                        new IncreaseAtomicIntegerRunnable(commitCalledCount));
                transactionalActionService.executeAfterCommit(
                        new IncreaseAtomicIntegerRunnable(commitCalledCount));

                transactionalActionService.executeAfterRollback(
                        new IncreaseAtomicIntegerRunnable(rollbackCalledCount));
                transactionalActionService.executeAfterRollback(
                        new IncreaseAtomicIntegerRunnable(rollbackCalledCount));


                Assert.assertEquals(commitCalledCount.get(), 0);
                Assert.assertEquals(rollbackCalledCount.get(), 0);

            }
        });


        Assert.assertEquals(commitCalledCount.get(), 0);
        Assert.assertEquals(rollbackCalledCount.get(), 2);
    }



    @Test
    public void testNewTransactionCommit()
    {
        executeInTransaction(new Runnable()
        {
            @Override
            public void run()
            {
                executeInNewTransaction(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        transactionalActionService.executeAfterCommit(
                                new IncreaseAtomicIntegerRunnable(commitCalledCount));
                        transactionalActionService.executeAfterRollback(
                                new IncreaseAtomicIntegerRunnable(rollbackCalledCount));

                        Assert.assertEquals(commitCalledCount.get(), 0);
                        Assert.assertEquals(rollbackCalledCount.get(), 0);
                    }
                });

                Assert.assertEquals(commitCalledCount.get(), 1);
                Assert.assertEquals(rollbackCalledCount.get(), 0);

            }
        });


        Assert.assertEquals(commitCalledCount.get(), 1);
        Assert.assertEquals(rollbackCalledCount.get(), 0);
    }

    @Test
    public void testNewTransactionRollback()
    {
        executeInTransaction(new Runnable()
        {
            @Override
            public void run()
            {
                executeInNewTransactionThenRollback(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        transactionalActionService.executeAfterCommit(
                                new IncreaseAtomicIntegerRunnable(commitCalledCount));
                        transactionalActionService.executeAfterRollback(
                                new IncreaseAtomicIntegerRunnable(rollbackCalledCount));

                        Assert.assertEquals(commitCalledCount.get(), 0);
                        Assert.assertEquals(rollbackCalledCount.get(), 0);
                    }
                });

                Assert.assertEquals(commitCalledCount.get(), 0);
                Assert.assertEquals(rollbackCalledCount.get(), 1);

            }
        });


        Assert.assertEquals(commitCalledCount.get(), 0);
        Assert.assertEquals(rollbackCalledCount.get(), 1);
    }

    @Test
    public void testNewTransactionCommitWithMultipleActions()
    {
        executeInTransaction(new Runnable()
        {
            @Override
            public void run()
            {

                transactionalActionService.executeAfterCommit(
                        new IncreaseAtomicIntegerRunnable(commitCalledCount));
                transactionalActionService.executeAfterRollback(
                        new IncreaseAtomicIntegerRunnable(rollbackCalledCount));

                Assert.assertEquals(commitCalledCount.get(), 0);
                Assert.assertEquals(rollbackCalledCount.get(), 0);

                executeInNewTransaction(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        transactionalActionService.executeAfterCommit(
                                new IncreaseAtomicIntegerRunnable(commitCalledCount));
                        transactionalActionService.executeAfterRollback(
                                new IncreaseAtomicIntegerRunnable(rollbackCalledCount));

                        Assert.assertEquals(commitCalledCount.get(), 0);
                        Assert.assertEquals(rollbackCalledCount.get(), 0);
                    }
                });

                Assert.assertEquals(commitCalledCount.get(), 1);
                Assert.assertEquals(rollbackCalledCount.get(), 0);

            }
        });


        Assert.assertEquals(commitCalledCount.get(), 2);
        Assert.assertEquals(rollbackCalledCount.get(), 0);
    }

    @Test
    public void testNewTransactionRollbackWithMultipleActions()
    {
        executeInTransaction(new Runnable()
        {
            @Override
            public void run()
            {

                transactionalActionService.executeAfterCommit(
                        new IncreaseAtomicIntegerRunnable(commitCalledCount));
                transactionalActionService.executeAfterRollback(
                        new IncreaseAtomicIntegerRunnable(rollbackCalledCount));

                Assert.assertEquals(commitCalledCount.get(), 0);
                Assert.assertEquals(rollbackCalledCount.get(), 0);

                executeInNewTransactionThenRollback(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        transactionalActionService.executeAfterCommit(
                                new IncreaseAtomicIntegerRunnable(commitCalledCount));
                        transactionalActionService.executeAfterRollback(
                                new IncreaseAtomicIntegerRunnable(rollbackCalledCount));

                        Assert.assertEquals(commitCalledCount.get(), 0);
                        Assert.assertEquals(rollbackCalledCount.get(), 0);
                    }
                });

                Assert.assertEquals(commitCalledCount.get(), 0);
                Assert.assertEquals(rollbackCalledCount.get(), 1);

            }
        });


        Assert.assertEquals(commitCalledCount.get(), 1);
        Assert.assertEquals(rollbackCalledCount.get(), 1);
    }

    @Test
    public void testNestedTransactionCommit()
    {
        executeInTransaction(new Runnable()
        {
            @Override
            public void run()
            {
                executeInNestedTransaction(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        transactionalActionService.executeAfterCommit(
                                new IncreaseAtomicIntegerRunnable(commitCalledCount));
                        transactionalActionService.executeAfterRollback(
                                new IncreaseAtomicIntegerRunnable(rollbackCalledCount));

                        Assert.assertEquals(commitCalledCount.get(), 0);
                        Assert.assertEquals(rollbackCalledCount.get(), 0);
                    }
                });

                Assert.assertEquals(commitCalledCount.get(), 0);
                Assert.assertEquals(rollbackCalledCount.get(), 0);

            }
        });


        Assert.assertEquals(commitCalledCount.get(), 1);
        Assert.assertEquals(rollbackCalledCount.get(), 0);
    }

    @Test
    public void testNestedTransactionRollback()
    {
        executeInTransaction(new Runnable()
        {
            @Override
            public void run()
            {
                executeInNestedTransactionThenRollback(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        transactionalActionService.executeAfterCommit(
                                new IncreaseAtomicIntegerRunnable(commitCalledCount));
                        transactionalActionService.executeAfterRollback(
                                new IncreaseAtomicIntegerRunnable(rollbackCalledCount));

                        Assert.assertEquals(commitCalledCount.get(), 0);
                        Assert.assertEquals(rollbackCalledCount.get(), 0);
                    }
                });

                Assert.assertEquals(commitCalledCount.get(), 0);
                Assert.assertEquals(rollbackCalledCount.get(), 0);

            }
        });


        Assert.assertEquals(commitCalledCount.get(), 1);
        Assert.assertEquals(rollbackCalledCount.get(), 0);
    }

    @Test
    public void testNestedTransactionCommitWithMultipleActions()
    {
        executeInTransaction(new Runnable()
        {
            @Override
            public void run()
            {
                transactionalActionService.executeAfterCommit(
                        new IncreaseAtomicIntegerRunnable(commitCalledCount));
                transactionalActionService.executeAfterRollback(
                        new IncreaseAtomicIntegerRunnable(rollbackCalledCount));

                Assert.assertEquals(commitCalledCount.get(), 0);
                Assert.assertEquals(rollbackCalledCount.get(), 0);

                executeInNestedTransaction(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        transactionalActionService.executeAfterCommit(
                                new IncreaseAtomicIntegerRunnable(commitCalledCount));
                        transactionalActionService.executeAfterRollback(
                                new IncreaseAtomicIntegerRunnable(rollbackCalledCount));

                        Assert.assertEquals(commitCalledCount.get(), 0);
                        Assert.assertEquals(rollbackCalledCount.get(), 0);
                    }
                });

                Assert.assertEquals(commitCalledCount.get(), 0);
                Assert.assertEquals(rollbackCalledCount.get(), 0);

            }
        });

        Assert.assertEquals(commitCalledCount.get(), 2);
        Assert.assertEquals(rollbackCalledCount.get(), 0);
    }

    @Test
    public void testNestedTransactionRollbackWithMultipleActions()
    {
        executeInTransaction(new Runnable()
        {
            @Override
            public void run()
            {
                transactionalActionService.executeAfterCommit(
                        new IncreaseAtomicIntegerRunnable(commitCalledCount));
                transactionalActionService.executeAfterRollback(
                        new IncreaseAtomicIntegerRunnable(rollbackCalledCount));

                Assert.assertEquals(commitCalledCount.get(), 0);
                Assert.assertEquals(rollbackCalledCount.get(), 0);

                executeInNestedTransactionThenRollback(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        transactionalActionService.executeAfterCommit(
                                new IncreaseAtomicIntegerRunnable(commitCalledCount));
                        transactionalActionService.executeAfterRollback(
                                new IncreaseAtomicIntegerRunnable(rollbackCalledCount));

                        Assert.assertEquals(commitCalledCount.get(), 0);
                        Assert.assertEquals(rollbackCalledCount.get(), 0);
                    }
                });

                Assert.assertEquals(commitCalledCount.get(), 0);
                Assert.assertEquals(rollbackCalledCount.get(), 0);

            }
        });

        Assert.assertEquals(commitCalledCount.get(), 2);
        Assert.assertEquals(rollbackCalledCount.get(), 0);
    }

    private void executeInTransaction(final Runnable runnable)
    {
        executeUsingTransactionTemplate(transactionRequiredTransactionTemplate, runnable, false);
    }

    private void executeInTransactionThenRollback(final Runnable runnable)
    {
        executeUsingTransactionTemplate(transactionRequiredTransactionTemplate, runnable, true);
    }

    private void executeInNewTransaction(final Runnable runnable)
    {
        executeUsingTransactionTemplate(newTransactionRequiredTransactionTemplate, runnable, false);
    }

    private void executeInNewTransactionThenRollback(final Runnable runnable)
    {
        executeUsingTransactionTemplate(newTransactionRequiredTransactionTemplate, runnable, true);
    }

    private void executeInNestedTransaction(final Runnable runnable)
    {
        executeUsingTransactionTemplate(nestedTransactionRequiredTransactionTemplate, runnable, false);
    }

    private void executeInNestedTransactionThenRollback(final Runnable runnable)
    {
        executeUsingTransactionTemplate(nestedTransactionRequiredTransactionTemplate, runnable, true);
    }

    private void executeUsingTransactionTemplate(
            TransactionTemplate transactionTemplate,
            final Runnable runnable, final boolean setRollbackOnlyFlag)
    {
        transactionTemplate.<Void>execute(new TransactionCallback<Void>()
        {
            @Override
            public Void doInTransaction(TransactionStatus status)
            {
                runnable.run();

                if (setRollbackOnlyFlag)
                {
                    status.setRollbackOnly();
                }

                return null;
            }
        });
    }


    private static class IncreaseAtomicIntegerRunnable implements Runnable
    {
        private final AtomicInteger theAtomicInteger;

        private IncreaseAtomicIntegerRunnable(AtomicInteger atomicInteger)
        {
            this.theAtomicInteger = atomicInteger;
        }

        @Override
        public void run()
        {
            theAtomicInteger.getAndIncrement();
        }
    }

    private static class ExecutionRegisteringRunnable implements Runnable
    {
        private final Deque<Runnable> deque;
        private final String name;

        private ExecutionRegisteringRunnable(String name, Deque<Runnable> deque)
        {
            this.name = name;
            this.deque = deque;
        }

        @Override
        public void run()
        {
            deque.addLast(this);
        }

        @Override
        public String toString()
        {
            return name;
        }
    }




}
