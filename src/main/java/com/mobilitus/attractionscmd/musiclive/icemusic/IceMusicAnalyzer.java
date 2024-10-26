package com.mobilitus.attractionscmd.musiclive.icemusic;

import com.mobilitus.gogo.attractions.ArtistWorker;
import com.mobilitus.gogo.attractions.EventWorker;
import com.mobilitus.gogo.attractions.VenueWorker;
import com.mobilitus.gogo.search.AttractionSearch;
import com.mobilitus.gogo.search.EventSearch;
import com.mobilitus.gogo.Cache;
import com.mobilitus.util.cache.MemcachedAdministrator;
import com.mobilitus.util.data.attractions.ArtistData;
import com.mobilitus.util.data.attractions.ArtistType;
import com.mobilitus.util.data.attractions.AttractionType;
import com.mobilitus.util.data.attractions.DataSource;
import com.mobilitus.util.data.attractions.MajorVenueCategory;
import com.mobilitus.util.data.attractions.PerformerType;
import com.mobilitus.util.data.attractions.VenueData;
import com.mobilitus.util.data.attractions.textHandler.TagCounter;
import com.mobilitus.util.data.attributes.AttributeData;
import com.mobilitus.util.data.aws.cloudsearch.GogoEventSearchData;
import com.mobilitus.util.data.aws.cloudsearch.GogoSearchData;
import com.mobilitus.util.data.gogo.EventSort;
import com.mobilitus.util.data.gogo.Festivals;
import com.mobilitus.util.data.gogo.PromoGhettoData;
import com.mobilitus.util.data.gogo.SearchAttractionFilter;
import com.mobilitus.util.data.gogo.SearchFilter;
import com.mobilitus.util.data.tabula.reportdata.SortOrder;
import com.mobilitus.util.distributed.aws.cloudsearch.DefaultSearchConfig;
import com.mobilitus.util.distributed.aws.cloudsearch.SearchConfig;
import com.mobilitus.util.distributed.dynamodb.AWSUtils;
import com.mobilitus.util.hexia.ID;
import com.mobilitus.util.hexia.Pair;
import com.mobilitus.util.hexia.StrUtil;
import com.mobilitus.util.hexia.location.CountryCode;
import com.mobilitus.util.hexia.location.LocationUtil;
import com.mobilitus.util.hexia.pagination.Page;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedAsyncClient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * @author helgaw
 * @todo add class description.
 * @since 11/25/21 10:40
 */
public class IceMusicAnalyzer
{
    private static final Logger logger = Logger.getLogger(IceMusicAnalyzer.class);
    private final MemcachedAdministrator cacheAdministrator;

    private DynamoDbEnhancedAsyncClient mapper;

    private EventSearch eventSearch;
    private ArtistWorker artistWorker;
    private VenueWorker venueWorker;
    private EventWorker eventWorker;
    private AttractionSearch gogoSearch;

    private Map<String, String>events = new HashMap<>(2000);

    List<VenueData> missingLocations = new ArrayList<>(10);
    List<VenueData> missingCapacity = new ArrayList<>(10);

