package com.mobilitus.attractionscmd.attractions;

import com.mobilitus.gogo.Cache;
import com.mobilitus.util.distributed.dynamodb.AWSUtils;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author helgaw
 * @since 9/2/24 11:05
 */
class RemoteTypeListTest
{
    static
    {
        BasicConfigurator.configure(new ConsoleAppender(new PatternLayout("[%l] %m%n")));
        Logger.getRootLogger().setLevel(Level.WARN);
    }



    @BeforeEach
    void setUp()
    {
        Logger.getRootLogger().setLevel(Level.INFO);
        AwsCredentialsProvider credentialsProvider = AWSUtils.getCredentialsProvider();

        Cache.create(credentialsProvider.resolveCredentials(), "localhost:11211");


    }

    @Test
    void getAllRemoteTypes()
    {
        RemoteTypeList remoteTypeList = new RemoteTypeList(AWSUtils.getCredentialsProvider(), "localhost:11211");
        remoteTypeList.getAllRemoteTypes();
    }
}
