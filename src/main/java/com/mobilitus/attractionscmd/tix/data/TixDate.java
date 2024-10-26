package com.mobilitus.attractionscmd.tix.data;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.LocalDateTime;

import com.google.gson.annotations.SerializedName;
import com.mobilitus.util.data.attractions.AttractionType;
import com.mobilitus.util.data.attractions.DataSource;
import com.mobilitus.util.data.attractions.MinorAttractionType;
import com.mobilitus.util.data.schema.SchemaEvent;
import com.mobilitus.util.data.schema.SchemaLocation;
import com.mobilitus.util.data.schema.SchemaOffers;
import com.mobilitus.util.data.ticketMaster.microflex.EventStatus;
import com.mobilitus.util.hexia.location.CountryCode;

/**
 * @author helgaw
 * @todo add class description.
 * @since 9/10/20 14:34
 */
public class TixDate
{
    @SerializedName(value = "Categories", alternate = {"categories"})
    private String categories;
    @SerializedName(value = "Tags")
    private String tags;
    @SerializedName(value = "MinPrice", alternate = {"minPrice"})
    private BigDecimal minPrice;
    @SerializedName(value = "MaxPrice", alternate = {"maxPrice"})
    private BigDecimal maxPrice;
    @SerializedName(value = "ProductPurchaseUrl")
    private String productPurchaseUrl;
    @SerializedName(value = "ProductPurchaseUrlEnglish")
    private String productPurchaseUrlEnglish;
    @SerializedName(value = "Products")
    private List products;
    @SerializedName(value = "EventId", alternate = {"eventId"})
    private Long eventID;
    @SerializedName(value = "Name", alternate = {"name"})
    private String name;
    @SerializedName(value = "StartDate")
    private String startDate;
    @SerializedName(value = "StartDateFormat", alternate = {"startDate"})
    private DateTime startDateFormat;
    @SerializedName(value = "EndDate")
    private String endDate;
    @SerializedName(value = "EndDateFormat", alternate = {"endDate"})
    private DateTime endDateFormat;
    @SerializedName(value = "WaitingList")
    private Boolean waitingList;
    @SerializedName(value = "OnlineSaleStart")
    private String onlineSaleStart;
    @SerializedName(value = "OnlineSaleStartFormat")
    private DateTime onlineSaleStartFormat;
    @SerializedName(value = "OnlineSaleEnd")
    private String onlineSaleEnd;
    @SerializedName(value = "OnlineSaleEndFormat")
    private DateTime onlineSaleEndFormat;
    @SerializedName(value = "Venue", alternate = {"venue"})
    private String venue;
    @SerializedName(value = "Hall", alternate = {"hall"})
    private String hall;
    @SerializedName(value = "Promoter", alternate = {"promoter"})
    private String promoter;
    @SerializedName(value = "SoldOut", alternate = {"soldOut"})
    private Boolean soldOut;
    @SerializedName(value = "Duration", alternate = {"duration"})
    private String duration;
    @SerializedName(value = "SaleStatus", alternate = {"saleStatus"})
    private Long saleStatus;
    @SerializedName(value = "Capacity", alternate = {"capacity"})
    private Long capacity;
    @SerializedName(value = "Remaining", alternate = {"remaining"})
    private Long remaining;
    @SerializedName(value = "SaleStatusText", alternate = {"saleSatusText"})
    private String saleStatusText;
    @SerializedName(value = "PurchaseUrl", alternate = {"purchaseUrl"})
    private String purchaseUrl;
    @SerializedName(value = "PurchaseUrlEnglish", alternate = {"purchaseUrlEnglish"})
    private String purchaseUrlEnglish;

    private static final Logger logger = Logger.getLogger(TixDate.class);

    public List<String> getCategories()
    {
        if (categories == null)
        {
            return Collections.emptyList();
        }

        String[] str = categories.split(",");
        return Arrays.asList(str);
    }


    public SchemaLocation getVenueData(TixEvent event)
    {
        if (venue != null)
        {
            SchemaLocation v = new SchemaLocation();

            v.setName(createVenueName());
            v.setSource(DataSource.tixis.name());

            if (hall != null && !venue.equalsIgnoreCase(hall))
            {
                v.addAttribute("hall", hall.trim());
            }

            //v.setTimezone(DateTimeZone.forID("Atlantic/Reykjavik"));
            //address.
            v.setId(v.getName().trim().toLowerCase());
            v.setAddressCountry(CountryCode.is.name());

            return v;
        }

        return null;
    }

