package com.mobilitus.attractionscmd.preparecountry;

import com.mobilitus.attractionscmd.bandsintown.BandsInTownWorker;
import com.mobilitus.attractionscmd.spotify.Spotify;
import com.mobilitus.attractionscmd.spotify.internal.model_objects.specification.Artist;
import com.mobilitus.attractionscmd.spotify.internal.model_objects.specification.ExternalUrl;
import com.mobilitus.gogo.Cache;
import com.mobilitus.gogo.attractions.ArtistWorker;
import com.mobilitus.gogo.attractions.EventWorker;
import com.mobilitus.gogo.attractions.VenueWorker;
import com.mobilitus.gogo.search.AttractionSearch;
import com.mobilitus.gogo.search.EventSearch;
import com.mobilitus.persisted.attractions.Home;
import com.mobilitus.persisted.attractions.artists.ArtistPersisted;
import com.mobilitus.persisted.attractions.artists.ArtistRelatedPersisted;
import com.mobilitus.util.data.attractions.ArtistData;
import com.mobilitus.util.data.attractions.ArtistType;
import com.mobilitus.util.data.attractions.AttractionType;
import com.mobilitus.util.data.attractions.DataSource;
import com.mobilitus.util.data.attractions.GenreData;
import com.mobilitus.util.data.attractions.PerformerType;
import com.mobilitus.util.data.attractions.VenueData;
import com.mobilitus.util.data.attractions.textHandler.TagCounter;
import com.mobilitus.util.data.aws.cloudsearch.GogoEventSearchData;
import com.mobilitus.util.data.aws.cloudsearch.GogoSearchData;
import com.mobilitus.util.data.face.FaceData;
import com.mobilitus.util.data.gogo.EventSort;
import com.mobilitus.util.data.gogo.ImageAttributeUtil;
import com.mobilitus.util.data.gogo.PromoGhettoData;
import com.mobilitus.util.data.gogo.SearchAttractionFilter;
import com.mobilitus.util.data.gogo.SearchFilter;
import com.mobilitus.util.data.tabula.reportdata.SortOrder;
import com.mobilitus.util.distributed.aws.cloudsearch.DefaultSearchConfig;
import com.mobilitus.util.distributed.aws.cloudsearch.SearchConfig;
import com.mobilitus.util.distributed.aws.memcached.ElastiCacheAdministrator;
import com.mobilitus.util.distributed.aws.s3.S3;
import com.mobilitus.util.distributed.dynamodb.AWSUtils;
import com.mobilitus.util.hexia.ID;
import com.mobilitus.util.hexia.KeyValue;
import com.mobilitus.util.hexia.Pair;
import com.mobilitus.util.hexia.StrUtil;
import com.mobilitus.util.hexia.pagination.Page;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedAsyncClient;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * @author helgaw
 * @todo add class description.
 * @since 5/13/23 12:31
 */
public class PrepareCountry
{

    private static Logger logger = Logger.getLogger(PrepareCountry.class);
    private final ID id;
    private final Spotify spotify;
    private final DynamoDbEnhancedAsyncClient mapper;
    private S3 s3;
    private SearchConfig searchConfig;
    private AttractionSearch gogoSearch;
    private EventSearch eventSearch;
    private EventWorker eventWorker;
    private VenueWorker venueWorker;
    private ArtistWorker artistWorker;

    private Map<String, String> artistMap = new HashMap<>(60000);
    private BandsInTownWorker bandsInTownWorker;

    private static ElastiCacheAdministrator cacheAdministrator;
//    private BandsInTownScraper bandsInTown = new BandsInTownScraper();
//    private SongkickScraper songKick = new SongkickScraper();


    public PrepareCountry(String scrapingbeeApiKey)
    {
        AwsCredentialsProvider credentialsProvider = AWSUtils.getCredentialsProvider();

        Cache.create(credentialsProvider.resolveCredentials(), "localhost:11211");

        mapper = AWSUtils.getMapper(credentialsProvider);


        SearchConfig searchConfig = new DefaultSearchConfig(credentialsProvider);

        eventSearch = new EventSearch(searchConfig.getCredentialsProvider(), searchConfig.getEventSearchURL());

        gogoSearch = new AttractionSearch(searchConfig.getCredentialsProvider(), searchConfig.getArtistSearchURL());
        id = new ID();
        eventWorker = new EventWorker(null, null, mapper, AWSUtils.getS3(), searchConfig);
        artistWorker = new ArtistWorker(null, null, mapper, AWSUtils.getS3(), searchConfig);
        venueWorker = new VenueWorker(id, new FaceData(), mapper, s3, searchConfig);

        cacheAdministrator = new ElastiCacheAdministrator();
        bandsInTownWorker = new BandsInTownWorker (scrapingbeeApiKey);
        spotify = new Spotify();

    }

    public List<ArtistData> getArtists(String country)
    {
        List<ArtistData> artists = new ArrayList<>();
        String offset = null;
        Integer limit = 100;
        Boolean done = false;
        String last = null;
        int totalUpcoming = 0;
        int total = 0;
        int active = 0;

        int i = 0;
        while (!done)
        {
            Page<ArtistData> p = getArtistsImpl(country, offset, limit);
//            artists.addAll(p.getList());
            for (ArtistData artist : p.getList())
            {
                System.out.println(i + "/" + p.getTotalSize() + " " + artist.getName() + "\t" +  artist.getMajorType() + " " + artist.getArtistType() + " " +(artist.getGogoGenre() != null ? artist.getGogoGenre() : "") + "\t" +
                        artist.getUpcoming() + "/" + artist.getAll() + "\t\t\t https://devdbackstage.promogogo.com/go/adminartists.do#!/artist/" + artist.getArtistID() + " " + artist.getCreated());

                artists.add(artist);
                totalUpcoming += artist.getUpcoming();
                total += artist.getAll();
                if (artist.getAll() > 0)
                {
                    active++;
                }
                i++;
            }
            offset = p.getNextOffset();
            if (offset == null)
            {
                done = true;
            }
        }
        System.out.println("Found " + artists.size() + " artists with " + totalUpcoming + " upcoming events out of " + total + " total events. " + active + " active artists");
        return artists;
    }


