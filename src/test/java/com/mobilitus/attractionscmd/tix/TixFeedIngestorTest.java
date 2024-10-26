package com.mobilitus.attractionscmd.tix;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author toti - last updated by $Author: $
 * @version $Revision$ - last updated at $Date: $
 * @todo add class description.
 * @since 10/26/24 16:51
 */
class TixFeedIngestorTest
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
        TixFeedIngestor tixFeedIngestor = new TixFeedIngestor();
        tixFeedIngestor.scrapeEvents();
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
