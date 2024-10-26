package com.mobilitus.attractionscmd.festivals.icelandairwaves;

/**
 * @author helgaw
 * @todo add class description.
 * @since 2019-04-16 16:47
 */
public class SpotifyHandler
{

//    private final ArtistWorker artistWorker;
//    private final DynamoDBMapper mapper;
//    private final S3 s3;
//    private final SearchConfig searchConfig;
//    private final EventSearch eventSearch;
//    private final AttractionSearch attractionSearch;
//    private final Spotify spotify = new Spotify();
//
//    private static final Logger logger = Logger.getLogger(SpotifyHandler.class);
//
//
//    public SpotifyHandler()
//    {
//        AWSCredentials credentials = AWSUtils.getCredentialsProvider().getCredentials();
//
//        AWSCredentialsProvider credentialsProvider  = new ProfileCredentialsProvider("prod");
//        mapper = AWSUtils.getMapper(credentialsProvider);
//
//
//        AmazonS3Client s3Client = new AmazonS3Client(credentials);
//        searchConfig = new DefaultSearchConfig(credentials);
//        s3 = new S3(s3Client, "");
//
//
//        eventSearch = new EventSearch(searchConfig.getCredentials(), searchConfig.getEventSearchURL());
//        attractionSearch = new AttractionSearch(searchConfig.getCredentials(), searchConfig.getArtistSearchURL());
//
//        artistWorker = new ArtistWorker(new ID(), null, mapper, s3, searchConfig);
//    }
//
//
//    public Boolean updateFromSpotify(String artistID, String spotifyID, Boolean forceUpdateImage)
//    {
//
//        ArtistData pggArtist = artistWorker.getArtist(artistID);
//
//        if (pggArtist == null)
//        {
//            return false;
//        }
//
//        if (spotifyID == null || spotifyID.isEmpty())
//        {
//            return false;
//        }
//
//        Artist spotifyArtist = spotify.getArtist(spotifyID);
//
//        if (spotifyArtist == null)
//        {
//            return false;
//        }
//        System.out.println("  Artist " + spotifyArtist.getName() + "' " + spotifyArtist.getPopularity() + " " + spotifyArtist.getFollowers().getTotal() +
//                " matches '" + pggArtist.getName() + "’ it has " + spotifyArtist.getGenres().length + " genres. " + Arrays.toString(spotifyArtist.getGenres()));
//
//        updateArtistFromSpotify(pggArtist, spotifyArtist, forceUpdateImage);
//
//
//        List<Artist> relatedArtists = spotify.findRelatedArtists(spotifyArtist.getId());
//        for (Artist related : relatedArtists)
//        {
//            System.out.println("\t\t\trelated Artist '" + related.getName() + "' " + related.getPopularity() + "% - followers " + related.getFollowers().getTotal() +
//                    " matches'" + pggArtist.getName() + "' it has " + related.getGenres().length + " genres. " + Arrays.toString(related.getGenres()));
//
//            //                    DataScienceLocal ds = DataScienceFactory.getLocal();
//            updateRelatedFromSpotify(pggArtist, related, false);
//        }
//        artistWorker.setUniqueAttribute(pggArtist.getArtistID(), DataSource.promogogo, "relatedImported", "true");
//
//        return true;
//    }
//
//    private GenreData getBestGogoGenre(Artist spotifyArtist)
//    {
//        if (spotifyArtist == null)
//        {
//            return null;
//        }
//
//        if (spotifyArtist.getGenres() == null)
//        {
//            return null;
//        }
//
//        for (String aGenre : spotifyArtist.getGenres())
//        {
//            String str = removeQualifier(aGenre);
//            Genre g = Genre.create(str);
//            if (g != null)
//            {
//                return new GenreData(g);
//            }
//        }
//        return null;
//    }
//
//    private String removeQualifier(String aGenre)
//    {
//        String[] countries = {"italian", "firenze", "icelandic", "belgian", "irish", "danish", "luxembourgian", "australian", "british", "milan", "korean"};
//
//        for (String s : countries)
//        {
//            if (aGenre.toLowerCase().contains(s))
//                return aGenre.replace(s, "").trim();
//        }
//        return aGenre;
//    }
//
//
//    private void updateArtistFromSpotify(ArtistData pggArtist, Artist spArt, Boolean forceUpdateImage)
//    {
//        if (spArt == null)
//        {
//            return;
//        }
//        ArtistPersisted artistPersisted = ArtistPersisted.find(mapper, pggArtist.getArtistID());
//        ExternalUrl url = spArt.getExternalUrls();
//        String spotifyUrl = url.get("spotify");
//        Boolean shouldUpdate = false;
//        if (artistPersisted.getSpotify() == null || !artistPersisted.getSpotify().equalsIgnoreCase(spotifyUrl))
//        {
//            artistPersisted.setSpotify(spotifyUrl);
//
//            artistPersisted.setType("Music");
//            artistPersisted.setMajorType(AttractionType.music.name());
//
//            shouldUpdate = true;
//        }
//        if (artistPersisted.getGogoGenre() == null ||artistPersisted.getGogoGenre().isEmpty() || artistPersisted.getGogoGenre().equalsIgnoreCase("unknown"))
//        {
//            if (spArt.getGenres().length > 0)
//            {
//                GenreData g = getBestGogoGenre(spArt);
//                if (g != null && !g.getId().equalsIgnoreCase("unknown" ))
//                {
//                    artistPersisted.setGogoGenre(g.getId());
//
//                    artistPersisted.setType("Music");
//                    artistPersisted.setMajorType(AttractionType.music.name());
//                    shouldUpdate = true;
//                }
//            }
//        }
//
//        if (artistPersisted.majorType() == null ||artistPersisted.majorType() == AttractionType.unknown)
//        {
//            artistPersisted.setType("Music");
//            artistPersisted.setMajorType(AttractionType.music.name());
//            shouldUpdate = true;
//        }
//
//
////         if (!artistPersisted.isImageGood() && spArt.getImages() != null && spArt.getImages().length > 0)
//        if (spArt.getImages() != null && spArt.getImages().length > 0)
//        {
//              if (forceUpdateImage || !artistPersisted.isImageGood())
//            {
//                artistPersisted.setBestImage(spArt.getImages()[0].getUrl());
//                artistPersisted.setBestThumbnail(spArt.getImages()[0].getUrl());
//                shouldUpdate = true;
//            }
//        }
//
//        if (shouldUpdate)
//        {
//            mapper.save(artistPersisted);
//        }
//
//        artistWorker.addSource(artistPersisted.getId(), DataSource.spotify, spArt.getId(), "", spArt.getHref());
//
//        artistWorker.setUniqueAttribute(pggArtist.getArtistID(), DataSource.spotify, "popularity", spArt.getPopularity() + "");
//        artistWorker.setUniqueAttribute(pggArtist.getArtistID(), DataSource.spotify, "followers", spArt.getFollowers().getTotal() + "");
//
//
//        for (String genre : spArt.getGenres())
//        {
//            try
//            {
//                artistWorker.addAttribute(pggArtist.getArtistID(), DataSource.spotify, "genre", genre);
//            }
//            catch (Exception e)
//            {
//                try
//                {
//                    Thread.sleep(1000);
//                }
//                catch (InterruptedException e1)
//                {
//                }
//            }
//        }
//
//
//        artistWorker.updateSearchForArtist(new ID(), pggArtist.getArtistID());
//        System.out.println("Updated  " + pggArtist.getName() + " from spotify");
//
//    }
//
//
//    private void updateRelatedFromSpotify(ArtistData pggArtist, Artist related, Boolean forceUpdateImage)
//    {
//        ArtistData myArtist = findArtistFromSpotify(related);
//        if (myArtist != null)
//        {
//            addRelated(pggArtist, myArtist);
//            updateArtistFromSpotify(myArtist, related, forceUpdateImage);
//        }
//
//    }
//
//
//    private ArtistData findArtistFromSpotify(Artist spotifyArtist)
//    {
//        ArtistPersisted artistByAlternativeID = artistWorker.getArtistByAlternativeID(DataSource.spotify, spotifyArtist.getId());
//        if (artistByAlternativeID != null)
//        {
//            return artistWorker.getArtist(artistByAlternativeID.getId());
//        }
//
//        Page<ArtistData> artists = artistWorker.findArtists(new ID(), spotifyArtist.getName(), PerformerType.attraction, null, 10);
//        for (ArtistData foundArtist : artists.getList())
//        {
//            if (foundArtist.nameMatches(spotifyArtist.getName()))
//            {
//                return foundArtist;
//            }
//        }
//
//        return null;
//
//    }
//
//
//    private void addRelated(ArtistData main, ArtistData second)
//    {
//        ArtistRelatedPersisted relationship = ArtistRelatedPersisted.find(mapper, main.getArtistID(), second.getArtistID(), DataSource.spotify.name());
//        if (relationship == null)
//        {
//            ArtistRelatedPersisted firstRshp = new ArtistRelatedPersisted(main.getArtistID(), second.getArtistID(), DataSource.spotify.name(), "");
//            mapper.save(firstRshp);
//        }
//        ArtistRelatedPersisted relationshipReverse = ArtistRelatedPersisted.find(mapper, second.getArtistID(), main.getArtistID(), DataSource.spotify.name());
//        if (relationshipReverse == null)
//        {
//            ArtistRelatedPersisted secondRshp = new ArtistRelatedPersisted(second.getArtistID(), main.getArtistID(), DataSource.spotify.name(), "");
//            mapper.save(secondRshp);
//        }
//    }
//

}
