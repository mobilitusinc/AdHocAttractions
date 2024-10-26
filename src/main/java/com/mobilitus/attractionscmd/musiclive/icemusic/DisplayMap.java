package com.mobilitus.attractionscmd.musiclive.icemusic;

import com.mobilitus.gogo.attractions.ArtistWorker;
import com.mobilitus.gogo.attractions.EventWorker;
import com.mobilitus.gogo.attractions.VenueWorker;
import com.mobilitus.gogo.search.AttractionSearch;
import com.mobilitus.gogo.search.EventSearch;
import com.mobilitus.persisted.attractions.events.EventPersisted;
import com.mobilitus.util.cache.Cache;
import com.mobilitus.util.cache.CacheConst;
import com.mobilitus.util.cache.ExpiredException;
import com.mobilitus.util.cache.MemcachedAdministrator;
import com.mobilitus.util.data.attractions.ArtistData;
import com.mobilitus.util.data.attractions.AttractionType;
import com.mobilitus.util.data.attractions.VenueData;
import com.mobilitus.util.data.aws.cloudsearch.GogoEventSearchData;
import com.mobilitus.util.data.aws.cloudsearch.GogoSearchData;
import com.mobilitus.util.data.aws.kinesis.KinesisStream;
import com.mobilitus.util.data.gogo.EventSort;
import com.mobilitus.util.data.gogo.PromoGhettoData;
import com.mobilitus.util.data.gogo.SearchFilter;
import com.mobilitus.util.data.pusher.MessageType;
import com.mobilitus.util.data.pusher.PusherMessage;
import com.mobilitus.util.data.tabula.reportdata.SortOrder;
import com.mobilitus.util.data.ticketMaster.microflex.EventStatus;
import com.mobilitus.util.distributed.aws.cloudsearch.DefaultSearchConfig;
import com.mobilitus.util.distributed.aws.cloudsearch.SearchConfig;
import com.mobilitus.util.distributed.aws.kinesis.Producer;
import com.mobilitus.util.distributed.aws.s3.S3;
import com.mobilitus.util.distributed.dynamodb.AWSUtils;
import com.mobilitus.util.hexia.ID;
import com.mobilitus.util.hexia.Pair;
import com.mobilitus.util.hexia.StrUtil;
import com.mobilitus.util.hexia.location.CountryCode;
import com.mobilitus.util.hexia.location.LocationInfo;
import com.mobilitus.util.hexia.location.Point;
import com.mobilitus.util.hexia.pagination.Page;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.Duration;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedAsyncClient;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * @author helgaw
 * @todo add class description.
 * @since 11/29/22 10:53
 */
public class DisplayMap // implements Callable<Integer>
{
    private final DynamoDbEnhancedAsyncClient mapper;
    private PromoGhettoData event;
    private String faceUrl;
    private Producer toPusher;



    private Integer minutes;

    private S3 s3;
    private SearchConfig searchConfig;
    private AttractionSearch gogoSearch;

    private EventWorker eventWorker;
    private VenueWorker venueWorker;
    private ArtistWorker artistWorker;


    private static final Logger logger = Logger.getLogger(DisplayMap.class);

    private EventSearch eventSearch;

    private MemcachedAdministrator  cacheAdministrator;
    private   List<String> iceArtists = new ArrayList<>(10) ;

    public DisplayMap()
    {
        AwsCredentialsProvider credentials  = AWSUtils.getCredentialsProvider();

        mapper = AWSUtils.getMapper(credentials);

        Cache.create(credentials.resolveCredentials(), "localhost:11211");


        SearchConfig searchConfig = new DefaultSearchConfig(credentials);

        eventSearch = new EventSearch(searchConfig.getCredentialsProvider(), searchConfig.getEventSearchURL());

        gogoSearch = new AttractionSearch(searchConfig.getCredentialsProvider(), searchConfig.getArtistSearchURL());

        eventWorker = new EventWorker( mapper, AWSUtils.getS3(), searchConfig);
        artistWorker = new ArtistWorker(null, null, mapper, AWSUtils.getS3(), searchConfig);
        venueWorker = new VenueWorker(null, null, mapper, AWSUtils.getS3(), searchConfig);
        toPusher = new Producer(KinesisStream.toPusher, credentials);
        this.minutes = 15;
        this.faceUrl = "ymsirvidburdir";
    }


