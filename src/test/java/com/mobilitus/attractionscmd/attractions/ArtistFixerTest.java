package com.mobilitus.attractionscmd.attractions;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * @author helgaw
 * @since 9/24/24 16:40
 */
class ArtistFixerTest
{

    private ArtistFixer toTest;

    static
    {
        BasicConfigurator.configure(new ConsoleAppender(new PatternLayout("[%l] %m%n")));
        Logger.getRootLogger().setLevel(Level.WARN);
    }

    @BeforeEach
    void setUp()
    {

        Logger.getRootLogger().setLevel(Level.INFO);
        toTest = new ArtistFixer();
    }


    @Test
    void fixArtistsOsloInnovation()
    {
         toTest.fixArtistsOsloInnovation();
//        toTest.fixVenuesOsloInnovation();
    }
}
