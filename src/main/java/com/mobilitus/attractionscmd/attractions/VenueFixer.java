package com.mobilitus.attractionscmd.attractions;

import com.mobilitus.gogo.Cache;
import com.mobilitus.gogo.attractions.VenueWorker;
import com.mobilitus.gogo.search.AttractionSearch;
import com.mobilitus.gogo.search.EventSearch;
import com.mobilitus.persisted.attractions.venues.VenuePersisted;
import com.mobilitus.util.cache.MemcachedAdministrator;
import com.mobilitus.util.data.attractions.VenueData;
import com.mobilitus.util.data.aws.cloudsearch.GogoSearchData;
import com.mobilitus.util.data.face.FaceData;
import com.mobilitus.util.data.gogo.EventSort;
import com.mobilitus.util.data.gogo.SearchAttractionFilter;
import com.mobilitus.util.data.tabula.reportdata.SortOrder;
import com.mobilitus.util.distributed.aws.cloudsearch.DefaultSearchConfig;
import com.mobilitus.util.distributed.aws.cloudsearch.SearchConfig;
import com.mobilitus.util.distributed.dynamodb.AWSUtils;
import com.mobilitus.util.distributed.mapbox.MapBoxHandler;
import com.mobilitus.util.hexia.ID;
import com.mobilitus.util.hexia.location.CountryCode;
import com.mobilitus.util.hexia.location.Point;
import com.mobilitus.util.hexia.pagination.Page;
import org.apache.log4j.Logger;
import org.joda.time.DateTimeZone;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedAsyncClient;

import java.util.HashMap;
import java.util.Map;

/**
 * @author helgaw
 * @since 9/19/24 14:38
 */
public class VenueFixer
{
    private final DynamoDbEnhancedAsyncClient mapper;
    private final EventSearch eventSearch;
    private final AttractionSearch gogoSearch;
    private final ID id;
    private final VenueWorker venueWorker;
    private final MemcachedAdministrator cacheAdministrator;
    //    private final MemcachedAdministrator cacheAdministrator;
    private Logger logger = Logger.getLogger(VenueFixer.class);

    public VenueFixer()
    {
        AwsCredentialsProvider credentialsProvider = AWSUtils.getCredentialsProvider();

        Cache.create(credentialsProvider.resolveCredentials(), "localhost:11211");

        mapper = AWSUtils.getMapper(credentialsProvider);


        SearchConfig searchConfig = new DefaultSearchConfig(credentialsProvider);

        eventSearch = new EventSearch(searchConfig.getCredentialsProvider(), searchConfig.getEventSearchURL());

        gogoSearch = new AttractionSearch(searchConfig.getCredentialsProvider(), searchConfig.getArtistSearchURL());
        id = new ID();

        venueWorker = new VenueWorker(id, new FaceData(), mapper, AWSUtils.getS3(), searchConfig);

        cacheAdministrator = new MemcachedAdministrator();


    }
    public void scanAndFixIceVenues()
    {
        SearchAttractionFilter filter = new SearchAttractionFilter();
        filter.searchVenues(true);
        filter.addCountry("IS");
        filter.setSort(EventSort.name, SortOrder.asc);

        String offset = null;
        Integer limit = 50;
        Boolean done = false;
        int i = 0;
        Integer total = null;
        Map<String, VenueData> fixed = new HashMap<>();
        String lastName = null;

        while (!done)
        {
            Page<GogoSearchData> venues = gogoSearch.findByName("", filter, offset, limit);
            if (total == null)
            {
                total = venues.getTotalSize();
            }
            for (GogoSearchData venue : venues)
            {
                if (venue.getCountry().equalsIgnoreCase("IS") && !inIceland(venue.getLocationPoint()))
                {
                    VenueData v = updateVenue(venue);
                    fixed.put(v.getLocationID(), v);
                    logger.info(i + "/" + total + " Venue " + venue.getDisplayName()  + " moved to " + v.getPrettyAddress() + " is now in  " + v.getCountryCode());
                }
                else
                {
                    VenuePersisted v = venueWorker.getVenuePersisted(venue.getId());
                    if (v != null)
                    {
                        logger.info(i + "/" + total + " Venue " + venue.getDisplayName()  + " " + v.getPrettyAddress() + " "+ v.getLocationInfo().getLocationPoint() + " is in Iceland");
                    }
                }
                i++;
            }
            offset = venues.getNextOffset();
            if (offset == null)
            {
                done= true;
            }

        }

        logger.info("Fixed " + fixed.size() + " venues");
        for (VenueData venue : fixed.values())
        {
            logger.info(venue.getName() + " " + venue.getPrettyAddress());
        }

    }

