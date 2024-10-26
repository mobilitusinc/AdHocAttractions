package com.mobilitus.attractionscmd.tix.data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.log4j.Logger;
import org.jsoup.Jsoup;

import com.google.gson.annotations.SerializedName;
import com.mobilitus.util.data.attractions.AttractionType;
import com.mobilitus.util.data.attractions.MinorAttractionType;
import com.mobilitus.util.data.schema.SchemaArtist;
import com.mobilitus.util.data.schema.SchemaEvent;
import com.mobilitus.util.data.ticketMaster.EventGhettoData;

/**
 * @author helgaw
 * @todo add class description.
 * @since 9/10/20 14:24
 */
public class TixEvent
{
    private Long id;
    @SerializedName(value = "Dates", alternate = {"dates"})
    private List<TixDate> dates;
    @SerializedName(value = "EventGroupId", alternate = {"eventGroupId"})
    private Long eventGroupID;
    @SerializedName(value = "Name", alternate = {"name"})
    private String name;
    @SerializedName(value = "NameEnglish")
    private String nameEnglish;
    @SerializedName(value = "SubTitle", alternate = {"subTitle"})
    private String subTitle;
    @SerializedName(value = "SubTitleEnglish")
    private String subTitleEnglish;
    @SerializedName(value = "Description", alternate = {"description"})
    private String description;
    @SerializedName(value = "DescriptionEnglish", alternate = {"descriptionEnglish"})
    private String descriptionEnglish;
    @SerializedName(value = "EventImagePath", alternate = {"eventImagePath"})
    private String image;
    @SerializedName(value = "FeaturedImagePath", alternate = {"featuredImagePath"})
    private String featuredImage;

    private String url;

    @SerializedName(value = "PurchaseUrl", alternate = {"purchaseUrl"})
    private String purchaseUrl;
    @SerializedName(value = "PurchaseUrlEnglish", alternate = {"purchaseUrlEnglish"})
    private String purchaseUrlEnglish;
    @SerializedName(value = "ExternalUrl")
    private String externalUrl;

    private String hostUrl;
    private String hostName;
    private String kenni;
    private Integer displayType;
//    private DateTime dateFrom;
//    private DateTime dateTo;
    private static final Logger logger = Logger.getLogger(TixEvent.class);


    public List<TixDate> getDates()
    {
        return dates;
    }

