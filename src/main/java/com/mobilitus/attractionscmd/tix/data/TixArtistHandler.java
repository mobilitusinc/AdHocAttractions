package com.mobilitus.attractionscmd.tix.data;

import com.mobilitus.util.data.attractions.ArtistData;
import com.mobilitus.util.data.attractions.AttractionType;
import com.mobilitus.util.data.attractions.Genre;
import com.mobilitus.util.data.attractions.GenreData;
import com.mobilitus.util.data.attractions.GogoClassification;
import com.mobilitus.util.data.attractions.MinorAttractionType;
import com.mobilitus.util.data.attractions.textHandler.IcelandicTextHandler;
import com.mobilitus.util.data.location.LocationData;
import com.mobilitus.util.hexia.location.CountryCode;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.jsoup.Jsoup;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author helgaw
 * @todo add class description.
 * @since 8/17/21 11:20
 */
public class TixArtistHandler
{
    private final TixEvent event;
    private final TixDate date;
    private AttractionType majorAttractionType;
    private AttractionTypeHandler attractionTypeHandler;

    private static final Logger logger = Logger.getLogger(TixArtistHandler.class);

    public TixArtistHandler(TixEvent tixEvent, TixDate date, AttractionType attractionType)
    {
        event = tixEvent;
        this.date = date;
        attractionTypeHandler = new AttractionTypeHandler(tixEvent, date);
        majorAttractionType = attractionTypeHandler.getMajorAttractionType();
        if (majorAttractionType == null)
        {
            majorAttractionType = attractionType;
        }
    }


    public List<ArtistData> getArtists()
    {
        if (majorAttractionType == null)
        {
            return createBasicArtist();
        }
        if (majorAttractionType == AttractionType.sports)
        {
            TixSportsTeamHandler team = new TixSportsTeamHandler();
            return team.getSportsTeam(event, date);
        }

        if (majorAttractionType == AttractionType.theater)
        {
            return getTheaterArtists();
        }
        if (majorAttractionType == AttractionType.arts)
        {
            return getTheaterArtists();
        }
        else if (majorAttractionType == AttractionType.music)
        {
            return getMusicArtists();
        }
        return createBasicArtist();
    }

    private List<ArtistData> createBasicArtist()
    {
        ArtistData artist = new ArtistData();

        String title = cleanName(event.createTitle(event.getName(), date.getName()));
        if (skipName(title))
        {
            return Collections.emptyList();
        }
        artist.setName(title);
        artist.setBestImage(event.getFeaturedImage());
        artist.setBestThumb(event.getFeaturedImage());
        if (majorAttractionType !=  AttractionType.film)
        {
            artist.setHome(new LocationData(CountryCode.is));
        }
        if (majorAttractionType != null)
        {
            artist.setArtistID(title.toLowerCase() + " " + majorAttractionType.name());
        }
        else
        {
            artist.setArtistID(title.toLowerCase() + " " + AttractionType.other.name());
        }

        artist.setMajorType(majorAttractionType);
        artist.addClassifications(IcelandicTextHandler.getClassifications(title, event.getDescription(), event.getCategories()));
        artist.addMinorTypes(IcelandicTextHandler.getMinorAttractionTypes(title, event.getDescription(), event.getCategories()));

        List<ArtistData> allArtists = new ArrayList<>();
        allArtists.add(artist);

        return allArtists;

    }

