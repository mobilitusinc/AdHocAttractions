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

/**
 * @author helgaw
 * @since 7/31/24 10:07
 */
class AttractionUtilTest
{

    private AwsCredentialsProvider credentialsProvider;
    private AttractionUtil toTest;

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
        toTest = new AttractionUtil (credentialsProvider, "localhost:11211");

    }

    @Test
    void createMenningarnottEvents()
    {
        toTest.addMenningarNottToEvents();
    }

    @Test
    void testCheckMissingVenues()
    {
        toTest.findEventsMissingVenueOrArtists();
    }

    @Test
    void addAkureyrarVakaToEvents()
    {
        toTest.addAkureyrarVakaToEvents();
    }
}