    public IceMusicAnalyzer()
    {
        AwsCredentialsProvider credentials  = AWSUtils.getCredentialsProvider();

        Cache.create(credentials.resolveCredentials(), "localhost:11211");

        cacheAdministrator = Cache.getCache();

        mapper = AWSUtils.getMapper(credentials);

        SearchConfig searchConfig = new DefaultSearchConfig(credentials);

        eventSearch = new EventSearch(searchConfig.getCredentialsProvider(), searchConfig.getEventSearchURL());

        gogoSearch = new AttractionSearch(searchConfig.getCredentialsProvider(), searchConfig.getArtistSearchURL());

        eventWorker = new EventWorker( mapper, AWSUtils.getS3(), searchConfig);
        artistWorker = new ArtistWorker(null, null, mapper, AWSUtils.getS3(), searchConfig);
        venueWorker = new VenueWorker(null, null, mapper, AWSUtils.getS3(), searchConfig);


    }

//    public void createTotalReport2022()
//    {
//        createTotalReport(2022, "IS");
//    }

//    public void mergeDuplicates()
//    {
//        SearchFilter filter = new SearchFilter();
//
//        filter.addNegativeLocation(null, null, "IS");
//        filter.addAttribute("artistcountry_" + "IS");
//
//        filter.setSort(EventSort.date, SortOrder.asc);
//        filter.setFrom(new DateTime(2023, 01, 01, 0, 0, 0, DateTimeZone.UTC));
//        filter.setUntil(new DateTime(2024, 01, 01, 0, 0, 0, DateTimeZone.UTC));
//        filter.setSort(EventSort.date, SortOrder.asc);
//        Boolean done = false;
//        String offset = null;
//        int i = 0;
//        int merged = 0;
//        Map<Pair<DateTime, String>, PromoGhettoData> map = new HashMap<>(1000);
//        while (!done)
//        {
//            Page<GogoEventSearchData> result = eventSearch.findEvents("", filter, offset, 50);
//            logger.info("");
//            for (GogoEventSearchData event : result)
//            {
//                PromoGhettoData pgg = eventWorker.getEvent(event.getId());
//                if (pgg == null || pgg.isParking() || pgg.isPrivate() || pgg.getTitle().toLowerCase().contains("parking passes"))
//                {
//                    i++;
//                    continue;
//                }
//                logger.info( i + "/" + result.getTotalSize() + " " + pgg.getWhen() + "\t" + pgg.getTitle() +  " https://dashboard.promogogo.com/go/promogogo/createmoment.do?event=" + pgg.getEventID());
//                i++;
//                DateTime theDate = pgg.getWhen().withTimeAtStartOfDay();
//                for (ArtistData artist : pgg.getArtists())
//                {
//                    Pair<DateTime, String> key = new Pair<>(theDate, artist.getArtistID());
//                    if (map.containsKey(key))
//                    {
//                        PromoGhettoData other = map.get(key);
//                        if (other.getEventID().equals(pgg.getEventID()))
//                        {
//                            continue;
//                        }
//                        logger.info("Duplicate " + pgg.getWhen() + "\t" + pgg.getTitle() + "\t" + other.getWhen() + " " + other.getTitle());
//                        if (other.getShowTime().getHourOfDay() == 00)
//                        {
//                            merged++;
//                            eventWorker.merge(pgg.getEventID(), other.getEventID());
//                            map.put(key, pgg);
//                        }
//                        else if (new Duration(other.getShowTime(), pgg.getShowTime()).isShorterThan(Duration.standardHours(2)))
//                        {
//                            merged++;
//                            eventWorker.merge(other.getEventID(), pgg.getEventID());
//
//                        }
//                    }
//                    else
//                    {
//                        map.put(key, pgg);
//                    }
//                }
//            }
//
//            offset = result.getNextOffset();
//            if (offset == null)
//            {
//                done = true;
//            }
//        }
//        logger.info("Merged " + merged);
//    }
//
//    public void createTotalReport2023()
//    {
//        createTotalReport(2023, "IS");
//    }

//    public void createTotalReport(Integer year, String country)
//    {
////        List<String> iceArtists = getInternationalMusicArtists( country);
//        SearchFilter filter = new SearchFilter();
//
//        filter.addNegativeLocation(null, null, country);
//        filter.addAttribute("artistcountry_" + country);
//
//        filter.setSort(EventSort.date, SortOrder.asc);
//        filter.setFrom(new DateTime(year, 01, 01, 0, 0, 0, DateTimeZone.UTC));
//        filter.setUntil(new DateTime(year + 1, 01, 01, 0, 0, 0, DateTimeZone.UTC));
//        filter.setSort(EventSort.date, SortOrder.asc);
//
//        Boolean done = false;
//        String offset = null;
//        int i = 0;
//        StringBuilder buf = new StringBuilder(30000);
//        ExcelWriter excel = new ExcelWriter();
//        excel.init(year, country);
//        while (!done)
//        {
//            Page<GogoEventSearchData> result = eventSearch.findEvents( "", filter, offset, 50);
//             logger.info("");
//            for (GogoEventSearchData event : result)
//            {
//                PromoGhettoData pgg = eventWorker.getEvent(event.getId());
//                if (pgg == null || pgg.isParking() || pgg.isPrivate() || pgg.getTitle().toLowerCase().contains("parking passes"))
//                {
//                    continue;
//                }
//                if (pgg.getVenue() == null)
//                {
//                    pgg = eventWorker.getEventImpl(event.getId());
//                }
//                if (pgg.getVenue() != null && pgg.getVenue().getName().toLowerCase().contains("live stream"))
//                {
//                    continue;
//                }
//                if (pgg.getMajorType() == null || (pgg.getMajorType() != AttractionType.music && pgg.getMajorType() != AttractionType.party))
//                {
//                    continue;
//                }
//                if (pgg.getTitle().toLowerCase().startsWith("international meeting"))
//                {
//                    continue;
//                }
//                if (pgg.getTitle().toLowerCase().startsWith("introduction festival"))
//                {
//                    continue;
//                }
//                if (pgg.getTitle().toLowerCase().startsWith("final semester"))
//                {
//                    continue;
//                }
//
//                Boolean isFestival = pgg.getFestival();
//                if (isFestival == null)
//                {
//                    isFestival = false;
//                }
//                Boolean first = true;
//                if (pgg.getArtists().isEmpty())
//                {
//                    System.out.println( "\t" + pgg.getWhen() +"\t" + pgg.getTitle() + "\t has no artists \thttps://dashboard.promogogo.com/go/promogogo/createmoment.do?event=" + pgg.getEventID());
//                    continue;
//                }
//                excel.addConcert(pgg);
//
//                logger.info(i + "/" + result.getTotalSize()+  "\t" + pgg.getWhen() + "\t" + pgg.getTitle() + "\t" + pgg.getMajorType() +" " +  pgg.getPurchaseLink());
//                i++;
//
//                for (ArtistData artist : pgg.getArtists())
//                {
//                    if (artist.getHome() != null && artist.getHome().getCountryCode() == CountryCode.is)
//                    {
//                        String headliner = "";
//                        if (first)
//                        {
//                            headliner = "Headliner";
//                        }
//                        String festival = "";
//                        if (isFestival)
//                        {
//                            headliner = "Festival";
//                        }
//                        if (headliner.isEmpty())
//                        {
//                            headliner = "Support";
//                        }
//                        try
//                        {
//                            System.out.println(i + "/" + result.getTotalSize()+ "\t" + pgg.getWhen() +"\t" + pgg.getTitle() + "\t" +
//                                    artist.getName() + "\t" + artist.getGogoGenre() +"\t" + artist.getTier() + "\t" + headliner +
//                                    "\t" +pgg.getVenueName() + "\t" + pgg.getVenue().getCity() +"\t" +(pgg.getVenue().getState() != null ?pgg.getVenue().getState()  : "")  +"\t" + pgg.getVenue().getCountryCode() +
//                                            "\t"  + pgg.getVenue().getCapacity() + "\t" + pgg.getVenue().getTier() +   "\thttps://dashboard.promogogo.com/go/promogogo/createmoment.do?event=" + pgg.getEventID());
//                            String artistlink = createArtistLink(artist);
//                            String venueLink = createVenueLink(pgg.getVenue());
//                            String momentEditLink = createMomentEditLink(pgg);
//                            buf.append( pgg.getWhen().toLocalDateTime() +"\t" + pgg.getTitle() + "\t" + artistlink + "\t" + artist.getGogoGenre() +"\t" + artist.getTier() + "\t" + headliner +
//                                    "\t" +venueLink + "\t" + pgg.getVenue().getCity() +"\t" + (pgg.getVenue().getState() != null ?pgg.getVenue().getState()  : "")  +"\t" + pgg.getVenue().getCountryCode() +
//                                    "\t"  + pgg.getVenue().getCapacity() + "\t" + pgg.getVenue().getTier() +  "\t" + momentEditLink +"\n");
//                        }
//                        catch (Exception e)
//                        {
//                            logger.error(createMomentEditLink(pgg));
//                            logger.error(StrUtil.stack2String(e));
//
//                        }
//                    }
//                    first = false;
//                }
//            }
//            offset = result.getNextOffset();
//            if (offset == null)
//            {
//                done = true;
//            }
//        }
//        excel.save();
//        String all = buf.toString().replaceAll("null", "");
//        System.out.println("\n\n" + all);
//    }
//
    private String createMomentEditLink(PromoGhettoData pgg)
    {
        return "=HYPERLINK(\"https://dashboard.promogogo.com/go/promogogo/createmoment.do?event=" + pgg.getEventID() +"\")";

    }

