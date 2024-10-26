package com.mobilitus.attractionscmd.tix.data;

import com.mobilitus.util.data.attractions.VenueData;
import com.mobilitus.util.data.ticketMaster.EventGhettoData;
import com.mobilitus.util.hexia.location.Point;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * @author helgaw
 * @todo add class description.
 * @since 9/24/21 12:24
 */
public class SalurinnVenue
{

    public static VenueData createVenue()
    {
        VenueData venueData = new VenueData();
        venueData.setName("Salurinn");
        venueData.setAddress("Hamraborg 6");
        venueData.setLocationID("1b596dbe-4bc0-4cd4-bbcd-65a5686295b0");
        venueData.setZipAndCity("200", "Kópavogur");
        venueData.setCountry("is");
        venueData.setLocationPoint(new Point("64.11198355837232, -21.909040401014323"));
        venueData.setSource("Salurinn");
        return venueData;
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
//                System.out.println(document);

            Elements description = document.getElementsByClass("col-xs-12");
            for (Element element : description)
            {
                if (element.getElementsByClass("entry-text") != null && element.getElementsByClass("entry-text").size() > 0)
                {
                    data.setUniqueAttribute("description", element.text());
                    data.setUniqueAttribute("description-html", element.html());
                    break;
                }
            }
        }
        catch (Exception e)
        {
        }
    }

}