    public void mergeSame()
    {
        SearchAttractionFilter filter = new SearchAttractionFilter();
        filter.searchVenues(true);
        filter.addCountry("IS");
        filter.setSort(EventSort.name, SortOrder.asc);

        String offset = null;
        Integer limit = 50;
        Boolean done = false;
        int i = 0;
        int merged = 0;
        Integer total = null;
        Map<String, VenueData> fixed = new HashMap<>();
        String lastName = null;
        VenueData previous = null;
        while (!done)
        {
            Page<GogoSearchData> venues = gogoSearch.findByName("", filter, offset, limit);
            if (total == null)
            {
                total = venues.getTotalSize();
            }
            for (GogoSearchData venue : venues)
            {
                if (previous == null)
                {
                    previous = venueWorker.getVenue(venue.getId());
                    lastName = previous.getName();
                    continue;
                }
                VenueData current = venueWorker.getVenue(venue.getId());
                if (current == null)
                {
                    continue;
                }
                VenuePersisted v = venueWorker.getVenuePersisted(venue.getId());
                if (current.getName().equalsIgnoreCase(lastName) || v.hasAlias(lastName))
                {
                    if (current.getLocationPoint() != null && previous.getLocationPoint() != null && current.getLocationPoint().isValid() && previous.getLocationPoint().isValid())
                    {
                        if (current.getLocationPoint().distanceTo(previous.getLocationPoint()) < 0.5)
                        {
                            logger.info(i + "/" + total + " (" + merged + ")  Venue " + current.getName()  + " " + current.getPrettyAddress() + " is the same as " + previous.getName() + " merging");
                            merged++;
                            if (previous.getAll() > current.getAll())
                            {
                                venueWorker.merge(previous.getLocationID(), current.getLocationID());
                            }
                            else
                            {
                                venueWorker.merge(current.getLocationID(), previous.getLocationID());
                                previous = current;
                                lastName = current.getName();
                            }
                        }
                    }
                }
                else
                {
                    logger.info(i + "/" + total + " Checking Venue " + current.getName()  + " " + current.getPrettyAddress() );
                    previous = current;
                    lastName = current.getName();
                }
                i++;
            }
            offset = venues.getNextOffset();
            if (offset == null)
            {
                done= true;
            }

        }

        logger.info("Fixed " + fixed.size() + " venues");
        for (VenueData venue : fixed.values())
        {
            logger.info(venue.getName() + " " + venue.getPrettyAddress());
        }

    }

    private boolean inIceland(Point locationPoint)
    {
        // all of iceland and nothing else is within this radius
        Point centerIce = new Point("64.5846131263839, -18.722521957340597");
        double distance = 350;
        if (locationPoint != null && locationPoint.isValid())
        {
            if (locationPoint.distanceTo(centerIce) < distance)
            {
                return true;
            }
        }
        return false;
    }

    private VenueData updateVenue(GogoSearchData venue)
    {
        VenuePersisted venueToUpdate = venueWorker.getVenuePersisted(venue.getId());

        MapBoxHandler mapbox = new MapBoxHandler();

        VenueData location = mapbox.getLocation(venueToUpdate.getName(), venueWorker.getVenue(venue.getId()));
        if (location != null)
        {
            //            venueToUpdate.setAddress1(location.getAddress1());
            //            venueToUpdate.setAddress2(location.getAddress2());
            //            venueToUpdate.setZip(location.getZip(), location.getCity());
            venueToUpdate.setCountry(location.getCountryCode().name());

            if (!venueToUpdate.getLocationInfo().getLocationPoint().isValid())
            {
                venueToUpdate.setLocation(location.getLocationPoint());
            }

            if (location.getCountryCode() == CountryCode.us || location.getCountryCode() == CountryCode.ca ||
                location.getCountryCode() == CountryCode.mx || location.getCountryCode() == CountryCode.au)
            {
                venueToUpdate.setRegion(location.getState());
            }
            //            venueToUpdate.setLocation(location.getLocation());
            if (venueToUpdate.getTimezone() == null || venueToUpdate.getTimezone().isEmpty())
            {
                DateTimeZone timezone = mapbox.getTimezone(location.getLocationPoint());
                venueToUpdate.setTimezone(timezone.getID());
            }
            VenuePersisted.getTable(mapper).updateItem(venueToUpdate);
            venueWorker.updateSearchForVenue(id, venueToUpdate.getId());
        }
        return venueWorker.getVenue(venueToUpdate.getId());
    }

}