    private String createArtistLink(ArtistData artist)
    {
        String name = artist.getName();
        // https://gjugg.is/asgeir-tickets/artist/147b85f5-75a6-4f00-b8d8-ba87282c0487
        String gjuggLink = "https://gjugg.is/" +StrUtil.createSEOURL(artist.getName()) + "/artist/" + artist.getArtistID();
        return "=HYPERLINK(\"" + gjuggLink +"\",\"" + name + "\")";
    }


    private String createVenueLink(VenueData venue)
    {
        String name = venue.getName();
        // https://gjugg.is/wild-rivers-tickets/venue/33ac37b0-afb0-455a-979a-d5ccd5faf5a8
        String gjuggLink = "https://gjugg.is/" +StrUtil.createSEOURL(venue.getName()) + "/venue/" + venue.getLocationID();
        return "=HYPERLINK(\"" + gjuggLink +"\",\"" + name + "\")";
    }

//    public void getIceArtistList()
//    {
//        {
//            List<String> result = new ArrayList<>(1000);
//            SearchAttractionFilter filter = new SearchAttractionFilter();
//
//            filter.searchArtists(true);
//            filter.addAttribute("export-is-2022");
//            filter.addLocationCountry("is");
//            filter.setAttractionType(AttractionType.music);
//            filter.setSort(EventSort.name, SortOrder.asc);
//            String offset = null;
//            Integer limit = 100;
//            Boolean done = false;
//
//            String[] artistNames ={"ADHD",
//                    "Ægir Sindri Bjarnason",
//                    "Árný Margrét",
//                    "Árstíðir",
//                    "Ásgeir Trausti",
//                    "Auðn",
//                    "Aulos Flute Ensemble",
//                    "Axel Flóvent",
//                    "Bára Gísladóttir",
//                    "Bassi Miraj",
//                    "Blood Harmony",
//                    "Börn",
//                    "Brek",
//                    "BSÍ",
//                    "Cult of Lilith",
//                    "Daníel Hjálmtýsson",
//                    "Erik Robert Qvick",
//                    "Eydís Evensen",
//                    "Frank Murder",
//                    "Guðmundur Steinn Gunnarsson",
//                    "GusGus",
//                    "Gróa",
//                    "Hatari",
//                    "Helgi Rafn Ingvarsson",
//                    "Ingi Bjarni Skúlason",
//                    "Kælan Mikla",
//                    "Kaktus Einarsson",
//                    "Mannveira",
//                    "Mikael Máni",
//                    "Múr",
//                    "Nyrst",
//                    "Ólafur Arnalds",
//                    "Ólöf Arnalds",
//                    "Pale Moon",
//                    "Ragnar Ólafsson",
//                    "S.L.Á.T.U.R.",
//                    "Sandrayati Fay",
//                    "Sigmar Þór Matthíasson",
//                    "Sigurður Flosason",
//                    "SKÁLMÖLD",
//                    "SKRZ sf. (Salóme Katrín Magnúsdóttir, Rakel Sigurðadóttir, Sara Flindt)",
//                    "Snorri Sigfús Birgisson",
//                    "Sóley",
//                    "Sunna Gunnlaugs",
//                    "Supersport!",
//                    "Svavar Knútur",
//                    "Sycamore Tree",
//                    "The Vintage Caravan",
//                    "Ulfur Eldjárn",
//                    "Ultraflex",
//                    "Umbra",
//                    "Volcanova",
//                    "Vök",
//                    "Þuríður Jónsdóttir",
//            };
//
//            int i = 0;
//            while (!done)
//            {
//                Page<GogoSearchData> artists = gogoSearch.findByName("", filter, offset, limit);
//                for (GogoSearchData searchArtist : artists)
//                {
//                    ArtistData artist = artistWorker.getArtist(searchArtist.getId());
//                    if (artist == null)
//                    {
//                        continue;
//                    }
//                    Integer gigsAbroad = countGigsAbroad(artist.getArtistID());
//                    if (gigsAbroad != null && gigsAbroad > 0)
//                    {
//                         Boolean funded = false;
//                        if (inList (artist, artistNames))
//                        {
//                            funded = true;
//                        }
//                        System.out.println(  artist.getName() + "\t" + (artist.getGogoGenre() != null ? artist.getGogoGenre().getGenre() : "") +  "\t" + gigsAbroad
//                                +"\t" + (funded ? "já" : "nei"));
//                    }
//                    result.add(searchArtist.getId());
//                    i++;
////                result.add(artist);
//                }
//                offset = artists.getNextOffset();
//                if (offset == null)
//                {
//                    done = true;
//                }
//            }
//
//          System.out.println("Total number of events " + events.size());
//        }
//    }
//
    private boolean inList(ArtistData artist, String[] artistNames)
    {
        for (String name : artistNames)
        {
            if (artist.nameMatches(name))
            {
                return true;
            }
        }
        return false;
    }

    private Integer countGigsAbroad(String artistID)
    {
        SearchFilter filter = new SearchFilter();
        filter.addNegativeLocation(null, null, "IS");
        filter.setFrom(new DateTime(2022, 1, 1, 0, 0, 0, 0,  DateTimeZone.UTC));
        filter.setUntil(new DateTime(2023, 1, 1, 0, 0, 0, 0,  DateTimeZone.UTC));
        filter.setMajorType(AttractionType.music);
        filter.addArtistID(artistID);;
        String offset = null;
        Integer limit = 100;
        Boolean done = false;
        int count = 0;
        DateTime lastDate = null;
        while (!done)
        {
            Page<GogoEventSearchData> events = eventSearch.findEvents("", filter, offset, 100);
            for (GogoEventSearchData event : events)
            {
                if (event.isPrivate())
                {
                    continue;
                }
                if (event.getDisplayName().toLowerCase().contains("parking"))
                {
                    continue;
                }
                if (lastDate == null)
                {
                    lastDate = event.getWhen().withTimeAtStartOfDay();
                    this.events.put(event.getId(), event.getId());
                    count++;
                }
                else if (lastDate.equals(event.getWhen().withTimeAtStartOfDay()))
                {
                    continue;
                }
                else
                {
                    lastDate = event.getWhen().withTimeAtStartOfDay();
                    this.events.put(event.getId(), event.getId());

                    count++;
                }
            }
            offset = events.getNextOffset();;
            if (offset == null)
            {
                done = true;
            }
        }
        return count;
    }