    private List<ArtistData> getMusicArtists()
    {
        String title = cleanName(event.createTitle(event.getName(), date.getName()));

        List<ArtistData> artists = new ArrayList<>(3);
        if (attractionTypeHandler.isTribute())
        {
            ArtistData artist = createIcelandicMusicArtist(title.trim());
            if (artist != null)
            {
                artists.add(artist);
            }
            return artists;
        }
        String[] parts = title.split("[|/:\\-–]");

        parts = mergeIfNeeded(parts);
        List<String> artistNames = new ArrayList<>(10);
        Boolean didSkip = false;
        for (String part : parts)
        {
            String[] parts2 = part.split("\\sog\\s|\\s&\\s|\\sásamt\\s|\\s\\+\\s");
            for (String pp : parts2)
            {
                String[] parts3 = pp.split(",");
                for (String p : parts3)
                {
                    if (p.trim().isEmpty())
                    {
                        continue;
                    }
                    String name = cleanName(p);
                    if (!name.isEmpty())
                    {
                        if (!skipName(name))
                        {
                            artistNames.add(name);
                        }
                        else
                        {
                            didSkip = true;
                        }
                    }
                }
            }
        }
//        List<String> moreArtists = getArtistsFromText(event.getDescription());
//        artistNames.addAll(moreArtists);


        for (String part : artistNames)
        {
            ArtistData artist = createIcelandicMusicArtist(part.trim());
            if (artist != null && !artists.contains(artist))
            {
//                logger.info("artist '" + artist.getName()+ "' '" + artist.getGogoGenre() + "' <-- "  + event.getCategories());

                artists.add(artist);
            }
        }

        if (parts.length > 1 || artists.isEmpty())
        {
            if (!didSkip)
            {
                ArtistData artist = createMusicArtist(event.getName().trim());
                if (artist != null && !artists.contains(artist))
                {

//                    logger.info("music artist '" + artist.getName() + "' '" + artist.getMajorType() + "' genre: '" + artist.getGogoGenre() + "' minor " +  artist.getMinorTypes() + "' classifications " + artist.getClassifications() + " <-- " + event.getCategories());

                    List<ArtistData> allArtists = new ArrayList<>(artists.size() + 1);
                    allArtists.add(artist);
                    allArtists.addAll(artists);
                    // the whole name should be first - as a main artist
                    return allArtists;
                }
            }
        }
        return artists;
    }

    private String[] mergeIfNeeded(String[] parts)
    {
        List<String> newList = new ArrayList<>(parts.length);
        boolean skipNext = false;
        if (parts != null && parts.length > 1)
        {
            for (int i = 0; i < parts.length; i++)
            {
                if (skipNext)
                {
                    skipNext = !skipNext;
                    continue;
                }
                if (i < (parts.length - 1) && parts[i].equalsIgnoreCase("ný") && parts[i + 1].toLowerCase().startsWith("klassík"))
                {
                    newList.add(parts[i] + "-" + parts[i + 1]);
                    skipNext = true;
                }
                else if (i < (parts.length - 1) && parts[i].toLowerCase().endsWith("austur") && parts[i + 1].toLowerCase().startsWith("evrópu"))
                {
                    newList.add(parts[i] + "-" + parts[i + 1]);
                    skipNext = true;
                }
                else
                {
                    newList.add(parts[i]);
                }
            }
            return newList.toArray(new String[0]);
        }
        else
        {
            return parts;
        }
    }


    private List<ArtistData> getTheaterArtists()
    {
        String name = cleanName(event.createTitle(event.getName(), date.getName()));


        ArtistData artist = new ArtistData();
        artist.setName(name);
        artist.setMajorType(majorAttractionType);
        artist.setArtistID(name.toLowerCase() + " " + majorAttractionType);
        artist.setHome(new LocationData(CountryCode.is));

        if (attractionTypeHandler.getMinorAttractionTypes() != null)
        {
            for (MinorAttractionType min : attractionTypeHandler.getMinorAttractionTypes())
            {
                artist.addMinorType(min);
            }
        }

        if (attractionTypeHandler.getClassifications() != null)
        {
            artist.addClassifications(attractionTypeHandler.getClassifications());
        }

        artist.setBestImage(event.getFeaturedImage());
        artist.setBestThumb(event.getFeaturedImage());

        List<ArtistData> allArtists = new ArrayList<>();
        allArtists.add(artist);

        return allArtists;
    }


    private ArtistData createMusicArtist(String name)
    {
        ArtistData artist = new ArtistData();
        String title = cleanName(name);
        artist.setName(title);
        artist.setMajorType(AttractionType.music);
        GenreData genre = attractionTypeHandler.getGenre();
        artist.setGogoGenre(genre);
        if (artist.getGogoGenre() != null)
        {
            artist.setArtistID(title.toLowerCase() + " " + AttractionType.music + " " + artist.getGogoGenre().getId());
        }
        else
        {
            artist.setArtistID(title.toLowerCase() + " " + AttractionType.music);
        }

        List<GogoClassification> classifications = IcelandicTextHandler.getClassifications(name, event.getDescription(), event.getCategories());
        artist.addClassifications (classifications);
        for (String str : event.getCategories())
        {
            if (artist.getGogoGenre() == null)
            {
                artist.setGogoGenre(new GenreData(Genre.create(str)));
            }
            artist.addAttribute("genre", str);
        }

        artist.setHome(new LocationData(CountryCode.is));

        artist.setBestImage(event.getFeaturedImage());
        artist.addAttribute("imageUrl", event.getFeaturedImage());
        artist.addAttribute("tix", "true");
        return artist;
    }


