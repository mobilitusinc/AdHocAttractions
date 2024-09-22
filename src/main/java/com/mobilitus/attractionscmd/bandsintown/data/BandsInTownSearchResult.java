package com.mobilitus.attractionscmd.bandsintown.data;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mobilitus.util.data.schema.SchemaArtist;
import com.mobilitus.util.hexia.json.serialization.DateTimeSerialization;
import com.mobilitus.util.hexia.json.serialization.DateTimeZoneSerialization;
import com.mobilitus.util.hexia.json.serialization.DurationSerialization;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.Duration;

/**
 * @author helgaw
 * @since 9/18/24 18:43
 */
public class BandsInTownSearchResult
{
    /**
     * {
     *   "body" : {
     *     "artists" : [ {
     *       "name" : "06 Boys",
     *       "id" : 15539038,
     *       "trackerText" : "85 Followers",
     *       "imageSrc" : "https://photos.bandsintown.com/thumb/16562693.jpeg",
     *       "properlySizedImageURL" : "https://media.bandsintown.com/300x300/16562693.webp",
     *       "verifiedSrc" : null,
     *       "verified" : false,
     *       "href" : "https://www.bandsintown.com/a/15539038-06-boys?came_from=257&utm_medium=web&utm_source=artist_page&utm_campaign=search_bar"
     *     } ],
     *     "events" : [ ],
     *     "festivals" : [ ],
     *     "venues" : [ ]
     *   },
     *   "cookies" : [ {
     *     "name" : "_csrf",
     *     "value" : "GjqAC2YEqdcXAFicrsrL3U5l",
     *     "domain" : "www.bandsintown.com",
     *     "path" : "/",
     *     "expires" : -1,
     *     "size" : 29,
     *     "httpOnly" : false,
     *     "secure" : false,
     *     "session" : true,
     *     "sameParty" : false,
     *     "sourceScheme" : "Secure",
     *     "sourcePort" : 443
     *   } ],
     *   "evaluate_results" : [ ],
     *   "js_scenario_report" : { },
     *   "headers" : {
     *     "cache-control" : "public, max-age=300",
     *     "content-length" : "424",
     *     "content-type" : "application/json; charset=utf-8",
     *     "date" : "Wed, 18 Sep 2024 18:39:53 GMT",
     *     "etag" : "W/\"1a8-+y4Yh0jWaA4b8EN8clMSRX5HDnI\"",
     *     "referrer-policy" : "strict-origin-when-cross-origin",
     *     "server" : "nginx/1.25.4",
     *     "set-cookie" : "_csrf=GjqAC2YEqdcXAFicrsrL3U5l; Path=/",
     *     "strict-transport-security" : "max-age=31536000;includeSubDomains\nmax-age=31536000; includeSubDomains; preload",
     *     "vary" : "Accept-Encoding,X-Auth-Type",
     *     "x-auth-type" : "simple",
     *     "x-frame-options" : "Deny",
     *     "x-powered-by" : "Bandsintown"
     *   },
     *   "type" : "json",
     *   "iframes" : [ ],
     *   "xhr" : [ ],
     *   "cost" : 5,
     *   "initial-status-code" : 200,
     *   "resolved-url" : "https://www.bandsintown.com/searchSuggestions?searchTerm=06+Boys&typeOfPage=artistPage&fetchVenueApiData=true"
     * }
     */
    private BandsInTownSearchResultBody body;
//    private BandsInTownSearchResultCookies[] cookies;
//    private BandsInTownSearchResultHeaders headers;



    public boolean hasArtists()
    {
        if (body == null)
        {
            return false;
        }
        return body.hasArtists();
    }

    public SchemaArtist getFirstArtist()
    {
       if (body == null)
       {
           return null;
       }
       if (body.hasArtists())
       {
           return body.getArtists().get(0);
       }
       return null;
    }


    public static BandsInTownSearchResult create(String json)
    {

        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(DateTime.class, new DateTimeSerialization(DateTimeZone.UTC));
        gsonBuilder.registerTypeAdapter(Duration.class, new DurationSerialization());
        gsonBuilder.registerTypeAdapter(DateTimeZone.class, new DateTimeZoneSerialization());
        Gson gson = gsonBuilder.create();
        BandsInTownSearchResult result = gson.fromJson(json, BandsInTownSearchResult.class);
        return result;
    }

}