    private List<GogoEventSearchData> getGigsAbroad(String artistID)
    {
        SearchFilter filter = new SearchFilter();
        filter.addNegativeLocation(null, null, "IS");
        filter.setFrom(new DateTime(2022, 1, 1, 0, 0, 0, 0,  DateTimeZone.UTC));
        filter.setUntil(new DateTime(2023, 1, 1, 0, 0, 0, 0,  DateTimeZone.UTC));
        filter.setMajorType(AttractionType.music);
        filter.addArtistID(artistID);;
        String offset = null;
        Integer limit = 100;
        Boolean done = false;
        List<GogoEventSearchData> result = new ArrayList<>(100);
        while (!done)
        {
            Page<GogoEventSearchData> events = eventSearch.findEvents("", filter, null, 100);

            result.addAll(events.getList());
        }
        return result;
    }

    public void checkExportReady()
    {
        SearchAttractionFilter filter = new SearchAttractionFilter();

        filter.searchArtists(true);
        filter.addAttribute("export-is");
        filter.addLocationCountry("is");
        filter.setAttractionType(AttractionType.music);
        filter.setSort(EventSort.name, SortOrder.asc);
        String offset = null;
        Integer limit = 100;
        Boolean done = false;



        int i = 0;
        while (!done)
        {
            Page<GogoSearchData> artists = gogoSearch.findByName("", filter, offset, limit);
            for (GogoSearchData searchArtist : artists)
            {
                ArtistData artist = artistWorker.getArtist(searchArtist.getId());
                i++;
                logger.info(i + "/" + artists.getTotalSize() + " "  + artist.getName() + " " +artist.getGogoGenre() + " " + artist.getTier() + " " + artist.getUpcoming() + "/" + artist.getAll() + " https://dashboard.promogogo.com/go/adminartists.do#!/artist/" +artist.getArtistID());
                if (artist.getHome() == null || artist.getHome().getCountryCode() != CountryCode.is)
                {
                    logger.info(  artist.getName() + " is from  " + artist.getHome());
                    if (artist.hasAttribute("export-is"))
                    {
                        Boolean deleteAttributes = true;
                        logger.info("");

                        if (deleteAttributes)
                        {
                            for (AttributeData attrib : artist.getAttributes())
                            {
                                if (attrib.getKey().startsWith("export-is"))
                                {
                                    artistWorker.removeAttribute(artist.getArtistID(), attrib.getKey(), attrib.getValue());
                                }
                            }
                        }
                        else
                        {
                            for (AttributeData attrib : artist.getAttributes())
                            {
                                if (attrib.getKey().equalsIgnoreCase("export-is"))
                                {
                                    artistWorker.removeAttributes(artist.getArtistID(), attrib.getKey());
                                }
                            }
                        }
                        artistWorker.updateSearchForArtist(new ID(), artist.getArtistID());
                    }
                }
            }
            offset = artists.getNextOffset();
            if (offset == null)
            {
                done = true;
            }
        }

    }

    public Page<ArtistData> findArtistByTag(String tag, String offset, Integer limit)
    {
        SearchAttractionFilter filter = new SearchAttractionFilter();
        filter.searchArtists(true);
        filter.setAttractionType(AttractionType.music);
        filter.setOrder(EventSort.name, SortOrder.asc);

        List<ArtistData> artists = new ArrayList<>(100);

        Page<GogoSearchData> result = gogoSearch.findByName(tag, filter, offset, limit);
        int i = 0;
        for (GogoSearchData anArtist : result)
        {
            ArtistData artist = artistWorker.getArtist(anArtist.getId());
            if (artist.getUpcoming() > 0)
            {
                logger.info(i + "/"+ result.getTotalSize() + "\t" + artist.getName() +"\t" + artist.getUpcoming() + "/" + artist.getAll());
                artists.add(artist);
                i++;
            }
        }
        Page p = new Page (artists).withOffsets(offset, result.getNextOffset(), result.getPreviousOffset(), limit);

        return p;
    }





    public void showUpcomingArtists()
    {
        List<String> list;
        Map<String, ArtistData> artists = new HashMap<>(100);

        TagCounter<String> upcoming = new TagCounter<>();


        List<String> iceArtists = getInternationalMusicArtists( "IS");
        SearchFilter filter = new SearchFilter();

        filter.addNegativeLocation(null, null, "IS");
        for (String artistID : iceArtists)
        {
                filter.addArtistID(artistID);
        }
        filter.setSort(EventSort.date, SortOrder.asc);
        filter.upcoming();
        filter.setFrom(new DateTime(2022, 01, 01, 0, 0, 0, DateTimeZone.UTC));

        Boolean done = false;
        String offset = null;
        int i = 0;
        while (!done)
        {
            Page<GogoEventSearchData> result = eventSearch.findEvents( "", filter, offset, 50);

            for (GogoEventSearchData event : result)
            {
                PromoGhettoData pgg = eventWorker.getEvent(event.getId());
                logger.info(i + "/" + result.getTotalSize()+  "\t" + pgg.getWhen() + "\t" + pgg.getTitle() + "\t" + pgg.getPurchaseLink());
                i++;
                for (ArtistData artist : pgg.getArtists())
                {
                    if (artist.getHome() != null && artist.getHome().getCountryCode() == CountryCode.is)
                    {
                        artists.put(artist.getArtistID(), artist);
                        upcoming.add(artist.getArtistID());
                    }
                }
            }
            offset = result.getNextOffset();
            if (offset == null)
            {
                done = true;
            }
        }


        StringBuilder buf = new StringBuilder(1000);
        for (Map.Entry<String, Integer> item : upcoming.getSorted().entrySet())
        {
            ArtistData myArtist = artists.get(item.getKey());
            System.out.println(myArtist.getName() + "\t" + myArtist.getGogoGenre() + "\t" + StringUtils.leftPad(StrUtil.formatNumber(item.getValue()), 5) +
                    "\t" + StringUtils.leftPad(StrUtil.formatNumber(myArtist.getUpcoming()), 5) +
                    "\t" + StringUtils.leftPad(StrUtil.formatNumber(myArtist.getAll()), 5));
            buf.append(myArtist.getName() + "\t" + myArtist.getGogoGenre() + "\t" + StringUtils.leftPad(StrUtil.formatNumber(item.getValue()), 5) +
                    "\t" + StringUtils.leftPad(StrUtil.formatNumber(myArtist.getUpcoming()), 5) +
                    "\t" + StringUtils.leftPad(StrUtil.formatNumber(myArtist.getAll()), 5) + "\n");

        }

        System.out.println("\n\n" + buf);

    }