    public Page<ArtistData> getArtistsImpl(String country, String offset, Integer limit)
    {
        SearchAttractionFilter filter = new SearchAttractionFilter();
        filter.setSort(EventSort.name, SortOrder.asc);
        filter.addLocation(null, null, country);
        //        filter.setAttractionType(AttractionType.music);
        filter.searchArtists(true);
        List<ArtistData> artists = new ArrayList<>();
        String name = "";
        Page<GogoSearchData> result = gogoSearch.findByName("", filter, offset, limit);
        for (GogoSearchData searchData : result)
        {
            ArtistData artist = artistWorker.getArtist(searchData.getId());

            if (artist.getName().equalsIgnoreCase(name))
            {
                System.out.println("Found " + artist.getName() +  " with same name as previous artist  https://dashboard.promogogo.com/go/adminartists.do#!/artist/" + artist.getArtistID());
                System.out.println("");
            }
            name = artist.getName();

            //            if (artist.getMajorType() != AttractionType.music)
            //            {
            //                continue;
            //            }
            artists.add(artist);
        }

        return new Page(artists).withPagination(result.getPagination());
    }



    public List<ArtistData> listUpcomingArtistsAbroad(String country)
    {
//        List<ArtistData> artists = getArtists(country);
//        clearEventsWithWrongArtistTag(artists, country);
        List<ArtistData> result = new ArrayList<>();

        SearchFilter filter = new SearchFilter();
        filter.addNegativeLocation(null, null, country);
        filter.addAttribute("artistcountry_" + country.toLowerCase());
//        for (ArtistData artist : artists)
//        {
//            if (artist.getUpcoming() > 0)
//            {
//                filter.addArtistID(artist.getArtistID());
//            }
//
//        }
        filter.upcoming();
        filter.addNegativeLocation(null, null, country);

        Page<Pair<String, Long>> artistIDs = eventSearch.countEventsByArtist(filter, null, 10000);
        int i = 0;
        int totalGigs = 0;
        for (Pair<String, Long> item : artistIDs)
        {
            ArtistData artist = artistWorker.getArtist(item.getKey());
            if (artist.getCountry() != null && artist.getCountry().equalsIgnoreCase(country))
            {
                result.add(artist);
                i++;
                logger.info(i +" " +artist.getName() + " " + artist.getMajorType() +" " + artist.getGogoGenre() + " is playing  " + item.getValue() + " gigs");
                totalGigs += item.getValue();
            }
        }

        logger.info ("Found " + result.size() + " artists with " + totalGigs + " upcoming events");
        return result;
    }


    public List<PromoGhettoData> listUpcomingEventsAbroad(String country)
    {
//        List<ArtistData> artists = getArtists(country);
//        clearEventsWithWrongArtistTag(artists, country);
        List<PromoGhettoData> result = new ArrayList<>();
        int i = 0;
        String offset = null;
        SearchFilter filter = new SearchFilter();
        filter.addNegativeLocation(null, null, country);
//        for (ArtistData artist : artists)
//        {
//            if (artist.getUpcoming() > 0)
//            {
//                filter.addArtistID(artist.getArtistID());
//            }
//
//        }
        filter.upcoming();
        filter.addNegativeLocation(null, null, country);
        Boolean done = false;
        while (!done)
        {
            Page<GogoEventSearchData> events = eventSearch.findEvents("artistcountry_" + country, filter, offset, 100);
            for (GogoEventSearchData event : events)
            {
                PromoGhettoData pgg = eventWorker.getEvent(event.getId());
                if (pgg.getMajorType() == AttractionType.music)
                {
//                    eventWorker.updateArtistCountryTagOnEvent(pgg);
                    result.add(pgg);
                    String artistNames = getArtistFromCountry(pgg, country);
                    System.out.println(i + "/" + events.getTotalSize() + " " + artistNames+ " -> " + pgg.getTitle() + " " + " is playing in " + pgg.getVenue().getPrettyAddress() + " " + pgg.getWhen());
                    i++;
                }
            }
            offset = events.getNextOffset();
            if (offset == null)
            {
                done = true;
            }
        }

        return result;
    }

    public List<VenueData> getVenues(String country)
    {
        List<VenueData> venues = new ArrayList<>();
        String offset = null;
        Integer limit = 100;
        Boolean done = false;
        String last = null;
        int totalUpcoming = 0;
        int total = 0;
        int active = 0;

        int i = 0;
        while (!done)
        {
            Page<VenueData> p = getVenuesImpl(country, offset, limit);
            //            venues.addAll(p.getList());
            for (VenueData venue : p.getList())
            {
                System.out.println(i + "/" + p.getTotalSize() + " " + venue.getName() + "\t" +  venue.getMajorCategory() + " " + venue.getCapacity() + "\t" +
                                   venue.getUpcoming() + "/" + venue.getAll() + "\t\t\t https://devbackstage.promogogo.com/venue" + venue.getLocationID() + " " + venue.getCreated());

                venues.add(venue);
                totalUpcoming += venue.getUpcoming();
                total += venue.getAll();
                if (venue.getAll() > 0)
                {
                    active++;
                }
                i++;
            }
            offset = p.getNextOffset();
            if (offset == null)
            {
                done = true;
            }
        }
        System.out.println("Found " + venues.size() + " venues with " + totalUpcoming + " upcoming events out of " + total + " total events. " + active + " active venues");
        return venues;
    }