    private ArtistData createIcelandicMusicArtist(String _name)
    {
        try
        {
            // if the name is something like 3  it is most likely the number of the event in a series.  Skip it.
            Integer.parseInt(_name.trim());
            return null;
        }
        catch (NumberFormatException e)
        {
        }

        String name = cleanName(_name);


        if (name.toLowerCase().contains("þorláksmessutónleikar bubba"))
        {
            return createMusicArtist("Bubbi Morthens");
        }
        if (name.toLowerCase().contains("af fingrum fram"))
        {
            return createMusicArtist("Af fingrum fram spjalltónleikaröð Jóns Ólafssonar");
        }
        if (name.toLowerCase().contains("spjalltónleikaröð jóns"))
        {
            return createMusicArtist("Af fingrum fram spjalltónleikaröð Jóns Ólafssonar");
        }
        if (name.toLowerCase().contains("sinfóníunar"))
        {
            return createMusicArtist("Sinfóníuhljómsveit Íslands");
        }
        if (name.toLowerCase().contains("sinfóníuhljómsveitar íslands"))
        {
            return createMusicArtist("Sinfóníuhljómsveit Íslands");
        }
        if (name.toLowerCase().contains("sinfónían"))
        {
            return createMusicArtist("Sinfóníuhljómsveit Íslands");
        }
        if (name.equalsIgnoreCase("sinfó"))
        {
            return createMusicArtist("Sinfóníuhljómsveit Íslands");
        }
        if (name.toLowerCase().contains("sinfóníuhljómsveitar íslands"))
        {
            return createMusicArtist("Sinfóníuhljómsveit Íslands");
        }
        if (name.toLowerCase().contains("sinfóníuhljómsveit íslands"))
        {
            return createMusicArtist("Sinfóníuhljómsveit Íslands");
        }
        if (name.toLowerCase().contains("íslenska óperan"))
        {
            return createArtist("Íslenska Óperan", AttractionType.theater, MinorAttractionType.opera);
        }


        return createMusicArtist(name);
    }

    private ArtistData createArtist(String name, AttractionType major, MinorAttractionType minor)
    {
        ArtistData artist = new ArtistData();
        String title = cleanName(name);
        if (skipName(title))
        {
            return null;
        }
        artist.setName(title);
        artist.setArtistID(title.toLowerCase() + " " + major.name());

        artist.setMajorType(major);
//        artist.setType(major.name());
        artist.addAttribute("type", major.name());
        GenreData genre = attractionTypeHandler.getGenre();
        artist.setGogoGenre(genre);
        artist.addMinorType(minor);
        artist.setHome(new LocationData(CountryCode.is));


        artist.setBestImage(event.getFeaturedImage());
        artist.addAttribute("imageUrl", event.getFeaturedImage());
        artist.addAttribute("tix", "true");
        return artist;

    }


    private List<String> getArtistsFromText(String description)
    {
        return Collections.emptyList();
    }


    private boolean isDate(String name)
    {
        String[] parts = name.split("[\\.\\s?]");
        List<String> res = new ArrayList<>(parts.length);
        for (String part : parts)
        {
            if (part.trim().isEmpty())
            {
                continue;
            }
            res.add(part.trim());
        }
        if (res.size() == 2)
        {
            if (digitsOnly(res.get(0)) && res.get(1).equalsIgnoreCase("Sýning"))
            {
                return true;
            }
            if (digitsOnly(res.get(0)) && isMonth(res.get(1)))
            {
                return true;
            }
        }

        if (isUSDate(res))
        {
            return true;
        }
        try
        {
            String pattern = "dd.mm.yyyy";
            DateTime dateTime = DateTime.parse(name, DateTimeFormat.forPattern(pattern));
            if (dateTime != null)
            {
                return true;
            }
            pattern = "dd.mm.yy";
            dateTime = DateTime.parse(name, DateTimeFormat.forPattern(pattern));
            if (dateTime != null)
            {
                return true;
            }
            pattern = "dd.mm.";
            dateTime = DateTime.parse(name, DateTimeFormat.forPattern(pattern));
            if (dateTime != null)
            {
                return true;
            }
        }
        catch (Exception e)
        {
            // not a date then
        }
        return false;
    }

    private boolean isUSDate(List<String> res)
    {
        if (res == null || res.isEmpty())
        {
            return false;
        }

        Pattern datePattern = Pattern.compile("\\d{1,2}(?:st|nd|rd|th)");
        if (isMonth(res.get(0)))
        {
            for (int i = 1; i < res.size(); i++)
            {
                if (res.get(i).equalsIgnoreCase("to"))
                {
                    continue;
                }

                if (!datePattern.matcher(res.get(i)).matches())
                {
                    return false;
                }
            }
        }
        else
        {
            return false;
        }

        return true;
    }


