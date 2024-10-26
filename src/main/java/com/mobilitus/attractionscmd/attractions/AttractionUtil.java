package com.mobilitus.attractionscmd.attractions;

import com.mobilitus.gogo.Cache;
import com.mobilitus.gogo.attractions.ArtistWorker;
import com.mobilitus.gogo.attractions.EventWorker;
import com.mobilitus.gogo.search.AttractionSearch;
import com.mobilitus.gogo.search.EventSearch;
import com.mobilitus.util.data.attractions.ArtistData;
import com.mobilitus.util.data.attractions.DataSource;
import com.mobilitus.util.data.aws.cloudsearch.GogoEventSearchData;
import com.mobilitus.util.data.gogo.EventSort;
import com.mobilitus.util.data.gogo.LocationConst;
import com.mobilitus.util.data.gogo.PromoGhettoData;
import com.mobilitus.util.data.gogo.SearchFilter;
import com.mobilitus.util.data.gogo.SourceListingData;
import com.mobilitus.util.data.tabula.reportdata.SortOrder;
import com.mobilitus.util.distributed.aws.cloudsearch.DefaultSearchConfig;
import com.mobilitus.util.distributed.aws.cloudsearch.SearchConfig;
import com.mobilitus.util.cache.MemcachedAdministrator;
import com.mobilitus.util.distributed.dynamodb.AWSUtils;
import com.mobilitus.util.hexia.location.Point;
import com.mobilitus.util.hexia.pagination.Page;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedAsyncClient;

import java.util.Arrays;
import java.util.List;

/**
 * @author helgaw
 * @since 7/31/24 10:06
 */
public class AttractionUtil
{
    private static MemcachedAdministrator cacheAdministrator = null;
    private final AwsCredentialsProvider credentials;
    private final DynamoDbEnhancedAsyncClient mapper;
    private static final Logger logger = Logger.getLogger(AttractionUtil.class);
    private final AttractionSearch gogoSearch;
    private final EventWorker eventWorker;
    private final EventSearch eventSearch;
    private final ArtistWorker artistWorker;


    public AttractionUtil(AwsCredentialsProvider credentials, String memcache)
    {
        this.credentials = credentials;

        if (cacheAdministrator == null)
        {
            Cache.create(credentials.resolveCredentials(), memcache);
            cacheAdministrator = new MemcachedAdministrator();
        }
        mapper = AWSUtils.getMapper(credentials);

        SearchConfig searchConfig = new DefaultSearchConfig(credentials);

        eventSearch = new EventSearch(searchConfig.getCredentialsProvider(), searchConfig.getEventSearchURL());

        gogoSearch = new AttractionSearch(searchConfig.getCredentialsProvider(), searchConfig.getArtistSearchURL());

        eventWorker = new EventWorker(null, null, mapper, AWSUtils.getS3(), searchConfig);
        artistWorker = new ArtistWorker(null, null, mapper, AWSUtils.getS3(), searchConfig);
    }


    public void addAkureyrarVakaToEvents()
    {
        String artistID = "01c32952-596b-4d0f-a04e-731716e98014";
        final ArtistData  artist = artistWorker.getArtist(artistID);
        logger.info(artist.getName() + " now has " +artist.getUpcoming() + " upcoming events");
        DateTime from = new DateTime (2024, 8, 30, 20, 0);
        DateTime until = new DateTime (2024, 9, 1, 23, 59);
        String []query = {"akureyrarvaka", "akureyrarvöku"};
        Arrays.stream(query).toList().forEach(q -> addFestivalArtistToFestivalEvents(artist, q, from, until, LocationConst.AKUREYRI, 3D));

        ArtistData  nowArtist = artistWorker.getArtist(artistID);
        logger.info(nowArtist.getName() + " now has " +nowArtist.getUpcoming() + " upcoming events");

    }

    public void addMenningarNottToEvents()
    {
        String cultureNightID = "1e4d5ca9-6c76-455f-8229-f7809849e5b3";
        ArtistData artist = artistWorker.getArtist(cultureNightID);
        addFestivalArtistToFestivalEvents(artist, "menningarnótt", new DateTime (2024, 8, 24, 0, 0), new DateTime(2024, 8, 25, 3, 0), LocationConst.RVK, 3D);
    }

    public void addFestivalArtistToFestivalEvents(ArtistData artist, String query, DateTime from, DateTime until, Point location, Double radius)
    {
        SearchFilter filter = new SearchFilter();
        filter.setFrom(from);
        filter.setUntil(until);

        filter.addPointRadiusKM(location, radius);
        filter.setSort(EventSort.date, SortOrder.asc);
        String artistID = artist.getArtistID();
        int i = 0;
        int found = 0;
        logger.info(artist.getName() + " has " +artist.getUpcoming() + " upcoming events");

        String offset = null;
        Integer limit = 100;
        Boolean done = false;

        Page<GogoEventSearchData> events = eventSearch.findEvents("", filter, offset, limit);

        logger.info("Found " + events.getTotalSize() + " events ") ;
        StringBuilder buf = new StringBuilder();
        while (!done)
        {
            events = eventSearch.findEvents("", filter, offset, limit);

            for (GogoEventSearchData event : events)
            {
                if (event.getArtistIDs() != null && event.getArtistIDs().contains(artistID))
                {
                    logger.info("Already has  " + event.getDisplayName() + " " + event.getWhen());
                }
                else
                {
                    logger.info(i + "/" + events.getTotalSize() + " Adding " + artist.getName() + " to " + event.getDisplayName() + " " + event.getWhen());
                    eventWorker.addArtistToEvent(artistID, event.getId());
                    i++;
                }
            }
            offset = events.getNextOffset();
            if (offset == null)
            {
                done = true;
            }
            eventSearch.flush();
            gogoSearch.flush();
        }
        artist = artistWorker.getArtist(artistID);
        logger.info(artist.getName() + " now has " +artist.getUpcoming() + " upcoming events");
    }





