package com.mobilitus.attractionscmd.preparecountry;

import com.mobilitus.util.data.attractions.Genre;
import com.mobilitus.util.data.attractions.GenreData;
import com.mobilitus.util.data.attractions.textHandler.TagCounter;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * @author helgaw
 * @todo add class description.
 * @since 5/16/23 15:59
 */
public class GenreHandler
{
    private static String[] countries = {
                    "italian", "firenze",  "milan", "italo",
                    "greek",  "swiss",
                    "icelandic", "sunnlensk",
                    "gothenburg", "swedish",
                    "norwegian", "norske", "trondersk", "nordnorsk", "bergen",  "rogaland", "oslo",
                    "finnish","suomi",
                    "london", "uk", "british","bristol",  "birmingham", "manchester",  "scottish",
                    "belgian", "brussels", "antwerp", "flemish", "belgium", "vlaamse","liegeois", "west-vlaamse", "belge", "ghent",
                    "dutch", "nederpop",
                    "irish", "celtic",
                    "danish", "dansktop",
                    "luxembourgian",
                    "australian", "aussietronica", "melbourne", "sydney","perth",  "aussie",  "brisbane", "didgeridoo", "adelaide",
                    "british", "korean",
                    "german", "munich",
                    "israeli",
                    "yemeni",
                    "polish",
                    "czech", "czsk", "prague", "brno",
                    "slovak",
                    "french", "francaise","francais",
                    "canadian", "calgary", "montreal", "toronto", "vancouver", "kelowna", "bc",
                    "russian",
                    "new zealand", "nz", "christchurch",
                    "australian",
                    "thai",
                    "argentine",
                    "hungarian",
                    "mexicana", "mexican",
                    "redneck",  "dakota", "memphis", "heartland",
                    "american", "texas", "nashville", "pennsylvania", "jersey", "detroit", "southern", "atlanta"
    };

    private static String [] codes = {
                    "it", "it", "it", "it",
                    "gr", "ch",
                    "is", "is",
                    "se", "se",
                    "no", "no", "no", "no", "no", "no", "no",
                    "fi", "fi",
                    "gb", "gb", "gb","gb", "gb","gb","gb",
                    "be","be","be","be","be", "be", "be", "be", "be","be",
                    "nl", "nl",
                    "ie","ie",
                    "dk", "dk",
                    "lu",
                    "au", "au",  "au",  "au", "au",  "au",   "au", "au",   "au",
                    "gb", "kr",
                    "de", "de",
                    "il",
                    "ye",
                    "pl",
                    "cz", "cz",   "cz", "cz",
                    "sk",
                    "fr", "fr", "fr",
                    "ca","ca","ca","ca","ca", "ca","ca",
                    "ru",
                    "nz", "nz", "nz",
                    "au",
                    "th",
                    "ar",
                    "hu",
                    "mx", "mx",
                    "us", "us", "us", "us", "us", "us",  "us", "us", "us", "us", "us", "us"
    };

    private List<String> genres = new ArrayList<>();
    private static final Logger logger = Logger.getLogger(GenreHandler.class);

    public static GenreHandler create (String[] genres)
    {
        GenreHandler countryTagCounter = new GenreHandler();

        for (String g : genres)
        {
            countryTagCounter.genres.add(g);
        }
        return countryTagCounter;
    }

    public List<String> getGenresRaw()
    {
        List<String> result = new ArrayList<>();
        for (String genre : genres)
        {
            String aGenre = removeQualifier(genre);
            if (!result.contains(aGenre))
            {
                result.add(aGenre);
            }
        }
        return result;
    }

    public String getCountry()
    {
        TagCounter<String>  tagCounter = new TagCounter<>();
        for (String genre : genres)
        {
            String[] parts = genre.split(" ");
            for (String part : parts)
            {
                logger.info ("\t\t\t" + part);
                if (isCountryOrCity(part))
                {
                    tagCounter.add(toCountry(part));
                }
            }
        }

        return tagCounter.getMostPopular();
    }


    private boolean isCountryOrCity(String part)
    {
        if (toCountry(part) != null)
        {
            return true;
        }
        return false;
    }

    private String toCountry(String part)
    {

        for (int i = 0; i < countries.length; i++)
        {
            if (part.equalsIgnoreCase(countries[i]))
            {
                return codes[i];
            }
        }
        return null;
    }


    public GenreData getBestGogoGenre()
    {
        TagCounter<Genre> tagCounter = new TagCounter<>();
        for (String aGenre : genres)
        {
            String str = removeQualifier(aGenre);
            Genre g = Genre.create(str);
            tagCounter.add(g);
        }
        if (tagCounter.getMostPopular() != null)
        {
            return new GenreData(tagCounter.getMostPopular());
        }

        return null;
    }

    private String removeQualifier(String aGenre)
    {
        for (String s : countries)
        {
            if (aGenre.toLowerCase().contains(s))
            {
                return aGenre.replace(s, "").trim();
            }
        }
        return aGenre;
    }


}