    public void showUpcomingEvents()
    {
        List<String> list;

        List<String> iceArtists = getInternationalMusicArtists( "IS");
        SearchFilter filter = new SearchFilter();

        filter.addNegativeLocation(null, null, "IS");
        for (String artistID : iceArtists)
        {
            filter.addArtistID(artistID);
        }
        filter.setSort(EventSort.date, SortOrder.asc);
        filter.upcoming();



        Boolean done = false;
        String offset = null;
        int i = 0;
        while (!done)
        {
            Page<GogoEventSearchData> result = eventSearch.findEvents( "", filter, offset, 50);

            for (GogoEventSearchData event : result)
            {
                PromoGhettoData pgg = eventWorker.getEvent(event.getId());
                logger.info(i + "/" + result.getTotalSize()+  "\t" + pgg.getWhen() + "\t" + pgg.getTitle() + "\t" + pgg.getPurchaseLink());
                if (pgg.getPurchaseLink() == null || pgg.getPurchaseLink().isEmpty())
                {
                    logger.info("\t\t\t\t\t\thttps://dashboard.promogogo.com/go/promogogo/event.do?event=" + pgg.getEventID());
                }
                i++;

            }
//            done = true;
            offset = result.getNextOffset();
            if (offset == null)
            {
                done = true;
            }
        }
    }


    public void showUpcomingVenues()
    {
        List<String> list;
        Map<String, VenueData> venues = new HashMap<>(100);

        TagCounter<String> upcoming = new TagCounter<>();


        List<String> iceArtists = getInternationalMusicArtists( "IS");
        SearchFilter filter = new SearchFilter();

        filter.addNegativeLocation(null, null, "IS");
        for (String artistID : iceArtists)
        {
            filter.addArtistID(artistID);
        }
        filter.setSort(EventSort.date, SortOrder.asc);
        filter.upcoming();
        filter.setFrom(new DateTime(2022, 01, 01, 0, 0, 0, DateTimeZone.UTC));



        Boolean done = false;
        String offset = null;
        int i = 0;
        while (!done)
        {
            Page<GogoEventSearchData> result = eventSearch.findEvents( "", filter, offset, 50);

            for (GogoEventSearchData event : result)
            {
                PromoGhettoData pgg = eventWorker.getEvent(event.getId());
                logger.info(i + "/" + result.getTotalSize()+  "\t" + pgg.getWhen() + "\t" + pgg.getTitle() + "\t" + pgg.getPurchaseLink());
                i++;

                if (pgg.getVenue() != null)
                {
                    venues.put(pgg.getVenue().getLocationID(), pgg.getVenue());
                    upcoming.add(pgg.getVenue().getLocationID());
                }
            }
            offset = result.getNextOffset();
            if (offset == null)
            {
                done = true;
            }
        }


        StringBuilder buf = new StringBuilder(1000);
        for (Map.Entry<String, Integer> item : upcoming.getSorted().entrySet())
        {
            VenueData myVenue = venues.get(item.getKey());
            System.out.println(myVenue.getName() + "\t" + myVenue.getPrettyAddress()+ "\t(" + myVenue.getLocationPoint() + ")\t" + StringUtils.leftPad(StrUtil.formatNumber(item.getValue()), 5) +
                    "\t" + StringUtils.leftPad(StrUtil.formatNumber(myVenue.getUpcoming()), 5) +
                    "\t" + StringUtils.leftPad(StrUtil.formatNumber(myVenue.getAll()), 5) +"\t" + myVenue.getCapacity() +"\t" + myVenue.getTier() + "\t" + myVenue.isVerified() +"\t=HYPERLINK(\"https://dashboard.promogogo.com/go/editvenue.do#!/venue/" + myVenue.getLocationID() +"\")");
            buf.append(myVenue.getName() + "\t" + myVenue.getPrettyAddress()+ "\t(" + myVenue.getLocationPoint() + ")\t" + StringUtils.leftPad(StrUtil.formatNumber(item.getValue()), 5) +
                    "\t" + StringUtils.leftPad(StrUtil.formatNumber(myVenue.getUpcoming()), 5) +
                    "\t" + StringUtils.leftPad(StrUtil.formatNumber(myVenue.getAll()), 5) +"\t" + myVenue.getCapacity() +"\t" + myVenue.getTier() +"\t" + myVenue.isVerified() +"\t=HYPERLINK(\"https://dashboard.promogogo.com/go/editvenue.do#!/venue/" + myVenue.getLocationID() +"\")\n");

        }

        System.out.println("\n\n" + buf);
    }

    public void createISReport( )
    {
        createReport("IS", null, null);
    }

    public void createReport(String country)
    {
        createReport(country, null, null);
    }

