package com.mobilitus.attractionscmd.festivals.icelandairwaves;

import com.mobilitus.util.data.attractions.AttractionType;
import com.mobilitus.util.data.aws.kinesis.KinesisStream;
import com.mobilitus.util.data.pusher.MessageType;
import com.mobilitus.util.data.pusher.PusherMessage;
import com.mobilitus.util.data.schema.SchemaArtist;
import com.mobilitus.util.data.schema.SchemaEvent;
import com.mobilitus.util.data.schema.SchemaImage;
import com.mobilitus.util.data.schema.SchemaLocation;
import com.mobilitus.util.data.utils.ActivityStream.internal.v2.PropertyType;
import com.mobilitus.util.distributed.aws.kinesis.Producer;
import com.mobilitus.util.distributed.dynamodb.AWSUtils;
import com.mobilitus.util.hexia.StrUtil;
import com.mobilitus.util.hexia.location.CountryCode;
import com.mobilitus.util.hexia.location.LocationUtil;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author helgaw
 * @todo add class description.
 * @since 4/5/20 17:55
 */
public class AirwavesScraper
{
    private  SchemaArtist airwaves;

    private Producer toCreator;

    private static Logger logger = Logger.getLogger(AirwavesScraper.class);
    Map<String, SchemaLocation> venues = new HashMap<>(100);
    Map<String, SchemaArtist> artists = new HashMap<>(100);


    public AirwavesScraper()
    {
        AwsCredentialsProvider credentialsProvider = AWSUtils.getCredentialsProvider();

        Map<String, SchemaLocation> venues = createVenueMap();
        Map<String, SchemaArtist> artists = createArtistMap();

        // for testing and developing (worker on helgas dev)
        toCreator = new Producer(KinesisStream.dev, credentialsProvider);

        // for prod
        toCreator = new Producer(KinesisStream.toSchema, credentialsProvider);

    }

    private Map<String, SchemaArtist> createArtistMap()
    {
        airwaves = new SchemaArtist();
        airwaves.setId("iceland-airwaves");
        airwaves.setName("Iceland Airwaves");
        airwaves.setLocation(CountryCode.is);
        airwaves.setAttractionType(AttractionType.music);
        airwaves.addInternalType("festival");
        airwaves.setSource("IcelandAirwaves");
        airwaves.setWebpage("https://icelandairwaves.is");
        airwaves.addAttribute("icelandAirwaves", "IcelandAirwaves2024");
        artists.put("iceland airwaves", airwaves);
        return artists;
    }

