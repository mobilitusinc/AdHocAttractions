package com.mobilitus.attractionscmd.scrapingbee;

import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;
import com.mobilitus.util.hexia.Pair;
import com.mobilitus.util.hexia.StrUtil;
import com.mobilitus.util.hexia.json.serialization.DateTimeSerialization;
import com.mobilitus.util.hexia.json.serialization.DateTimeZoneSerialization;
import com.mobilitus.util.hexia.json.serialization.DurationSerialization;
import com.mobilitus.util.httputil.HTTPUtil;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.Duration;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.lang.reflect.Modifier;
import java.net.URLEncoder;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @author helgaw
 * @todo add class description.
 * @since 9/12/23 11:52
 */
public class ScrapingBeeDocument
{
    private Integer status;
    private String body;
    private List<CookieData> cookies;
    @SerializedName("evaluate_results")
    private List evaluateResults;
    @SerializedName("js_scenario_report")
    private Object jsScenarioReport;
    private Map<String, String> headers;
    private String type;
    private List iframes;
    private List<SubDocument> xhr;
    private Long cost;
    @SerializedName("initial-status-code")
    private Integer statusCode;

    @SerializedName("resolved-url")
    private String resolvedUrl;
    private MetaData metadata;


    private static final Logger logger = Logger.getLogger(ScrapingBeeDocument.class);

    public static Pair<Integer, String> getJson(String _url, String apikey)
    {
        if (_url == null  || _url.isEmpty())
        {
            return null;
        }
        String url = "https://app.scrapingbee.com/api/v1/?api_key="+ apikey + "&" +
                     "url=" + URLEncoder.encode(_url) +
                                     //                "&wait=1000" +
                                     //                "&return_page_source=true" +
                     "&block_resources=true" +
                     "&wait_browser=load" +
                     "&block_ads=true" +
                     "&forward_headers=true" +
                     "&json_response=true";
        List<Pair<String, String>> headers = Collections.singletonList(new Pair<>("Spb-Accept-Language", "en"));
        Pair<Integer, String> json = HTTPUtil.getStatusAndBody(url, headers,60000);

        //        logger.info(StrUtil.formatAsJson(json.getValue()));
        if (json == null)
        {
            return null;
        }
        //        logger.info(_url + " " + json.getKey());
        if (json.getKey() >= 500)
        {
//            logger.info (StrUtil.formatAsJson(json.getValue()));
            // try again with more time and fewer restrictions
            url = "https://app.scrapingbee.com/api/v1/?api_key="+ apikey + "&" +
                  "url=" + URLEncoder.encode(_url) +
                  "&wait=10000" +
                                  //                "&return_page_source=true" +
                  "&block_resources=false" +
                  "&wait_browser=load" +
                  "&block_ads=true" +
                  "&forward_headers=true" +
                  "&json_response=true";
            json = HTTPUtil.getStatusAndBody(url, headers,60000);

        }
        return json;
    }


    public static ScrapingBeeDocument create(String _url, String apikey)
    {
        if (_url == null  || _url.isEmpty())
        {
            return null;
        }
        String url = "https://app.scrapingbee.com/api/v1/?api_key="+ apikey + "&" +
                "url=" + URLEncoder.encode(_url) +
//                "&wait=1000" +
//                "&return_page_source=true" +
                "&block_resources=true" +
                "&wait_browser=load" +
                "&block_ads=true" +
                "&forward_headers=true" +
                "&json_response=true";
        List<Pair<String, String>> headers = Collections.singletonList(new Pair<>("Spb-Accept-Language", "en"));
        Pair<Integer, String> json = HTTPUtil.getStatusAndBody(url, headers,30000);
//        logger.info(StrUtil.formatAsJson(json.getValue()));
        if (json == null)
        {
            return null;
        }
//        logger.info(_url + " " + json.getKey());
        if (json.getKey() >= 500)
        {
            logger.info (StrUtil.formatAsJson(json.getValue()));
            // try again with more time and fewer restrictions
            url = "https://app.scrapingbee.com/api/v1/?api_key="+ apikey + "&" +
                    "url=" + URLEncoder.encode(_url) +
                    "&wait=10000" +
//                "&return_page_source=true" +
                    "&block_resources=false" +
                    "&wait_browser=load" +
                    "&block_ads=true" +
                    "&forward_headers=true" +
                    "&json_response=true";
            json = HTTPUtil.getStatusAndBody(url, headers,60000);

        }

        ScrapingBeeDocument doc = load(json.getValue());
        if (json == null)
        {
            return null;
        }
//        logger.info(_url + " " + json.getKey());
        if (json.getKey() >= 500)
        {
            return null;
        }
        if (doc == null)
        {
            return null;
        }
        doc.setStatus(json.getKey());
        return doc;
    }

    private void setStatus(Integer key)
    {
        this.status = key;
    }


    public Integer getStatus()
    {
        return status;
    }

    private static ScrapingBeeDocument load(String json)
    {
        if (json == null)
        {
            return null;
        }
        GsonBuilder gsonBuilder = new GsonBuilder().excludeFieldsWithModifiers(Modifier.STATIC, Modifier.TRANSIENT);

        // DateTime
        gsonBuilder.registerTypeAdapter(DateTime.class, new DateTimeSerialization());
        gsonBuilder.registerTypeAdapter(Duration.class, new DurationSerialization());
        gsonBuilder.registerTypeAdapter(DateTimeZone.class, new DateTimeZoneSerialization());

        try
        {
            ScrapingBeeDocument result = gsonBuilder.create().fromJson(json, ScrapingBeeDocument.class);
            if (result != null)
            {
                return result;
            }
        }
        catch (Exception e)
        {
            logger.error("exception " + StrUtil.stack2String(e) + "\n json is " + StrUtil.formatAsJson(json));
        }
        return null;

    }
    public String getBody()
    {
        return body;
    }

    public Document getDocument()
    {
        try
        {
            return Jsoup.parse(body);
        }
        catch (Exception e)
        {
            logger.error(StrUtil.stack2String(e));

        }
        return null;
    }


    public String getTitle()
    {
        if (metadata != null)
        {
            return metadata.getTitle();
        }
        return null;
    }

    public String getImage()
    {
        if (metadata != null)
        {
            return metadata.getImage();
        }
        return null;
    }
}
