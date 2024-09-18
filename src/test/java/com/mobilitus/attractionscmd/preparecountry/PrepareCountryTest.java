package com.mobilitus.attractionscmd.preparecountry;

import com.mobilitus.util.data.attractions.ArtistData;
import com.mobilitus.util.data.aws.cloudsearch.GogoEventSearchData;
import com.mobilitus.util.hexia.KeyValue;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.joda.time.DateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * @author helgaw
 * @since 9/17/24 15:57
 */
class PrepareCountryTest
{
    private PrepareCountry toTest;
    String country = "NO";

    static
    {
        BasicConfigurator.configure(new ConsoleAppender(new PatternLayout("[%l] %m%n")));
        Logger.getRootLogger().setLevel(Level.WARN);
    }

    @BeforeEach
    void setUp()
    {

        Logger.getRootLogger().setLevel(Level.INFO);
        toTest = new PrepareCountry();

    }

    @Test
    void getEventsInCountry()
    {
        // start 3679 upcoming events in Norway
        // 4532 events in eod
        List<GogoEventSearchData> events = toTest.getEvents(country);
        int i = 0;
        for (GogoEventSearchData event : events)
        {
            System.out.println(i +"/" + events.size() + " "+ event.getDisplayName() +" " + event.getMajorType() + " " + event.getVenueName() + " " + event.getCity() + " " + event.getWhen());
            i++;
        }
    }


    @Test
    void getArtists()
    {
        // start
        // 778 artists with 901 upcoming events of 15.109, 583 are active (with one or more events)
        // 1679 eod
        List<ArtistData> artists = toTest.getArtists(country);
        for (ArtistData artist : artists)
        {
            System.out.println(artist.getName() + " " + artist.getMajorType() + " " + artist.getUpcoming() + "/" + artist.getAll());
        }
    }
//
//    @Test
//    void listUpcomingArtistsAbroad()
//    {
//    }
//
    @Test
    void mapGenres()
    {
        // logs out the genres for the country so we can update GenreHandler for country-specific genres
         toTest.mapGenres(country);

    }

    @Test
    void updateFromSpotify()
    {
        toTest.updateFromSpotify(country);
    }

     @Test
    public void testGetPlaylist()
    {
        String top50 = "https://open.spotify.com/playlist/37i9dQZEVXbJvfa0Yxg7E7";
        String topNorawy = "https://open.spotify.com/playlist/5ezCvZuFJcmMUeOynIYp6g";
        String itsHits = "https://open.spotify.com/playlist/37i9dQZF1EIg0Su2BAe75w";

        String madeInNorway = "https://open.spotify.com/playlist/37i9dQZF1DX3hgbB9nrEB1";
        String topSongsNorway = "https://open.spotify.com/playlist/37i9dQZEVXbLWYFZ5CkSvr";
        String hotHitsNorway = "https://open.spotify.com/playlist/37i9dQZF1DWUJF24WXSSyO";
        String newMusicFridayNorway = "https://open.spotify.com/playlist/37i9dQZF1DWV3RrjH1jDkx";
        String norwegianMetal = "https://open.spotify.com/playlist/37i9dQZF1EIdYpirRrgo3q";
        String norweginanRap = "https://open.spotify.com/playlist/37i9dQZF1EIg5p6qnOqh5a";
        String viralNorway = "https://open.spotify.com/playlist/37i9dQZEVXbOcsE2WCaJa2";
        String topsify = "https://open.spotify.com/playlist/6wWQ3S5cD5UoxvlPjRoKDN";
        String folk = "https://open.spotify.com/playlist/37i9dQZF1EIhdqqTeHDBFL";
        String jazz = "https://open.spotify.com/playlist/37i9dQZF1EIdm9cZxHpgN8";
        String top20NO = "https://open.spotify.com/playlist/6nyoIdRU6MWkIqpEID0Qrg";
        List<String> playlists = new ArrayList<>();
        playlists.add(top50);
        playlists.add(topNorawy);
        playlists.add(itsHits);
        playlists.add(madeInNorway);
        playlists.add(topSongsNorway);
        playlists.add(hotHitsNorway);
        playlists.add(newMusicFridayNorway);
        playlists.add(norwegianMetal);
        playlists.add(norweginanRap);
        playlists.add(viralNorway);
        playlists.add(topsify);
        playlists.add(folk);
        playlists.add(jazz);
        playlists.add(top20NO);

        List<ArtistData> allArtists = new ArrayList<>(100);

        for (String playlist : playlists)
        {
            List<ArtistData> artists = toTest.getArtistsFromPlaylist(playlist);
            for (ArtistData art : artists)
            {
                if (!allArtists.contains(art))
                {
                    allArtists.add(art);
                }
            }
        }

        List<KeyValue> attributes = new ArrayList<>(2);
        Integer year = DateTime.now().getYear();
        attributes.add(new KeyValue("popularInYear", "popular_" +country +"_" + year));


        int i = 0;
        for (ArtistData artist : allArtists)
        {
            System.out.println(i + "/" + allArtists.size() + " " + artist.getName() + "  " + artist.getGogoGenre() + "  " + artist.getCountry());
            i++;
        }

        toTest.tagArtists(allArtists, attributes);
    }

}