    private Map<String, SchemaLocation> createVenueMap()
    {
        // just hammer it in, there are only of them and limited info on the web
        //
        // if the location of the event is not very accurate and the source can't really be trusted use
        // the following code to add a hint to the event
        //
        // if (venue.addHint("location", "approximate")
        //
        // this means that the location is not very accurate, possibly without a street address
        //
        // or
        //
        // venue.addHint("latlng", "from_event"))
        //
        // this is used from the facebook importer when there is no venue set on the event, just a point on a map (that is often inaccurate)
        //
        // both will result in a more lenient location check with a bigger radius if there is a location point present

        SchemaLocation artMuseum = new SchemaLocation();
        artMuseum.setId("art-museum");
        artMuseum.setName("Art Museum");
        artMuseum.setStreetAddress("Tryggvagata 17");
        artMuseum.setPostalCode("101");
        artMuseum.setAddressLocality("Reykjavik");
        artMuseum.setAddressCountry("IS");
        artMuseum.addAttribute("accessibility", "fully accessible");
        artMuseum.addSameAs("https://www.listasafn.is");
        artMuseum.setSource("IcelandAirwaves");

        venues.put("art museum", artMuseum);

        SchemaLocation kolaport = new SchemaLocation();
        kolaport.setId("kolaport");
        kolaport.setName("Kolaport");
        kolaport.setStreetAddress("Tryggvagata 19");
        kolaport.setPostalCode("101");
        kolaport.setAddressLocality("Reykjavik");
        kolaport.setAddressCountry("IS");
        kolaport.addAttribute("accessibility", "fully accessible");
        kolaport.addSameAs("https://www.kolaportid.is");
        kolaport.setSource("IcelandAirwaves");
        venues.put("kolaport", kolaport);

        SchemaLocation gaukurinn = new SchemaLocation();
        gaukurinn.setId("gaukurinn");
        gaukurinn.setName("Gaukurinn");
        gaukurinn.setStreetAddress("Tryggvagata 22");
        gaukurinn.setPostalCode("101");
        gaukurinn.setAddressLocality("Reykjavik");
        gaukurinn.setAddressCountry("IS");
        gaukurinn.addSameAs("https://www.gaukurinn.is");
        gaukurinn.setSource("IcelandAirwaves");
        gaukurinn.addAttribute("accessibility", "limited; stairs");

        venues.put("gaukurinn", gaukurinn);

        SchemaLocation idno = new SchemaLocation();
        idno.setId("idno");
        idno.setName("Iðnó");
        idno.setStreetAddress("Vonarstræti 3");
        idno.setPostalCode("101");
        idno.setAddressLocality("Reykjavik");
        idno.setAddressCountry("IS");
        idno.addSameAs("https://www.idno.is");
        idno.setSource("IcelandAirwaves");
        idno.addAttribute("accessibility", "fully accessible");

        venues.put("idno", idno);

        SchemaLocation nasa = new SchemaLocation();
        nasa.setId("nasa");
        nasa.setName("Nasa");
        nasa.setStreetAddress("Thorvaldsensstræti 2");
        nasa.setPostalCode("101");
        nasa.setAddressLocality("Reykjavik");
        nasa.setAddressCountry("IS");
        nasa.setSource("IcelandAirwaves");
        nasa.addAttribute("accessibility", "fully accessible");

        venues.put("nasa", nasa);

        SchemaLocation frikirkjan = new SchemaLocation();
        frikirkjan.setId("frikirkjan");
        frikirkjan.setName("Fríkirkjan");
        frikirkjan.setStreetAddress("Fríkirkjuvegur 5");
        frikirkjan.setPostalCode("101");
        frikirkjan.setAddressLocality("Reykjavik");
        frikirkjan.setAddressCountry("IS");
        frikirkjan.addAttribute("accessibility", "fully accessible");
        frikirkjan.setSource("IcelandAirwaves");
        venues.put("frikirkjan", frikirkjan);
        venues.put("fríkirkjan", frikirkjan);
        // this is just all put in a map because the same venues are reused over and over again
        return venues;
    }


    public void scrapeEvents()
    {
        Integer index = 0;
        Integer total = 0;
        String[] urls = { "https://icelandairwaves.is/schedule-thursday/",
                           "https://icelandairwaves.is/schedule-friday/",
                           "https://icelandairwaves.is/schedule-saturday/"
                        };

        DateTime dayOf = new DateTime(2024, 11, 7, 0, 0, 0);
        for (String url : urls)
        {
            Document htmlPage = getDocument (url);
            List<SchemaEvent> events = parsePage(htmlPage, dayOf);
            total += events.size();
            for (SchemaEvent event : events)
            {
                logger.info(index + "/" + total + "\n" + StrUtil.formatAsJson(event.toJson()));
                String pushID = event.getId() + "icelandArwaves";
                PusherMessage msg = wrapEnvelope(event, pushID);
                //                updateDB(entry.getId(), msg.getTitle(),  "in pipeline", EntryStatus.inPipeline);
                try
                {
                    toCreator.sendAsObj(pushID, MessageType.eventUpdated.name(), msg.toJson());
                }
                catch (Exception e)
                {
                    logger.error(event.getName());
                    logger.error(StrUtil.stack2String(e));
                }
                index++;
            }
            dayOf = dayOf.plusDays(1);
        }
        toCreator.flush();
    }

