package com.mobilitus.attractionscmd.bandsintown;

import com.mobilitus.attractionscmd.bandsintown.data.BandsInTownSearchResult;
import com.mobilitus.attractionscmd.scrapingbee.ScrapingBeeDocument;
import com.mobilitus.gogo.Cache;
import com.mobilitus.gogo.attractions.ArtistWorker;
import com.mobilitus.gogo.attractions.EventWorker;
import com.mobilitus.gogo.attractions.VenueWorker;
import com.mobilitus.gogo.search.AttractionSearch;
import com.mobilitus.gogo.search.EventSearch;
import com.mobilitus.util.data.attractions.ArtistData;
import com.mobilitus.util.data.attractions.VenueData;
import com.mobilitus.util.data.face.FaceData;
import com.mobilitus.util.data.schema.SchemaArtist;
import com.mobilitus.util.distributed.aws.cloudsearch.DefaultSearchConfig;
import com.mobilitus.util.distributed.aws.cloudsearch.SearchConfig;
import com.mobilitus.util.distributed.aws.kinesis.Producer;
import com.mobilitus.util.distributed.aws.memcached.ElastiCacheAdministrator;
import com.mobilitus.util.distributed.aws.s3.S3;
import com.mobilitus.util.distributed.dynamodb.AWSUtils;
import com.mobilitus.util.hexia.ID;
import com.mobilitus.util.hexia.Pair;
import org.apache.log4j.Logger;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedAsyncClient;

import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

/**
 * @author helgaw
 * @todo add class description.
 * @since 7/29/22 15:34
 */
public class BandsInTownArtistScraper extends BaseDocument
{
    private final ID id;
    private final DynamoDbEnhancedAsyncClient mapper;
    private S3 s3;
    private SearchConfig searchConfig;

    private Map<String, VenueData> venueMap = new HashMap<>(100);
    private Map<String, ArtistData> artistMap = new HashMap<>(100);
    private AttractionSearch gogoSearch;
    private EventSearch eventSearch;
    private EventWorker eventWorker;
    private ArtistWorker artistWorker;
    private VenueWorker venueWorker;

    private Producer toAttractionWorker;
    private Logger logger = Logger.getLogger(BandsInTownArtistScraper.class);

    private static ElastiCacheAdministrator cacheAdministrator = null;

    private String artistRoot = "https://www.bandsintown.com/a/";


    public BandsInTownArtistScraper()
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
        artistWorker = new ArtistWorker(id, new FaceData(), mapper, s3, searchConfig);
        eventWorker = new EventWorker(id, new FaceData(), mapper, s3, searchConfig);

        cacheAdministrator = new ElastiCacheAdministrator();
    }

    public BandsInTownArtistScraper(String scrapingbeeApiKey)
    {
        this();
        this.scrapingbeeApiKey = scrapingbeeApiKey;
    }


    public SchemaArtist searchForArtist(String name)
    {
        String encodedName = URLEncoder.encode(name);
        String url = "https://www.bandsintown.com/searchSuggestions?searchTerm=" + encodedName +"&typeOfPage=artistPage&fetchVenueApiData=true";

        Pair<Integer, String> result = ScrapingBeeDocument.getJson(url, scrapingbeeApiKey);
        if (result == null)
        {
            return null;
        }
        BandsInTownSearchResult bitResult = BandsInTownSearchResult.create(result.getValue());
        if (bitResult != null && bitResult.hasArtists())
        {
            return bitResult.getFirstArtist();
        }
        return null;
    }


//    public ArtistData createArtist(SchemaArtist artist)
//    {
//        String thirdPartyID = artist.getId();
////        artist.setArtistID(null);
//        if (artist.getName() == null || artist.getName().isEmpty())
//        {
//            return null;
//        }
//        ArtistData artistData = null;
//        ArtistData remoteArtist = null;
//        if (thirdPartyID != null && !thirdPartyID.isEmpty())
//        {
//             remoteArtist = getArtistData(thirdPartyID);
//
//        }
//        if (remoteArtist != null)
//        {
//            artistData = artistWorker.create(DataSource.bandsInTown, remoteArtist);
//        }
//        else
//        {
//            artistData = artistWorker.create(DataSource.bandsInTown, artist.toData());
//        }
//
//        Track.trackArtist(mapper, artistData.getArtistID(), artistData.getName(), "create", "New artist created via BandsInTown importer");
//
//        if (thirdPartyID != null && !thirdPartyID.isEmpty())
//        {
//            artistWorker.addSource(artistData.getArtistID(), DataSource.bandsInTown, thirdPartyID, "bandsInTown", artistRoot + thirdPartyID);
//        }
//        return artistData;
//    }


//    public SchemaArtist getArtist(String artistID)
//    {
//        if (artistID == null || artistID.isEmpty())
//        {
//            return null;
//        }
//        document = getDocument(artistRoot + artistID);
//
//        if (document != null)
//        {
//            SchemaArtist artist = new SchemaArtist();
//            artist.setId(artistID);
//            String name = document.getElementsByAttributeValue("property", "og:title").get(0).attr("content");
//            artist.setName(name);
//            String image = document.getElementsByAttributeValue("property", "og:image").get(0).attr("content");
//            if (!defaultImage(image))
//            {
//                artist.setImage(image);
//            }
//            String skurl = document.getElementsByAttributeValue("property", "og:url").get(0).attr("content");
//            artist.setUrl(cleanUrl(skurl));
//            SchemaArtist jsonArtist = getArtist(document);
//            if (jsonArtist != null)
//            {
//                artist.setGenre(jsonArtist.getGenre());
//                artist.setLocation(jsonArtist.getAddress());
//                artist.setInteractionStatistic(jsonArtist.getInteractionStatistic());
//            }
//            return artist;
//        }
//        return null;
//
//    }



    static
    {
        cacheAdministrator =  Cache.getCache();
    }

}