    private boolean isMonth(String str)
    {
        String[] months = {"janúar", "febrúar", "mars", "apríl", "maí", "júní", "júlí", "ágúst", "september", "október", "nóvember", "desember"};
        String[] monthsEn = {"january", "february", "mars", "april", "may", "june", "july", "august", "september", "oktober", "november", "december"};
        List<String> monthList = new ArrayList<>();
        monthList.addAll(Arrays.asList(months));
        monthList.addAll(Arrays.asList(monthsEn));

        if (monthList.contains(str.toLowerCase()))
        {
            return true;
        }

        return false;

    }

    private boolean isDay(String str)
    {
        String[] months = {"mánudagur", "þriðjudagur", "miðvikudagur", "fimmtudagur", "föstudagur", "laugardagur", "sunnudagur"};
        String[] monthsEn = {"monday", "tuesday", "wednesday", "thursday", "friday", "saturday", "sunday"};
        List<String> monthList = new ArrayList<>();
        monthList.addAll(Arrays.asList(months));
        monthList.addAll(Arrays.asList(monthsEn));

        if (monthList.contains(str.toLowerCase()))
        {
            return true;
        }

        return false;

    }

    private boolean isShowNumber(String name)
    {
        String[] parts = name.split("[\\.\\s]");
        List<String> res = new ArrayList<>(parts.length);
        for (String part : parts)
        {
            if (part.trim().isEmpty())
            {
                continue;
            }
            res.add(part.trim());
        }
        if (res.size() == 2)
        {
            if (digitsOnly(res.get(0)) && res.get(1).equalsIgnoreCase("Sýning"))
            {
                return true;
            }
        }
        return false;
    }

