package com.mobilitus.attractionscmd.musiclive.icemusic;

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
 * @since 10/8/24 15:57
 */
class IceMusicAnalyzerTest
{

    static
    {
        BasicConfigurator.configure(new ConsoleAppender(new PatternLayout("[%l] %m%n")));
        Logger.getRootLogger().setLevel(Level.WARN);
    }


    private IceMusicAnalyzer toTest;

    @BeforeEach
    void setup()
    {
        AwsCredentialsProvider credentialsProvider = AWSUtils.getCredentialsProvider();

        Cache.create(credentialsProvider.resolveCredentials(), "localhost:11211");
        toTest = new IceMusicAnalyzer();

    }

    @Test
    void showUpcomingArtists()
    {
        toTest.showUpcomingArtists();
    }

    @Test
    void showUpcomingEvents()
    {
        toTest.showUpcomingEvents();
    }

    @Test
    void showUpcomingVenues()
    {
        toTest.showUpcomingVenues();
    }

    @Test
    void createReport()
    {
        Logger.getRootLogger().setLevel(Level.INFO);

        toTest.createISReport();
    }

    @Test
    void createAUReport()
    {
        Logger.getRootLogger().setLevel(Level.INFO);

        toTest.createReport("AU");
    }

}
