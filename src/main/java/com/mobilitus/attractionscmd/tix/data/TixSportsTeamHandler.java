package com.mobilitus.attractionscmd.tix.data;

import com.mobilitus.util.data.attractions.ArtistData;
import com.mobilitus.util.data.attractions.AttractionType;
import com.mobilitus.util.data.attractions.GogoClassification;
import com.mobilitus.util.data.attractions.MinorAttractionType;
import com.mobilitus.util.data.attractions.textHandler.IcelandicTextHandler;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author helgaw
 * @todo add class description.
 * @since 8/10/21 19:38
 */
public class TixSportsTeamHandler
{
    private static final Logger logger = Logger.getLogger(TixSportsTeamHandler.class);

    public List<ArtistData> getSportsTeam(TixEvent event, TixDate date)
    {
        return getSportsTeams(event, date);

    }

    private List<ArtistData> getSportsTeams(TixEvent tixEvent, TixDate date)
    {
        AttractionTypeHandler attractionType = new AttractionTypeHandler(tixEvent, date);
        AttractionType majorAttractionType = attractionType.getMajorAttractionType();

        String title = tixEvent.getName();
        if (title == null)
        {
            return Collections.emptyList();
        }

        title = cleanFirst(title);
        String [] parts = title.split("[|/:\\-–]");
        List<ArtistData> result = new ArrayList<>(3);
        for (String part : parts)
        {
            if (isTeam(part))
            {
                ArtistData artist = new ArtistData();
                artist.setName(part.trim());
                artist.setArtistID (part.trim() + " " + majorAttractionType);
                artist.setBestImage(tixEvent.getFeaturedImage());
                artist.setBestThumb(tixEvent.getFeaturedImage());

                artist.setMajorType(attractionType.getMajorAttractionType());
                if (attractionType.getMinorAttractionTypes() != null)
                {
                    for (MinorAttractionType min : attractionType.getMinorAttractionTypes())
                    {
                        if (!artist.getArtistID().contains(min.name()))
                        {
                            artist.setArtistID (artist.getArtistID() + " " + min);
                        }
                        artist.addMinorType(min);
                    }
                }
                if (getLeagueName(tixEvent, date) != null && !getLeagueName(tixEvent, date).isEmpty())
                {
                    artist.addAttribute("League", getLeagueName(tixEvent, date));
                }

                result.add(artist);
            }
            else
            {
                ArtistData artist = createNonTeamArtist(tixEvent, date, part);
                if (artist != null)
                {
                    result.add(artist);
                }

            }
        }
        if (result.isEmpty())
        {
            ArtistData artist = new ArtistData();
            artist.setName(tixEvent.getName());
            artist.setMajorType(attractionType.getMajorAttractionType());
            result.add(artist);
        }
        return result;
    }

    public TixSportsTeamHandler()
    {
    }

    private ArtistData createNonTeamArtist(TixEvent tixEvent, TixDate date, String _name)
    {
        String name = _name.trim();
        if (name.toLowerCase().contains("color run"))
        {
            ArtistData artist = new ArtistData();
            artist.setName("Color Run");
            artist.setMajorType(AttractionType.sports);
            artist.addMinorType(MinorAttractionType.running);
            artist.addClassification(GogoClassification.familyFriendly);

            List<MinorAttractionType> minorTypes = IcelandicTextHandler.getMinorAttractionTypes(name, tixEvent.getDescription(), tixEvent.getCategories());
            artist.addMinorTypes(minorTypes);

            return artist;

        }
        if (name.toLowerCase().contains("pepsi max"))
        {
            return null;
        }
        if (name.toLowerCase().contains("u 21"))
        {
            return null;
        }
        if (name.toLowerCase().contains("u21"))
        {
            return null;
        }
        if (name.toLowerCase().contains("fitness"))
        {
            ArtistData artist = new ArtistData();
            artist.setName(name);
            artist.setMajorType(AttractionType.sports);
            List<MinorAttractionType> minorTypes = IcelandicTextHandler.getMinorAttractionTypes(name, tixEvent.getDescription(), tixEvent.getCategories());
            artist.addMinorTypes(minorTypes);


            return artist;

        }
        if (name.toLowerCase().contains("herrakvöld") || name.toLowerCase().contains("kvennakvöld"))
        {
            ArtistData artist = new ArtistData();
            artist.setName(name);
            artist.setMajorType(AttractionType.community);

            List<MinorAttractionType> minorTypes = IcelandicTextHandler.getMinorAttractionTypes(name, tixEvent.getDescription(), tixEvent.getCategories());
            artist.addMinorTypes(minorTypes);


            return artist;

        }
        if (name.toLowerCase().contains("hlaupið"))
        {
            ArtistData artist = new ArtistData();
            artist.setName(name);
            artist.setMajorType(AttractionType.sports);
            artist.addMinorType(MinorAttractionType.running);
            List<MinorAttractionType> minorTypes = IcelandicTextHandler.getMinorAttractionTypes(name, tixEvent.getDescription(), tixEvent.getCategories());
            artist.addMinorTypes(minorTypes);


            return artist;

        }
        if (name.toLowerCase().contains("landsmót hestamanna"))
        {
            ArtistData artist = new ArtistData();
            artist.setName(name);
            artist.setMajorType(AttractionType.sports);
            artist.addMinorType(MinorAttractionType.horses);
            List<MinorAttractionType> minorTypes = IcelandicTextHandler.getMinorAttractionTypes(name, tixEvent.getDescription(), tixEvent.getCategories());
            artist.addMinorTypes(minorTypes);


            return artist;

        }

        logger.info("could not create detailed artist for " + name);

        ArtistData artist = new ArtistData();
        artist.setName(name);
        artist.setMajorType(AttractionType.sports);

        return artist;
    }

