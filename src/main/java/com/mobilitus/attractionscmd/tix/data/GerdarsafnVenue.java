package com.mobilitus.attractionscmd.tix.data;

import com.mobilitus.util.data.attractions.MajorVenueCategory;
import com.mobilitus.util.data.attractions.MinorVenueCategory;
import com.mobilitus.util.data.attractions.VenueData;
import com.mobilitus.util.data.attractions.VenueType;
import com.mobilitus.util.data.ticketMaster.EventGhettoData;
import com.mobilitus.util.hexia.location.Point;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * @author helgaw
 * @todo add class description.
 * @since 9/24/21 12:58
 */
public class GerdarsafnVenue
{
    //

    public static VenueData createVenue()
    {
        VenueData venueData = new VenueData();
        venueData.setName("Gerðarsafn");
        venueData.setAddress("Hamraborg 6");
        venueData.setZipAndCity("200", "Kópavogur");
        venueData.setCountry("is");
        venueData.setLocationID("733846f4-577b-480b-b376-b67fa447039b");
        venueData.setLocationPoint(new Point("64.11209598268928, -21.910081098072784"));
        venueData.setSource("Gerðarsafn");
        venueData.setType(VenueType.venue);
        venueData.setMajorCategory(MajorVenueCategory.culture);
        venueData.addMinorCategory(MinorVenueCategory.museum);
        return venueData;
    }

    public static void addDetails(String url, EventGhettoData data)
    {
        Document document;
        try
        {
            //Get Document object after parsing the html from given url.

            String theUrl = url;

            document = Jsoup.connect(theUrl).get();

//                System.out.println(document);

            Elements description = document.getElementsByClass("main-text");

            for (Element element : description)
            {
                String txt = element.text();
                data.addUniqueAttribute("description", txt);

                String html = element.html();
                data.addUniqueAttribute("description-html", html);
            }
        }
        catch (Exception e)
        {
        }
    }
}
