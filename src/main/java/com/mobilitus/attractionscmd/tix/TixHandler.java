package com.mobilitus.attractionscmd.tix;

import com.mobilitus.attractionscmd.tix.data.TixDate;
import com.mobilitus.attractionscmd.tix.data.TixEvent;
import com.mobilitus.util.data.attractions.ArtistData;
import com.mobilitus.util.data.attractions.AttractionType;
import com.mobilitus.util.data.attractions.MinorVenueCategory;
import com.mobilitus.util.data.attractions.VenueData;
import com.mobilitus.util.data.face.FaceData;
import com.mobilitus.util.data.ticketMaster.EventGhettoData;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author helgaw
 * @todo add class description.
 * @since 3/1/21 11:31
 */
public class TixHandler
{
    private static final Logger logger = Logger.getLogger(TixHandler.class);

    private TixAPI api;

    public TixHandler(String source)
    {
        api = new TixAPI(source);
    }


    public List<VenueData> getVenues()
    {
        List<TixEvent> events = api.getEvents();
        List<VenueData> venues = new ArrayList<>(100);
        Map<String, VenueData> venueMap = new HashMap(100);
        for (TixEvent event : events)
        {
            for (VenueData v : event.getVenues())
            {
//                logger.info( v.getName() + " " + v.getMajorCategory() + " " + v.getMinorCategory());
//                logger.info("\t"+ event.getName() + " " + event.getType() + " " + event.getCategories());

                if (venueMap.get(v.getName().trim()) == null)
                {
                    venueMap.put(v.getName().trim(), v);
                }
                else
                {
                    VenueData myVenue = venueMap.get(v.getName().trim());
                    if (myVenue.getMajorCategory() == null)
                    {
                        myVenue.setMajorCategory(v.getMajorCategory());
                    }

                    if (v.getMinorCategory() != null)
                    {
                        for (MinorVenueCategory min : v.getMinorCategory())
                        {
                            myVenue.addMinorCategory(min);
                        }
                    }
                    venueMap.put(v.getName().trim(), myVenue);
                }

            }
        }
        venues.addAll(venueMap.values());
        return venues;
    }


    public List<ArtistData> getArtists()
    {
        List<TixEvent> events = api.getEvents();

        Map<String, FaceData> artistMap = new HashMap(200);
        List<ArtistData> artists = new ArrayList<>(200);
        for (TixEvent event : events)
        {
            for (TixDate date : event.getDates())
            {
                for (ArtistData a : event.getArtists(date))
                {
//                System.out.println("\t" + a.getName());
                    if (artistMap.get(a.getName().trim()) == null)
                    {
                        artistMap.put(a.getName().trim(), a);
                        artists.add(a);
                    }
                }
            }
        }
        return artists;
    }


    public List<EventGhettoData> getEvents()
    {
        List<TixEvent> events = api.getEvents();

        if (events == null || events.isEmpty())
        {
            return Collections.emptyList();
        }

        List<EventGhettoData> result = new ArrayList<>(300);
        int index = 0;
        int dateIndex = 0;

        Map<String, ArtistData> artistMap = new HashMap<>(1000);


        for (TixEvent event : events)
        {
            index++;
//            logger.info(event.getName() + " " + event.getCategories() + "\n\t" + event.getSubTitle() + " " + event.getDescription()) ;

            List<EventGhettoData> eventList = event.toDataList();
            if (eventList.isEmpty())
            {
                continue;
            }

//            if (eventList.get(0).getMajorType() != AttractionType.music  && eventList.get(0).getMajorType() != AttractionType.sports  && eventList.get(0).getMajorType() != AttractionType.theater)
//            {
//                logger.info( index + " " + event.getName() + " "  +  event.getSubTitle());
//            }
//            else
//            {
//                continue;
//            }

//            for (int i = 0; i < event.getDates().size(); i++)
//            {
//
//                dateIndex++;

//                logger.info(event.getName() + " " + event.getDates().get(i).getName() + " " + event.getCategories() + " " + event.getDates().get(i).getTags()) ;
//                if (eventList.get(i).getArtists() != null)
//                {
//                    int j = 0;
//                    for (Object obj : eventList.get(i).getArtists())
//                    {
//                        ArtistData artist = (ArtistData) obj;
//                        if (!artistMap.containsKey(artist.getArtistID()))
//                        {
//                            logger.info(j + " '" + artist.getName() + "' '" + artist.getArtistID() + "' " + artist.getMajorType() + " "+ artist.getMinorTypes() + " "+ artist.getGogoGenre() + " "+ artist.getClassifications());
//                            artistMap.put(artist.getArtistID(), artist);
//                            j++;
//                        }
//
//                    }
//                }
//
//                if (eventList.get(i).getMajorType() != null)
//                {
////                    logger.info(index + "/" + dateIndex + "\t " + event.getName() + " " + event.getDates().get(i).getName() + " " + event.getDates().get(i).getCategories()); //+ event.getDates().get(i).getVenue() + " " + event.getDates().get(i).getHall());
//                    if (eventList.get(i).getMajorType() == AttractionType.sports)
//                    {
////                        logger.info("\t\t\t'" + eventList.get(i).getTitle() + "' " + eventList.get(i).getMajorType() + " " + eventList.get(i).getMinorTypes() + " " +
////                                eventList.get(i).getClassifications() +  " " + eventList.get(i).getAttribute("League") +
////                                "\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t "  + getArtistList(eventList.get(i)));
//                    }
//                    else if (eventList.get(i).getMajorType() == AttractionType.music)
//                    {
//
////                        logger.info("\t\t'" + eventList.get(i).getTitle() + "' " + eventList.get(i).getMajorType() + " " + eventList.get(i).getGogoGenre() + " " +
////                                eventList.get(i).getClassifications() +  "\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t " + getArtistList(eventList.get(i)));
//                    }
//                    else if (eventList.get(i).getMajorType() != AttractionType.theater)
//                    {
//
////                        logger.info("\t\t'" + eventList.get(i).getTitle() + "' " + eventList.get(i).getMajorType() + " " + eventList.get(i).getMinorTypes() + " " +
////                                eventList.get(i).getClassifications() +  "\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t "  + getArtistList(eventList.get(i)));
//                    }
//                }
//            }
            dateIndex = 0;
            result.addAll(eventList);
        }
        return result;
    }

    private String getArtistList(EventGhettoData eventGhettoData)
    {
        StringBuilder buf = new StringBuilder();
        for (Object obj : eventGhettoData.getArtists())
        {
            ArtistData a = (ArtistData)obj;
            String emoji = "";
            if (a.getMajorType() == AttractionType.theater)
            {
                emoji = "\uD83C\uDFAD";
            }
            if (a.getMajorType() == AttractionType.music)
            {
                emoji = "\uD83C\uDFB8";
            }
            if (a.getMajorType() == AttractionType.sports)
            {
                emoji = "⛹️\u200D";
            }

            String str = emoji + " '" + a.getName() + "' " + a.getMajorType() +" " + a.getMinorTypes()  ;
            if (eventGhettoData.getMajorType() == AttractionType.sports)
            {
                str = str + " League: " + eventGhettoData.getAttribute("League");
            }
            if (eventGhettoData.getMajorType() == AttractionType.music)
            {
                str = str + " Genre: " + eventGhettoData.getGogoGenre();
            }
            buf.append("[" + str  +"], " );
        }
        return buf.toString();
    }

}