    public SchemaEvent toData(TixEvent tixEvent)
    {
//        logger.info(getName() + " " + getType() + " onsale "  + getOnlineSaleStartFormat().toString("HH:mm dd MMM YYYY") +
//                    " showtime " +  getStartDateFormat() .toString("HH:mm dd MMM YYYY") + " " + getCategories() + " " + createEventStatus());
        SchemaEvent data = new SchemaEvent();

        data.setName(getName());

        data.setType("event");

        data.setId(getEventID() + "");

        String urlStr = getPrimaryEventUrl();
        /*if (urlStr != null && !urlStr.isEmpty())
        {
            data.addOffer(urlStr);

            data.setUrl(urlStr);
            data.addAttribute("partnerURL", urlStr);
        }*/

        // this is needed to let the worker know that
        // this import may have hot events set. (The feedreaded does, but not the DiscoAPI)


        data.addAttribute("importer", "feedreader");

        data.addAttribute("promoter", getPromoter());


        data.setEventStatus(createEventStatus());


        DateTimeZone timeZone = DateTimeZone.forID("Atlantic/Reykjavik");
        if (getVenue() != null)
        {

            SchemaLocation locVenue = getVenueData(tixEvent);
            data.setLocation(locVenue);

        }

        SchemaOffers offers = new SchemaOffers();
        data.addOffer("",getPurchaseUrl(),getMinPrice(),getMaxPrice(),getCurrency());


        if (getStartDateTime() != null)
        {
            // these dates are always GMT.  Need to check the timezone as well
            DateTime showTime =  getStartDateTime().toDateTime(timeZone);
            data.setStartDate(showTime.toLocalDateTime().toString());

            data.addAttribute("localTime", getStartLocalTime());
            if (getEndDateTime() != null)
            {
                data.setEndDate(getEndDateTime().toString());
            }
            else if (duration != null && !duration.isEmpty())
            {
                try
                {
                    String myDur = duration;
                    String[] parts = myDur.trim().split(" ");
                    if (myDur.contains("min"))
                    {
                        myDur = myDur.replace("min", "");
                    }
                    if (myDur.contains("mín"))
                    {
                        myDur = myDur.replace("mín", "");
                    }
                    if (parts[0].contains("[,.]"))
                    {
                        String str = parts[0];
                        if (str.contains(","))
                        {
                            str.replace(",", ".");
                        }
                        Double dur = Double.parseDouble(str);
                        data.setLocalEndTime(showTime.plusMinutes((int) Math.round(60 * dur)).toString());
                    }
                    else
                    {
                        Integer dur = Integer.parseInt(parts[0]);
                        data.setLocalEndTime(showTime.plusMinutes(dur).toString());
                    }
                }
                catch (NumberFormatException e)
                {
//                logger.error(StrUtil.stack2String(e));

                }
            }
        }


        //data.addAttribute("source", "tix.is");
        data.setSource(DataSource.tixis.name());

        data.addAttribute("channel", "primary");

        data.addAttribute("eventURL", getPurchaseUrl());

        AttractionTypeHandler attractionTypeHandler = new AttractionTypeHandler(tixEvent, this);
        AttractionType majorAttractionType = attractionTypeHandler.getMajorAttractionType();
        if ( majorAttractionType != null)
        {
            data.setAttractionType(majorAttractionType);
            //data.addAttribute("type",  majorAttractionType.name());
            //data.addAttribute("majorCategory",  majorAttractionType.name());
        }
        else
        {
//            logger.error("No major attraction type");
        }
        for (String category : getCategories())
        {
            /*if (!data.hasAttribute("type", category.trim()))
            {
                data.addAttribute("genre", category.trim());
            }
            String translated = attractionTypeHandler.inEnglish(category.trim());
            if (!translated.isEmpty() && !translated.equalsIgnoreCase(category.trim()))
            {
                if (!data.hasAttribute("type",translated))
                {
                    data.addAttribute("genre", translated);
                }
            }*/
        }

        for(MinorAttractionType minor : attractionTypeHandler.getMinorAttractionTypes())
        {
            if (minor != null)
            {
                data.addInternalType(minor.name());
            }
        }

        //data.setType(GhettoType.EVENT);
        if (getTags() != null && !getTags().isEmpty())
        {
            for (String tag : getTags())
            {
                data.addAttribute("tags", tag);
            }
        }


        if (attractionTypeHandler.isFestival())
        {
            data.addAttribute("festival", "true");
            data.addAttribute("festivalname", getFestivalName());
        }

        if (getWaitingList())
        {
            data.addAttribute("waitinglist", "true");
        }


        /*if (getOnsaleStartDateTime() != null)
        {
            data.setStartSellingAt(getOnsaleStartDateTime().toDateTime(timeZone));
        }
        if (getOnsaleEndDateTime() != null)
        {
            data.setEndSellingAt(getOnsaleEndDateTime().toDateTime(timeZone));
        }*/

        return data;
    }

