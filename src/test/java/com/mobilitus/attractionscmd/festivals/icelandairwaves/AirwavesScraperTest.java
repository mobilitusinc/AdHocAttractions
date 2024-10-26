package com.mobilitus.attractionscmd.festivals.icelandairwaves;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * @author helgaw
 * @since 10/24/24 11:53
 */
class AirwavesScraperTest
{

    static
    {
        BasicConfigurator.configure(new ConsoleAppender(new PatternLayout("[%l] %m%n")));
        Logger.getRootLogger().setLevel(Level.INFO);
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
    void scrapeEvents()
    {
        AirwavesScraper airwavesScraper = new AirwavesScraper();
        airwavesScraper.scrapeEvents();
        try
        {
            Thread.sleep(10000);
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
