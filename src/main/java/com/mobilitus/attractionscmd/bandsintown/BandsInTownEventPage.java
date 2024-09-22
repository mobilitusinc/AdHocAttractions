package com.mobilitus.attractionscmd.bandsintown;

import com.mobilitus.util.data.schema.SchemaArtist;
import com.mobilitus.util.data.schema.SchemaEvent;
import com.mobilitus.util.data.schema.SchemaLocation;
import com.mobilitus.util.data.schema.SchemaOffers;
import com.mobilitus.util.data.ticketMaster.EventGhettoData;
import com.mobilitus.util.hexia.StrUtil;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

/**
 * @author helgaw
 * @todo add class description.
 * @since 3/7/22 12:16
 */
public class BandsInTownEventPage extends BaseDocument
{
    private String url;

    private static final Logger logger = Logger.getLogger(BandsInTownEventPage.class);
    private SchemaEvent schemaEvent;

    private EventGhettoData event;
    private SchemaArtist artist;
    private SchemaLocation venue;

    public BandsInTownEventPage(String apikey)
    {
        super(apikey);
    }

    public BandsInTownEventPage()
    {


    }


    public static BandsInTownEventPage create(String url, String apikey)
    {
        BandsInTownEventPage page = new BandsInTownEventPage(apikey);
        page.url = url;
        page.document = page.getDocument(url);
        return page;
    }


    public static BandsInTownEventPage createViaJsoup(String url, String apikey)
    {
        BandsInTownEventPage page = new BandsInTownEventPage();
        if (url.contains("?"))
        {
            page.url = url.substring(0, url.indexOf("?"));
        }
        else
        {
            page.url = url;
        }

        try
        {
            page.document = Jsoup.connect(url)
                    .userAgent("Mozilla/5.0 (iPhone; CPU iPhone OS 14_4_2 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/14.0.3 Mobile/15E148 Safari/604.1")
                    //                         .referrer("http://www.google.com")
                    .get();

            if (page.document == null)
            {
               System.out.println(url + " document is null!");
                return null;
            }
//            System.out.println(page.document);
        }
        catch (IOException e)
        {
        }
        return page;
    }



    public String getTitle()
    {
        if (document == null)
        {
            return null;
        }

        Elements allEvents = document.getElementsByClass("microformat");
        for (Element events : allEvents)
        {

            String json = events.data();
//            System.out.println(StrUtil.formatAsJson(json));
            List<SchemaEvent> schemaEvents = SchemaEvent.createList(json);
            for (SchemaEvent eventOn : schemaEvents)
            {
                return eventOn.getName();
            }
        }
        return null;
    }


    public String getImage()
    {
        if (document == null)
        {
            return null;
        }

        Elements allEvents = document.getElementsByClass("microformat");
//        System.out.println(allEvents);
        for (Element events : allEvents)
        {

            String json = events.data();
            List<SchemaEvent> schemaEvents = SchemaEvent.createList(json);
            for (SchemaEvent eventOn : schemaEvents)
            {
                return "https://images.sk-static.com/images/" + eventOn.getImage();
            }
        }
        return null;
    }

    public List<String> getWebPage()
    {
        if (document == null)
        {
            return Collections.emptyList();
        }

        Elements allEvents = document.getElementsByClass("microformat");
//        System.out.println(allEvents);
        for (Element events : allEvents)
        {

            String json = events.data();
            List<SchemaEvent> schemaEvents = SchemaEvent.createList(json);
            for (SchemaEvent eventOn : schemaEvents)
            {
                if (eventOn.getLocation() != null && eventOn.getLocation().getSameAs() != null)
                {
                    return eventOn.getLocation().getSameAs();
                }
            }
        }
        return Collections.emptyList();

    }

    public List<SchemaArtist> getPerformers()
    {
        if (document == null)
        {
            return null;
        }

        Elements allEvents = document.getElementsByClass("microformat");
//        System.out.println(allEvents);
        for (Element events : allEvents)
        {

            String json = events.data();
            List<SchemaEvent> schemaEvents = SchemaEvent.createList(json);
            for (SchemaEvent eventOn : schemaEvents)
            {
                return eventOn.getPerformers();
            }
        }
        return Collections.emptyList();
    }

    public DateTime getEnd(DateTimeZone timezone)
    {
        if (document == null)
        {
            return null;
        }

        Elements allEvents = document.getElementsByClass("microformat");

        for (Element events : allEvents)
        {
            String json = events.data();

//            System.out.println(StrUtil.formatAsJson(json));
            List<SchemaEvent> schemaEvents = SchemaEvent.createList(json);
            for (SchemaEvent eventOn : schemaEvents)
            {
                return eventOn.getEndDate(timezone);
            }
        }
        return null;

    }