    public Page<VenueData> getVenuesImpl(String country, String offset, Integer limit)
    {
        SearchAttractionFilter filter = new SearchAttractionFilter();
        filter.setSort(EventSort.name, SortOrder.asc);
        filter.addLocation(null, null, country);
        //        filter.setAttractionType(AttractionType.music);
        filter.searchVenues(true);
        List<VenueData> venues = new ArrayList<>();
        String name = "";
        Page<GogoSearchData> result = gogoSearch.findByName("", filter, offset, limit);
        for (GogoSearchData searchData : result)
        {
            VenueData venue = venueWorker.getVenue(searchData.getId());

            if (venue.getName().equalsIgnoreCase(name))
            {
                System.out.println("Found " + venue.getName() +  " with same name as previous venue  https://dashboard.promogogo.com/go/editvenue.do#!/venue/" + venue.getLocationID());
                System.out.println("");
            }
            name = venue.getName();

            //            if (venue.getMajorType() != AttractionType.music)
            //            {
            //                continue;
            //            }
            venues.add(venue);
        }

        return new Page(venues).withPagination(result.getPagination());
    }


    private void clearEventsWithWrongArtistTag(List<ArtistData> artists, String country)
    {
        SearchFilter filter = new SearchFilter();
        filter.addAttribute("artistcountry_" + country.toLowerCase());
        if(country != null && !country.isEmpty())
        {
            filter.addNegativeLocation(null, null, country.toUpperCase(Locale.ROOT));
        }
        filter.setFrom(DateTime.now().minusYears(2));
        filter.setSort(EventSort.date, SortOrder.asc);
        List<String> artistIDs = new ArrayList<>(1000);
        for (ArtistData artist : artists)
        {
            artistIDs.add(artist.getArtistID());
        }
        String offset = null;
        Boolean done = false;
        while (!done)
        {
            Page<GogoEventSearchData> events = eventSearch.findEvents("", filter, offset, 100);
            for (GogoEventSearchData event : events)
            {
                if (!isInList(event.getArtistIDs(), artistIDs))
                {
                    PromoGhettoData pgg = eventWorker.getEvent(event.getId());
                    logger.info ("No artist from " + country + " playing " + pgg.getTitle() + " " + pgg.getWhen());
                    eventWorker.removeAttributes(pgg.getEventID(), "artistcountry_" + country.toLowerCase());
                    eventWorker.updateSearch(pgg.getEventID());
                }
            }
            offset = events.getNextOffset();
            if (offset == null)
            {
                done = true;
            }
//            offset = events.getNextOffset();
//            if (offset == null)
//            {
//                done = true;
//            }
        }
        eventWorker.flushSearch();
    }

    private boolean isInList(List<String> artists, List<String> fullList)
    {
        for (String artist : artists)
        {
            if (fullList.contains(artist))
            {
                return true;
            }
        }
        return false;
    }

    private String getArtistFromCountry(PromoGhettoData pgg, String country)
    {
        String str = "";

        for (ArtistData artist : pgg.getArtists())
        {
            if (artist.getCountry() != null && artist.getCountry().equalsIgnoreCase(country))
            {
                if (str.isEmpty())
                {
                    str += artist.getName();
                }
                else
                {
                    str += ", " + artist.getName();
                }
            }
        }
        return str;
    }


