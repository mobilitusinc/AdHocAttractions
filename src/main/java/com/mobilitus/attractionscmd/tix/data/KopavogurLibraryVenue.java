package com.mobilitus.attractionscmd.tix.data;

import com.mobilitus.util.data.attractions.AttractionType;
import com.mobilitus.util.data.attractions.Genre;
import com.mobilitus.util.data.attractions.GenreData;
import com.mobilitus.util.data.attractions.GogoClassification;
import com.mobilitus.util.data.attractions.MinorAttractionType;
import com.mobilitus.util.data.attractions.VenueData;
import com.mobilitus.util.data.attractions.textHandler.IcelandicTextHandler;
import com.mobilitus.util.data.ticketMaster.EventGhettoData;
import com.mobilitus.util.hexia.location.Point;
import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.util.Collections;
import java.util.List;

/**
 * @author helgaw
 * @todo add class description.
 * @since 10/8/21 13:57
 */
public class KopavogurLibraryVenue
{
    private static final Logger logger = Logger.getLogger(KopavogurLibraryVenue.class);

    public static VenueData createVenue(String hall)
    {
        if (hall == null || hall.isEmpty() || hall.equalsIgnoreCase("aðalsafn"))
        {
            VenueData venueData = new VenueData();
            venueData.setName("Bókasafn Kópavogs | Aðalsafn");
            venueData.setAddress("Hamraborg 6a");
            venueData.setLocationID("0144c8d2-4002-432d-9e7f-48a18d792051");
            venueData.setZipAndCity("200", "Kópavogur");
            venueData.setCountry("is");
            venueData.setLocationPoint(new Point("64.11198355837232, -21.909040401014323"));
            venueData.setSource("Bókasafn Kópavogs");
            return venueData;
        }
        else if (hall.equalsIgnoreCase("lindasafn"))
        {
            VenueData venueData = new VenueData();
            venueData.setName("Bókasafn Kópavogs | Lindasafn");
            venueData.setAddress("Núpalind 7");
//            venueData.setLocationID("0144c8d2-4002-432d-9e7f-48a18d792051");
            venueData.setZipAndCity("201", "Kópavogur");
            venueData.setCountry("is");
//            venueData.setLocationPoint(new Point("64.11198355837232, -21.909040401014323"));
            venueData.setSource("Bókasafn Kópavogs");
            return venueData;
        }
        return null;
    }

    public static void addDetails(String url, EventGhettoData data)
    {
        if (url == null || url.isEmpty())
        {
            return;
        }
        Document document;
        try
        {
            //Get Document object after parsing the html from given url.

            String theUrl = url;

            document = Jsoup.connect(theUrl).get();
             logger.info(document);

            Elements description = document.getElementsByClass("main-text");

            String text = description.get(0).text();
            String html = description.get(0).html();
            data.setUniqueAttribute("description", text);
            data.setUniqueAttribute("description-html", html);

        }
        catch (Exception e)
        {
        }
    }


    public static AttractionType getMajorAttractionType(EventGhettoData data)
    {
        return IcelandicTextHandler.getMajorAttractionType(data.getTitle(), data.getAttribute("description"), Collections.emptyList());
    }

    public static List<MinorAttractionType> getMinorAttractionType(EventGhettoData data)
    {
        return IcelandicTextHandler.getMinorAttractionTypes(data.getTitle(), data.getAttribute("description"), Collections.emptyList());
    }

    public static GenreData getGenre(EventGhettoData data)
    {
        Genre g = IcelandicTextHandler.getGenre(data.getTitle(), data.getAttribute("description"), Collections.emptyList());
        if (g != null)
            return new GenreData(g);

        return null;
    }

    public static List<GogoClassification> getClassifications(EventGhettoData data)
    {
        return IcelandicTextHandler.getClassifications(data.getTitle(), data.getAttribute("desciption"), Collections.emptyList());
    }

}