    public Integer runIt(String sel) throws Exception
    {
//        iceArtists = getIceArtistsIDs();
        runEventList(sel);

        return 0;
    }



    public void cacheEvents(String listSelect)
    {


        SearchFilter filter = null;
        if (listSelect.equalsIgnoreCase("is"))
        {
            filter = createIsFilter();
        }
        else if (listSelect.equalsIgnoreCase("be"))
        {
            filter = createArtistAbroadFilter("be");
        }
        else if (listSelect.equalsIgnoreCase("uton"))
        {
            filter = createArtistAbroadFilter("is");
        }
        if (filter == null)
        {
            return;
        }
        String offset = null;
        Boolean done = false;
        while (!done)
        {
            Page<GogoEventSearchData> searchResults = eventSearch.findEvents("", filter, offset, 10);
            searchResults.getList().parallelStream().forEach((searchEvent ->
            {

                EventPersisted event = getEventPersistedNoCache(searchEvent.getId());
                getVenueNoCache(event);
                getArtistPersistedNoCache(event);

                logger.info(event.getWhen() + " " + event.getTitle());
            }));

            offset = searchResults.getNextOffset();
            if (offset == null)
            {
                done = true;
            }
        }

    }

    public void runEventList(String listSelect)
    {
        int index = 0;

        SearchFilter filter = null;
        if (listSelect.equalsIgnoreCase("is"))
        {
            filter = createIsFilter();
        }
        else if (listSelect.equalsIgnoreCase("be"))
        {
            filter = createArtistAbroadFilter("be");
        }
        else if (listSelect.equalsIgnoreCase("uton"))
        {
            filter = createArtistAbroadFilter("is");
        }
        if (filter == null)
        {
            return;
        }

        int i = 0;
        List<Pair<Duration, GogoSearchData>> moments = getScaledTimeline(filter, minutes);
        DateTime start = DateTime.now();
        for (Pair<Duration, GogoSearchData> moment : moments)
        {
            i++;
            PusherMessage pusherMessage = createPurchase(moment.getValue());
            if (pusherMessage == null)
            {
                continue;
            }
             logger.info(i + "/" + moments.size() + " " +pusherMessage.getWhen() + " --> " + start.plus(moment.getKey()) + " - " + new Duration(DateTime.now() , start.plus(moment.getKey())));

//            napUntil(start.plus(moment.getKey()));

            pusherMessage.setOwnerFace(faceUrl);

            String title = moment.getValue().getDisplayName();
            if (title.length() > 30)
            {
                title = title.substring(0, 29);
            }

             logger.info(index + "\t" + StringUtils.rightPad(title, 30) + "\t" + moment.getValue().getWhen() + " " );

            index++;

             toPusher.sendSingle(faceUrl, MessageType.purchaseCompleted.name(), pusherMessage.toJson());

        }

        toPusher.flush();

        logger.info (i + " events in " + new Duration(start, DateTime.now()));
    }





    private SearchFilter createArtistAbroadFilter(String country)
    {
        SearchFilter filter = new SearchFilter();
        filter.setFrom(new DateTime(DateTime.now().year().get(), 1, 1, 0, 0, 0, DateTimeZone.UTC));
        filter.setUntil((new DateTime(DateTime.now().year().get() + 1, 1, 1, 0, 0, 0, DateTimeZone.UTC)).minusMinutes(1));
        filter.addNegativeLocation(null, null, country);
        filter.setMajorType(AttractionType.music);
        filter.setSort(EventSort.date, SortOrder.asc);
        filter.addAttribute("artistcountry_" + country);
//        List<String> artistIDs = getIceArtistsIDs();
//        filter.addArtistIDs(artistIDs);
        return  filter;
    }