    public List<SchemaEvent> toDataList()
    {
        if (dates == null)
        {
            return Collections.emptyList();
        }

        List<SchemaEvent> events = new ArrayList<>(dates.size());

        Boolean isTribute = false;


        for (TixDate concert : dates)
        {
            SchemaEvent data = concert.toData(this);
            AttractionTypeHandler attractionHandler = new AttractionTypeHandler(this, concert);

            String title = attractionHandler.createTitle(getName(), data.getName());
            data.setName(title);
            data.setDescription(getDescription());

            AttractionType majorAttractionType = attractionHandler.getMajorAttractionType();
            data.setType(majorAttractionType);
            for (MinorAttractionType minor : attractionHandler.getMinorAttractionTypes())
            {
                data.addInternalType(minor.name());
            }
            /*if (attractionHandler.getGenre() != null)
            {
                data.addGogoGenre(Genre.create(attractionHandler.getGenre().getId()));
            }*/


//                logger.info("'" + title + "' ('" + getName() +"' / '" + concert.getName() + "') " + getSubTitle()  +  " // " + majorAttractionType + " "  + " // artists "  + getArtistNames(concert) );
//
//             logger.info (data.getTitle() + " --> " + getName());

            if (eventGroupID != 0)
            {
                data.setId(eventGroupID + "-" + data.getId());
            }
            else
            {
                if (hostUrl != null && !hostUrl.isEmpty())
                {
                    ;
                }
                {
                    data.setId( hostName + "-" + id);
                    data.setUrl(hostUrl + url);
                    if ((concert.getMinPrice()  == null || concert.getMinPrice().intValue() == 0)  &&
                        (concert.getMaxPrice()  == null || concert.getMaxPrice().intValue() == 0))
                    {
                        data.addAttribute("freeEntry", "true");
                        data.addAttribute("url", hostUrl + url);
                    }
                }
            }

            /*data.addAttribute("description", cleanMarkup(getDescription()));
            data.addAttribute("description-html",  getDescription());
*/
            if (attractionHandler.isTribute())
            {
                data.addAttribute("genre", "tribute");
                data.addHint("tribute","true");
            }
//            logger.info("event is " + title + " " + getCategories());

            for (SchemaArtist f : getArtists(concert, majorAttractionType))
            {
                data.addArtist(f);
                /*if (data.getMajorType() == null && f.getMajorType() != null)
                {
                    data.setMajorType(f.getMajorType());
                }
                if (f.getMajorType() == null)
                {
                    f.setMajorType(majorAttractionType);
                }
                for (MinorAttractionType minor : f.getMinorTypes())
                {
                    data.addMinorType(minor);
                }
                if (f.getGogoGenre() != null)
                {
                    data.setGogoGenre(f.getGogoGenre());
                }
                for (AttributeData att : f.getAttributes())
                {
                    data.addAttribute(att.getKey(), att.getValue());
                }*/

            }
            if (getImageUrl() != null && !getImageUrl().isEmpty())
            {
                // image url is a squareish image getImage is a header image (get dimensions)
                String imageUrl = getImageUrl();
                String featuredImage = getFeaturedImage();

                if (imageUrl != null && !imageUrl.isEmpty())
                {
                    imageUrl = imageUrl.replaceAll("https://cdn.tix.is/tix", "https://cdn.tixly.com/is/tix");
                }
                if(featuredImage != null && !featuredImage.isEmpty())
                {
                    featuredImage = featuredImage.replaceAll("https://cdn.tix.is/tix", "https://cdn.tixly.com/is/tix");
                }

                data.addImage(imageUrl);
                //data.addImage(featuredImage);
            }
            data.addAttribute("eventURL", getPurchaseUrl());
            data.setUrl(getPurchaseUrl());



            //data.setPurchaseLink(getPurchaseUrl());
             if (getDescriptionEnglish() != null && !getDescriptionEnglish().isEmpty())
             {
                 data.addAttribute("description-en", cleanMarkup(getDescriptionEnglish()));
                 data.addAttribute("description-en-html", getDescriptionEnglish());
             }
            if (getSubTitleEnglish() != null && !getSubTitleEnglish().isEmpty())
            {
                data.addAttribute("subtitle-en", getSubTitleEnglish());
            }
            if (getSubTitle() != null&& !getSubTitle().isEmpty())
            {
                data.addAttribute("subtitle", getSubTitle());
            }
            if (getNameEnglish() != null && !getNameEnglish().isEmpty())
            {
                data.addAttribute("name-en", getNameEnglish());
            }
            //data.setStaleAfter(null);
            events.add(data);

        }
        return events;
    }

    private void extendForHost(String hostName, String hall, EventGhettoData data)
    {
        if (hostName.equalsIgnoreCase("Salurinn"))
        {
             extendSalurinn(data);
        }
        if (hostName.equalsIgnoreCase("Gerðarsafn"))
        {
            extendGerdarsafn(data);
        }
        if (hostName.equalsIgnoreCase("Bókasafn Kópavogs"))
        {
            extendBokKop(data, hall);
        }
        if (hostName.equalsIgnoreCase("Náttúrufræðistofa Kópavogs"))
        {
            extendNatKop(data);
        }
}