    public void createReport(String country, DateTime from, DateTime until)
    {
        List<String> artistIDs = getInternationalMusicArtists(country);

        SearchFilter filter = new SearchFilter();

        filter.addNegativeLocation(null, null, country);
        for (String artistID : artistIDs)
        {
            filter.addArtistID(artistID);
        }
        filter.setSort(EventSort.date, SortOrder.asc);
        if (from == null && until == null)
        {
            filter.upcoming();
            filter.ongoing();
        }
        filter.setType(PerformerType.event);
        Integer totalAudience = 0;
        Integer gigsWithAudienceCount = 0;
        Integer gigsWithoutAudienceCount = 0;
        Integer festivals = 0;

         Integer upcoming = 0;
        TagCounter<Integer> tierCount = new TagCounter<>();
        TagCounter<String> genreCount = new TagCounter<>();
        TagCounter<String> countryCount = new TagCounter<>();
        TagCounter<String> artistCount = new TagCounter<>();
        TagCounter<String> venueCount = new TagCounter<>();
        Map<String, ArtistData> artistMap = new HashMap<>(100);
        Map<String, VenueData> venueMap = new HashMap<>(100);
        Map<String, ArtistData> festivalMap = new HashMap<>(100);
        Map<String, List<ArtistData>> festivalArtistMap = new HashMap<>(100);
        Map<String,String> festivalLocation = new HashMap<>(100);
        TagCounter<String> festivalCount = new TagCounter<>();
        List<Pair<DateTime, String>> festivalList = new ArrayList<>(100);

        Integer createdInLastWeek = 0;
        Integer eventsNextWeek = 0;
        List<PromoGhettoData> upcomingNextWeek = new ArrayList<>();
        List<PromoGhettoData> newThisWeek = new ArrayList<>();
        Boolean done = false;
        String offset = null;
        int i = 0;

        while (!done)
        {
            Page<GogoEventSearchData> events = eventSearch.findEvents("", filter, offset, 100);
            if (upcoming == 0)
            {
                upcoming = events.getTotalSize();
            }
            for (GogoEventSearchData event : events)
            {
                i++;


//                if (event.getDisplayName().toLowerCase().contains("parking"))
//                {
//                    eventWorker.remove(event.getId());
//                    continue;
//                }

                PromoGhettoData pgg = eventWorker.getEventFresh(event.getId());
                addArtistTagToEvents(pgg);
                try
                {
                    logger.info(i + "/" + events.getTotalSize()+  "\t" + pgg.getWhen().toString("dd-MM-yy HH:mm ") + "\t'" + pgg.getTitle() + "' at '" + pgg.getVenueName() + "' " +(pgg.getVenue() != null ? pgg.getVenue().getCountryCode() : "") + " created : "+ pgg.getCreated());
                }
                catch (Exception e)
                {
                    logger.error(StrUtil.stack2String(e));

                }

                if (pgg.getShowTime().isAfterNow() && pgg.getShowTime().minusDays(10).isBeforeNow())
                {
                    eventsNextWeek++;
                    upcomingNextWeek.add(pgg);
                }
                if (pgg.getCreated().plusDays(7).isAfterNow() && pgg.getGig().getCreated().plusDays(7).isAfterNow())
                {
                    createdInLastWeek++;
                    newThisWeek.add(pgg);
                }
//                if (pgg.hasArtist("ca0dd6af-e714-46d1-91ba-7412cb9716fd") && pgg.getVenue().getCountryCode() == CountryCode.us)
//                {
//                    createdInLastWeek++;
//                    newThisWeek.add(pgg);
//                }
                if (pgg.isFestival())
                {
                    festivals++;
                    ArtistData artist = getFestival(pgg);
                    if (artist != null)
                    {
                        if (!festivalMap.containsKey(artist.getArtistID()))
                        {
                            festivalList.add(new Pair<>(pgg.getShowTime(), artist.getArtistID()));
                        }

                        festivalMap.put(artist.getArtistID(), artist);
                        festivalCount.add(artist.getArtistID());
                        if (LocationUtil.usesRegion(pgg.getVenue().getCountryCode()))
                        {
                            festivalLocation.put(artist.getArtistID(), pgg.getVenue().getCity() + " " + pgg.getVenue().getState() + " " +  pgg.getVenue().getCountryCode());
                        }
                        else
                        {
                            festivalLocation.put(artist.getArtistID(), pgg.getVenue().getCity() + " " +  pgg.getVenue().getCountryCode());
                        }
                        addArtistToFestivalMap(festivalArtistMap, country, artist.getArtistID(), pgg);

                    }
                }

                if (pgg.getVenue() != null && pgg.getVenue().getTier() != null)
                {
                    VenueData venue = pgg.getVenue();
                    venueMap.put(venue.getLocationID(), venue);
                    venueCount.add(venue.getLocationID());
                    countryCount.add(venue.getCountryCode().name());

                    if (!pgg.isFestival() && pgg.getVenue().getMajorCategory() != null &&
                            pgg.getVenue().getMajorCategory() != MajorVenueCategory.park &&
                            pgg.getVenue().getMajorCategory() != MajorVenueCategory.square)
                    {
                        tierCount.add(venue.getTier());
                        if (pgg.getVenue().getCapacity() != null && pgg.getVenue().getCapacity() > 0)
                        {
                            totalAudience += pgg.getVenue().getCapacity();
                            gigsWithAudienceCount++;
                        }
                    }
                    else
                    {
                        if (!pgg.isFestival() && (venue.getCapacity() == null || venue.getCapacity() == 0))
                        {
                            logger.info("missing capacity for " +pgg.getVenue() + " https://dashboard.promogogo.com/go/editvenue.do#!/venue/" + venue.getLocationID());
                            missingCapacity.add(pgg.getVenue());
                            gigsWithoutAudienceCount++;
                        }
                    }
                    if (venue.getLocationPoint() == null || !venue.getLocationPoint().isValid())
                    {
                        logger.info("missing location for for " +pgg.getVenue() + " https://dashboard.promogogo.com/go/editvenue.do#!/venue/" + venue.getLocationID());
                        missingLocations.add(pgg.getVenue());
                    }

                }
                else
                {
                    VenueData venue = pgg.getVenue();

                    if (venue != null)
                    {
                        venueMap.put(venue.getLocationID(), venue);
                        venueCount.add(venue.getLocationID());
                        countryCount.add(venue.getCountryCode().name());

                        if (!pgg.isFestival() && venue.getMajorCategory() != null && venue.getMajorCategory() != MajorVenueCategory.festivalGrounds && venue.getCapacity() == null)
                        {
                            // don't include in count
                            logger.info("missing capacity for " + pgg.getVenue() + " https://dashboard.promogogo.com/go/editvenue.do#!/venue/" + venue.getLocationID());
                            missingCapacity.add(pgg.getVenue());
                            gigsWithoutAudienceCount++;
                        }
                        else if ( venue.getMajorCategory() == null && venue.getCapacity() == null)
                        {
                            // don't include in count
                            logger.info("missing capacity for " + pgg.getVenue() + " https://dashboard.promogogo.com/go/editvenue.do#!/venue/" + venue.getLocationID());
                            missingCapacity.add(pgg.getVenue());
                            gigsWithoutAudienceCount++;
                        }
                        if (venue.getLocationPoint() == null || !venue.getLocationPoint().isValid())
                        {
                            logger.info("missing location for for " + pgg.getVenue() + " https://dashboard.promogogo.com/go/editvenue.do#!/venue/" + venue.getLocationID());
                            missingLocations.add(pgg.getVenue());
                        }
                    }
                    else
                        logger.info("missing venue for for " + pgg.getTitle()+ " " + pgg.getShowTime());

                }
                if (pgg.getGogoGenre() != null)
                {
                    genreCount.add(pgg.getGogoGenre().getGenre());
                }
                else if (pgg.getMainArtist() != null && pgg.getMainArtist().getGogoGenre() != null)
                {
                    genreCount.add(pgg.getMainArtist().getGogoGenre().getGenre());
                }
                for (ArtistData artist : pgg.getArtists())
                {

                    if (artist != null && artist.getHome() != null && artist.getHome().getCountryCode() == CountryCode.is)
                    {
                        artistMap.put(artist.getArtistID(), artist);
                        artistCount.add(artist.getArtistID());
                    }
                }
            }
            offset = events.getNextOffset();
            if (offset == null)
            {
                done = true;
            }
        }
        System.out.println("Missing locations for " + missingLocations.size() + " venues");
        for (VenueData missingLoc : missingLocations)
        {
            System.out.println(missingLoc.getName() + " " + missingLoc.getPrettyAddress() + " https://dashboard.promogogo.com/go/editvenue.do#!/venue/" + missingLoc.getLocationID());
        }

        System.out.println("Missing capacity for " + missingCapacity.size() + " venues");
        for (VenueData missingCap : missingCapacity)
        {
            System.out.println(missingCap.getName() + " " + missingCap.getPrettyAddress() + " https://dashboard.promogogo.com/go/editvenue.do#!/venue/" + missingCap.getLocationID());
        }
        System.out.println("");
        System.out.println("<H3>Event analysis for " +country.toUpperCase() +"  Music Live</H3>");

        System.out.println("<H3>Summary</H3>");
        System.out.println("Total upcoming events " + upcoming + "  <br/>new this week " + createdInLastWeek +"<br>");

        System.out.println("Total potential audience " + StrUtil.formatNumber(totalAudience)+"<br><br>");
        System.out.println("Festivals " + festivals+"<br>");


        System.out.println("Events in venues with known capacity: " + gigsWithAudienceCount + " - without: " + gigsWithoutAudienceCount+"<br>");

        System.out.println("<H3>Events pr Tier </H3>");

        for (Map.Entry<Integer, Integer> entry : tierCount.getSorted().entrySet())
        {
            System.out.println("Tier " + entry.getKey() + " : " + entry.getValue() +"<br>");
        }


        System.out.println("<H3>Events pr Country</H3>");
        for (Map.Entry<String, Integer> countryGigs : countryCount.getSorted().entrySet())
        {
            System.out.println(createLink ("https://www.icelandmusic.is/live#events/in/" + countryGigs.getKey(), getCountryName(countryGigs.getKey())) + " : " + countryGigs.getValue()+"<br>");
        }


        System.out.println("<H3>Events pr Genre</H3>");

        for (Map.Entry<String, Integer> entry : genreCount.getSorted().entrySet())
        {
            System.out.println(entry.getKey() + " : " + entry.getValue()+"<br>");
        }

//        genreCount.print();

        System.out.println("<H3>Top 10 Artists of a total of " + artistCount.getSorted().size() +"</H3>");

        int count = 0;
        for (Map.Entry<String, Integer> entry : artistCount.getSorted().entrySet())
        {
            System.out.println(createLink("https://devbackstage.promogogo.com/attraction//"+ entry.getKey(),  artistMap.get(entry.getKey()).getName()) + " :  " + entry.getValue()+" events <br>");
            count++;
            if (count > 10)
            {
                break;
            }

        }
//        artistCount.print();
//        System.out.println("<H3>Top 10 Venues of a total of " + venueCount.getSorted().size() +"</H3>");
//        count = 0;
//        for (Map.Entry<String, Integer> entry : venueCount.getSorted().entrySet())
//        {
//            VenueData venueData = venueMap.get(entry.getKey());
//            System.out.println(venueData.getName() +" " + venueData.getPrettyAddress() + " capacity: " + venueData.getCapacity() + " count: " +  entry.getValue()+"<br>");
//            count++;
//            if (count > 10)
//            {
//                break;
//            }
//
//        }

        HashMap<String, Integer> upcomingArtists = getIsArtists(upcomingNextWeek);
        System.out.println("<H3>Upcoming next 10 days</H3>");
        System.out.println("<H3>Artists " + upcomingArtists.size() +"</H3>");
        System.out.println("<H3>Events " + upcomingNextWeek.size() +"</H3>");

        for (Map.Entry<String, Integer>  upcomingArtist : upcomingArtists.entrySet())
        {
            System.out.println( createLink("https://devbackstage.promogogo.com/attraction//" + upcomingArtist.getKey(), artistMap.get(upcomingArtist.getKey()).getName()) + " " + upcomingArtist.getValue()+"<br>");
        }

        System.out.println ("<H4>Events</H4>");
        for (PromoGhettoData pgg : upcomingNextWeek)
        {
            System.out.println(createLink("https://devbackstage.promogogo.com/event/" + pgg.getEventID(), pgg.getWhen().toString("dd-MM-yy HH:mm") +
                    "\t" + pgg.getTitle() + "\t ("+ getIsArtistNames(pgg)) +   ")  @ " + pgg.getVenueName() + ", " +
                    getCountryName(pgg.getVenue().getCountryCode().name())+"<br>");
        }

        HashMap<String, Integer> newArtists = getIsArtists(newThisWeek);

        System.out.println("<H3> " + newThisWeek.size() +" events created past week for " + newArtists.size() +" artists "+"</H3>");
        for (Map.Entry<String, Integer> entry : newArtists.entrySet())
        {
            System.out.println(createLink("https://devbackstage.promogogo.com/attraction//" + entry.getKey(), artistMap.get(entry.getKey()).getName())+ " " + entry.getValue()+"<br>");

        }
        System.out.println ("<H4>New Events</H4>");

        for (PromoGhettoData pgg : newThisWeek)
        {
            if (pgg.getVenue() != null)
            {
                System.out.println(createLink("https://devbackstage.promogogo.com/event/" + pgg.getEventID(), pgg.getWhen().toString("dd-MM-yy HH:mm") +
                        "\t" + pgg.getTitle() + "\t " + country + " artists: "+ getIsArtistNames(pgg)) +   "  at " + pgg.getVenueName() + ", " +
                        getCountryName(pgg.getVenue().getCountryCode().name())+  " created : "+ pgg.getCreated().toString("dd-MM-yy HH:mm")+"<br>");
            }
            else
            {
                System.out.println(createLink("https://devbackstage.promogogo.com/event/" + pgg.getEventID(), pgg.getWhen().toString("dd-MM-yy HH:mm") +
                        "\t" + pgg.getTitle() + "\t " + country  +" artists: "+ getIsArtistNames(pgg)) +   "  at " + pgg.getVenueName() + ",   " +
                          " created : "+ pgg.getCreated().toString("dd-MM-yy HH:mm")+"<br>");
            }
        }

        System.out.println ("<H4>Festivals</H4>");

        for (Pair<DateTime, String> festivalDate : festivalList)
        {
            ArtistData festival =  festivalMap.get(festivalDate.getValue());
            String artistNames = "";
            for (ArtistData artist : festivalArtistMap.get(festivalDate.getValue()))
            {
                if (artistNames.length() > 0)
                {
                if (!artistNames.contains(artist.getName()))
                {
                        artistNames += ", ";
                    }
                }
                    artistNames += artist.getName();
                }

            System.out.println(festivalDate.getKey().toString("dd-MM-yy")  + "\t" + festival.getName() +  ",\t" +
                               festivalLocation.get(festivalDate.getValue()) + "\t\t" + artistNames+"<br>" );
        }

        //        venueCount.print();
    }


