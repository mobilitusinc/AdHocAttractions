package com.mobilitus.attractionscmd.bandsintown;

/**
 * @author helgaw
 * @todo add class description.
 * @since 7/29/22 15:34
 */
public class BandsInTownVenueScraper extends BaseDocument
{
//    private final ID id;
//    private DynamoDBMapper mapper;
//    private S3 s3;
//    private SearchConfig searchConfig;
//
//    private Map<String, VenueData> venueMap = new HashMap<>(100);
//    private Map<String, ArtistData> artistMap = new HashMap<>(100);
//    private AttractionSearch gogoSearch;
//    private EventSearch eventSearch;
//    private EventWorker eventWorker;
//    private ArtistWorker artistWorker;
//    private VenueWorker venueWorker;
//
//    private Producer toAttractionWorker;
//    private Logger logger = Logger.getLogger(BandsInTownVenueScraper.class);
//
//    private static MemcachedAdministrator cacheAdministrator = null;
//
//    static
//    {
//        cacheAdministrator =  Cache.getCache();
//    }
//
//
//    public BandsInTownVenueScraper()
//    {
//        AWSCredentials credentials = AWSUtils.getCredentialsProvider().getCredentials();
//
//        AWSCredentialsProvider credentialsProvider  = new ProfileCredentialsProvider("prod");
//        mapper = AWSUtils.getMapper(credentialsProvider);
//
//        searchConfig = new DefaultSearchConfig(credentials);
//        gogoSearch = new AttractionSearch(searchConfig.getCredentials(), searchConfig.getArtistSearchURL());
//        eventSearch = new EventSearch(searchConfig.getCredentials(), searchConfig.getEventSearchURL());
//
//        AmazonS3Client s3Client = new AmazonS3Client(credentials);
//
//        s3 = new S3(s3Client, "");
//
//        id = new ID();
//        artistWorker = new ArtistWorker(id, new FaceData(), mapper, s3, searchConfig);
//        venueWorker = new VenueWorker(id, new FaceData(), mapper, s3, searchConfig);
//        eventWorker = new EventWorker(id, new FaceData(), mapper, s3, searchConfig);
//    }
//
//    public BandsInTownVenueScraper(String scrapingbeeApiKey)
//    {
//        this();
//        this.scrapingbeeApiKey = scrapingbeeApiKey;
//    }
//
//    public List<EventGhettoData> scrapeEventsForVenue(String id)
//    {
//        int i = 0;
//
//        List<EventGhettoData> result = new ArrayList<>(50);
//
//        String root = "https://www.bandsInTown.com/venues/";
//        Map<String, EventGhettoData> done = new HashMap<>(100);
//        String url = root + id ;
//
//        //Get Document object after parsing the html from given url.
//        getDocument(url);
//        if (document == null)
//        {
//            return result;
//        }
//        Elements allEvents = document.getElementsByClass("microformat");
//        for (Element events : allEvents)
//        {
//            i++;
//            //                System.out.println(events);
//            String json = events.data();
//
//            List<EventGhettoData> myEvents = importEvent (json);
//
//            for (EventGhettoData event : myEvents)
//            {
//                logger.info("\t\t\t" + i + "/" + allEvents.size() + " " + event.getTitle() + " " + event.getShowTime() + " "+ event.getStatus());
//
//                if (!hasChanged(event))
//                {
//                    continue;
//                }
//
//                addToCache(event);
//
//
//                if (event.getShowTime() == null)
//                {
//                    continue;
//                }
//                if (event.getShowTime().plusDays(365).isBeforeNow())
//                {
//                    continue;
//                }
//
//                if (done.containsKey(event.getEventID()))
//                {
//                    continue;
//                }
//
//                done.put(event.getEventID(), event);
//                BandsInTownEventPage eventPage = BandsInTownEventPage.create(event.getUrl(), scrapingbeeApiKey);
//
//                if (eventPage !=  null && event.hasAttribute("festival"))
//                {
////                    event.setTitle(eventPage.getTitle());
////                    event.setBestImage(eventPage.getImage());
////                    List<SchemaArtist> list = eventPage.getPerformers();
////                    logger.info("Found " + list.size() + " performers at " + event.getTitle() + " festival");
////                    event.removeArtists();
////                    event.addClassification(GogoClassification.festival);
////                    String festivalName = eventPage.getTitle();
////                    if (festivalName.endsWith("2022"))
////                    {
////                        festivalName = festivalName.substring(0, festivalName.length() - 4).trim();
////                    }
////
////                    event.addAttribute("festivalName", festivalName);
////                    ArtistData mainArtist = new ArtistData();
////                    mainArtist.setName(festivalName);
////                    mainArtist.setBestImage(eventPage.getImage());
////                    mainArtist.setMajorType(AttractionType.music);
////                    mainArtist.setArtistType(ArtistType.festival);
////                    mainArtist.setWebpage(eventPage.getWebPage());
////
////                    event.addArtist(mainArtist);
////
////                    for (SchemaArtist artist : list)
////                    {
////                        logger.info ("   adding " + artist.getName());
////                        event.addArtist(artist.toData());
////                    }
////                }
////                SchemaOffers offers = eventPage.getOffer();
////                if (offers == null || offers.isEmpty())
////                {
////                    continue;
////                }
////                event.addAvailableTicket(offers.toData());
////
////                event.removeAttribute("description");
////                event.removeAttribute("description-html");
////
////                VenueData venue = getOrCreateVenue(event.getVenue(), eventPage);
////
////                if (venue == null)
////                {
////                    continue;
////                }
////                event.setVenue(venue);
////                if (event.hasAttribute("wholeday"))
////                {
////                    // if this is a whole day event we need to reset the start time with the timezone
////                    event.setShowTime(eventPage.getShowTime(venue.getTimezone()));
////                    event.setEnd(eventPage.getEnd(venue.getTimezone()));
////                }
////                event.setEventTimezone(venue.getTimezone());
////                List<ArtistData> artists = new ArrayList<>(10);
////                for (Object artist : event.getArtists())
////                {
////                    ArtistData myArtist = getOrCreateArtist((ArtistData) artist);
////                    if (myArtist == null)
////                    {
////                        continue;
////                    }
////                    if (myArtist.getBestImage().isEmpty() && artists.isEmpty())
////                    {
////                        myArtist.setBestImage(eventPage.getImage());
////                    }
////
////                    addExportReady(myArtist, event);
////                    artists.add(myArtist);
//                }
////                event.removeArtists();
////                for (ArtistData artist : artists)
////                {
////                    event.addArtist(artist);
////                }
//                // find or store artist
//
//                if (event.getStatus() == EventStatus.cancelled || event.getStatus() == EventStatus.postponed)
//                {
//                    if (eventExists(event))
//                    {
//                        List<String> eventIDs = findEvents(event);
//                        for (String myEventID : eventIDs)
//                        {
//                            Boolean isPrivate = true;
//                            eventWorker.setEventStatus(myEventID, EventStatus.cancelled, isPrivate);
//                        }
//                        logger.info(i + "/" + allEvents.size() + " " + event.getTitle() + " has been cancelled");
//                    }
//                    continue;
//                }
//                logger.info(i + "/" + allEvents.size() + " " + event.getTitle() + " " + event.getShowTime().toDateTime(event.getEventTimezone()) + "-" + event.getEnd() + " " + event.getVenue().getName() + " " + event.getUrl());
//                result.add(event);
//            }
//        }
//        return result;
//    }
//
//    private void addExportReady(ArtistData myArtist, EventGhettoData event)
//    {
//        if (myArtist.getCountry() != null && myArtist.getCountry().equalsIgnoreCase("is"))
//        {
//            if (event.getVenue() != null && event.getVenue().getCountryCode() != CountryCode.is)
//            {
//                Integer yearOfShow = event.getShowTime().year().get();
//                if (!myArtist.hasAttribute("export", "export-is-" + yearOfShow))
//                {
//                    logger.info("we thinks " + myArtist.getName() + " is icelandic");
//                    artistWorker.addAttribute(myArtist.getArtistID(), "export", "export-is-" + yearOfShow);
//                }
//                if (!myArtist.hasAttribute("export", "export-is"))
//                {
//                    artistWorker.addAttribute(myArtist.getArtistID(), "export", "export-is");
//                }
//            }
//        }
//    }
//
//    private EventGhettoData getEvent(EventGhettoData event)
//    {
//        String query = event.getTitle();
//
//        SearchFilter filter = new SearchFilter();
//        filter.setFrom(event.getShowTime().minusHours(2));
//        filter.setUntil(event.getShowTime().plusHours(2));
//        if (event.getVenue() != null)
//        {
//            filter.addLocation(event.getVenue().getCity(), event.getVenue().getState(), event.getVenue().getCountryCode().name());
//        }
//        Page<GogoEventSearchData> events = eventSearch.findEventsByArtist(event.getMainArtist().getArtistID(), filter, null, 10);
//        if (events.isEmpty())
//        {
//            return null;
//        }
//        for (GogoEventSearchData myEvent : events)
//        {
//            if (myEvent.getDisplayName().toLowerCase().contains("parking"))
//            {
//                continue;
//            }
//            return eventWorker.getEvent(myEvent.getId()).getGig();
//        }
//        return null;
//    }
//
//    private boolean eventExists(EventGhettoData event)
//    {
//        String query = event.getTitle();
//
//        SearchFilter filter = new SearchFilter();
//        filter.setFrom(event.getShowTime().minusHours(3));
//        filter.setUntil(event.getShowTime().plusHours(3));
//        if (event.getVenue() != null)
//        {
//            filter.addLocation(event.getVenue().getCity(), event.getVenue().getState(), event.getVenue().getCountryCode().name());
//        }
//        Page<GogoEventSearchData> events = eventSearch.findEventsByArtist(event.getMainArtist().getArtistID(), filter, null, 10);
//        if (events.isEmpty())
//        {
//            return false;
//        }
//        return true;
//    }
//
//    private List<String> findEvents(EventGhettoData event)
//    {
//        SearchFilter filter = new SearchFilter();
//        filter.setFrom(event.getShowTime().minusHours(3));
//        filter.setUntil(event.getShowTime().plusHours(3));
//        if (event.getVenue() != null)
//        {
//            filter.addLocation(event.getVenue().getCity(), event.getVenue().getState(), event.getVenue().getCountryCode().name());
//        }
//
//        List<String> result = new ArrayList<>(10);
//        Page<GogoEventSearchData> events = eventSearch.findEventsByArtist(event.getMainArtist().getArtistID(), filter, null, 10);
//        if (events.isEmpty())
//        {
//            for (GogoEventSearchData ev : events)
//            {
//                result.add(ev.getId());
//            }
//        }
//        return result;
//    }
//
//    private List<EventGhettoData> importEvent(String json)
//    {
//        List<EventGhettoData> events = new ArrayList<>(3);
//        //        System.out.println("\n\n" + StrUtil.formatAsJson(json));
//
//        List<SchemaEvent> schemaEvents = SchemaEvent.createList(json);
//        for (SchemaEvent eventOn : schemaEvents)
//        {
//            EventGhettoData event = eventOn.toData();
//
//            if (event == null)
//            {
//                // this is not an event
//                continue;
//            }
//            if (event.getBestImage() != null)
//            {
//                if (!event.getBestImage().startsWith("http"))
//                {
//                    event.setBestImage("https://images.sk-static.com/images/" + event.getBestImage());
//                }
//            }
//            String eventID = getBandsInTownIDFromUrl(eventOn.getUrl());
//            event.setEventID(eventID);
//            if (eventOn.getUrl().contains("?"))
//            {
//                // remove the tracker params
//                event.setUrl(eventOn.getUrl().substring(0, eventOn.getUrl().indexOf("?")));
//                event.addUniqueAttribute("url",event.getUrl());
//
//            }
//            else
//            {
//                event.setUrl(eventOn.getUrl());
//            }
//            event.addUniqueAttribute("url",event.getUrl());
//
//            // the description is trash
//            event.removeAttribute("description-html");
//            event.removeAttribute("description");
//
//            event.setStatus(eventOn.getEventStatus());
//
//            event.setSource(DataSource.bandsInTown);
//            event.setGhettoType(GhettoType.EVENT);
//            events.add(event);
//        }
//        return events;
//    }
//
//    private void addToCache(EventGhettoData event)
//    {
//        cacheAdministrator.putInCache(event.getEventID(), event.toJson(), CacheConst.TIMEOUT_24H * 7);
//    }
//
//    private boolean hasChanged(EventGhettoData event)
//    {
//        String json = event.toJson();
//        try
//        {
//            String stored = (String)cacheAdministrator.getFromCache(event.getEventID());
//            if (stored != null && stored.equals(json))
//            {
//                return false;
//            }
//        }
//        catch (ExpiredException e)
//        {
//        }
//
//        return true;
//    }
//
//    private String getBandsInTownIDFromUrl(String url)
//    {
//        int startIndex = url.lastIndexOf("/");
//        int lastIndex = url.indexOf("?");
//        return url.substring(startIndex + 1, lastIndex);
//    }
//
//
//    private ArtistData getOrCreateArtist(ArtistData artist)
//    {
//        String artistID = artist.getArtistID();
//        if (artist.hasAttribute("sameAs"))
//        {
//            artistID = getBandsInTownIDFromUrl(artist.getAttribute("sameAs"));
//            artist.setArtistID(artistID);
//        }
//        if (artist.getArtistID() != null && !artist.getArtistID().isEmpty())
//        {
//            ArtistPersisted theArtist = artistWorker.getArtistByAlternativeID(DataSource.bandsInTown, artist.getArtistID());
//            if (theArtist != null)
//            {
//                ArtistData myArtist =  artistWorker.getArtist(theArtist.getId());
//                return myArtist;
//            }
//        }
//
//        ArtistData best = getBestMatch(artist);
//        if (best != null)
//        {
//            if (artistID != null && !artistID.isEmpty())
//            {
//                BandsInTownArtistScraper scraper = new BandsInTownArtistScraper();
//                ArtistData newArtist = scraper.getArtistData(artistID);
//
//                newArtist.setArtistID(best.getArtistID());
//                ArtistData myArtist = artistWorker.update(DataSource.bandsInTown, newArtist);
//
//                artistWorker.addSource(best.getArtistID(), DataSource.bandsInTown, artistID,"bandsInTown",
//                                       "https://www.bandsInTown.com/artists/" +artistID);
//                return myArtist;
//            }
//            return best;
//        }
//
//        ArtistData myArtist = createArtist (artist);
//        return myArtist;
//    }
//
//    private ArtistData getBestMatch(ArtistData artist)
//    {
//        if (artist.getName().toLowerCase().trim().endsWith("and oceans"))
//        {
//            String artistID = "5d217263-c525-4d4c-b677-760da8144513";
//            ArtistData foundArtist = artistWorker.getArtist(artistID);
//            if (foundArtist != null)
//            {
//                return foundArtist;
//            }
//        }
//
//        Page<GogoSearchData> matches = gogoSearch.find(artist.getName(), PerformerType.attraction, null, 100);
//
//        ArtistData best = null;
//        for (GogoSearchData match : matches)
//        {
//            ArtistData foundArtist = artistWorker.getArtist(match.getId());
//
//            if (foundArtist.getMajorType() == artist.getMajorType())
//            {
//                if (foundArtist.nameMatches(artist.getName()))
//                {
//                    best = foundArtist;
//                }
//            }
//        }
//
//        if (best != null)
//        {
//            return best;
//        }
//        return null;
//
//    }
//
//
//    public ArtistData createArtist(ArtistData artist)
//    {
//        String thirdPartyID = artist.getArtistID();
//        artist.setArtistID(null);
//        if (artist.getName() == null || artist.getName().isEmpty())
//        {
//            return null;
//        }
//        ArtistData artistData = null;
//        ArtistData remoteArtist = null;
//        if (thirdPartyID != null && !thirdPartyID.isEmpty())
//        {
//            BandsInTownArtistScraper scraper = new BandsInTownArtistScraper();
//            remoteArtist = scraper.getArtistData(thirdPartyID);
//
//        }
//        if (remoteArtist != null)
//        {
//            artistData = artistWorker.create(DataSource.bandsInTown, remoteArtist);
//        }
//        else
//        {
//            artistData = artistWorker.create(DataSource.bandsInTown, artist);
//        }
//
//        Track.trackArtist(mapper, artistData.getArtistID(), artistData.getName(), "create", "New artist created via BandsInTown importer");
//
//        if (thirdPartyID != null && !thirdPartyID.isEmpty())
//        {
//            artistWorker.addSource(artistData.getArtistID(), DataSource.bandsInTown, thirdPartyID, "bandsInTown", "https://bandsInTown.com/artists/" +thirdPartyID);
//        }
//        return artistData;
//    }
//
//
//
//
//    private VenueData getOrCreateVenue(VenueData aVenue, BandsInTownEventPage eventPage)
//    {
//        if (aVenue == null)
//        {
//            return null;
//        }
//        VenuePersisted venuePersisted = venueWorker.getVenue(DataSource.bandsInTown, eventPage.getVenueID());
//
//        if (venuePersisted != null)
//        {
//            updateFromPage(venuePersisted.getId(), eventPage);
//            if (!venuePersisted.getVerified() && (venuePersisted.getAddress1() == null || venuePersisted.getAddress1().isEmpty()))
//            {
//                venuePersisted.setAddress1(aVenue.getAddress1());
//                venuePersisted.setAddress2(aVenue.getAddress2());
//                venuePersisted.setCity(aVenue.getCity());
//                venuePersisted.setRegion(aVenue.getState());
//                venuePersisted.setCountry(aVenue.getCountryCode().name());
//                venuePersisted.setLocation(aVenue.getLocationPoint());
//                mapper.save(venuePersisted);
//                venueWorker.updateSearchForVenue(new ID(), venuePersisted.getId());
//            }
//            return venueWorker.getVenue(venuePersisted.getId());
//        }
//
//        SearchAttractionFilter filter = new SearchAttractionFilter();
//        filter.searchVenues(true);
//        filter.addLocation(aVenue.getCity(), aVenue.getState(), aVenue.getCountryCode().name());
//        if (aVenue.getLocationPoint() != null && aVenue.getLocationPoint().isValid())
//        {
//            filter.addPointRadiusKM(aVenue.getLocationPoint(), 10D);
//        }
//        Page<GogoSearchData> matches = null;
//        try
//        {
//            matches = gogoSearch.findByName(aVenue.getName(), filter, null, 5);
//        }
//        catch (Exception e)
//        {
//            logger.error("find failed - fix character handling " + aVenue.getName());
//            return null;
//        }
//
//        for (GogoSearchData match : matches)
//        {
//            VenuePersisted matchedVenue = VenuePersisted.find(mapper, match.getId());
//            if (matchedVenue.same(match))
//            {
//                updateFromPage(matchedVenue.getId(), eventPage);
//                if (!matchedVenue.getVerified() && (matchedVenue.getAddress1() == null || matchedVenue.getAddress1().isEmpty()))
//                {
//                    matchedVenue.addAlias(aVenue.getName());
//                    matchedVenue.setAddress1(aVenue.getAddress1());
//                    matchedVenue.setAddress2(aVenue.getAddress2());
//                    matchedVenue.setCity(aVenue.getCity());
//                    matchedVenue.setRegion(aVenue.getState());
//                    matchedVenue.setCountry(aVenue.getCountryCode().name());
//                    matchedVenue.setLocation(aVenue.getLocationPoint());
//                    try
//                    {
//                        mapper.save(matchedVenue);
//                    }
//                    catch (Exception e)
//                    {
//                        //                        logger.error(StrUtil.stack2String(e));
//                    }
//                    venueWorker.updateSearchForVenue(new ID(), matchedVenue.getId());
//                }
//
//                venueWorker.addSource(matchedVenue.getId(), DataSource.bandsInTown, eventPage.getVenueID(), "bandsInTown", aVenue);
//                VenueData venue = venueWorker.getVenue(match.getId());
//                return venue;
//            }
//        }
//        return createVenue(aVenue, eventPage);
//    }
//
//    private void updateFromPage(String id, BandsInTownEventPage eventPage)
//    {
//        VenuePersisted venuePersisted = venueWorker.getVenuePersisted(id);
//
//        if (venuePersisted == null)
//        {
//            return;
//        }
//        if (venuePersisted.getWebpage() == null || venuePersisted.getWebpage().isEmpty())
//        {
//            if (eventPage.getWebpage() != null)
//            {
//                venuePersisted.setWebpage(eventPage.getWebpage());
//                mapper.save(venuePersisted);
//            }
//        }
//
//        if (venuePersisted.getCapacity() == null && eventPage.getVenueCapacity() != null && eventPage.getVenueCapacity() > 0)
//        {
//            venueWorker.updateVenueCapacity(venuePersisted.getId(), eventPage.getVenueCapacity());
//        }
//    }
//
//    private VenueData createVenue(VenueData aVenue, BandsInTownEventPage eventPage)
//    {
//        if (aVenue.getName() == null || aVenue.getName().isEmpty())
//        {
//            return null;
//        }
//        logger.info("Creating venue " + aVenue.getName() + " "+ aVenue.getPrettyAddress());
//
//        String venueID = aVenue.getLocationID();
//        aVenue.setMajorCategory(MajorVenueCategory.musicVenue);
//        //        if (isFestival(aVenue))
//        //        {
//        //            aVenue.addMinorCategory(MinorVenueCategory.festivalGrounds);
//        //        }
//
//        MapBoxHandler mapbox = new MapBoxHandler();
//
//        VenueData location = mapbox.getLocation(aVenue.getName(), aVenue);
//        if (location != null &&
//            (location.getName().toLowerCase().contains(aVenue.getName().toLowerCase()) || aVenue.getName().toLowerCase().contains(location.getName().toLowerCase())))
//        {
//            aVenue.setAddress(location.getAddress1());
//            aVenue.setAddress2(location.getAddress2());
//            aVenue.setZipAndCity(location.getZip(), location.getCity());
//            aVenue.setCountry(location.getCountryCode().name());
//            if (location.getCountryCode() == CountryCode.us || location.getCountryCode() == CountryCode.ca || location.getCountryCode() == CountryCode.mx)
//            {
//                aVenue.setRegion(location.getState());
//            }
//            aVenue.setLocation(location.getLocation());
//        }
//        if (location.getLocationPoint() != null && location.getLocationPoint().isValid())
//        {
//            DateTimeZone timezone = mapbox.getTimezone(location.getLocationPoint());
//            aVenue.setTimezone(timezone);
//        }
//        else
//        {
//            DateTimeZone nullZone = null;
//            DateTimeZone timezone = LocationUtil.getTimezone(nullZone, location.getCountryCode().name());
//            aVenue.setTimezone(timezone);
//        }
//        VenueData venueData = venueWorker.create(DataSource.bandsInTown, aVenue);
//        venueWorker.addSource(venueData.getLocationID(), DataSource.bandsInTown, venueID);
//
//        if (location != null && (location.getName().toLowerCase().contains(aVenue.getName().toLowerCase()) || aVenue.getName().toLowerCase().contains(location.getName().toLowerCase())))
//        {
//            for (AttributeData attrib : location.getAttributes())
//            {
//                venueWorker.addAttribute(venueData.getLocationID(), DataSource.mapbox, attrib.getKey(), attrib.getValue());
//            }
//        }
//
//        updateFromPage(venueData.getLocationID(), eventPage);
//        venueWorker.addSource(venueData.getLocationID(), DataSource.bandsInTown, eventPage.getVenueID(), "bandsInTown", aVenue);
//
//        Track.trackVenue(mapper, venueData.getLocationID(), venueData.getName() + " " + venueData.getPrettyAddress(), "create",
//                         "New venue created via BandsInTown importer");
//        return venueData;
//    }
//

}
