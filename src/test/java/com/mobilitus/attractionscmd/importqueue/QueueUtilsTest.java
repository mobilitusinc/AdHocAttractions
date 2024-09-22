package com.mobilitus.attractionscmd.importqueue;

import com.mobilitus.gogo.Cache;
import com.mobilitus.util.distributed.dynamodb.AWSUtils;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;

/**
 * @author helgaw
 * @since 8/1/24 12:36
 */
class QueueUtilsTest
{

    static
    {
        BasicConfigurator.configure(new ConsoleAppender(new PatternLayout("[%l] %m%n")));
        Logger.getRootLogger().setLevel(Level.WARN);
    }


    @BeforeEach
    void setUp()
    {
    }

    @AfterEach
    void tearDown()
    {
    }

    @Test
    void testQueueUtils()
    {

        Logger.getRootLogger().setLevel(Level.INFO);
        AwsCredentialsProvider credentialsProvider = AWSUtils.getCredentialsProvider();

        Cache.create(credentialsProvider.resolveCredentials(), "localhost:11211");

        QueueUtils queueUtils = new QueueUtils(credentialsProvider, "localhost:11211");
        queueUtils.updateStaleEntries();
        try
        {
            Thread.sleep(1000000);
        }
        catch (InterruptedException e)
        {
            /**
             * @todo improve error handling
             *
             **/


        }
    }
    @Test
    void fixEventDatesForIndexes()
    {

        Logger.getRootLogger().setLevel(Level.INFO);
        AwsCredentialsProvider credentialsProvider = AWSUtils.getCredentialsProvider();

        Cache.create(credentialsProvider.resolveCredentials(), "localhost:11211");

        QueueUtils queueUtils = new QueueUtils(credentialsProvider, "localhost:11211");
        queueUtils.fixEventDatesForIndexes();
        try
        {
            Thread.sleep(1000000);
        }
        catch (InterruptedException e)
        {
            /**
             * @todo improve error handling
             *
             **/


        }
    }


    @Test
    void updateStale()
    {

        Logger.getRootLogger().setLevel(Level.INFO);
        AwsCredentialsProvider credentialsProvider = AWSUtils.getCredentialsProvider();

        Cache.create(credentialsProvider.resolveCredentials(), "localhost:11211");

        QueueUtils queueUtils = new QueueUtils(credentialsProvider, "localhost:11211");
        queueUtils.updateStaleEntries();
        try
        {
            Thread.sleep(1000000);
        }
        catch (InterruptedException e)
        {
            /**
             * @todo improve error handling
             *
             **/


        }
    }


    @Test
    void reImport()
    {

        Logger.getRootLogger().setLevel(Level.INFO);
        AwsCredentialsProvider credentialsProvider = AWSUtils.getCredentialsProvider();

        Cache.create(credentialsProvider.resolveCredentials(), "localhost:11211");

        QueueUtils queueUtils = new QueueUtils(credentialsProvider, "localhost:11211");
        queueUtils.reImport();
        try
        {
            Thread.sleep(1000000);
        }
        catch (InterruptedException e)
        {
            /**
             * @todo improve error handling
             *
             **/


        }
    }


    @Test
    void cleanImportProcess()
    {

        Logger.getRootLogger().setLevel(Level.INFO);
        AwsCredentialsProvider credentialsProvider = AWSUtils.getCredentialsProvider();

        Cache.create(credentialsProvider.resolveCredentials(), "localhost:11211");

        QueueUtils queueUtils = new QueueUtils(credentialsProvider, "localhost:11211");
        queueUtils.updateStaleInSchemaWorker();
        try
        {
            Thread.sleep(1000000);
        }
        catch (InterruptedException e)
        {
            /**
             * @todo improve error handling
             *
             **/


        }
    }


}