    private SearchFilter createCountryFilter(String country)
    {
        SearchFilter filter = new SearchFilter();
        filter.setFrom(new DateTime(DateTime.now().year().get(), 1, 1, 0, 0, 0, DateTimeZone.UTC));
        filter.setUntil((new DateTime(DateTime.now().year().get() + 1, 1, 1, 0, 0, 0, DateTimeZone.UTC)).minusMinutes(1));
        filter.addCountry(country);
        filter.setMajorType(AttractionType.music);
        filter.setSort(EventSort.date, SortOrder.asc);
        return  filter;
    }


    private SearchFilter createIsFilter()
    {
        return createCountryFilter("is");
    }

    private List<Pair<Duration, GogoSearchData>> getScaledTimeline(SearchFilter filter, Integer minutes)
    {
        String offset = null;
        Boolean done = false;
        DateTime startOfYear = filter.getFrom();
        Long minutesInYear = Duration.standardDays(365).getStandardMinutes();
        Long milliSecondsInWindow = Duration.standardMinutes(minutes).getMillis();

        Page<GogoEventSearchData> events = eventSearch.findEvents("", filter, offset, 10);
        logger.info("we have " + events.getTotalSize() +" events to display in that timeframe");

        List<Pair<Duration, GogoSearchData>> result = new ArrayList<>(5000);
        DateTime now = DateTime.now();
        int i = 0;
        while (!done)
        {
            events = eventSearch.findEvents("", filter, offset, 10);

            try
            {
                for (GogoEventSearchData event : events)
                {
                    Duration timeFromStart = new Duration(startOfYear, event.getWhen());
                    Double milliOffset = calcOffset (timeFromStart, minutesInYear, milliSecondsInWindow);
                    if (event.getDisplayName().toLowerCase().startsWith("open lab"))
                    {
                        continue;
                    }
                    if (event.getDisplayName().toLowerCase().contains("pub quiz"))
                    {
                        continue;
                    }
                    if (event.getDisplayName().toLowerCase().contains("fræðsluerindi"))
                    {
                        continue;
                    }
                    if (event.getDisplayName().toLowerCase().contains("happy hour"))
                    {
                        continue;
                    }

                    logger.info (i + "/" + events.getTotalSize() + " " + event.getWhen() + " --> "    + event.getDisplayName() + " " + event.getLocationPoint()
                            + " https://dashboard.promogogo.com/go/promogogo/createmoment.do?event=" + event.getId());
                    Pair<Duration, GogoSearchData> moment = new Pair(Duration.millis(milliOffset.longValue()), event);
                    result.add(moment);
                    i++;
                }
                offset = events.getNextOffset();
                if (offset == null)
                {
                    done = true;
                }
            }
            catch (Exception e)
            {
                logger.error(StrUtil.stack2String(e));
               e.printStackTrace();
            }
        }
        return result;
    }

    private Double calcOffset(Duration timeFromStart, Long minutesInYear, Long milliSecondsInWindow)
    {
        Double offsetInYear = (timeFromStart.getStandardMinutes() * 1.0D) / (minutesInYear * 100.0);
        return milliSecondsInWindow * offsetInYear;
    }