    public List<MinorAttractionType> getMinorTypesForSport(TixEvent tixEvent, TixDate tixDate)
    {
        List<MinorAttractionType> list = findInText(tixDate.getName());
        if (!list.isEmpty())
        {
            return list;
        }

         list = findInText(tixEvent.getName());
        if (!list.isEmpty())
        {
            return list;
        }

        list = findInText(tixEvent.getDescription());
        if (!list.isEmpty())
        {
            return list;
        }

        return list;
    }

    private List<MinorAttractionType> findInText(String text)
    {
        List<MinorAttractionType> list = new ArrayList<>(3);
        if (text.toLowerCase().contains("pepsi max deild kvenna"))
        {
            list.add(MinorAttractionType.soccer);
        }
        else if (text.toLowerCase().contains("mjólkurbikar karla"))
        {
            list.add(MinorAttractionType.soccer);
        }
        else if (text.toLowerCase().contains("pepsi max deild karla"))
        {
            list.add(MinorAttractionType.soccer);
        }
        else if (text.toLowerCase().contains("pepsi max deild"))
        {
            list.add(MinorAttractionType.soccer);
        }
        else
        {
            return Collections.emptyList();
        }
        return list;
    }



    private String cleanFirst(String title)
    {
        title = title.replaceAll("\\(.*?\\)", "");

        if (title.toLowerCase().contains("mfl. kvk"))
        {
            return remove (title, "mfl. kvk");
        }
        if (title.toLowerCase().contains("mfl. kk"))
        {
            return remove (title, "mfl. kk");
        }
        if (title.toLowerCase().contains("mfl kvk"))
        {
            return remove (title, "mfl kvk");
        }
        if (title.toLowerCase().contains("mfl kk"))
        {
            return remove (title, "mfl kk");
        }
        return title;
    }



    private String remove(String title, String subText)
    {
        int index = title.toLowerCase().indexOf(subText.toLowerCase());
        if (index > 0)
        {
            int len = subText.length();
            return  title.substring(0, index) + title.substring(index + len);
        }
        return title;
    }


    private boolean isTeam(String part)
    {
        return IcelandicSportsTeam.has(part);
    }


    public String getLeagueName(TixEvent tixEvent, TixDate tixDate)
    {
        String name = getLeague(tixDate.getName());
        if (!name.isEmpty())
        {
            return name;
        }

        name = getLeague(tixEvent.getName());
        if (!name.isEmpty())
        {
            return name;
        }

        name = getLeague(tixEvent.getDescription());
        if (!name.isEmpty())
        {
            return name;
        }

        return "";

    }


    private String getLeague(String text)
    {
        List<MinorAttractionType> list = new ArrayList<>(3);
        if (text.toLowerCase().contains("pepsi max deild kvenna"))
        {
            return "Meistaraflokkur Kvenna";
        }
        if (text.toLowerCase().contains("meistaraflokkur kvenna"))
        {
            return "Meistaraflokkur Kvenna";
        }
        if (text.toLowerCase().contains("mfl kvk"))
        {
            return "Meistaraflokkur Kvenna";
        }
        if (text.toLowerCase().contains("mfl. kvk"))
        {
            return "Meistaraflokkur Kvenna";
        }
        if (text.toLowerCase().contains("mfl kk"))
        {
            return "Meistaraflokkur Karla";
        }
        if (text.toLowerCase().contains("mfl. kk"))
        {
            return "Meistaraflokkur Karla";
        }
        if (text.toLowerCase().contains("mjólkurbikar karla"))
        {
            return "Meistaraflokkur Karla";
        }
        if (text.toLowerCase().contains("lengjudeild karla"))
        {
            return "Meistaraflokkur Karla";
        }
        if (text.toLowerCase().contains("lengjudeild kvenna"))
        {
            return "Meistaraflokkur Kvenna";
        }
        if (text.toLowerCase().contains("u21"))
        {
            return "U21";
        }

        return "";
    }

}
