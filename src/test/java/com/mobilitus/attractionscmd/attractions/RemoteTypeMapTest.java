package com.mobilitus.attractionscmd.attractions;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author helgaw
 * @since 9/16/24 15:49
 */
class RemoteTypeMapTest
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

    @Test
    void createFromFile()
    {
        Logger.getRootLogger().setLevel(Level.INFO);

        String filename = "/Users/helgaw/Desktop/remote_types.csv";
        RemoteTypeMap remoteTypeMap = new RemoteTypeMap();
        remoteTypeMap.createFromFile(filename);
    }
}
