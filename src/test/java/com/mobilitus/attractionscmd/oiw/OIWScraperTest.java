package com.mobilitus.attractionscmd.oiw;

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
 * @since 9/23/24 19:13
 */
class OIWScraperTest
{
    private AwsCredentialsProvider credentialsProvider;

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

//        Cache.create(credentialsProvider.resolveCredentials(), "localhost:11211");

    }

    @Test
    void scrapeOIW()
    {
        OIWScraper scraper = new OIWScraper();
        scraper.scrapeOIW();
    }
}