    private String getFestivalName()
    {

        return "";
    }


    public List<String> getTags()
    {
        if (tags == null)
            return Collections.emptyList();

        String[] str = tags.split(",");
        return Arrays.asList(str);
    }


    public BigDecimal getMinPrice()
    {
        return minPrice;
    }


    public BigDecimal getMaxPrice()
    {
        return maxPrice;
    }


    public String getProductPurchaseUrl()
    {
        return productPurchaseUrl;
    }


    public String getProductPurchaseUrlEnglish()
    {
        return productPurchaseUrlEnglish;
    }


    public List getProducts()
    {
        return products;
    }


    public Long getEventID()
    {
        return eventID;
    }


    public String getName()
    {
        if (name == null)
            return "";
        return name.trim();
    }


    public DateTime getStartDateFormat()
    {
        return startDateFormat;
    }

    public DateTime getEndDateFormat()
    {
        return endDateFormat;
    }



    public Boolean getWaitingList()
    {
        if (waitingList == null)
        {
            return false;
        }
        return waitingList;
    }



    public DateTime getOnlineSaleStartFormat()
    {
        return onlineSaleStartFormat;
    }


    public DateTime getOnlineSaleEndFormat()
    {
        return onlineSaleEndFormat;
    }


    public String getVenue()
    {
        return venue;
    }


    public String getHall()
    {
        return hall;
    }


    public String getPromoter()
    {
        return promoter;
    }


    public Boolean getSoldOut()
    {
        return soldOut;
    }


    public String getDuration()
    {
        return duration;
    }


    public Long getSaleStatus()
    {
        return saleStatus;
    }


    public Long getCapacity()
    {
        return capacity;
    }


    public Long getRemaining()
    {
        return remaining;
    }


    public String getSaleStatusText()
    {
        return saleStatusText;
    }


    public String getPurchaseUrl()
    {
        return purchaseUrl;
    }


    public String getPurchaseUrlEnglish()
    {
        return purchaseUrlEnglish;
    }

    protected String createVenueName()
    {

        if (hall != null && venue != null && venue.equalsIgnoreCase(hall))
        {
            return (venue.trim());
        }
        else if (hall == null && venue == null )
        {
            return null;
        }
        else if (hall == null )
        {
            return (venue.trim());
        }
        else if (venue ==null)
        {
            return (hall.trim());
        }
        else
        {
            String name = venue + " | " + hall;
            return name.trim();
        }


    }

    private EventStatus createEventStatus()
    {
        Long status = getSaleStatus();
        if (status == null || status == 0)
        {
            return EventStatus.onSale;
        }
        else if (status == 1)
        {
            return EventStatus.fewTicketsAvailable;
        }
        else if (status == 2)
        {
            return EventStatus.soldOut;
        }
        else if (status == 3)
        {
            return EventStatus.cancelled;
        }
        else if (status == 4)
        {
            return EventStatus.notOnSale;
        }

        return null;
    }


    private DateTime createWholeday(String startLocalDate, DateTimeZone timeZone)
    {
        String dateString = startLocalDate + "T00:00";
        LocalDateTime localDate = LocalDateTime.parse(dateString);
        if (localDate != null && timeZone != null)
        {
            DateTime dt = localDate.toDateTime(timeZone);
            return dt;
        }
        return null;
    }



    private String getCurrency()
    {
        return "ISK";

    }

    private String getLeague(TixEvent event)
    {
        TixSportsTeamHandler handler = new TixSportsTeamHandler();
        return handler.getLeagueName(event, this);

    }


    private DateTime getOnsaleEndDateTime()
    {
        return getOnlineSaleEndFormat();
    }


    private DateTime getOnsaleStartDateTime()
    {
        return getOnlineSaleStartFormat();
    }


    private String getPrimaryEventUrl()
    {
        if (purchaseUrl != null)
        {
            return purchaseUrl;
        }

        return purchaseUrlEnglish;
    }


    private DateTime getStartDateTime()
    {
        return getStartDateFormat();
    }

    private DateTime getEndDateTime()
    {
        return getEndDateFormat();
    }


    private String getStartLocalTime()
    {
        if (getStartDateFormat() != null)
        {
            return getStartDateFormat().toString("HH:mm");
        }
        return null;
    }


    public Boolean containsText(String txt)
    {
        if (name != null && name.toLowerCase().contains(txt))
            return true;
        return false;
    }
}