    private String cleanName(String name)
    {
        String str = name.trim();

//        private String cleanName(String p)
//        {
//            String name = p.trim();
//            if (name.toLowerCase().contains("með"))
//            {
//                name = name.substring(name.toLowerCase().indexOf("með") + 3);
//            }
//            if (isDate(name))
//            {
//                return "";
//            }
//            if (isShowNumber(name))
//            {
//                return "";
//            }
//
//            name = remove (name,"-");
//
//
//            return name.trim();
//        }
        ///        String[] junk = {"dagana","ICE sub", "ENG sub", "NO sub", "2 fyrir 1", "25. júlí við tölvu",
        //       "Velkomin heim", "Sumartónleikaröð",  "Hátíðarpassi",    "Opnunartónleikar", "Myndlykill Símans",
        //               "síðustu sýningar", "Þorlákshöfn",    "Radical Waves á Húrra:", "Eins árs afmælis",  "Opera",   "öð",  "Ballet", "cinemalive",
        //               " 6th August 2021","Síminn", "ekkert", "hljómsveit í Bæjarbíó",  "Síðustu sýningar", "Norræna húsið","Sumartónleikar", "Þorlákshöfn", "Risa sveitaball í Sjallanum", "Hátíðarpassi" };

        str = str.replaceAll("[F|f]orsýning", "");
        str = str.replace("á Listahátíð í Reykjavík", "");
        str = str.replaceAll("\\sí\\s+(.)+$", "");
        str = str.replaceAll("[T|t]ónleihkr", "");
        str = str.replaceAll("[F|f]orsýning", "");
        str = str.replaceAll("FORSÝNING", "");
        str = str.replaceAll("[F|f]rumsýning", "");
        str = str.replaceAll("[L|l]okasýning", "");
        str = str.replaceAll("LOKASÝNING", "");
        str = str.replaceAll("[A|a]ukasýning", "");
        str = str.replaceAll("[A|a]ukatónleikar", "");
        str = str.replaceAll("SUMARHÁTÍÐ", "");
        str = str.replaceAll("AUKASÝNING", "");
        str = str.replaceAll("AUKATÓNLEIKAR", "");
        str = str.replaceAll("UPPSELT", "");
        str = str.replaceAll("FÁIR MIÐAR EFTIR", "");
        str = str.replaceAll("[F|f]áir miðar eftir", "");
        str = str.replaceAll("[Í|í] [B|b][O|o][Ð|ð][I|i] .*", "");

        str = str.replaceAll("[S|s]íðustu sýningar", "");

        str = str.replaceAll("\\d*\\s*\\.?\\s*viðbótarsýning", "");
        str = str.replaceAll("\\-?\\s*\\d*\\s*\\.?\\s*\\-?\\s*[S|s]ýning", "");
        str = str.replaceAll("tónleikasýning", "");
        str = str.replaceAll("\\sex-.*", "");

        str = str.replaceAll("stórafmælistónleikar", "");
        str = str.replaceAll("STÓRAFMÆLISTÓNLEIKAR", "");
        str = str.replaceAll("STÓR-AFMÆLIS-TÓNLEIKAR", "");

        str = str.replaceAll("[E|e]ins\\s*ár[s|a](\\s*afmæli(s)?)?(\\s*afmælissýning)?", "");
        str = str.replaceAll("\\d*\\s*\\.?\\s*ár[s|a](\\s*afmæli)?(\\s*afmælissýning)?", "");
        str = str.replaceAll("í\\s*\\d*\\s*ár", "");


        str = str.replaceAll("[S|s]umartónleikar", "");
        str = str.replaceAll("[S|s]umarhátíð", "");
        str = str.replaceAll("[F|f]jölskyldutónleikar", "");
        str = str.replaceAll("[O|o]pnunartónleikar", "");
        str = str.replaceAll("ÚTGÁFUTÓNLEIKAR", "");
        str = str.replaceAll("[Ú|ú]tskriftartónleikar", "");
        str = str.replaceAll("[Ú|ú]tgáfutónleikar", "");
        str = str.replaceAll("[Ú|ú]tgáfuhóf", "");
        str = str.replaceAll("[H|h]átíðarpassi", "");
        str = str.replaceAll("[J|j]ólatónleikar", "");

        str = str.replaceAll("[S|s]túka", "");
        str = str.replaceAll("[S|s]tæði", "");

        str = str.replaceAll("NO sub", "");
        str = str.replaceAll("ICE sub", "");
        str = str.replaceAll("ENG sub", "");
        str = str.replaceAll("FRESTAÐ", "");
        str = str.replaceAll("[F|f]restað", "");
        str = str.replaceAll("\\s*\\-?\\s*ný dagsetning tilkynnt síðar", "");

        // date of the form 11.11.1111
        str = str.replaceAll("(\\d{1,2}\\.){2}\\d{2,4}", "");
        str = str.replaceAll("20\\d\\d", "");
//        str = str.replaceAll("með\\s+.*", "");

        // remove somthing like (SE/NO)
        str = str.replaceAll("\\(\\w\\w(/\\w\\w)*\\)", "");
        // remove a hanging - from the start or end of a string
        str = str.replaceAll("(\\s+\\-?\\s+)*$", "");
        str = str.replaceAll("^(\\s+\\-?\\s+)*", "");
        str = str.replaceAll("\\s*\\|\\|\\s*$", "");
        str = str.replaceAll("^\\s*\\|\\|\\s*", "");

        // trim multiple consecutive spaces to one
        str = str.replaceAll("\\s+\\s*", " ");
        // remove empty ()
        str = str.replaceAll("\\(\\)", "");

        return str.trim();
    }


    private boolean digitsOnly(String str)
    {
        // Regex to check string
        // contains only digits
        String regex = "[0-9]+";

        // Compile the ReGex
        Pattern p = Pattern.compile(regex);

        // If the string is empty
        // return false
        if (str == null)
        {
            return false;
        }

        // Find match between given string
        // and regular expression
        // using Pattern.matcher()
        Matcher m = p.matcher(str);

        // Return if the string
        // matched the ReGex
        return m.matches();
    }

    private String cleanMarkup(String str)
    {
        String myStr = str.replaceAll("(\r?\n)+", " ");
        myStr = myStr.replaceAll("<br>", "<br>\n");
        myStr = myStr.replaceAll("<p>", "<p>\n\n");
        String result = Jsoup.parse(myStr).wholeText();

//         logger.info(str);
//         logger.info(result);
        result = result.replaceAll("(\r?\n)(\r?\n)+", "\n\n");
        result = result.replaceAll("  +", " ");
//         logger.info(" ");
//         logger.info(result.trim());
        return result.trim();
    }

    private String remove(String title, String subText)
    {
        int index = title.toLowerCase().indexOf(subText.toLowerCase());
        if (index > 0)
        {
            int len = subText.length();
            return title.substring(0, index) + title.substring(index + len);
        }
        return title;
    }


    private boolean skipName(String name)
    {

        if (NotArtists.get(name) != null)
        {
            return true;
        }
        if (isDate(name))
        {
            return true;
        }
        if (isDay(name))
        {
            return true;
        }
        return false;
    }


}