    private void extendNatKop(EventGhettoData data)
    {
        /*if (data.getVenue() == null)
        {
            data.setVenue(KopavogurNatureMuseumVenue.createVenue());
        }
        KopavogurNatureMuseumVenue.addDetails(data.getAttribute("url"), data);

        if (data.getMajorType() == null)
        {
            AttractionType type =KopavogurNatureMuseumVenue.getMajorAttractionType(data);
            data.setMajorType(type);
        }
        List<MinorAttractionType> minor =KopavogurNatureMuseumVenue.getMinorAttractionType(data);

        List<GogoClassification> classifications = KopavogurNatureMuseumVenue.getClassifications(data);
//        GenreData genre =KopavogurNatureMuseumVenue.getGenre(data);
        for (MinorAttractionType min : minor)
        {
            data.addMinorType(min);
        }
        for (GogoClassification clas : classifications)
        {
            data.addClassification(clas);
        }*/
//        if (genre != null)
//        {
//            data.setGogoGenre(genre);
//        }
        logger.info (data.getTitle() + "\t"  + data.getAttribute("description"));
        logger.info ("major " + data.getMajorType());
        logger.info ("minor " + data.getMinorTypes());
        logger.info ("class " + data.getClassifications());
        logger.info ("");
    }



    private void extendBokKop(EventGhettoData data, String hall)
    {
        /*if (data.getVenue() == null)
        {
            data.setVenue(KopavogurLibraryVenue.createVenue(hall));
        }
        KopavogurLibraryVenue.addDetails(data.getAttribute("url"), data);

        if (data.getMajorType() == null)
        {
            AttractionType type = KopavogurLibraryVenue.getMajorAttractionType(data);
            data.setMajorType(type);
        }
        List<MinorAttractionType> minor = KopavogurLibraryVenue.getMinorAttractionType(data);

        List<GogoClassification> classifications = KopavogurLibraryVenue.getClassifications(data);
        GenreData genre = KopavogurLibraryVenue.getGenre(data);
        for (MinorAttractionType min : minor)
        {
            data.addMinorType(min);
        }
        for (GogoClassification clas : classifications)
        {
            data.addClassification(clas);
        }
        if (genre != null)
        {
            data.setGogoGenre(genre);
        }
        logger.info (data.getTitle() + "\t"  + data.getAttribute("description"));
        logger.info ("major " + data.getMajorType());
        logger.info ("minor " + data.getMinorTypes());
        logger.info ("class " + data.getClassifications());
        logger.info ("");*/
    }

    private void extendSalurinn(EventGhettoData data)
    {
        if (data.getVenue() == null)
        {
            data.setVenue(SalurinnVenue.createVenue());
        }
        SalurinnVenue.addDetails(data.getAttribute("url"), data);

        if (data.getMajorType() == null)
        {
            data.setMajorType(AttractionType.music);
        }
        // parse subPage
    }


    private void extendGerdarsafn(EventGhettoData data)
    {
        /*if (data.getVenue() == null)
        {
            data.setVenue(GerdarsafnVenue.createVenue());
        }
        GerdarsafnVenue.addDetails(data.getAttribute("url"), data);
        if (data.getMajorType() == null)
        {
            data.setMajorType(AttractionType.arts);
        }*/
        // parse subPage
    }


    private String getImageUrl()
    {
        return this.getFeaturedImage();
    }


    public String getDescription()
    {
        return description;
    }



    public String getName()
    {
        if (name == null)
        {
            return "";
        }
        return name.trim();
    }


    public String getNameEnglish()
    {
        return nameEnglish;
    }


    public String getSubTitle()
    {
        return subTitle;
    }


    public String getSubTitleEnglish()
    {
        return subTitleEnglish;
    }


    public String getDescriptionEnglish()
    {
        return descriptionEnglish;
    }


    public String getImage()
    {
        if (hostUrl != null && !hostUrl.isEmpty() && !image.startsWith("http"))
        {
            return hostUrl + image;
        }
        return image;
    }


    public String getFeaturedImage()
    {
        if (hostUrl != null && !hostUrl.isEmpty() && !featuredImage.startsWith("http"))
        {
            return hostUrl + featuredImage;
        }

        return featuredImage;
    }

    public Long getId()
    {
        return id;
    }

    public Long getEventGroupID()
    {
        return eventGroupID;
    }