    private List<SchemaEvent> parsePage(Document document, DateTime dayOf)
    {
        // the feed is a html table with no markup.

        // the cells hold the name of the artist + the url
        List<SchemaEvent> events = new ArrayList<>(100);
        Elements tableLines = document.getElementsByTag("tr");

        // first line is a header
        for (int i = 1; i < tableLines.size(); i++)
        {
            Element line = tableLines.get(i);
            List<SchemaEvent> lineEvents = parseLine(line, dayOf);
            if (lineEvents != null && !lineEvents.isEmpty())
            {
                events.addAll(lineEvents);
            }
        }
        return events;
    }

    private List<SchemaEvent> parseLine(Element line, DateTime dayOf)
    {
        // the feed is a html table with no markup.
        // we need to know that each venue has a column
        // column 1 is the time
        // column 2 is the the art museum
        // column 3 is the kolaport
        // column 4 is the gaukurinn
        // column 5 is the idno
        // <th data-class="expand" class=" wdtheader sort FirstCol" style="">TIME</th>
        // <th class=" wdtheader sort ." style="">ART MUSEUM</th>
        // <th class=" wdtheader sort " style="">KOLAPORT</th>
        // <th class=" wdtheader sort " style="">GAUKURINN</th>
        // <th class=" wdtheader sort " style="">IDNO</th>
        // <th class=" wdtheader sort " style="">wdt_created_by</th>
        // <th class=" wdtheader sort " style="">wdt_created_at</th>
        // <th class=" wdtheader sort " style="">wdt_last_edited_by</th>
        // <th class=" wdtheader sort " style="">wdt_last_edited_at</th>
        // <th class=" wdtheader sort " style="">NASA</th>
        // <th class=" wdtheader sort " style="">FRÍKIRKJAN</th>
        //</tr>
        //<tr id="table_7_row_0" data-row-index="0">
        Elements headers =  line.parent().parent().getElementsByTag("th");

        List<SchemaEvent> events = new ArrayList<>(5);
        Elements cells = line.getElementsByTag("td");
        if (cells.isEmpty())
        {
            return Collections.emptyList();
        }
        DateTime startTime = parseDate(dayOf, cells.get(1).text());
        if (startTime == null)
        {
            // ad row
            return Collections.emptyList();
        }

        for (int i = 2; i < cells.size(); i++)
        {
            Element cell = cells.get(i);

//            logger.info(cell);
//            logger.info(headers.get(i));

            SchemaLocation venue = getVenue(headers.get(i));
            if (venue == null)
            {
//                logger.info("No venue for header " + headers.get(i).text());
                continue;
            }
//            logger.info( headers.get(i).text() + " --> " + venue.getName());

            SchemaArtist artist = getArtistFromCell(cell);
            if (artist == null)
            {
                continue;
            }

            SchemaEvent event = createEvent(artist, venue, startTime);
            if (event != null)
            {
                events.add(event);
            }
        }
        return events;
    }

    private SchemaLocation getVenue(Element element)
    {
        String text = element.text();
        if (text.toLowerCase().startsWith("wdt"))
        {
            return null;
        }
        SchemaLocation loc = venues.get(element.text().toLowerCase());
        return loc;
    }

    private SchemaArtist getArtistFromCell(Element element)
    {
        if (element == null)
        {
            return null;
        }
        String artistName = element.text();
        if (artistName.isEmpty())
        {
            return null;
        }
        if (artistName.equalsIgnoreCase("Doors Open"))
        {
            return null;
        }
//        logger.info (element);
        artistName = artistName.trim();

        SchemaArtist artist = artists.get(artistName.toLowerCase());
        if (artist != null)
        {
            return artist;
        }
        artist = scrapeArtistPage(element.getElementsByTag("a").attr("href"), artistName);
        if (artist != null)
        {
            artists.put(artistName.toLowerCase(), artist);
        }
        return artist;
    }