    public void updateArtistFromSpotify(String artistID)
    {
        ArtistData pggArtist = artistWorker.getArtist(artistID);

        if (pggArtist == null)
        {
            return;
        }
        if (pggArtist.getSourceKey(DataSource.spotify) != null && !pggArtist.getSourceKey(DataSource.spotify).isEmpty())
        {
            updateFromSpotify(artistID, pggArtist.getSourceKey(DataSource.spotify));
        }
        else
        {
            List<Artist> spotifyArtists = spotify.findArtist(pggArtist.getName());

            for (Artist spArt : spotifyArtists)
            {
                if (pggArtist.nameMatches(spArt.getName()))
                {

                    updateFromSpotify(artistID, spArt.getId());
                }
            }
        }
    }

//    public void updateFromBandsinTown(String country)
//    {
//        String offset = null;
//        Integer limit = 10;
//        Boolean done = false;
//        Integer upcoming =0;
//        Integer all = 0;
//        Integer skip = 620;
//        int i = 0;
//        while (!done)
//        {
//            Page<ArtistData> artists = getArtistsImpl(country, offset, limit);
//            for (ArtistData artist : artists)
//            {
//                Pair<Integer, Integer> eventsFound = null;
//                BandsinTownResult artistPage = null;
//                if (i < skip)
//                {
//                    i++;
//                    continue;
//                }
//
//                if (artist.getSourceKey(DataSource.bandsInTown) == null || artist.getSourceKey(DataSource.bandsInTown).isEmpty())
//                {
//                    ArtistData updated = findInBandsInTown(artist);
//                    if (updated == null)
//                    {
//                        logger.info ("No match for " + artist.getName());
//                        i++;
//                        continue;
//                    }
//                    artistPage = importEventsFromBandsInTown(updated);
//                }
//                else
//                {
//                    artistPage = importEventsFromBandsInTown(artist);
//                }
//
//                if (artistPage == null)
//                {
//                    logger.info ("No match for " + artist.getName());
//                    i++;
//                    continue;
//                }
//
//                List<ArtistData> relatedArtists = artistPage.getRelatedArtists();
//                for (ArtistData related : relatedArtists)
//                {
//                    logger.info(artist.getName() +"   <  >   " + related.getName());
//                    addRelated(DataSource.bandsInTown, related, artist);
//                }
//
//                artistWorker.setUniqueAttribute(artist.getArtistID(), DataSource.bandsInTown, "relatedImported", "true");
//
//                upcoming += artistPage.getEvents();
//
//                logger.info (i + "/" + artists.getTotalSize() + " Looking at "  + artist.getName()
//                        + " Found " + artistPage.getEvents() +  " " + " Total is  " +  upcoming);
//                i++;
//            }
//            offset = artists.getNextOffset();
//            if (offset == null)
//            {
//                done = true;
//            }
//        }
//
//    }
//
//
//    public void updateFromSongkick(String country)
//    {
//        String offset = null;
//        Integer limit = 10;
//        Boolean done = false;
//        Integer upcoming =0;
//        Integer all = 0;
//        int i = 0;
//        while (!done)
//        {
//            Page<ArtistData> artists = getArtistsImpl(country, offset, limit);
//            for (ArtistData artist : artists)
//            {
//                Pair<Integer, Integer> eventsFound = null;
//                if (artist.getSourceKey(DataSource.songKick) == null || artist.getSourceKey(DataSource.songKick).isEmpty())
//                {
//                    ArtistData updated = findInSongkick(artist);
//
//                    eventsFound = importEventsFromSongkick(updated);
//                }
//                else
//                {
//                     eventsFound = importEventsFromSongkick(artist);
//                }
//                upcoming += eventsFound.getKey();
//                all += eventsFound.getValue();
//                System.out.println(i + "/" + artists.getTotalSize() + " Looking at "  + artist.getName()
//                        + " Found " + eventsFound.getKey() +"/" + eventsFound.getValue()  + " " + " Total is  " +  upcoming + "/"  + all + " ");
//                i++;
//            }
//            offset = artists.getNextOffset();
//            if (offset == null)
//            {
//                done = true;
//            }
//        }
//    }

    public void mapGenres(String country)
    {
        Map<String, ArtistData> spotifyMap = new HashMap<>(1000);

        String offset = null;
        Integer limit = 50;
        Boolean done = false;
        TagCounter<String> tagCounter = new TagCounter<>();

        while (!done)
        {
            Page<ArtistData> artists = getArtistsImpl(country, offset, limit);
            List<String> spotifyIDs = new ArrayList<>(1000);

            for (ArtistData artist : artists)
            {
                if (artist.getSpotifyID() != null)
                {
                    spotifyMap.put(artist.getSpotifyID(), artist);
                    spotifyIDs.add(artist.getSpotifyID());
                }
            }
            List<Artist> spotifyArtists = spotify.getArtists(spotifyIDs);
            for (Artist spArt : spotifyArtists)
            {
                for (String genre : spArt.getGenres())
                {

                    String[] parts = genre.split(" ");
                    for (String part : parts)
                    {
                        logger.info ("\t\t\t" + part);

                        tagCounter.add(part);
                    }
                }
            }
            tagCounter.print();

            offset = artists.getNextOffset();
            if (offset == null)
            {
                done = true;
            }
        }
        tagCounter.print();

    }

    public void updateArtistsFromBandsInTown(String country)
    {

        Map<String, ArtistData> bitMap = new HashMap<>(1000);
        String offset = null;
        Integer limit = 10;
        Boolean done = false;
        int skip = 0;
        int i = 0;
        int hasArtist = 0;
        int added = 0;
        while (!done)
        {
            Page<ArtistData> artists = getArtistsImpl(country, offset, limit);

            for (ArtistData artist : artists)
            {
                i++;
                if (i < skip)
                {
                    continue;
                }
                if (bandsInTownWorker.hasBandsInTownArtist(artist))
                {
                    logger.info(hasArtist + "/" + i + "/" + artists.getTotalSize() + "  " + artist.getName() +   " has  bandsintown" + "   https://dashboard.promogogo.com/go/adminartists.do#!/artist/" + artist.getArtistID());
                    hasArtist++;
                }
                else
                {
                    logger.info( "   " + i + "/" + artists.getTotalSize() + "  " + artist.getName() + " does not have bandsintown" + "   https://dashboard.promogogo.com/go/adminartists.do#!/artist/" + artist.getArtistID());
                }

                Boolean bAdded = bandsInTownWorker.scrapeArtist(artist);
                if (bAdded)
                {
                    added++;
                }
            }
            offset = artists.getNextOffset();
            if (offset == null)
            {
                done = true;
            }
        }
        logger.info ("found " + hasArtist + " added " + added);
    }

    public void updateVenueFromBandsInTown(String country)
    {

//        String offset = null;
//        Integer limit = 50;
//        Boolean done = false;
//        int skip = 0;
//        int i = 0;
//        while (!done)
//        {
//            Page<VenueData> venues = getVenueImpl(country, offset, limit);
//
//            for (VenueData venue : venues)
//            {
//                i++;
//                if (i < skip)
//                {
//                    continue;
//                }
//                if (venue.getSourceValue(DataSource.bandsInTown)  != null && !venue.getSourceValue(DataSource.bandsInTown).isEmpty())
//                {
//                    logger.info(i + "/" + venues.getTotalSize() + "  " + venue.getName() + " " + venue.getSourceValue(DataSource.bandsInTown) + "   https://dashboard.promogogo.com/go/adminartists.do#!/artist/" + venue.getArtistID());
//                }
//                else
//                {
//                    logger.info(i + "/" + venues.getTotalSize() + "  " + venue.getName() + " does not have spotify" + "   https://dashboard.promogogo.com/go/adminartists.do#!/artist/" + venue.getArtistID());
//                }
//                bandsInTownWorker.importVenue(venue);
//            }
//            offset = venues.getNextOffset();
//            if (offset == null)
//            {
//                done = true;
//            }
//        }
    }



