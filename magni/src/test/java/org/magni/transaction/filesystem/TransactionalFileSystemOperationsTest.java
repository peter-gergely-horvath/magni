
package org.magni.transaction.filesystem;

import org.apache.commons.io.FileUtils;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

public class TransactionalFileSystemOperationsTest
{
    private TransactionTemplate transactionTemplate;
    private TransactionalFileSystemOperations transactionalFileSystemOperations;

    private File tempFile;

    @BeforeClass
    public void beforeClass()
    {
        SingleConnectionDataSource dataSource = new SingleConnectionDataSource();
        dataSource.setUrl("jdbc:h2:mem:test;");

        DataSourceTransactionManager transactionManager = new DataSourceTransactionManager();
        transactionManager.setDataSource(dataSource);
        transactionManager.afterPropertiesSet();

        transactionTemplate = new TransactionTemplate(transactionManager);

        transactionalFileSystemOperations = new TransactionalFileSystemOperations();
    }

    @BeforeMethod
    public void setupTempFileBeforeFileTests() throws IOException
    {
        tempFile = File.createTempFile("test-file", ".tmp");
        tempFile.delete(); // we need the file reference, but not the file itself
        if (tempFile.exists()) throw new IllegalStateException("Temp file exists: " + tempFile);
    }

    @AfterMethod
    public void cleanupTempFileAfterFileTests() throws IOException
    {
        if (tempFile.exists())
        {
            tempFile.delete();
        }
    }


    @Test
    public void testFileCreatedOnCommit() throws IOException
    {
        final String originalText = "TEST-123";

        executeInTransaction(new Runnable()
        {
            @Override
            public void run()
            {
                try
                {
                    transactionalFileSystemOperations.createFile(tempFile, originalText.getBytes());
                } catch (IOException e)
                {
                    throw new RuntimeException(e);
                }

            }
        });


        Assert.assertTrue(tempFile.exists());

        String fileContent = FileUtils.readFileToString(tempFile);
        Assert.assertEquals(fileContent, originalText);


    }

    @Test
    public void testFileNotCreatedOnRollback()
    {
        final String originalText = "TEST-123";

        executeInTransactionThenRollback(new Runnable()
        {
            @Override
            public void run()
            {
                try
                {
                    transactionalFileSystemOperations.createFile(tempFile, originalText.getBytes());
                } catch (IOException e)
                {

                    throw new RuntimeException(e);
                }


            }
        });

        Assert.assertFalse(tempFile.exists());
    }

    @Test
    public void testFileContentIsChangedOnCommit() throws IOException
    {
        final String originalText = "ORIGINAL CONTENT";
        final String changedText = "TEST-123";

        FileUtils.writeStringToFile(tempFile, originalText);

        executeInTransaction(new Runnable()
        {
            @Override
            public void run()
            {
                try
                {

                    transactionalFileSystemOperations.replaceFileContent(tempFile, changedText.getBytes());
                } catch (IOException e)
                {
                    throw new RuntimeException(e);
                }
            }
        });

        final String fileContent = FileUtils.readFileToString(tempFile);

        Assert.assertEquals(fileContent, changedText);
    }

    @Test
    public void testFileContentIsRestoredOnRollback() throws IOException
    {
        final String originalText = "ORIGINAL CONTENT";
        final String changedText = "TEST-123";

        FileUtils.writeStringToFile(tempFile, originalText);

        executeInTransactionThenRollback(new Runnable()
        {
            @Override
            public void run()
            {
                try
                {

                    transactionalFileSystemOperations.replaceFileContent(tempFile, changedText.getBytes());
                } catch (IOException e)
                {
                    throw new RuntimeException(e);
                }
            }
        });

        final String fileContent = FileUtils.readFileToString(tempFile);

        Assert.assertEquals(fileContent, originalText);
    }

    @Test
    public void testFileIsMovedOnCommit() throws IOException
    {
        final String uniqueFileId = UUID.randomUUID().toString();
        FileUtils.writeStringToFile(tempFile, uniqueFileId);

        final File moveDestination = new File(tempFile.getParentFile(), String.format("%s.tmp", uniqueFileId));

        executeInTransaction(new Runnable()
        {
            @Override
            public void run()
            {
                try
                {
                    transactionalFileSystemOperations.moveFile(tempFile, moveDestination);
                } catch (IOException e)
                {
                    throw new RuntimeException(e);
                }

            }
        });

        Assert.assertTrue(moveDestination.exists());
        Assert.assertFalse(tempFile.exists());

        final String fileContentReadFromDestination = FileUtils.readFileToString(moveDestination);

        Assert.assertTrue(uniqueFileId.equals(fileContentReadFromDestination));

    }

    @Test
    public void testFileIsMovedBackOnRollback() throws IOException
    {
        final String uniqueFileId = UUID.randomUUID().toString();
        FileUtils.writeStringToFile(tempFile, uniqueFileId);

        final File moveDestination = new File(tempFile.getParentFile(), String.format("%s.tmp", uniqueFileId));

        executeInTransactionThenRollback(new Runnable()
        {
            @Override
            public void run()
            {
                try
                {
                    transactionalFileSystemOperations.moveFile(tempFile, moveDestination);
                } catch (IOException e)
                {
                    throw new RuntimeException(e);
                }

            }
        });

        Assert.assertFalse(moveDestination.exists());
        Assert.assertTrue(tempFile.exists());

        final String fileContentReadFromOriginal = FileUtils.readFileToString(tempFile);

        Assert.assertTrue(uniqueFileId.equals(fileContentReadFromOriginal));
    }


    private void executeInTransaction(final Runnable runnable)
    {
        transactionTemplate.<Void>execute(new TransactionCallback<Void>()
        {
            @Override
            public Void doInTransaction(TransactionStatus status)
            {
                runnable.run();

                return null;
            }
        });
    }

    private void executeInTransactionThenRollback(final Runnable runnable)
    {
        transactionTemplate.<Void>execute(new TransactionCallback<Void>()
        {
            @Override
            public Void doInTransaction(TransactionStatus status)
            {
                runnable.run();

                status.setRollbackOnly();

                return null;
            }
        });
    }



}