    private SchemaArtist scrapeArtistPage(String url, String artistName)
    {
        if (url == null || url.isEmpty())
        {
            return null;
        }
        if (url.startsWith(".."))
        {
            url = "https://icelandairwaves.is" + url.substring(2);
        }
        if (url.endsWith("|"))
        {
            url = url.substring(0, url.length() - 1);
        }
        if (url.startsWith("//"))
        {
            url = "https:" + url;
        }
        logger.info(artistName + " --> " + url);
        Document document = getDocument(url);
        if (document == null)
        {
            return null;
        }
//        logger.info (document);

        Elements artistElements = document.getElementsByClass("artist");
        SchemaArtist artist = new SchemaArtist();
        artist.setName(artistName);
        artist.setId(artist.getName().toLowerCase().replace(" ", "-"));
        artist.setSource("IcelandAirwaves");
        artist.setUrl(url);
        artist.addImage(getMainImage(document));
        artist.setLocation(getLocation(artistElements));
        artist.setAttractionType(AttractionType.music);

        for (String genre : getGenre(artistElements))
        {
            if (artist.getGenre() == null)
            {
                artist.setGenre(genre);
            }
            artist.addGenre(genre);
        }

        String facebook = getFacebook(artistElements);
        if (facebook != null)
        {
            artist.setFacebook(facebook);
            artist.addSameAs(facebook);
        }
        String instagram = getInstagram(artistElements);
        if (instagram != null)
        {
            artist.setInstagram(instagram);
            artist.addSameAs(instagram);
        }
        String twitter = getTwitter(artistElements);
        if (twitter != null)
        {
            artist.setTwitter(twitter);
            artist.addSameAs(twitter);
        }
        String spotify = getSpotify(artistElements);
        if (spotify != null)
        {
            artist.setSpotify(spotify);
            artist.addSameAs(spotify);
        }
        String video = getVideoEmbed(artistElements);
        if(video != null)
        {
            artist.setDefaultEmbed(video);
        }
        String description = getDescription(document);
        artist.setDescription(description);
        artist.addAttribute("icelandAirwaves", "IcelandAirwaves2024");
        artist.addAttribute("festivals", "Iceland Airwaves");
        return artist;
    }


    private SchemaEvent createEvent (SchemaArtist artist, SchemaLocation venue, DateTime start)
    {
        SchemaEvent event = new SchemaEvent();
        event.setName(artist.getName() + " @ Airwaves");
        event.setId(artist.getId() + ":" + start.toString());
        event.setLocation(venue);
        event.setStartDate(start.toString());
        event.setEndDate(start.plusMinutes(40).toString());
        for (SchemaImage image : artist.getImages())
        {
            event.addImage(image);
        }
        event.setDescription(artist.getDescription());
        if (artist.getGogo().getMajorType() != null && AttractionType.create(artist.getGogo().getMajorType()) != AttractionType.unknown)
        {
            event.setGogoTypes(AttractionType.create(artist.getGogo().getMajorType()), Collections.emptyList(), Collections.emptyList());

        }
        event.setAttractionType(AttractionType.music);

        event.addInternalType("Festival");
        if (artist.getGogo().getGenres() != null)
        {
            for (String genre : artist.getGogo().getGenres())
            {
                event.addGenre(genre);
            }
        }
        event.setSource("IcelandAirwaves");
        event.addArtist(artist);
        event.addArtist(airwaves);

         // add hint useAI if you want to use the AI to parse the event to find artists or if the event could be multiple events
        // event.addHint("useAI", "true");
        // the NLP called used in that case is  https://translator.promogogo.com/docs#/default/event_nlp_event_post

        return event;
    }

    private String getSocialMedia(Elements artistElements, String site)
    {
        Elements socials = artistElements.get(0).getElementsByClass("socials");
        if (socials == null || socials.isEmpty())
        {
            return null;
        }
        Elements links = socials.get(0).getElementsByTag("a");
        for (Element link : links)
        {
            if (link.attr("class").contains(site))
            {
                return link.attr("href");
            }
        }
        return null;
    }


    private DateTime parseDate(DateTime dayOf, String time)
    {
        if (time.isEmpty())
        {
            return null;
        }
        String[] parts = time.split(":");
        int hour = Integer.parseInt(parts[0]);
        int minute = Integer.parseInt(parts[1]);
        DateTime showTime =  dayOf.withHourOfDay(hour).withMinuteOfHour(minute).withZone(DateTimeZone.forID("Atlantic/Reykjavik"));
        if (hour < 8)
        {
            showTime = showTime.plusDays(1);
        }
        return showTime;
    }