    public void updateFromSpotify(String country)
    {

        Map<String, ArtistData> spotifyMap = new HashMap<>(1000);
        String offset = null;
        Integer limit = 50;
        Boolean done = false;
        int skip = 0;
        int i = 0;
        while (!done)
        {
            Page<ArtistData> artists = getArtistsImpl(country, offset, limit);
            List<String> spotifyIDs = new ArrayList<>(1000);

            for (ArtistData artist : artists)
            {
                i++;
                if (i < skip)
                {
                    continue;
                }
                if (artist.getSpotifyID() != null)
                {
                    logger.info (i + "/" + artists.getTotalSize() + "  " + artist.getName() + " " + artist.getSpotify() + "   https://dashboard.promogogo.com/go/adminartists.do#!/artist/" + artist.getArtistID());
                    spotifyMap.put(artist.getSpotifyID(), artist);
                    spotifyIDs.add(artist.getSpotifyID());
                }
                else
                {
                    logger.info (i + "/" + artists.getTotalSize() + "  " + artist.getName() + " does not have spotify"  + "   https://dashboard.promogogo.com/go/adminartists.do#!/artist/" + artist.getArtistID());
                }


            }
            updateFromSpotifyImpl(spotifyIDs, spotifyMap);

            offset = artists.getNextOffset();
            if (offset == null)
            {
                done = true;
            }
            if (i > 150)
            {
                done = true;
            }
        }
    }



    public void updateFromSpotify (String artistID, String spotifyID)
    {
        if (spotifyID == null || spotifyID.isEmpty())
        {
            return;
        }
        ArtistData pggArtist = artistWorker.getArtist(artistID);

        if (pggArtist == null)
        {
            return;
        }


        Artist spotifyArtist = spotify.getArtist(spotifyID);
        updateArtistFromSpotify(pggArtist, spotifyArtist);
        findAndUpdateRelatedArtists(pggArtist, spotifyArtist);

        if (pggArtist.getGogoGenre() == null)
        {
            if (spotifyArtist.getGenres().length > 0)
            {
                ArtistPersisted artist = artistWorker.getArtistPersisted(pggArtist.getArtistID());

                pggArtist.setGogoGenre(getGenre(spotifyArtist));

                pggArtist.setMajorType(AttractionType.music);
                ArtistPersisted.getTable(mapper).updateItem(artist);
//                mapper.save(artist);
            }
        }


    }

    private void findAndUpdateRelatedArtists(ArtistData pggArtist, Artist spotifyArtist)
    {
        List<Artist> relatedArtists = spotify.findRelatedArtists(spotifyArtist.getId());
        for (Artist related : relatedArtists)
        {
            try
            {
                logger.info("\t\t\trelated Artist " + related.getName() + "' " + related.getPopularity() + "% - followers " +
                            StrUtil.formatNumber(related.getFollowers().getTotal()) +
                            " matches'" + pggArtist.getName() + "' it has " + related.getGenres().length + " genres. " + Arrays.toString(related.getGenres()));
            }
            catch (Exception e)
            {
                logger.error(StrUtil.stack2String(e));

            }

            updateRelatedFromSpotify(pggArtist, related);
        }
        artistWorker.setUniqueAttribute(pggArtist.getArtistID(), DataSource.spotify, "relatedImported", "true");
    }


    private void addRelated(DataSource source, ArtistData main, ArtistData second)
    {
        ArtistRelatedPersisted relationship = ArtistRelatedPersisted.find(mapper, main.getArtistID(), second.getArtistID(), source);
        if (relationship == null)
        {
            ArtistRelatedPersisted firstRhs = new ArtistRelatedPersisted(main.getArtistID(), second.getArtistID(), source.name(), "");
            ArtistRelatedPersisted.getTable(mapper).putItem(firstRhs);
//            mapper.save(firstRhs);
        }
        ArtistRelatedPersisted relationshipReverse = ArtistRelatedPersisted.find(mapper, second.getArtistID(), main.getArtistID(), source);
        if (relationshipReverse == null)
        {
            ArtistRelatedPersisted secondRhs = new ArtistRelatedPersisted(second.getArtistID(), main.getArtistID(), source.name(), "");
            ArtistRelatedPersisted.getTable(mapper).putItem(secondRhs);
//            mapper.save(secondRhs);
        }
    }

    private ArtistData getOrCreateArtist (Artist spotifyArtist)
    {
        ArtistData artist = findArtistFromSpotify(spotifyArtist);
        if (artist == null)
        {
            artist = createArtist(spotifyArtist);
        }
        else
        {
            updateArtistFromSpotify(artist, spotifyArtist);
            artistWorker.updateSearchForArtist(id, artist.getArtistID());
        }
        return artist;
    }