    private void addArtistTagToEvents(PromoGhettoData pgg)
    {
        Boolean update = false;
        if (pgg == null)
        {
            return;
        }
        for (ArtistData artist : pgg.getArtists())
        {
            if (artist.getArtistType() == ArtistType.festival)
            {
                continue;
            }
            if (artist.getCountry() != null && !artist.getCountry().equalsIgnoreCase("unknown"))
            {
                eventWorker.addAttribute(pgg.getEventID(), DataSource.promogogo, "artistcountry_" + artist.getCountry(), "true");
                update = true;
            }
        }
        if (update)
        {
            eventWorker.updateSearch(pgg.getEventID());
        }
    }


    private void addArtistToFestivalMap(Map<String, List<ArtistData>> festivalArtistMap, String country, String festivalID, PromoGhettoData pgg)
    {
        List<ArtistData> artists = festivalArtistMap.get(festivalID);
        if (artists == null)
        {
            artists = new ArrayList<>();
        }
        for (ArtistData artist : pgg.getArtists())
        {
            if (artist.getCountry() != null && artist.getCountry().equalsIgnoreCase(country))
            {
                artists.add(artist);
            }
        }
        festivalArtistMap.put(festivalID, artists);
    }

    private ArtistData getFestival(PromoGhettoData pgg)
    {
        for (ArtistData artist : pgg.getArtists())
        {
            if (artist.getArtistType() == ArtistType.festival)
            {
                return artist;
            }
            if (Festivals.isFestival(artist.getName()))
            {
                return artist;
            }


        }
        if (Festivals.isFestival(pgg.getTitle()))
        {
            ArtistData artist = new ArtistData();
            artist.setArtistID(pgg.getTitle());
            artist.setName(pgg.getTitle());
            return artist;
        }
        return null;
    }

