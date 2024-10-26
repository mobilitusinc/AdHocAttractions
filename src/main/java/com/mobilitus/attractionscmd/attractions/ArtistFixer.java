package com.mobilitus.attractionscmd.attractions;

import com.mobilitus.gogo.Cache;
import com.mobilitus.gogo.attractions.ArtistWorker;
import com.mobilitus.gogo.attractions.EventWorker;
import com.mobilitus.gogo.search.AttractionSearch;
import com.mobilitus.gogo.search.EventSearch;
import com.mobilitus.util.cache.MemcachedAdministrator;
import com.mobilitus.util.data.attractions.ArtistData;
import com.mobilitus.util.data.attractions.ArtistType;
import com.mobilitus.util.data.attractions.AttractionType;
import com.mobilitus.util.data.attractions.VenueData;
import com.mobilitus.util.data.aws.cloudsearch.GogoEventSearchData;
import com.mobilitus.util.data.face.FaceData;
import com.mobilitus.util.data.gogo.EventSort;
import com.mobilitus.util.data.gogo.PromoGhettoData;
import com.mobilitus.util.data.gogo.SearchFilter;
import com.mobilitus.util.data.tabula.reportdata.SortOrder;
import com.mobilitus.util.distributed.aws.cloudsearch.DefaultSearchConfig;
import com.mobilitus.util.distributed.aws.cloudsearch.SearchConfig;
import com.mobilitus.util.distributed.dynamodb.AWSUtils;
import com.mobilitus.util.hexia.ID;
import com.mobilitus.util.hexia.pagination.Page;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedAsyncClient;

import java.util.HashMap;
import java.util.Map;

/**
 * @author helgaw
 * @since 9/24/24 16:27
 */
public class ArtistFixer
{
    private final DynamoDbEnhancedAsyncClient mapper;
    private final EventSearch eventSearch;
    private final AttractionSearch gogoSearch;
    private final ID id;
    private final ArtistWorker artistWorker;
    private final MemcachedAdministrator cacheAdministrator;
    private final EventWorker eventWorker;
    //    private final MemcachedAdministrator cacheAdministrator;
    private Logger logger = Logger.getLogger(ArtistFixer.class);

    public ArtistFixer()
    {
        AwsCredentialsProvider credentialsProvider = AWSUtils.getCredentialsProvider();

        Cache.create(credentialsProvider.resolveCredentials(), "localhost:11211");

        cacheAdministrator = new MemcachedAdministrator();

        mapper = AWSUtils.getMapper(credentialsProvider);


        SearchConfig searchConfig = new DefaultSearchConfig(credentialsProvider);

        eventSearch = new EventSearch(searchConfig.getCredentialsProvider(), searchConfig.getEventSearchURL());

        gogoSearch = new AttractionSearch(searchConfig.getCredentialsProvider(), searchConfig.getArtistSearchURL());
        id = new ID();

        artistWorker = new ArtistWorker(id, new FaceData(), mapper, AWSUtils.getS3(), searchConfig);
        eventWorker = new EventWorker(id, new FaceData(), mapper, AWSUtils.getS3(), searchConfig);


    }