    private ArtistData createArtist(Artist spotifyArtist)
    {
        ArtistData artist = new ArtistData();
        artist.setName(spotifyArtist.getName());
        artist.setMajorType(AttractionType.music);
        artist.setArtistType(ArtistType.band);
        if (spotifyArtist.getImages() != null && spotifyArtist.getImages().length > 0)
        {
            artist.setBestImage(spotifyArtist.getImages()[0].getUrl());
        }
        artist.setGogoGenre(getGenre(spotifyArtist));
        artist.setCountry(getCountryCodeFromGenres(spotifyArtist));
        artist.setArtistID(spotifyArtist.getId());
        ArtistData artistData = artistWorker.create(DataSource.spotify, artist);
        updateArtistFromSpotify(artistData, spotifyArtist);
        artistWorker.updateSearchForArtist(id, artist.getArtistID());
        return artistData;
    }


    private ArtistData findArtistFromSpotify(Artist spotifyArtist)
    {
        ArtistPersisted artistByAlternativeID = artistWorker.getArtistByAlternativeID(DataSource.spotify, spotifyArtist.getId());
        if (artistByAlternativeID != null)
        {
            return artistWorker.getArtist(artistByAlternativeID.getId());
        }

        Page<ArtistData> artists = artistWorker.findArtists(new ID(), spotifyArtist.getName(), PerformerType.attraction, null, 10);
        for (ArtistData foundArtist : artists.getList())
        {
            if (foundArtist.nameMatches(spotifyArtist.getName()))
            {
                return foundArtist;
            }
        }

        return null;

    }

//    private ArtistData findInBandsInTown(ArtistData artist)
//    {
//        List<ArtistData> result = bandsInTown.findBandsInTownArtist(artist.getName());
//        if (result.size() == 1)
//        {
//            artist.addSource(DataSource.bandsInTown, result.get(0).getArtistID(), "https://www.bandsintown.com/a/" + result.get(0).getArtistID());
//            artistWorker.addSource(artist.getArtistID(), DataSource.bandsInTown, result.get(0).getArtistID(), "bandsintown", "https://www.bandsintown.com/a/" + result.get(0).getArtistID());
//            // https://rest.bandsintown.com/artists/dadi+freyr/events?app_id=squarespace-ville+valo&date=all
//            artist.addSource(DataSource.bandsInTown, result.get(0).getArtistID(), result.get(0).getUrl());
//            artistWorker.addSource(artist.getArtistID(), DataSource.bandsInTown, result.get(0).getArtistID(), "bandsintown", result.get(0).getUrl());
//            return artist;
//        }
//        for (ArtistData found : result)
//        {
//            if (artist.nameMatches(found.getName()))
//            {
//                artist.addSource(DataSource.bandsInTown, found.getArtistID(), "https://www.bandsintown.com/a/" + found.getArtistID());
//                artistWorker.addSource(artist.getArtistID(), DataSource.bandsInTown, found.getArtistID(), "bandsintown", "https://www.bandsintown.com/a/" + found.getArtistID());
//                // https://rest.bandsintown.com/artists/dadi+freyr/events?app_id=squarespace-ville+valo&date=all
//                artist.addSource(DataSource.bandsInTown, found.getArtistID(), found.getUrl());
//                artistWorker.addSource(artist.getArtistID(), DataSource.bandsInTown, found.getArtistID(), "bandsintown", found.getUrl());
//                return artist;
//            }
//        }
//        return null;
//    }
//
//    private ArtistData findInSongkick(ArtistData artist)
//    {
//        List<ArtistData> result = songKick.findSongkickArtist(artist.getName());
//        if (result.size() == 1)
//        {
//            artist.addSource(DataSource.songKick, result.get(0).getArtistID(), "https://www.songkick.com/artists/" + result.get(0).getArtistID());
//            artistWorker.addSource(artist.getArtistID(), DataSource.songKick, result.get(0).getArtistID(), "Songkick", "https://www.songkick.com/artists/" + result.get(0).getArtistID());
//            return artist;
//        }
//        for (ArtistData found : result)
//        {
//            if (artist.nameMatches(found.getName()))
//            {
//                artist.addSource(DataSource.songKick, found.getArtistID(), "https://www.songkick.com/artists/" + found.getArtistID());
//                artistWorker.addSource(artist.getArtistID(), DataSource.songKick, found.getArtistID(), "Songkick", "https://www.songkick.com/artists/" + found.getArtistID());
//                return artist;
//            }
//        }
//        return artist;
//    }
//
private String getCountryCodeFromGenres(Artist spArt)
    {
        return GenreHandler.create(spArt.getGenres()).getCountry();

    }

    private GenreData getGenre(Artist spArt)
    {
        return GenreHandler.create(spArt.getGenres()).getBestGogoGenre();

    }
//
//    private BandsinTownResult importEventsFromBandsInTown(ArtistData artist)
//    {
//        if (artist.getSourceKey(DataSource.bandsInTown) == null || artist.getSourceKey(DataSource.bandsInTown).isEmpty())
//        {
//            return null;
//        }
//
//        BandsinTownResult bandsinTownArtistPage = bandsInTown.scrapeArtistPage(artist.getArtistID(), artist.getSourceKey(DataSource.bandsInTown));
//        return bandsinTownArtistPage;
//    }
//
//
//    private  Pair<Integer, Integer> importEventsFromSongkick(ArtistData artist)
//    {
//        if (artist.getSourceKey(DataSource.songKick) == null || artist.getSourceKey(DataSource.songKick).isEmpty())
//        {
//            return new Pair<>(0,0);
//        }
//        Pair<Integer, Integer> eventsFound = songKick.countEvents(artist.getSourceKey(DataSource.songKick));
//        if (eventsFound.getKey()  > 0)
//        {
//            songKick.scrape(artist);
//        }
//        return eventsFound;
//    }