    private PusherMessage createEndMessage()
    {
        ID id = new ID();

        PusherMessage pusherMessage = new PusherMessage(id, "mobilitus.is");

        pusherMessage.setCreated(DateTime.now());
        pusherMessage.setWhen(DateTime.now());

        LocationInfo locationInfo = new LocationInfo();
        locationInfo.setCity("Reykjavik");
        locationInfo.setCountryCode(CountryCode.is);
        locationInfo.setLocationPoint(new Point("64.88579703592035, -18.23348554708724"));
        pusherMessage.setLocation(locationInfo);
        pusherMessage.add("venueLocation", locationInfo.getLocationPoint());

        pusherMessage.setCreated(DateTime.now());

        pusherMessage.add("guid", UUID.randomUUID());
        //        pusherMessage.add("destination", article.getPart1());
//        pusherMessage.add("testAggregate", "true");
        //        pusherMessage.setOwnerFace(outputFace);
        pusherMessage.add("eventID","0");
        pusherMessage.add("orderType", "primary");
        pusherMessage.add("title", "mobilitus.is");
        pusherMessage.add("pageType", "purchaseCompleted");
        pusherMessage.add("numberOfTickets", 1);
        pusherMessage.add("eventTime", DateTime.now());
        pusherMessage.add("site", "pgg");

        pusherMessage.add("bestImage", "");

        pusherMessage.add("artistID", "");
        pusherMessage.add("artistName", "Mobilitus");

        return pusherMessage;
    }


    private PusherMessage createPurchase(GogoSearchData searchEvent)
    {
        ID id = new ID();

        EventPersisted eventPersisted = getEventPersisted(searchEvent.getId());
//        try
//        {
//            pgg =  (PromoGhettoData) cacheAdministrator.getFromCache("event."+ searchEvent.getId());
//        }
//        catch ( ExpiredException e)
//        {
//            pgg = eventWorker.getEventLite(searchEvent.getId());
//            cacheAdministrator.putInCache("event." + pgg.getEventID(), pgg, CacheConst.TIMEOUT_6H);
//        }

        if (eventPersisted.isPrivate())
        {
            return null;
        }
        if (eventPersisted.eventStatus() == EventStatus.cancelled || eventPersisted.eventStatus() == EventStatus.postponed)
        {
            return null;
        }
        if (eventPersisted.getTitle().toLowerCase().contains("canceled") || eventPersisted.getTitle().toLowerCase().contains("fresta") ||
                eventPersisted.getTitle().toLowerCase().contains("aflýst") ||
                eventPersisted.getTitle().toLowerCase().contains("cancelled"))
        {
            return null;
        }
        if (searchEvent.getType() != null && searchEvent.getType().equalsIgnoreCase("parking"))
        {
            return null;
        }
        if (eventPersisted.getTitle().toLowerCase().contains("parking"))
        {
            return null;
        }
        String title = createTitle(eventPersisted);

        PusherMessage pusherMessage = new PusherMessage(id, title);
        VenueData venue = getVenue(eventPersisted);
        if (venue != null && venue.getCountryCode() != null && venue.getCountryCode() == CountryCode.ru)
        {
            logger.error("event " + eventPersisted.getTitle() + " is in Russia https://dev.promogogo.com/go/promogogo/createmoment.do?event=" + eventPersisted.getEventID() );
            return null;
        }
        if (venue != null && venue.hasGeoLocation() && venue.getLocationPoint().isValid())
        {
            pusherMessage.setLocation(venue.getLocation());
            pusherMessage.add("venueID", venue.getLocationID());
            pusherMessage.add("venueName",venue.getName());
            pusherMessage.add("venueLocation", venue.getLocation().getLocationPoint());

        }
        else
        {
            logger.error("event " + eventPersisted.getTitle() + " has no location https://dashboard.promogogo.com/go/promogogo/createmoment.do?event=" + eventPersisted.getEventID() );
            venue = venueWorker.getVenue(eventPersisted.getVenueID());
            if (venue != null)
            {
                cacheAdministrator.putInCache("venue." + venue.getLocationID(), venue, CacheConst.TIMEOUT_24H * 7);
                pusherMessage.setLocation(venue.getLocation());
                pusherMessage.add("venueID", venue.getLocationID());
                pusherMessage.add("venueName",venue.getName());
                pusherMessage.add("venueLocation", venue.getLocation().getLocationPoint());

            }
            else
            {
                logger.error("");
                return null;
            }
        }

        pusherMessage.setCreated(DateTime.now());
        pusherMessage.setWhen(eventPersisted.when());
        pusherMessage.add("guid", eventPersisted.getEventID());
        //        pusherMessage.add("destination", article.getPart1());
        pusherMessage.add("testAggregate", "true");
        //        pusherMessage.setOwnerFace(outputFace);
        pusherMessage.add("eventID", eventPersisted.getEventID());
        pusherMessage.add("orderType", "primary");
        pusherMessage.add("title", title);
        pusherMessage.add("pageType", "purchaseCompleted");
        pusherMessage.add("numberOfTickets", 1);
        pusherMessage.add("eventTime", eventPersisted.when());
        pusherMessage.add("site", "pgg");

        Boolean bestImageSet = false;
        if (eventPersisted.getBestImage() != null && !eventPersisted.getBestImage().isEmpty())
        {
            pusherMessage.add("bestImage", eventPersisted.getBestImage());
            bestImageSet = true;
        }
        ArtistData artist = getMainArtist (eventPersisted);
        if (artist != null)
        {
            if (!bestImageSet)
            {
                pusherMessage.add("bestImage", artist.getBestImage());
            }
            pusherMessage.add("artistID", artist.getArtistID());
            pusherMessage.add("artistName", artist.getName());
        }

        return pusherMessage;
    }

