package com.mobilitus.attractionscmd.tix.data;

import com.mobilitus.util.data.attractions.AttractionType;
import com.mobilitus.util.data.attractions.Genre;
import com.mobilitus.util.data.attractions.GenreData;
import com.mobilitus.util.data.attractions.GogoClassification;
import com.mobilitus.util.data.attractions.MajorVenueCategory;
import com.mobilitus.util.data.attractions.MinorAttractionType;
import com.mobilitus.util.data.attractions.MinorVenueCategory;
import com.mobilitus.util.data.attractions.textHandler.IcelandicTextHandler;

import java.util.Collections;
import java.util.List;

/**
 * @author helgaw
 * @todo add class description.
 * @since 8/17/21 10:42
 */
public class AttractionTypeHandler
{
    private final TixEvent event;
    private final TixDate date;
    private final String venueName;
    private final String eventName;
    private final String dateName;


    public AttractionTypeHandler (TixEvent event, TixDate date)
    {
        this.event = event;
        this.date = date;
        this.venueName = date.createVenueName();
        this.eventName = event.getName();
        this.dateName = date.getName();
    }




    public List<GogoClassification> getClassifications()
    {
        return IcelandicTextHandler.getClassifications(createTitle(eventName, dateName), event.getDescription(), date.getCategories());
//        List<GogoClassification> classifications = new ArrayList<>();
//        List<String> categories = date.getCategories();
//
//        for (String category : categories)
//        {
//            GogoClassification att = toClassification(category);
//            if (att != null)
//            {
//                if (!classifications.contains(att))
//                {
//                    classifications.add(att);
//                }
//            }
//        }
//
//        GogoClassification att = nameToClassification(eventName);
//        if (att != null)
//        {
//            if (!classifications.contains(att))
//            {
//                classifications.add(att);
//            }
//        }
//        att = nameToClassification(dateName);
//        if (att != null)
//        {
//            if (!classifications.contains(att))
//            {
//                classifications.add(att);
//            }
//        }
//        if (isFestival())
//        {
//            att = GogoClassification.festival;
//
//            if (!classifications.contains(att))
//            {
//                classifications.add(att);
//            }
//        }
//       return classifications;
    }

    public GenreData getGenre()
    {
        Genre g = IcelandicTextHandler.getGenre(createTitle(eventName, dateName), event.getDescription(), date.getCategories());
        if (g != null)
            return new GenreData(g);

        return null;
//        List<String> categories = date.getCategories();
//        List<Genre> potentials = new ArrayList<>(3);
//        for (String category : categories)
//        {
//            GenreData myGenre = toGenre(category);
//            if (myGenre != null && !potentials.contains(myGenre))
//            {
//                potentials.add(myGenre);
//            }
//        }
//
//        Genre myGenre = genreFromName(event.getName());
//
//        if (myGenre != null && !potentials.contains(myGenre))
//        {
//            potentials.add(myGenre);
//        }
//        if (potentials.isEmpty())
//        {
//            return null;
//        }
//         myGenre = genreFromName(date.getName());
//
//        if (myGenre != null && !potentials.contains(myGenre))
//        {
//            potentials.add(myGenre);
//        }
//        if (potentials.isEmpty())
//        {
//            return null;
//        }
//
//
//        if (potentials.size() == 1)
//        {
//            return new GenreData(potentials.get(0));
//        }
//        else
//        {
//            return new GenreData(potentials.get(0));
//        }
    }


    public List<MinorAttractionType> getMinorAttractionTypes()
    {
        if (getMajorAttractionType() == AttractionType.sports)
        {
            List<MinorAttractionType> minorTypes = getMinorTypesForSport();
            return minorTypes;
        }
        if (getMajorAttractionType() == AttractionType.theater)
        {
            List<MinorAttractionType> minorTypes = getMinorTypesForTheatre();
            return minorTypes;
        }
        if (getMajorAttractionType() == AttractionType.music)
        {
            return Collections.emptyList();
        }
        return Collections.emptyList();
    }


    public Boolean hasLeague(MinorAttractionType minor)
    {
        if (minor == MinorAttractionType.soccer)
        {
            return true;
        }

        return false;
    }


    public Boolean isFestival()
    {
        List<String> categories = date.getCategories();
        for (String str : categories)
        {
            if (str.trim().equalsIgnoreCase("festival"))
            {
                return true;
            }
            if (str.trim().equalsIgnoreCase("hátíð"))
            {
                return true;
            }
            if (str.trim().equalsIgnoreCase("hátíðir"))
            {
                return true;
            }
        }
        if (eventName.toLowerCase().contains("festival"))
        {
            return true;
        }
        if (eventName.toLowerCase().contains("múlinn jazzklúbbur"))
        {
            return true;
        }
        if (eventName.toLowerCase().contains("listahátíð"))
        {
            return true;
        }
        return false;
    }