    private String toGenre(String[] genres)
    {
        if (genres == null)
        {
            return "";
        }
        String str = new String();
        for (String genre : genres)
        {
            str += genre + ", ";
        }
        return str;
    }


    private void updateArtistFromSpotify(ArtistData pggArtist, Artist spArt)
    {
        if (spArt == null)
        {
            return;
        }
        String countryCode = getCountryCodeFromGenres(spArt);

        ArtistPersisted artistPersisted = ArtistPersisted.find(mapper, pggArtist.getArtistID());


        ExternalUrl url = spArt.getExternalUrls();
        String spotifyUrl = url.get("spotify");
        Boolean changed = false;
        if (artistPersisted.getSpotify() == null || !artistPersisted.getSpotify().equalsIgnoreCase(spotifyUrl))
        {
            artistPersisted.setSpotify(spotifyUrl);
            changed = true;
        }

        if (pggArtist.getBestImage() ==  null ||  pggArtist.getBestImage().isEmpty()|| ImageAttributeUtil.isStockImage(pggArtist.getBestImage()))
        {
            if (spArt.getImages() != null || spArt.getImages().length > 0)
            {
                try
                {
                    artistPersisted.setBestImage(spArt.getImages()[0].getUrl());
                    changed = true;
                }
                catch (Exception e)
                {
                }
            }
        }
        if (countryCode != null)
        {
            if (!artistPersisted.hasCountry() || !artistPersisted.getCountry().equalsIgnoreCase(countryCode))
            {
                Home h = artistPersisted.getHome();
                if (h == null)
                {
                    h = new Home();
                }
                h.setCountry(countryCode);
                artistPersisted.setHome(h);
                changed = true;
            }
        }
        if (!artistPersisted.hasGenre() && spArt.getGenres() != null && spArt.getGenres().length > 0)
        {
            GenreData genre = getGenre(spArt);
            if (genre != null)
            {
                artistPersisted.setGenre(genre.getGenre());
                changed = true;
            }
        }

        if (changed)
        {
            ArtistPersisted.getTable(mapper).updateItem(artistPersisted);
//            mapper.save(artistPersisted);
        }

        artistWorker.addSource(artistPersisted.getId(), DataSource.spotify, spArt.getId(), "", spArt.getHref());
        artistWorker.addSource(artistPersisted.getId(), DataSource.spotify, spArt.getId(), "", spotifyUrl);
        if (pggArtist.getWikipedia() != null && !pggArtist.getWikipedia().isEmpty())
        {
            artistWorker.addSource(artistPersisted.getId(), DataSource.wikipedia, spArt.getId(), "", pggArtist.getWikipedia());
        }

        artistWorker.setUniqueAttribute(pggArtist.getArtistID(), DataSource.spotify, "popularity", spArt.getPopularity() + "");
        artistWorker.setUniqueAttribute(pggArtist.getArtistID(), DataSource.spotify, "followers", spArt.getFollowers().getTotal() + "");
        for (String genre : spArt.getGenres())
        {
            try
            {
                artistWorker.addAttribute(pggArtist.getArtistID(), DataSource.spotify, "genre", genre);
            }
            catch (Exception e)
            {
                try
                {
                    Thread.sleep(200);
                }
                catch (InterruptedException e1)
                {
                }

            }
        }

        artistWorker.updateSearchForArtist(new ID(), pggArtist.getArtistID());
    }


    private void updateFromSpotifyImpl(List<String> spotifyIDs, Map<String, ArtistData> spotifyMap)
    {
        if (spotifyIDs.isEmpty())
        {
            return;
        }
        List<Artist> spotifyArtists = spotify.getArtists(spotifyIDs);
        for (Artist artist : spotifyArtists)
        {
            String genre = toGenre(artist.getGenres());
            System.out.println(artist.getName() + "\t" + genre + " " + artist.getPopularity() .intValue()+ " " + artist.getFollowers().getTotal());
        }
        for (Artist spotifyArtist : spotifyArtists)
        {
            ArtistData pggArtist = spotifyMap.get(spotifyArtist.getId());
            if (pggArtist != null)
            {
                updateArtistFromSpotify(pggArtist, spotifyArtist);
                List<Artist> relatedSpotifyArtists = spotify.findRelatedArtists(spotifyArtist.getId());
                for (Artist otherSpotifiyArtist : relatedSpotifyArtists)
                {
                    ArtistPersisted artistPersisted = artistWorker.getArtistByAlternativeID(DataSource.spotify, otherSpotifiyArtist.getId());
                    if (artistPersisted == null)
                    {
                        if (otherSpotifiyArtist.getPopularity() > 10)
                        {
                            ArtistData newArtist = createArtist(otherSpotifiyArtist);
                            logger.info ("        " + pggArtist.getName() + " related newly created " + newArtist.getName());
                            artistWorker.addRelationship(DataSource.spotify, pggArtist, newArtist, "Spotify", true);
                            spotifyMap.put(otherSpotifiyArtist.getId(), newArtist);

                        }
                        else
                        {
                            System.out.println("       not found  " + otherSpotifiyArtist.getName() + " " +
                                    toGenre(otherSpotifiyArtist.getGenres()) + " " +
                                    otherSpotifiyArtist.getPopularity().intValue() + " " +
                                    otherSpotifiyArtist.getFollowers().getTotal());
                            continue; // we don't have this artist in our system and it is a very unpopular artist
                        }
                    }
                    else
                    {
                        System.out.println("       adding  " + otherSpotifiyArtist.getName() + " " + toGenre(otherSpotifiyArtist.getGenres()) + " " + otherSpotifiyArtist.getPopularity() .intValue()+ " " + otherSpotifiyArtist.getFollowers().getTotal());
                        ArtistData pggOtherArtist  = artistWorker.getArtist(artistPersisted.getId());
                        if (!spotifyMap.containsKey(otherSpotifiyArtist.getId()))
                        {
                            // if we don't have this artist in the map, add it, otherwise we have already processed it or will later in this session
                            updateArtistFromSpotify(pggOtherArtist, otherSpotifiyArtist);
                            spotifyMap.put(otherSpotifiyArtist.getId(), pggOtherArtist);
                        }
                        logger.info ("        " + pggArtist.getName() + " related to " + pggOtherArtist.getName());
                        artistWorker.addRelationship(DataSource.spotify, pggArtist, pggOtherArtist, "Spotify", true);
                        artistWorker.updateSearchForArtist(id, pggOtherArtist.getArtistID());

                    }
                }
                artistWorker.updateSearchForArtist(id, pggArtist.getArtistID());

            }
        }

    }

