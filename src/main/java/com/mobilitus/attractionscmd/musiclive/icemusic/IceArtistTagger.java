package com.mobilitus.attractionscmd.musiclive.icemusic;

/**
 * @author helgaw
 * @todo add class description.
 * @since 11/26/22 13:10
 */
public class IceArtistTagger
{
//    private static final Logger logger = Logger.getLogger(IceArtistTagger.class);
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
//    private ArtistCrawler crawler;
//
//    private IcelandicNames names;
//
//    public IceArtistTagger()
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
//        crawler = new ArtistCrawler(mapper);
//
//    }
//
//    public void tagHatidni()
//    {
//        String artists = "AVRAMOVA!\n" +
//                "Berglind Ágústsdóttir\n" +
//                "Bethany Ley\n" +
//                "BKPM\n" +
//                "cavedame\n" +
//                "Dice Therapy\n" +
//                "DJ Melerito de Jeré\n" +
//                "Einakróna\n" +
//                "Flaaryr\n" +
//                "Gaddavír\n" +
//                "Hera Lind\n" +
//                "Iðunn Einars\n" +
//                "Kate\n" +
//                "Kötturinn sem gufaði upp\n" +
//                "Krill Noise\n" +
//                "KUSK & Óviti\n" +
//                "Lil Data\n" +
//                "lúpína\n" +
//                "Masaya Ozaki\n" +
//                "N I T E F I S H\n" +
//                "nothingtowear\n" +
//                "Ókindarhjarta\n" +
//                "Paid in Worms\n" +
//                "PELLEGRINA\n" +
//                "「 ronja &&& enemies 」\n" +
//                "Sara Flindt\n" +
//                "simmi (DJ)\n" +
//                "Sucks to be you, Nigel\n" +
//                "Supersport!\n" +
//                "tildra\n" +
//                "Trailer Todd\n" +
//                "Virgin Orchestra\n" +
//                "Xiupill\n";
//
//        String eventID = "8e9847ce-5c7f-4397-9622-860792b71010";
//
//        PromoGhettoData event = eventWorker.getEvent(eventID);
//        String[] artistNames = artists.split("\n");
//        for (String artistName : artistNames)
//        {
//            ArtistData artist = findBestArtist(artistName);
//            if (artist == null)
//            {
//                System.out.println("creating " +artistName);
//                ArtistData newArtist = new ArtistData();
//                newArtist.setName(artistName);
//                newArtist.setMajorType(AttractionType.music);
//                newArtist.setArtistType(ArtistType.band);
//                newArtist.setCountry("IS");
//                artist = artistWorker.create(DataSource.promogogo, newArtist);
//            }
//            System.out.println("adding " +artistName);
//            eventWorker.addArtistToEvent(artist.getArtistID(), eventID);
//        }
//        eventWorker.flushSearch();
//        gogoSearch.flush();
//    }
//
//    private ArtistData findBestArtist(String artistName)
//    {
//        SearchAttractionFilter filter = new SearchAttractionFilter();
//        filter.searchArtists(true);
//        filter.setAttractionType(AttractionType.music);
//        Page<GogoSearchData> artists = gogoSearch.findByName(artistName, filter, null, 10);
//        for (GogoSearchData artist : artists)
//        {
//            return artistWorker.getArtist(artist.getId());
//        }
//        return null;
//    }
//
//
//    public void findArtistsWithBadImages()
//    {
//        int i = 0;
//        int badImage = 0;
//        int updated = 0;
//        int missing = 0;
//        int total;
//        List<String> artistIDs = getIcelandicArtists();
//        for (String artistID : artistIDs)
//        {
//            i++;
//
//            ArtistData artist = getArtist(artistID);
//            if (artist == null)
//            {
//                continue;
//            }
//
//            if (artist.getBestImage().contains("temp") || badImage(artist.getBestImage()))
//            {
//                Boolean bOK = getBetterImage(artist);
//                if (bOK)
//                {
//                    ArtistPersisted art = ArtistPersisted.find(mapper, artistID);
//                    if (artist.getBestImage() != null && !artist.getBestImage().equalsIgnoreCase(artist.getBestImage()))
//                    {
//                        updated++;
//                        logger.info(badImage + "/" + i + "/" + artistIDs.size() + "  " + artist.getName() + " fixed image (  " + updated
//                                + ") " +
//                                artist.getUpcoming() + "/" + artist.getAll() + " https://dashboard.promogogo.com/go/adminartists.do#!/artist/" + artist.getArtistID());
//                        artistWorker.updateSearchForArtist(new ID(), artist.getArtistID());
//                    }
//                }
//                else
//                {
//                    ArtistPersisted art = ArtistPersisted.find(mapper, artist.getArtistID());
//                    art.setBestImage(null);
//                    art.setBestThumbnail(null);
//                    mapper.save(art);
//                    artistWorker.updateSearchForArtist(new ID(), artist.getArtistID());
//
//                    badImage++;
//                    logger.info(badImage + "/" + i + "/" + artistIDs.size() + "  " + artist.getName() + " has bad image "  + artist.getBestImage() + " " +
//                            artist.getUpcoming() + "/" + artist.getAll() +  " https://dashboard.promogogo.com/go/adminartists.do#!/artist/" + artist.getArtistID() );
//
//                }
//
//            }
//            else if (artist.getBestImage() == null || artist.getBestImage().isEmpty())
//            {
//                Boolean bOK = getBetterImage(artist);
//                if (bOK)
//                {
//                    ArtistPersisted art = ArtistPersisted.find(mapper, artistID);
//                    if (artist.getBestImage() != null && !artist.getBestImage().equalsIgnoreCase(artist.getBestImage()))
//                    {
//
//                        updated++;
//                        logger.info(badImage + "/" + i + "/" + artistIDs.size() + "  " + artist.getName() + " fixed image ( " + updated + ")" +
//                                artist.getUpcoming() + "/" + artist.getAll() + " https://dashboard.promogogo.com/go/adminartists.do#!/artist/" + artist.getArtistID());
//                        artistWorker.updateSearchForArtist(new ID(), artist.getArtistID());
//                    }
//                }
//                else
//                {
//                    missing++;
//                    logger.info(badImage + "/" + i + "/" + artistIDs.size() + "  " + artist.getName() + " has no image  - missing is "  + missing +
//                            " " + artist.getUpcoming() + "/" + artist.getAll() +  " https://dashboard.promogogo.com/go/adminartists.do#!/artist/" + artist.getArtistID() );
//                }
//            }
//
//        }
//        logger.info( "\t\t" + artistIDs.size() + " musical artists; " + badImage + "with bad or missing update. " + updated+ " images updated ");
//    }
//
//    private Boolean getBetterImage(ArtistData artist)
//    {
//        return crawler.updateSitesAndImages(new ID(), artist.getArtistID());
//    }
//
//
//    private boolean badImage(String bestImage)
//    {
//        if (bestImage == null || bestImage.isEmpty())
//        {
//            return false;
//        }
//        byte[] raw = HTTPUtil.getRaw(bestImage);
//        if (raw == null || raw.length < 10)
//        {
//            return true;
//        }
//
//        return false;
//    }
//
//
//    public void tagGender()
//    {
//        int i = 0;
//        int withGender = 0;
//        int female = 0;
//        int male = 0;
//        int mixed = 0;
//        int femaleFronted = 0;
//        int other = 0;
//
//        names = new IcelandicNames();
//
//        int total;
//        List<String> artistIDs = getIcelandicMusicArtists();
//        for (String artistID : artistIDs)
//        {
//            i++;
//
//            ArtistData artist = getArtist(artistID);
//            if (artist == null)
//            {
//                continue;
//            }
//
//            if (artist.getArtistType() == ArtistType.band)
//            {
//                logger.info(withGender + "/" + i + "/" + artistIDs.size() + "  " + artist.getName() + " is a band "  + artist.getTier() +" " +  artist.getAttribute("gender") +  " " +
//                        artist.getUpcoming() + "/" + artist.getAll() +  " https://dashboard.promogogo.com/go/adminartists.do#!/artist/" + artist.getArtistID() );
//
//                continue;
//            }
//            if (artist.hasAttribute("gender")  && !artist.getAttribute("gender").isEmpty())
//            {
//                withGender++;
//                logger.info(withGender + "/" + i + "/" + artistIDs.size() + " " + artist.getName() + " gender "  + artist.getAttribute("gender")  + " " + artist.getTier() +" " + artist.getUpcoming() + "/" + artist.getAll());
//            }
//            if (!artist.hasAttribute("gender") || artist.getAttribute("gender").isEmpty())
//            {
//                String gender = guessGender(artist);
//                if (gender != null && !gender.isEmpty())
//                {
//                    artist.addAttribute("gender", gender);
//                    withGender++;
//                    artistWorker.setUniqueAttribute(artistID, DataSource.promogogo,"gender", gender);
//                     logger.info(withGender + "/" + i + "/" + artistIDs.size() + " guessing " + artist.getName() + " gender "  + gender + " " + artist.getTier() +" " +
//                             artist.getUpcoming() + "/" + artist.getAll());
//                }
//                else if (artist.getUpcoming() > 0 || artist.getAll() > 5)
//                {
//                    if (artist.hasAttribute("export"))
//                    {
//                        logger.info(withGender + "/" + i + "/" + artistIDs.size() + " guessing " + artist.getName() + " unknown gender "  + artist.getTier() +" " +
//                                artist.getUpcoming() + "/" + artist.getAll() +  " https://dashboard.promogogo.com/go/adminartists.do#!/artist/" + artist.getArtistID() );
//                    }
//                }
//
//            }
//            if (artist.hasAttribute("gender", "female"))
//            {
//                female++;
//            }
//            else if (artist.hasAttribute("gender", "male"))
//            {
//                male++;
//            }
//            else if (artist.hasAttribute("gender", "mixed"))
//            {
//                mixed++;
//            }
//            else if (artist.hasAttribute("gender", "femaleFronted"))
//            {
//                femaleFronted++;
//            }
//            else if (artist.hasAttribute("gender"))
//            {
//                other++;
//            }
//
//        }
//        logger.info( "\t\t" + artistIDs.size() + " musical artists; " + withGender + " with gender specified. " + male + " males " + female + " females " +
//                femaleFronted + " femaleFronted " + mixed + " mixed " + other + " other");
//    }
//
//    private String guessGender(ArtistData artist)
//    {
//        if (artist.getName().toLowerCase().endsWith("dóttir"))
//        {
//            return "female";
//        }
//        if (artist.getName().toLowerCase().endsWith("son"))
//        {
//            return "male";
//        }
//        if (artist.getName().toLowerCase().contains("karlakór"))
//        {
//            return "male";
//        }
//        if (artist.getName().toLowerCase().contains("kvennakór"))
//        {
//            return "female";
//        }
//        if (names != null)
//        {
//            return names.getGender(artist.getName());
//        }
//        return null;
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
//
//
//    public void tagISEventsAbroad()
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
//        int skip = 2185;
//        List<String> artistID = new ArrayList<>(1000);
//        while (!done)
//        {
//            Page<GogoSearchData> artists = gogoSearch.findByName("", filter, offset, limit);
//            for (GogoSearchData artist : artists)
//            {
//                if (i < skip)
//                {
//                    i++;
//                    continue;
//                }
//                Integer count = tagEventsAbroad(artist);
//                ArtistData artistData = artistWorker.getArtist(artist.getId());
//                try
//                {
//                    logger.info(i + "/" + artists.getTotalSize() +"\t" +  artist.getDisplayName() + " " + artist.getTier() + " " +count + " " + artistData.getUpcoming() +"/" + artistData.getAll() +" " + StrUtil.formatNumber(artist.getPriority(), 2));
//                }
//                catch (Exception e)
//                {
//
//                }
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
//
//    }
//
//    private Integer tagEventsAbroad(GogoSearchData artist)
//    {
//        SearchFilter filter = new SearchFilter();
//        filter.setFrom(DateTime.now().minusYears(2));
//        filter.addArtistID(artist.getId());
//        filter.addNegativeLocation(null, null, "IS");
//
//        Boolean done = false;
//        String offset = null;
//        Integer count = 0;
//        while (!done)
//        {
//            Page<GogoEventSearchData> events = eventSearch.findEvents("", filter, offset, 10);
//            events.getList().forEach(event -> {
//                PromoGhettoData pgg = eventWorker.getEvent(event.getId());
//                if (pgg != null)
//                {
//                    if (pgg.getVenue() != null)
//                    {
//                        logger.info(pgg.getWhen() + "\t"+ pgg.getTitle() + " " + pgg.getVenueName()  +" " + pgg.getVenue().getPrettyAddress());
//                    }
//                    else
//                    {
//                        logger.info(pgg.getWhen() + "\t"+ pgg.getTitle() );
//                    }
//                    eventWorker.updateArtistCountryTagOnEvent(pgg);
//                }
//            });
//            count = events.getTotalSize();
//            offset = events.getNextOffset();
//            if (offset == null)
//            {
//                done = true;
//            }
//        }
//        return count;
//    }
//
//    public List<String> getIcelandicMusicArtists()
//    {
//        SearchAttractionFilter filter = new SearchAttractionFilter();
//        filter.addLocationCountry("IS");
//        filter.searchArtists(true);
//        filter.setAttractionType(AttractionType.music);
//        filter.setOrder(EventSort.name, SortOrder.asc);
//        filter.addAttribute("export-is-2023");
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
//                ArtistData artistData = artistWorker.getArtist(artist.getId());
//                logger.info(i + "/" + artists.getTotalSize() +"\t" +  artist.getDisplayName() + " '" + artist.getSortName() + "' " + artist.getTier() + " " + artistData.getUpcoming() +"/" + artistData.getAll() +" " + StrUtil.formatNumber(artist.getPriority(), 2));
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
//    public List<String> tagIcelandicMusicArtistsExport2023()
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
//                ArtistData artistData = artistWorker.getArtist(artist.getId());
//                if (hasEventsAbroad(artist.getId()))
//                {
//                    artistWorker.addAttribute(artist.getId(), DataSource.promogogo, "export", "export-is-2023");
//                }
//                logger.info(i + "/" + artists.getTotalSize() +"\t" +  artist.getDisplayName() + " " + artist.getTier() + " " + artistData.getUpcoming() +"/" + artistData.getAll() +" " + StrUtil.formatNumber(artist.getPriority(), 2));
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
//    private boolean hasEventsAbroad(String artistID)
//    {
//        SearchFilter filter = new SearchFilter();
//        filter.addArtistID(artistID);
//        filter.addNegativeLocation(null, null,"IS");
//        filter.upcoming();
//        Page events = eventSearch.findEvents("", filter, null, 10);
//        if (events.getTotalSize() > 0)
//        {
//            return true;
//        }
//        return false;
//    }
//
//
//    private List<String> getIcelandicArtists()
//    {
//        SearchAttractionFilter filter = new SearchAttractionFilter();
//        filter.addLocationCountry("IS");
//        filter.searchArtists(true);
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
////                logger.info(i + "/" + artists.getTotalSize() +"\t" +  artist.getDisplayName());
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



}
