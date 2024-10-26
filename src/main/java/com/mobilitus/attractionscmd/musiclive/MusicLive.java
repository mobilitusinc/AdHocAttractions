package com.mobilitus.attractionscmd.musiclive;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mobilitus.gogo.Cache;
import com.mobilitus.gogo.attractions.ArtistWorker;
import com.mobilitus.gogo.attractions.EventWorker;
import com.mobilitus.gogo.search.AttractionSearch;
import com.mobilitus.gogo.search.EventSearch;
import com.mobilitus.util.data.attractions.ArtistData;
import com.mobilitus.util.data.aws.cloudsearch.GogoSearchData;
import com.mobilitus.util.data.gogo.SearchAttractionFilter;
import com.mobilitus.util.data.schema.SchemaEvent;
import com.mobilitus.util.distributed.aws.cloudsearch.DefaultSearchConfig;
import com.mobilitus.util.distributed.aws.cloudsearch.SearchConfig;
import com.mobilitus.util.cache.MemcachedAdministrator;

import com.mobilitus.util.distributed.dynamodb.AWSUtils;
import com.mobilitus.util.hexia.KeyValue;
import com.mobilitus.util.hexia.Pair;
import com.mobilitus.util.hexia.json.serialization.DateTimeSerialization;
import com.mobilitus.util.hexia.json.serialization.DateTimeZoneSerialization;
import com.mobilitus.util.hexia.json.serialization.DurationSerialization;
import com.mobilitus.util.hexia.location.CountryCode;
import com.mobilitus.util.hexia.pagination.Page;
import com.mobilitus.util.httputil.HTTPUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.Duration;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedAsyncClient;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * @author helgaw
 * @since 7/31/24 10:06
 */
public class MusicLive
{
    private static MemcachedAdministrator cacheAdministrator = null;
    private final AwsCredentialsProvider credentials;
    private final DynamoDbEnhancedAsyncClient mapper;
    private static final Logger logger = Logger.getLogger(MusicLive.class);
    private final AttractionSearch gogoSearch;
    private final EventWorker eventWorker;
    private final ArtistWorker artistWorker;
    private final EventSearch eventSearch;


    public MusicLive(AwsCredentialsProvider credentials, String memcache)
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

        eventWorker = new EventWorker( mapper, AWSUtils.getS3(), searchConfig);
        artistWorker = new ArtistWorker(null, null, mapper, AWSUtils.getS3(), searchConfig);
    }


    public void verifyAustralianNess()
    {
        String[] toCheck = {"Hawkwind",
                            "THE BLACK CHARADE",
                            "FELL OUT BOY",
                            "Declan McKenna",
                            "Cobra The Impaler",
                            "Fat Freddy's Drop",
                            "Mantra",
                            "Ayria",
                            "Dream Wife",
                            "Tuk Smith",
                            "The Restless Hearts",
                            "The The",
                            "Shang",
                            "DAÐI FREYR",
                            "Ironwood",
                            "Southbound",
                            "Bloom",
                            "Lee Scott",
                            "Black Josh",
                            "Maes",};

        List<KeyValue> artistCountry = new ArrayList<>();
        for (String artist : toCheck)
        {
            SearchAttractionFilter filter = new SearchAttractionFilter();
            filter.searchArtists(true);

            String offset = null;
            Integer limit = 20;

            Page<GogoSearchData> artists = gogoSearch.findByName(artist, filter, offset, limit);
            Boolean found = false;
            Boolean inAu = false;
            for (GogoSearchData anArtist : artists)
            {
                ArtistData artistData = artistWorker.getArtist(anArtist.getId());
                if (!artistData.nameMatches(artist))
                {
                    continue;
                }
                CountryCode cc = null;
                if (artistData.getHome() != null)
                {
                    cc = artistData.getHome().getCountryCode();
                }
                if (cc != null)
                {
                    found = true;
                    if (cc == CountryCode.au)
                    {
                        inAu = true;
                        artistCountry.add(new KeyValue(artist,  "https://dashboard.promogogo.com/go/adminartists.do#!/artist/" + anArtist.getId()));
                    }
                    logger.info("Found artist " + anArtist.getDisplayName()+ " from " + cc + " for " + artist +  " " + artistData.getUpcoming() + "/" + artistData.getAll() +  "   https://dashboard.promogogo.com/go/adminartists.do#!/artist/" + anArtist.getId()) ;
                }
            }
            if (!found)
            {
                logger.info("Did not find artist " + artist + " from  https://dashboard.promogogo.com/go/adminartists.do#!/artist/" + artists.get(0).getId());
            }
        }

        logger.info("");
        for (KeyValue kv : artistCountry)
        {
            logger.info("Artist " + kv.getKey() + " is from Australia " + kv.getValue());
        }

    }

    public void measurePageLoadTimes(String _url)
    {

        String[] countryList = Locale.getISOCountries();
        String url = "https://data.promogogo.com/p/events/abroad.do?light=true&limit=50&country=au&visitcountry=";
        logger.info("countrycode\t country                      \ttime\t totalEvents\tloaded Events");

        for (String country : countryList)
        {
            String visitUrl = url + country;

            DateTime start = DateTime.now();
            Pair<Integer, String> response = HTTPUtil.getJsonWithStatus(visitUrl);
            if (response.getKey() == 200)
            {
                DateTime end = DateTime.now();

                Page<SchemaEvent> page = jsonToSchemaEvents(response.getValue());
                if (page.getTotalSize() > 2000 || page.getTotalSize() == 0)
                {
                    continue;
                }
                logger.info(country + "\t " + StringUtils.rightPad(countryName(country), 30) + "\t" + (new Duration(start, end)).getMillis() + "\t" + page.getTotalSize() + "\t" + page.getList().size() + " " ) ;
            }
        }


        String offset = null;
    }

    private String countryName(String countryCode)
    {
        Locale locale = new Locale.Builder().setRegion(countryCode).build();
        return locale.getDisplayCountry();
    }



    private Page<SchemaEvent> jsonToSchemaEvents(String json)
    {

        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(DateTime.class, new DateTimeSerialization(DateTimeZone.UTC));
        gsonBuilder.registerTypeAdapter(Duration.class, new DurationSerialization());
        gsonBuilder.registerTypeAdapter(DateTimeZone.class, new DateTimeZoneSerialization());
        Gson gson = gsonBuilder.create();
        Page page = gson.fromJson(json, Page.class);
        return page;
    }

}