    private void updateRelatedFromSpotify(ArtistData pggArtist, Artist related)
    {
        ArtistData myArtist = findArtistFromSpotify(related);
        if (myArtist != null)
        {
            addRelated (DataSource.spotify, pggArtist, myArtist);
            updateArtistFromSpotify(myArtist, related);
        }

    }


    public List<ArtistData> getArtistsFromPlaylist(String url)
    {
        List<String> artistsFromPlaylist = spotify.getArtistsFromPlaylist(url);
        List<ArtistData> artists = new ArrayList<>();
        List<String>  missingArtists = new ArrayList<>();
        for (String artistFromPlaylist : artistsFromPlaylist)
        {
            ArtistPersisted artistPersisted = artistWorker.getArtistByAlternativeID(DataSource.spotify, artistFromPlaylist);
            if (artistPersisted != null)
            {
                ArtistData artist = artistWorker.getArtist(artistPersisted.getId());
                if (artist.getUpcoming() > 0)
                {
                    System.out.println("artist " +artist.getName() + " " + artist.getCountry()  +" has "+ artist.getUpcoming() + " upcoming events ");
                }

                artists.add(artist);
            }
            else
            {
                missingArtists.add(artistFromPlaylist);
//                System.out.println("artist " +artistFromPlaylist  +" not found ");
            }
        }
        if (!missingArtists.isEmpty())
        {
            List<Artist> spotifyArtists = spotify.getArtists(missingArtists);
            for (Artist spotifyArtist : spotifyArtists)
            {
                logger.info("Checking " + spotifyArtist.getName());
                ArtistData artist = getOrCreateArtist(spotifyArtist);

                System.out.println("artist " +artist.getName()  +" created ");

                updateRelatedFromSpotify(artist, spotifyArtist);
                artists.add(artist);
            }
        }

        return artists;
    }

    public void tagArtists(List<ArtistData> artists,List<KeyValue> attrib)
    {
        int i = 0;


        for (ArtistData artist : artists)
        {
            logger.info ("tagging popular " + artist.getName());
            for (KeyValue attr : attrib)
            {
                artistWorker.addAttribute(artist.getArtistID(), DataSource.promogogo, attr.getKey(), attr.getValue());
                artistWorker.updateSearchForArtist(id, artist.getArtistID());
            }
        }

        SearchFilter filter = new SearchFilter();
        filter.upcoming();
        filter.setSort(EventSort.date, SortOrder.asc);
        for (ArtistData artist : artists)
        {
            filter.addArtistID(artist.getArtistID());
        }

        Boolean done = false;
        String offset = null;
        Integer limit = 100;
          i = 0;
        while (!done)
        {
            Page<GogoEventSearchData> events = eventSearch.findEvents("", filter, offset, limit);
            for (GogoEventSearchData event : events)
            {
                logger.info (i + "/" + events.getTotalSize() + "  tagging event " + event.getDisplayName() +"  " + event.getWhen() );
                i++;
                for (KeyValue attr : attrib)
                {
                    eventWorker.addAttribute(event.getId(), DataSource.promogogo, attr.getKey(), attr.getValue());
                    eventWorker.updateSearch(event.getId());
                }
            }
            offset = events.getNextOffset();
            if (offset == null)
            {
                done = true;
            }
        }
        artistWorker.flush();
        eventWorker.flushSearch();
    }

    private List<List<String>> splitIntoSublists(Page<Pair<String, Long>> list, int maxSize)
    {
        int size = 1;
        if (maxSize > 1)
        {
            size = list.getSize() / maxSize  + 1;
        }
        List<List<String>> result = new ArrayList<>(size);
        int currentIndex = 0;
        List<String> sublist = new ArrayList<>(maxSize);
        for (Pair<String, Long> item : list)
        {
            sublist.add(item.getKey());
            currentIndex++;
            if (sublist.size() >= maxSize)
            {
                result.add(sublist);
                sublist = new ArrayList<>(maxSize);
                currentIndex = 0;
            }
        }

        if (!sublist.isEmpty())
        {
            result.add(sublist);
        }
        return result;
    }


    public List<GogoEventSearchData> getEvents(String country)
    {
        List<GogoEventSearchData> result = new ArrayList<>();
        SearchFilter filter = new SearchFilter();
        filter.upcoming();
        filter.addCountry("no");
        Boolean done = false;
        String offset = null;
        while (!done)
        {
            Page<GogoEventSearchData> events = eventSearch.findEvents("", filter, offset, 100);
            result.addAll(events.getList());
            offset = events.getNextOffset();
            if (offset == null)
            {
                done = true;
            }
        }
        return result;
    }
}