    private String createLink(String link, String text)
    {
        return "<a href=\"" + link + "\">" + text + "</a>";
    }

    private String getCountryName(String key)
    {
        Locale l = new Locale("", key);
        return l.getDisplayCountry();
    }

    private String getIsArtistNames(PromoGhettoData pgg)
    {
        String str = "";
        List<ArtistData> artists = getIsArtistsFromEvent(pgg);
        for (ArtistData artist : artists)
        {
            if (!str.isEmpty())
            {
                str = str + ", ";
            }
            str = str + artist.getName();
        }
        return str;
    }

    private HashMap<String, Integer> getIsArtists(List<PromoGhettoData> eventList)
    {
        TagCounter<String> counter = new TagCounter<>();

        for (PromoGhettoData promoGhettoData : eventList)
        {
            List<ArtistData> artists = getIsArtistsFromEvent(promoGhettoData);
            for (ArtistData artist : artists)
            {
                counter.add(artist.getArtistID());
            }

        }
        return counter.getSorted();

    }

    private List<ArtistData> getIsArtistsFromEvent(PromoGhettoData promoGhettoData)
    {
        List<ArtistData> isArtists = new ArrayList<>(10);

        for (ArtistData artist : promoGhettoData.getArtists())
        {
            if (artist.getHome() != null && artist.getHome().getCountryCode() == CountryCode.is)
            {
                isArtists.add(artist);
            }
        }
        return isArtists;
    }


    private List<String> getInternationalMusicArtists(String countryOfOrigin)
    {
        SearchAttractionFilter filter = new SearchAttractionFilter();
        filter.addLocationCountry(countryOfOrigin);
        filter.searchArtists(true);
        filter.setAttractionType(AttractionType.music);
        String offset = null;
        Integer limit = 100;
        Boolean done = false;
        filter.setSort(EventSort.name, SortOrder.asc);
        List<String> artistIDs = new ArrayList<>(1000);
        int i = 0;
        int all = 0;
        while (!done)
        {
            Page<GogoSearchData> artists = gogoSearch.findByName("", filter, offset, limit);
            for (GogoSearchData artist : artists)
            {
                if (isInternationalArtist(countryOfOrigin, artist.getId()))
                {
                    ArtistData artistData = artistWorker.getArtist(artist.getId());
                    logger.info(i + "/" +all + "/" + artists.getTotalSize() + " " + artist.getDisplayName()+  " " + artistData.getUpcoming() + "/" + artistData.getAll() + " is international https://devbackstage.promogogo.com/attraction/" + artist.getId());
                    i++;
                    artistWorker.addAttribute(artist.getId(), "export", "export-" + countryOfOrigin +"-2024");
                    artistWorker.addAttribute(artist.getId(), "export", "export-" + countryOfOrigin);
                    artistIDs.add(artist.getId());
                }
                all++;
            }
            offset = artists.getNextOffset();
            if (offset == null)
            {
                done = true;
            }
        }

        return artistIDs;
    }

    private boolean isInternationalArtist(String country, String artistID)
    {
        SearchFilter filter = new SearchFilter();

        filter.addNegativeLocation(null, null, country);

        filter.addArtistID(artistID);
        filter.setSort(EventSort.date, SortOrder.asc);
        filter.setFrom(DateTime.now().minusYears(1));

        Page<GogoEventSearchData> result = eventSearch.findEvents( "", filter, null, 50);

        if (result.getTotalSize() > 0)
        {
            return true;
        }

        return false;

    }




}