    private Document getDocument(String url)
    {

        try
        {
            // Send the request and get the response
            Document response = Jsoup.connect(url)
                                     .get();

            return response;
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return null;
    }


    private SchemaImage getMainImage(Document document)
    {
        Elements meta = document.head().getElementsByTag("meta");
        SchemaImage image = new SchemaImage();
        for (Element element : meta)
        {
            if (element.attr("property").equalsIgnoreCase("og:image"))
            {
                image.setContentUrl(element.attr("content"));
            }
            if (element.attr("property").equalsIgnoreCase("og:title"))
            {
                image.setCaption(element.attr("content"));
            }
        }
        if (image.getContentUrl() != null)
        {
            return image;
        }
        return null;
    }

    private String getDescription(Document document)
    {
        Elements elementsByClass = document.getElementsByClass("description-content");
        return elementsByClass.html();
    }

    private String getFacebook(Elements artistElements)
    {
        return getSocialMedia(artistElements, "facebook");
    }


    private String getInstagram(Elements artistElements)
    {
        String str = getSocialMedia(artistElements, "instagram");
        if (str != null)
        {
            if (str.contains("?"))
            {
                str = str.substring(0, str.indexOf("?"));
            }
        }
        return str;
    }

    private String getTwitter(Elements artistElements)
    {
        return getSocialMedia(artistElements, "twitter");
    }

    private String getSpotify(Elements artistElements)
    {
        String str = getSocialMedia(artistElements, "spotify");
        if (str == null)
        {
            return null;
        }
        if (str.contains("?si="))
        {
            str = str.substring(0,str.indexOf("?"));
        }
        return str;
    }

    private List<String> getGenre(Elements artistElements)
    {
        Elements tags = artistElements.get(0).getElementsByClass("tags");

        List<String> genres = new ArrayList<>(5);
        for (Element tag : tags)
        {
            Elements spanTags = tag.getElementsByTag("span");
            Boolean first = true;
            for (Element spanTag : spanTags)
            {
                if (first)
                {
                    // first item is the country. Great semantic structure
                    first = false;
                    continue;
                }
                genres.add(spanTag.text().trim());
            }
        }
        return genres;
    }

    private CountryCode getLocation(Elements artistElements)
    {
        Elements tags = artistElements.get(0).getElementsByClass("tags");

        //        logger.info(tags);
        for (Element tag : tags)
        {
            Elements spanTags = tag.getElementsByTag("span");
            for (Element spanTag : spanTags)
            {
                //                logger.info (spanTag);
                CountryCode code = LocationUtil.getCountryCodeForName(spanTag.text());
                if (code != null && code != CountryCode.unknown)
                {
                    //                    logger.info(spanTag.text() + " --> " + code);
                    return code;
                }
            }
        }
        return null;
    }

    private String getVideoEmbed(Elements artistElements)
    {
        Elements videoElement = artistElements.get(0).getElementsByClass("video-preview");
        if (videoElement == null || videoElement.isEmpty())
        {
            return null;
        }
        Elements iframe = videoElement.get(0).getElementsByTag("iframe");
        if (iframe != null && !iframe.isEmpty())
        {
            String video = iframe.attr("src");
            if (video != null && !video.isEmpty())
            {
                if (video.contains("?"))
                {
                    video = video.substring(0, video.indexOf("?"));
                }
                return video;
            }
        }
        return null;
    }


    private PusherMessage wrapEnvelope(SchemaEvent ev, String id)
    {
        PusherMessage msg = new PusherMessage();

        msg.setSubject(ev.getName());

        msg.add(PropertyType.eventID.name(), ev.getId());

        msg.add(PropertyType.site.name(), ev.getImportSource());
        msg.setWhen(ev.getStartDate());

        msg.setPayload(ev.toJson());
        msg.add("importerid", id);


//        logger.info(ev.getName() + " Sending event '" + ev.getName() + "' to importer");
        return msg;
    }


}
