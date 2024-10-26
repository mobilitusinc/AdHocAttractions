package com.mobilitus.attractionscmd.musiclive.icemusic;

/**
 * @author helgaw
 * @todo add class description.
 * @since 11/26/22 13:10
 */
public class IceArtistFromDice
{
//    private static final Logger logger = Logger.getLogger(ImportIceMusic.class);
//
//    private DynamoDBMapper mapper;
//    private S3 s3;
//    private SearchConfig searchConfig;
//    private EventSearch eventSearch;
//    private ArtistWorker artistWorker;
//    private VenueWorker venueWorker;
//    private EventWorker eventWorker;
//    private AttractionSearch gogoSearch;
//    private EDPScraper scraper = new EDPScraper();
//
//    private String instance = "promogogo";
//    private String faceUrl = "ymsirvidburdir";
//    private String faceguid = "e30991cf7022af62ea33e19b1141fe53";
//    private String userLogin = "helgaw@promogogo.com";
//
//    private int diceCount = 0;
//    private int count = 0;
//    private int totalUpcoming = 0;
//    private ElastiCacheAdministrator cacheAdministrator;
//
//
//    public IceArtistFromDice()
//    {
//        AWSCredentialsProvider credentials  = new ProfileCredentialsProvider("prod");
//        mapper = AWSUtils.getMapper(credentials);
//
//
//        AmazonS3Client s3Client = new AmazonS3Client(credentials);
//
//        Cache.create(credentials.getCredentials(), "localhost:11211");
//
//        cacheAdministrator = Cache.getCache();
//
//        searchConfig = new DefaultSearchConfig(credentials.getCredentials());
//
//        eventSearch = new EventSearch(searchConfig.getCredentials(), searchConfig.getEventSearchURL());
//        gogoSearch = new AttractionSearch(searchConfig.getCredentials(), searchConfig.getArtistSearchURL());
//
//
//        s3 = new S3(s3Client, "");
//        ID id = new ID();
//        artistWorker = new ArtistWorker(id, new FaceData(), mapper, s3, searchConfig);
//        venueWorker = new VenueWorker(id, new FaceData(), mapper, s3, searchConfig);
//        eventWorker = new EventWorker(id, new FaceData(), mapper, s3, searchConfig);
//    }
//
//    public void getDiceImports()
//    {
//
//        List <ArtistData> iceArtists = getExportReadyIceArtists();
//        logger.info("iceArtists are " + iceArtists.size());
//        int count = 0;
//        diceCount = 0;
//        totalUpcoming = 0;
//        for(char alphabet = 'a'; alphabet <='z'; alphabet++ )
//        {
//            List<DiceArtist> diceArtists = getFromDice(alphabet + "");
//            logger.info(diceArtists.size() + " artists from " + alphabet + " total " + count);
//            addDiceToIceArtists(iceArtists, diceArtists);
//            count += diceArtists.size();
//
//        }
//        List<DiceArtist> diceArtists = getFromDice("other");
//        addDiceToIceArtists(iceArtists, diceArtists);
//        logger.info("total " + count + " diceArtist, " + iceArtists.size() + " icelandic export artist, imported " +
//                totalUpcoming + " for " + count + " ice / dice artists");
//    }
//
//    private void addDiceToIceArtists(List<ArtistData> iceArtists, List<DiceArtist> diceArtists)
//    {
//        for (DiceArtist dice : diceArtists)
//        {
//            diceCount++;
//            for (ArtistData artist : iceArtists)
//            {
//                if (artist.nameMatches(dice.getName()))
//                {
//                    ArtistPersisted dicePromoArtist = artistWorker.getArtistByAlternativeID(DataSource.dice, dice.getSlug());
//                    if (dicePromoArtist != null && !dicePromoArtist.getId().equals(artist.getArtistID()))
//                    {
//                        logger.info("artist " + dicePromoArtist + " " + dice.getSlug() + " " + dicePromoArtist.getHome() + " found but does not match " + artist.getName());
//                        continue;
//                    }
//                    count++;
//                    logger.info(count + "/" + diceCount +  " Found " + artist.getName() + " as " + dice.getName() + " "
//                            + dice.getUrl() + " https://dev.promogogo.com/go/adminartists.do#!/artist/" + artist.getArtistID());
//
//                    artistWorker.addSource(artist.getArtistID(), DataSource.dice, dice.getSlug(),"dice", dice.getUrl());
//                    CountryCode code = null;
//                    if (artist.getHome() != null)
//                    {
//                        code =  artist.getHome().getCountryCode();
//                    }
//                    if  (code == CountryCode.unknown)
//                    {
//                        code = null;
//                    }
//                    int events = scraper.importFromPage(dice.getUrl(), instance, userLogin, faceUrl, faceguid, code);
//                    logger.info("     found " + events + " upcoming events");
//                    totalUpcoming += events;
//                }
//            }
//        }
//    }
//
//    private List getFromDice (String letter)
//    {
//        DiceApi diceApi = new DiceApi();
//        return diceApi.getArtistList(letter);
//    }
//
//    private List<ArtistData> getExportReadyIceArtists()
//    {
//        List<ArtistData> result = new ArrayList<>(1000);
//        SearchAttractionFilter filter = new SearchAttractionFilter();
//
//        filter.searchArtists(true);
//        filter.addAttribute("export-is");
//        filter.addLocationCountry("is");
//        filter.setAttractionType(AttractionType.music);
//        filter.setSort(EventSort.name, SortOrder.asc);
//        String offset = null;
//        Integer limit = 100;
//        Boolean done = false;
//
//        int i = 0;
//        while (!done)
//        {
//            Page<GogoSearchData> artists = gogoSearch.findByName("", filter, offset, limit);
//            for (GogoSearchData searchArtist : artists)
//            {
//                ArtistData artist = artistWorker.getArtist(searchArtist.getId());
//                i++;
////                logger.info(i + "/" + artists.getTotalSize() + " "  + artist.getName() + " " +artist.getGogoGenre() + " " + artist.getTier() + " " + artist.getUpcoming() + "/" + artist.getAll() + " https://dev.promogogo.com/go/adminartists.do#!/artist/" +artist.getArtistID());
//                result.add(artist);
//            }
//            offset = artists.getNextOffset();
//            if (offset == null)
//            {
//                done = true;
//            }
//        }
//        return result;
//    }
//
//
//    public void scrapeIS()
//    {
//        int i = 0;
//        int withDice = 0;
//        int totalUpcoming = 0;
//        int newUpcoming = 0;
//        Boolean all = false;
//        List<String> artistIDs = getIcelandicMusicArtists();
//        for (String artistID : artistIDs)
//        {
//            i++;
//
//            if (i < 1000)
//                continue;
//            ArtistData artist = getArtist(artistID);
//            if (artist == null)
//            {
//                continue;
//            }
//
//            if (artist.getSourceKey(DataSource.dice) != null && !artist.getSourceKey(DataSource.dice).isEmpty())
//            {
//                withDice++;
//                System.out.println(withDice + "/" + i + "/" + artistIDs.size() + " " + artist.getName() + " with dice "  + artist.getSourceKey(DataSource.dice) + " " + + artist.getUpcoming() + "/" + artist.getAll());
//
//                for (SourcedAttributeData source : artist.getSources())
//                {
//                    if (source.getSource() != DataSource.dice)
//                    {
//                        continue;
//                    }
//
//                    int count = importEventsForArtist(artist, source.getUrl());
//                    totalUpcoming += count;
//                    System.out.println( "\t\t\tFound " + count + "  upcoming for " + artist.getName() + " total now is " + totalUpcoming);
//                }
//            }
//            else
//            {
//                System.out.println( "  /" + i + "/" + artistIDs.size() + "  " + artist.getName() + " does not have a dice id " + artist.getUpcoming() + "/" + artist.getAll());
//            }
//        }
//        System.out.println( "\t\tFound " + totalUpcoming + " (" + newUpcoming + " new) for "  + withDice + " artists");
//    }
//
//
//    private int importEventsForArtist(ArtistData artist, String url)
//    {
//
//        logger.info(count + "/" + diceCount +  " Found " + artist.getName() +" as dice " + url);
//
//        if (url.contains("api"))
//        {
//            List events = getEventsFromArtistProfile(url);
//            if (events != null)
//            {
//                return events.size();
//            }
//            else
//            {
//                return 0;
//            }
//        }
//        else
//        {
//            CountryCode code = artist.getCountryCode();
//            int events = scraper.importFromPage(url, instance, userLogin, faceUrl, faceguid, code);
//            logger.info("     found " + events + " upcoming events");
//            return events;
//        }
//    }
//
//    private List<DiceProfileEvent> getEventsFromArtistProfile(String url)
//    {
//        DiceApi api = new DiceApi();
//        DiceArtistProfile artistProfile = api.getArtistProfile(url);
//
//        if (artistProfile == null) {
//            return Collections.emptyList();
//        }
//        return artistProfile.getEvents();
//    }
//
//
//    private ArtistData getArtist(String artistID)
//    {
//        if (artistID == null)
//        {
//            return null;
//        }
//        try
//        {
//            return (ArtistData) cacheAdministrator.getFromCache("artist." + artistID);
//        }
//        catch (Exception e)
//        {
//        }
//
//        ArtistData artist = artistWorker.getArtist(artistID);
//        if (artistID != null)
//        {
//            cacheAdministrator.putInCache("artist." + artistID, artist, CacheConst.TIMEOUT_24H * 7);
//        }
//        return artist;
//    }
//    private List<String> getIcelandicMusicArtists()
//    {
//        SearchAttractionFilter filter = new SearchAttractionFilter();
//        filter.addLocationCountry("IS");
//        filter.searchArtists(true);
//        filter.setAttractionType(AttractionType.music);
//        filter.setOrder(EventSort.name, SortOrder.asc);
//        String offset = null;
//        Integer limit = 100;
//        Boolean done = false;
//        int i = 0;
//        List<String> artistID = new ArrayList<>(1000);
//        while (!done)
//        {
//            Page<GogoSearchData> artists = gogoSearch.findByName("", filter, offset, limit);
//            for (GogoSearchData artist : artists)
//            {
////                System.out.println(i + "/" + artists.getTotalSize() +"\t" +  artist.getDisplayName());
//                i++;
//                artistID.add(artist.getId());
//            }
//            offset = artists.getNextOffset();
//            if (offset == null)
//            {
//                done = true;
//            }
//        }
//
//        return artistID;
//    }
//
//
//
//

}