    public void fixArtistsOsloInnovation()
    {
        SearchFilter filter = new SearchFilter();
        filter.setFrom(DateTime.now().minusDays(5));
        filter.setSort(EventSort.date, SortOrder.asc);
        Map<String, String> artistDone = new HashMap<>(1000);
        ArtistData oiwArtist = artistWorker.getArtist("a57e05f6-3e10-47a0-be9c-d39994250163");
        String offset = null;
        int i = 0;
        int artistFound = 0;
        Boolean done = false;
        while (!done)
        {
            Page<GogoEventSearchData> events = eventSearch.findEvents("Oslo Innovation Week", filter, offset, 100);
            for (GogoEventSearchData event : events)
            {
                logger.info(i + "/" + events.getTotalSize() + " " + event.getDisplayName());
                i++;
                PromoGhettoData eventGhettoData = eventWorker.getEvent(event.getId());
                Boolean hasOIW = false;
                for (ArtistData artist : eventGhettoData.getArtists())
                {
                    if (artist.getArtistID().equals(oiwArtist.getArtistID()))
                    {
                        hasOIW = true;
                        break;
                    }
                    if (artistDone.containsKey(artist.getArtistID()))
                    {
                        continue;
                    }
                    ArtistData myArtist = artistWorker.getArtist(artist.getArtistID());
                    logger.info( artistFound + " checking " + myArtist.getName() +  "  " + myArtist.getArtistType() + " " + myArtist.getMajorType() + " https://devbackstage.promogogo.com/attraction/" + myArtist.getArtistID());
                    artistFound++;
                    boolean fix = false;
                    if (myArtist.getArtistType() != null && myArtist.getArtistType() == ArtistType.speaker)
                    {
                        if (myArtist.getMajorType() != null && myArtist.getMajorType() == AttractionType.music)
                        {
                            myArtist.setMajorType(AttractionType.lecture);
                            fix = true;
                        }
                    }
                    else if (myArtist.getArtistType() != null && myArtist.getArtistType() == ArtistType.company)
                    {
                        if (myArtist.getMajorType() != null && myArtist.getMajorType() == AttractionType.music)
                        {
                            myArtist.setMajorType(AttractionType.other);
                            fix = true;
                        }
                    }
                    else if (myArtist.getArtistType() != null && myArtist.getArtistType() == ArtistType.conference)
                    {
                        if (myArtist.getMajorType() != null && myArtist.getMajorType() != AttractionType.conference)
                        {
                            myArtist.setMajorType(AttractionType.conference);
                            fix = true;
                        }
                    }
                    else
                    {

                        myArtist.setArtistType(ArtistType.company);
                        myArtist.setMajorType(AttractionType.other);
                        fix = true;
                    }
                    if (fix)
                    {
                        artistWorker.update(myArtist.getArtistID(), myArtist, true);
                    }

                    artistDone.put(artist.getArtistID(), artist.getArtistID());
                }
                if (!hasOIW)
                {
                    logger.info("adding OIW to " + event.getDisplayName());
                    eventWorker.addArtistToEvent(oiwArtist.getArtistID(), event.getId());
                    eventWorker.updateSearch(event.getId());
                }
            }
            offset = events.getNextOffset();
            if (offset == null)
            {
                done = true;
            }
        }
        artistWorker.updateSearchForArtist(new ID(), oiwArtist.getArtistID());

    }

    public void fixVenuesOsloInnovation()
    {
        SearchFilter filter = new SearchFilter();
        filter.setFrom(DateTime.now().minusDays(1));
        filter.setSort(EventSort.date, SortOrder.asc);
        Map<String, String> venuesDone = new HashMap<>(1000);
        String offset = null;
        int i = 0;
        int venuesFound = 0;
        Boolean done = false;
        while (!done)
        {
            Page<GogoEventSearchData> events = eventSearch.findEvents("Oslo Innovation Week", filter, null, 100);
            for (GogoEventSearchData event : events)
            {
//                logger.info(i + "/" + events.getTotalSize() + " " + event.getDisplayName());
//                i++;
                PromoGhettoData eventGhettoData = eventWorker.getEvent(event.getId());
                VenueData venue = eventGhettoData.getVenue();
                if (venue == null)
                {
                    logger.info(  event.getDisplayName() + " has no venue https://devbackstage.promogogo.com/event/" +  event.getId());
                    continue;
                }
                if(venuesDone.containsKey(venue.getLocationID()))
                {
                    continue;
                }
                logger.info( venuesFound + " checking " + venue.getName() +  "  " + venue.getPrettyAddress() + " https://dashboard.promogogo.com/go/editvenue.do#!/venue/" + venue.getLocationID());
                venuesDone.put(venue.getLocationID(), venue.getLocationID());
                venuesFound++;
            }
            offset = events.getNextOffset();
            if (offset == null)
            {
                done = true;
            }
        }


    }


}