    public Boolean isTribute()
    {
        return IcelandicTextHandler.isTribute(createTitle(eventName, dateName), event.getDescription(), date.getCategories());
    }

    protected MajorVenueCategory getMajorVenueCategory()
    {
        MajorVenueCategory cat = getMajorCatFromName(venueName);
        if (cat != null)
        {
            return cat;
        }

        AttractionType type = getMajorAttractionType();
        if (type == null)
        {
            return null;
        }

        if (type == AttractionType.sports)
        {
            return MajorVenueCategory.sportsVenue;
        }

        if (type == AttractionType.theater)
        {
            return MajorVenueCategory.theater;
        }

        if (type == AttractionType.music)
        {
            return MajorVenueCategory.musicVenue;
        }

        return null;
    }

    protected List<MinorVenueCategory> getMinorVenueCategory()
    {
        return IcelandicTextHandler.getMinorVenueCategories(venueName, event.getDescription(),  date.getCategories());
    }

    private Genre genreFromName(String name)
    {
        return IcelandicTextHandler.getGenre(name);
    }

    public AttractionType getMajorAttractionType()
    {
        List<String> categories = event.getCategories();
        categories.addAll(event.getTags());

        AttractionType att = IcelandicTextHandler.getMajorAttractionType (createTitle(eventName, dateName), event.getDescription(), categories);
        return att;
    }


    private MajorVenueCategory getMajorCatFromName(String venueName)
    {
        return IcelandicTextHandler.getMajorVenueCategory(venueName);
    }

    private List<MinorAttractionType> getMinorTypesForSport()
    {
        TixSportsTeamHandler handler = new TixSportsTeamHandler();
        return handler.getMinorTypesForSport(event, date);
    }


    private List<MinorAttractionType> getMinorTypesForTheatre()
    {
        List<String> categories = date.getCategories();

        return IcelandicTextHandler.getMinorAttractionTypes(createTitle(eventName, dateName), event.getSubTitle() +"\n " + event.getDescription(), categories);
    }


//    private boolean hasGenre(String genre)
//    {
//        for (String str :  date.getCategories())
//        {
//            if (str.equalsIgnoreCase(genre))
//            {
//                return true;
//            }
//        }
//
//        return false;
//    }




    private AttractionType toAttractionType(String title, String text, List<String> categories)
    {

        return IcelandicTextHandler.getMajorAttractionType(title, text, categories);
    }



    private GenreData toGenre(String _category)
    {
        Genre g =  IcelandicTextHandler.getGenre(createTitle (eventName, dateName), event.getDescription(), date.getCategories());
        if (g != null)
        {
            return new GenreData(g);
        }

        return null;
    }



    public static String createTitle(String lhs, String rhs)
    {

        // this is an event title
//        logger.info("Create title from '" + lhs +  "' '" + rhs + "'");
//        String[] lhsParts = lhs.split("[|/:\\-–]");
//        String[] rhsParts = rhs.split("[|/:\\-–]");
//


        if (rhs.toLowerCase().trim().contains(lhs.toLowerCase().trim()))
        {
//            logger.info("Title is  " + rhs.trim());
            return rhs.trim();
        }
        if (lhs.toLowerCase().trim().contains(rhs.toLowerCase().trim()))
        {
//            logger.info("Title is  " + lhs.trim());
            return lhs.trim();
        }
        String lhsStripped = stripSpacesAndMarks (lhs);
        String rhsStripped = stripSpacesAndMarks (rhs);

        if (lhsStripped.equalsIgnoreCase(rhsStripped))
        {
            return lhs.trim();
        }

        if (rhsStripped.toLowerCase().trim().contains(lhsStripped.toLowerCase().trim()))
        {
            return rhs.trim();
        }
        if (lhsStripped.toLowerCase().trim().contains(rhsStripped.toLowerCase().trim()))
        {
            return lhs.trim();
        }

        String title = lhs.trim();
//        if (title.equals("."))
//        {
////            logger.info("Title is  " + rhs.trim());
//
//            return rhs.trim();
//        }
//        logger.info("Title is  " + lhs.trim());


        String myTitle =  lhs.trim() + " | " + rhs.trim();;
        if (myTitle.contains(" - -"))
            myTitle = myTitle.replace(" - -", " - ");
        return myTitle.trim().replaceAll("\\s+", " ");

    }

    private static String stripSpacesAndMarks(String _str)
    {
        String str = _str;
        str = str.replaceAll("\\s+", "");
        str = str.replaceAll("[\\.|\\,|\\-|\\:|!|?|&|\\|]+", "");
        str = str.replaceAll("\\,", "");
        str = str.replaceAll("\\.", "");
        return str;
    }


    String inEnglish(String icelandicCategory)
    {
        return CatMap.get(icelandicCategory);
    }

}