    public String getUrl()
    {
        return url;
    }

    public String getHostUrl()
    {
        return hostUrl;
    }

    public String getHostName()
    {
        return hostName;
    }

    public String getPurchaseUrl()
    {
        return purchaseUrl;
    }


    public String getPurchaseUrlEnglish()
    {
        return purchaseUrlEnglish;
    }


    public String getExternalUrl()
    {
        return externalUrl;
    }

    public String getArtistNames(TixDate concert)
    {
        StringBuilder buf = new StringBuilder();

        for (SchemaArtist artist : getArtists(concert, null))
        {
            if (buf.length() > 1)
            {
                buf.append(", ");
            }
            buf.append("\'" + artist.getName() + "\'");
        }

        return "[" + buf.toString() + "]";
    }



    public List<SchemaArtist> getArtists(TixDate date)
    {
        TixArtistHandler artistHandler = new TixArtistHandler(this, date, null);
        List<SchemaArtist> artists =  artistHandler.getArtists();
        return artists;
    }


    public List<SchemaArtist> getArtists(TixDate date, AttractionType attractionType)
    {
        TixArtistHandler artistHandler = new TixArtistHandler(this, date, attractionType);
        List<SchemaArtist> artists =  artistHandler.getArtists();
//        for (ArtistData artist : artists)
//        {
//            logger.info("\tartist '" + artist.getName() + "' '" + artist.getMajorType() + "' genre: '" + artist.getGogoGenre() + "' minor " +  artist.getMinorTypes() + "  classifications " + artist.getClassifications() + " <-- " + date.getCategories() + " " + date.getTags());
//
//        }
        return artists;
    }



    public List<String> getCategories()
    {
        List<String> result = new ArrayList<>(10);
        for (TixDate d : dates)
        {
            for (String str : d.getCategories())
            {
                if (!result.contains(str))
                {
                    result.add(str);
                }
            }
        }
        return result;
    }


    public List<String> getTags()
    {
        List<String> result = new ArrayList<>(10);
        for (TixDate d : dates)
        {
            if (d.getTags() != null)
            {
                for (String str : d.getTags())
                {
                    if (!result.contains(str))
                    {
                        result.add(str);
                    }
                }
            }
        }
        return result;
    }

    private String cleanMarkup (String str)
    {
        String myStr = str.replaceAll("(\r?\n)+", " ");
        myStr = myStr.replaceAll("<br>", "<br>\n");
        myStr = myStr.replaceAll("<p>", "<p>\n\n");
        String result = Jsoup.parse(myStr).wholeText();

//         logger.info(str);
//         logger.info(result);
        result =  result.replaceAll("(\r?\n)(\r?\n)+", "\n\n");
        result =  result.replaceAll("  +", " ");
//         logger.info(" ");
//         logger.info(result.trim());
        return result.trim();
    }



    public Boolean containsText(String txt)
    {
        if (name != null && name.toLowerCase().contains(txt))
        {
            return true;
        }
        if (nameEnglish != null && nameEnglish.toLowerCase().contains(txt))
        {
            return true;
        }
        if (subTitle != null && subTitle.toLowerCase().contains(txt))
        {
            return true;
        }
        if (subTitleEnglish != null && subTitleEnglish.toLowerCase().contains(txt))
        {
            return true;
        }
        if (description != null && description.toLowerCase().contains(txt))
        {
            return true;
        }
        if (descriptionEnglish != null && descriptionEnglish.toLowerCase().contains(txt))
        {
            return true;
        }

        return false;
    }

    @Override
    public String toString()
    {
        final StringBuilder sb = new StringBuilder("TixEvent{");
        sb.append("  name='").append(name).append("', [");
        for (TixDate date : dates)
        {
            sb.append(date.getStartDateFormat() + " ");
        }

        sb.append("]}");
        return sb.toString();
    }

    public String createTitle(String name, String name1)
    {
        return AttractionTypeHandler.createTitle(name, name1);
    }
}