    public DateTime getShowTime(DateTimeZone timezone)
    {
        if (document == null)
        {
            return null;
        }

        Elements allEvents = document.getElementsByClass("microformat");

        for (Element events : allEvents)
        {
            String json = events.data();

//            System.out.println(StrUtil.formatAsJson(json));
            List<SchemaEvent> schemaEvents = SchemaEvent.createList(json);
            for (SchemaEvent eventOn : schemaEvents)
            {
                return eventOn.getStartDate(timezone);
            }
        }
        return null;

    }

    public List<SchemaOffers> getOffer()
    {
        if (document == null)
        {
            return null;
        }

        Elements allEvents = document.getElementsByClass("microformat");

        if (allEvents == null)
        {
            return null;
        }
        for (Element events : allEvents)
        {
            String json = events.data();
//            System.out.println(StrUtil.formatAsJson(json));
            List<SchemaEvent> schemaEvents = SchemaEvent.createList(json);
            for (SchemaEvent eventOn : schemaEvents)
            {
                return eventOn.getOffers();
            }
        }
        return null;
    }

    public String getEventID()
    {
        int startIndex = url.lastIndexOf("/");
        if (url.contains("?"))
        {
            int lastIndex = url.indexOf("?");
            return url.substring(startIndex, lastIndex);
        }
        else
        {
            return url.substring(startIndex);
        }
    }



    public String getVenueID()
    {
        if (document == null)
        {
            return null;
        }

        Elements elements = document.getElementsByClass("venue-info");
        if (elements == null || elements.size() == 0)
        {
            return null;
        }
        Elements urlElements = elements.get(0).getElementsByClass("url");
        for (int i = 0; i < urlElements.size(); i++)
        {
            Element element = urlElements.get(i);
            String url = element.attr("href");
            if (url != null && url.startsWith("/venues/"))
            {
                String venueID = url.replace("/venues/", "");
                return venueID.trim();
            }
        }
        return null;

    }


    public Integer getVenueCapacity()
    {

        Elements elements = document.getElementsByClass("capacity");
        if (elements == null || elements.size() == 0)
        {
            return null;
        }

        String cap = elements.get(0).text();
        cap = cap.replace("Capacity:", "");
        cap = cap.replace(",", "");
//        cap = cap.replace(".", "");

        try
        {
            return Integer.parseInt(cap.trim());
        }
        catch (NumberFormatException e)
        {

        }

        return null;


    }


    public String getWebpage()
    {
        if (document == null)
        {
            return null;
        }

        Elements elements = document.getElementsByClass("venue-info");
        if (elements == null || elements.size() == 0)
        {
            return null;
        }

        Elements urlElements = elements.get(0).getElementsByClass("url");
        for (int i = 0; i < urlElements.size(); i++)
        {
            Element element = urlElements.get(i);
            String url = element.attr("href");
            if (url != null && !url.startsWith("/venues/"))
            {
                if (url.startsWith("http"))
                {
                    return url.trim();
                }
                else
                {
                    return "https://" + url.trim();
                }
            }
        }
        return null;
    }



    private void parseDocument()
    {
        if (document == null)
        {
            return;
        }

        Elements allEvents = document.getElementsByTag("script");

        for (Element events : allEvents)
        {
            String json = events.data();
            if (json == null || json.isEmpty())
            {
                continue;
            }
            if (json.startsWith("window.__data"))
            {
                json = json.substring(json.indexOf("{"), json.lastIndexOf("}") + 1);
                BandsInTownPageInfo pageInfo = BandsInTownPageInfo.create(json);
                logger.info("Found " + pageInfo.getTitle() );
                event = pageInfo.toData();
//                logger.info (StrUtil.formatAsJson(json));
//                logger.info("");

            }
            else if ( events.attr("type") != null && events.attr("type").equalsIgnoreCase("application/ld+json"))
            {
                json = events.data();
                if (json.contains("\"@type\":\"MusicEvent\""))
                {
                    logger.info (StrUtil.formatAsJson(json));
//                    logger.info("");
//                    schemaEvent = SchemaEvent.create(json);
                }
                else if (json.contains("\"@type\":\"MusicGroup\""))
                {
                    logger.info (StrUtil.formatAsJson(json));
//                    logger.info("");
//                    artist = SchemaArtist.create(json);
                }
                else
                {
                    logger.info (StrUtil.formatAsJson(json));
//                    logger.info("");
                }
            }
            else
            {
                continue;
            }
        }
    }

    public EventGhettoData toData ()
    {
        parseDocument();

        if (event == null)
        {
            if (schemaEvent != null)
            {
                return schemaEvent.toData();
            }
            return null;
        }
        EventGhettoData data = event;
//        data.setMainArtist(artist.toData());
        return data;
    }

}
