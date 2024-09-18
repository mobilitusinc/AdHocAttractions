package com.mobilitus.attractionscmd.musiclive;

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

/**
 * @author helgaw
 * @since 8/5/24 14:18
 */
class MusicLiveTest
{

    private AwsCredentialsProvider credentialsProvider;
    private MusicLive toTest;

    static
    {
        BasicConfigurator.configure(new ConsoleAppender(new PatternLayout("[%l] %m%n")));
        Logger.getRootLogger().setLevel(Level.WARN);
    }

    @BeforeEach
    void setUp()
    {

        Logger.getRootLogger().setLevel(Level.INFO);
        credentialsProvider = AWSUtils.getCredentialsProvider();

        Cache.create(credentialsProvider.resolveCredentials(), "localhost:11211");
        toTest = new MusicLive (credentialsProvider, "localhost:11211");

    }



    @Test
    void testLoadTimes()
    {
        toTest.measurePageLoadTimes("https://www.musiclive.com");
    }


    @Test
    void verifyAustralianness()
    {
        toTest.verifyAustralianNess();
    }

}