    public void findEventsMissingVenueOrArtists()
    {
        SearchFilter filter = new SearchFilter();
        filter.setCreatedAfter(new DateTime(2024, 8, 1, 0, 0));

        String offset = null;
        Integer limit = 100;
        Boolean done = false;
        int i = 0;
        int found = 0;
        Page<GogoEventSearchData> events = eventSearch.findEvents("", filter, offset, limit);

        logger.info("Found " + events.getTotalSize() + " events created after aug1 ") ;
        StringBuilder buf = new StringBuilder();
        while (!done)
        {
            events = eventSearch.findEvents("", filter, offset, limit);

            for (GogoEventSearchData event : events)
            {
                i++;
                if (event.getCountry() == null || event.getCountry().isEmpty() || event.getCountry().equalsIgnoreCase("is"))
                {
                    if (event.getVenueID() == null || event.getVenueID().isEmpty())
                    {
                        PromoGhettoData event1 = eventWorker.getEvent(event.getId());
                        if (hasSource(event1, DataSource.facebook))
                        {
                            found++;
                            String sources = toSourceString(event1.getSources());
                            logger.info(found + "/" + events.getTotalSize() + " " + " " + event.getDisplayName() + "  " + sources + " "  + event1.getCreated()  + " is missing a venue https://dashboard.promogogo.com/go/promogogo/createmoment.do?event=" + event.getId());
                            if (event1.getWhen().isAfterNow())
                            {
                                eventWorker.addAttribute(event1.getEventID(), DataSource.promogogo, "missing_venue", "missing venue from import");
                                try
                                {
                                    Thread.sleep(100);
                                }
                                catch (InterruptedException e)
                                {
                                }
                                eventWorker.updateSearch(event1.getEventID());
                            }
                        }
                    }
                    else if (event.getArtistIDs() == null || event.getArtistIDs().isEmpty())
                    {
                        PromoGhettoData event1 = eventWorker.getEvent(event.getId());

                        if (hasSource(event1, DataSource.facebook))
                        {
//                            found++;
                            String sources = toSourceString(event1.getSources());
//                            logger.info(found + "/" + events.getTotalSize() + " " + event.getDisplayName() + "  " + sources + " is missing an artist https://dashboard.promogogo.com/go/promogogo/createmoment.do?event=" + event.getId());
                        }
                    }

//                    if (i % 100 == 0)
//                    {
//                        PromoGhettoData event1 = eventWorker.getEvent(event.getId());
//                        if (event1 != null)
//                        {
//                            String sources = toSourceString(event1.getSources());
////                            logger.info("    " + i + "/" + events.getTotalSize() + " " + event.getDisplayName() + " " + event.getWhen() + " from  " + sources);
//                        }
//                    }
                }
            }
            offset = events.getNextOffset();
            if (offset == null)
            {
                done = true;
            }
        }
        eventSearch.flush();
        gogoSearch.flush();
    }

    private boolean hasSource(PromoGhettoData event1, DataSource dataSource)
    {
        for (SourceListingData source : event1.getSources())
        {
            if (source.getSource() == dataSource)
            {
                return true;
            }
        }
        return false;
    }

    private String toSourceString(List<SourceListingData> sources)
    {
        StringBuilder sb = new StringBuilder();
        for (SourceListingData source : sources)
        {
            if (sb.length() > 0)
            {
                sb.append(", ");
            }
            sb.append(source.getSource() + ":" + source.getSourceID());
        }
        return sb.toString();
    }


    public void cleanupEvents(String artistID)
    {
        SearchFilter filter = new SearchFilter();
        filter.addArtistID(artistID);
        filter.setFrom(new DateTime(2024, 1, 1, 0, 0));

        String offset = null;
        Integer limit = 100;
        Boolean done = false;
        while (!done)
        {
            Page<GogoEventSearchData> events = eventSearch.findEvents("", filter, offset, limit);

            logger.info("Found " + events.getTotalSize() + " events for " + artistID) ;
            for (GogoEventSearchData event : events)
            {
                logger.info("Removing artist from  " + event.getId() + " for " + artistID + " and " + event.getDisplayName() + " " + event.getWhen());
                eventWorker.removeArtistFromEvent(artistID, event.getId());
            }
            offset = events.getNextOffset();
            if (offset == null)
            {
                done = true;
            }
        }
        eventSearch.flush();
        gogoSearch.flush();
    }
}
