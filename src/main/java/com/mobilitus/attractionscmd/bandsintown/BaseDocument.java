package com.mobilitus.attractionscmd.bandsintown;

import com.mobilitus.attractionscmd.scrapingbee.ScrapingBeeDocument;
import com.mobilitus.util.hexia.StrUtil;
import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

/**
 * @author helgaw
 * @todo add class description.
 * @since 8/31/22 14:57
 */
public class BaseDocument
{
    protected Document document;
    private static final Logger logger = Logger.getLogger(BaseDocument.class);
    private static final String userAgent = "Mozilla/5.0 (iPhone; CPU iPhone OS 14_4_2 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/14.0.3 Mobile/15E148 Safari/604.1";
    private static final String referrer = null;
    protected String scrapingbeeApiKey;

    public BaseDocument()
    {
    }

    public BaseDocument(String scrapingbeeApiKey)
    {
        this.scrapingbeeApiKey = scrapingbeeApiKey;
    }

    protected Document getDocument(String url)
    {
        if (scrapingbeeApiKey != null)
        {
            return getDocumentViaScrapingBee(url);
        }
        else
        {
            return getDocumentViaJSoup(url);
        }
    }

    private Document getDocumentViaScrapingBee(String url)
    {
        ScrapingBeeDocument document = ScrapingBeeDocument.create(url, scrapingbeeApiKey);
        if (document != null)
        {
            if (document.getStatus() == 200)
            {
                return document.getDocument();
            }
            else
            {
                logger.error("Could not scrape url '" + url + "' via scrapingbee. Status code: " + document.getStatus() + " body: " + document.getBody());
                return document.getDocument();
            }
        }
        logger.error ("Could not scrape url '" + url + "' via scrapingbee ");
        return null;


    }

    protected Document getDocumentViaJSoup(String url)
    {
        try
        {
            if (referrer != null)
            {
                //Get Document object after parsing the html from given url.
                Document document = Jsoup.connect(url)
                        .userAgent(userAgent)
                        .referrer(referrer)
                        .get();
                return document;
            }
            else
            {
                //Get Document object after parsing the html from given url.
                Document document = Jsoup.connect(url)
                        .userAgent(userAgent)
                        //                         .referrer("http://www.google.com")
                        .get();
                return document;
            }
        }
        catch (IOException e)
        {
            logger.error(StrUtil.stack2String(e));

        }
        return null;

    }

    protected String getFromMetaElement(String name, String value)
    {
        Elements metaElements = document.getElementsByTag("meta");
        for (Element metaElement : metaElements)
        {
            if (metaElement.attr(name).equals(value))
            {
                String str = metaElement.attr("content").trim();
                if (str == null || str.equalsIgnoreCase("null"))
                {
                    return null;
                }
                return str;
            }
        }
        return null;
    }



    protected String getDescriptionFromMeta()
    {
        return getFromMetaElement("name", "description");
    }



    protected String getOgImageFromMeta()
    {
        return getFromMetaElement("property", "og:image");
    }




}