    private String createTitle(EventPersisted event)
    {

        String title = event.getTitle();
        if (title.contains("|"))
        {
            title = title.substring(0, title.indexOf("|")).trim();
        }

        if (title.toLowerCase().contains("- vip"))
        {
            title = title.substring(0, title.toLowerCase().indexOf("- vip")).trim();
        }
        if (title.toLowerCase().contains("- gold vip"))
        {
            title = title.substring(0, title.toLowerCase().indexOf("- gold")).trim();
        }
        if (title.toLowerCase().contains("2023"))
        {
            title = title.substring(0, title.toLowerCase().indexOf("2023")).trim();
        }
        if (title.toLowerCase().contains("2022"))
        {
            title = title.substring(0, title.toLowerCase().indexOf("2022")).trim();
        }
        if (title.toLowerCase().contains("hotel"))
        {
            title = title.substring(0, title.toLowerCase().indexOf("hotel")).trim();
        }
        List<String> artistIDs = new ArrayList<>(10);
        artistIDs.addAll(event.getArtistIDs());
        artistIDs.add(0, event.getMainArtistID());

        if (event.getTitle().toLowerCase().contains("festival") || event.getArtistIDs().size() > 5)
        {
            StringBuilder builder = new StringBuilder();
            for (String artistID : artistIDs)
            {
                if (iceArtists.contains(artistID))
                {
                    ArtistData artist = getArtist(artistID);
                    if (title.toLowerCase().contains(artist.getName().toLowerCase()))
                    {
                        continue;
                    }
                    if (builder.length() > 0)
                    {
                        builder.append(", ");
                    }
                    builder.append(artist.getName());
                }
            }
            if (builder.length() > 0)
            {
                if (!title.contains(builder))
                {
                    title += " feat " + builder  ;
                }
            }
        }
        else
        {
            artistIDs = new ArrayList<>(10);
            artistIDs.addAll(event.getArtistIDs());
            StringBuilder builder = new StringBuilder();
            for (String artistID : artistIDs)
            {
                if (iceArtists.contains(artistID))
                {
                    if (event.getMainArtistID() != null && event.getMainArtistID().equals(artistID))
                    {
                        continue;
                    }
                    ArtistData artist = getArtist(artistID);
                    if (title.toLowerCase().contains(artist.getName().toLowerCase()))
                    {
                        continue;
                    }
                    if (builder.length() > 0)
                    {
                        builder.append(", ");
                    }
                    builder.append(artist.getName());
                }
            }
            if (builder.length() > 0)
            {
                title += " supp " + builder  ;
            }

        }
        return title;
    }

    private ArtistData getArtist(String artistID)
    {
        if (artistID == null)
        {
            return null;
        }
        try
        {
            return (ArtistData) cacheAdministrator.getFromCache("artist." + artistID);
        }
        catch (Exception e)
        {
        }

        ArtistData artist = artistWorker.getArtist(artistID);
        if (artistID != null)
        {
            cacheAdministrator.putInCache("artist." + artistID, artist, CacheConst.TIMEOUT_24H * 7);
        }
        return artist;
    }

    private EventPersisted getEventPersisted(String id)
    {
        try
        {
            return (EventPersisted) cacheAdministrator.getFromCache("eventpersisted." + id);
        }
        catch (ExpiredException e)
        {
     }
        EventPersisted event = EventPersisted.find(mapper, id);
        if (event == null)
        {
            return null;
        }
        cacheAdministrator.putInCache("eventpersisted." + id, event, CacheConst.TIMEOUT_24H);
        return event;
    }

    private ArtistData getMainArtist(EventPersisted event)
    {
        String artistID = event.getMainArtistID();

        if (artistID == null || artistID.isEmpty())
        {
            for (String anArtist : event.getArtistIDs())
            {
                if (anArtist != null && !anArtist.isEmpty())
                {
                    artistID = anArtist;
                    break;
                }
            }
        }

        return getArtist(artistID);
    }

    private VenueData getVenue(EventPersisted event)
    {
        if (event.getVenueID() == null)
        {
            return null;
        }
        try
        {
            return (VenueData) cacheAdministrator.getFromCache("venue." + event.getVenueID());
        }
        catch (Exception e)
        {
        }

        VenueData venue = venueWorker.getVenue(event.getVenueID());
        if (venue != null)
        {
            cacheAdministrator.putInCache("venue." + event.getVenueID(), venue, CacheConst.TIMEOUT_24H );
        }
        return venue;

    }
    private EventPersisted getEventPersistedNoCache(String id)
    {

        EventPersisted event = EventPersisted.find(mapper, id);
        if (event == null)
        {
            return null;
        }
        cacheAdministrator.putInCache("eventpersisted." + id, event, CacheConst.TIMEOUT_24H);
        return event;
    }
    private List<ArtistData> getArtistPersistedNoCache(EventPersisted event)
    {

        List<ArtistData> artistList = new ArrayList<>(10);
        List<String> artistIDs = event.getArtistIDs();
        for (String artistID : artistIDs)
        {
            if (artistID == null)
            {
                continue;
            }
            try
            {
                ArtistData artist = artistWorker.getArtist(artistID);
                if (artist != null)
                {
                    cacheAdministrator.putInCache("artist." + artist.getArtistID(), artist, CacheConst.TIMEOUT_24H );
                }
                artistList.add(artist);
            }
            catch (Exception e)
            {
            }
        }
        return artistList;
    }


    private VenueData getVenueNoCache(EventPersisted event)
    {
        if (event.getVenueID() == null)
        {
            return null;
        }


        VenueData venue = venueWorker.getVenue(event.getVenueID());
        if (venue != null)
        {
            cacheAdministrator.putInCache("venue." + event.getVenueID(), venue, CacheConst.TIMEOUT_24H );
        }
        return venue;

    }





    private void napUntil(DateTime until)
    {
        if (until.isAfterNow())
        {
            Duration dur = new Duration(DateTime.now(), until);
            try
            {
//                logger.info("sleepinxþfor " + dur);
                Thread.sleep(dur.getMillis());
            }
            catch (InterruptedException e)
            {
            }
        }
        else
        {
//            logger.info (until + " is before " + DateTime.now());
        }
    }



}
