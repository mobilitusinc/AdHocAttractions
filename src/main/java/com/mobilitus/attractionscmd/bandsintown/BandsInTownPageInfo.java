package com.mobilitus.attractionscmd.bandsintown;

import com.google.gson.GsonBuilder;
import com.mobilitus.attractionscmd.bandsintown.data.eventView.EventView;
import com.mobilitus.util.data.attractions.VenueData;
import com.mobilitus.util.data.ticketMaster.EventGhettoData;
import com.mobilitus.util.hexia.StrUtil;
import com.mobilitus.util.hexia.json.serialization.DateTimeSerialization;
import com.mobilitus.util.hexia.json.serialization.DateTimeZoneSerialization;
import com.mobilitus.util.hexia.json.serialization.DurationSerialization;
import com.mobilitus.util.hexia.json.serialization.LocalDateTimeSerialization;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.Duration;

import java.lang.reflect.Modifier;
import java.time.LocalDateTime;

/**
 * @author helgaw
 * @todo add class description.
 * @since 9/15/23 18:27
 */
public class BandsInTownPageInfo
{
    private String title;
    private EventView eventView;
    private SchemaJsonContainer jsonLdContainer;

    private static final Logger logger =Logger.getLogger(BandsInTownPageInfo.class);

    public static BandsInTownPageInfo create(String json)
    {
        if (json == null || json.isEmpty())
        {
            return null;
        }
        try
        {
            GsonBuilder gsonBuilder = new GsonBuilder().excludeFieldsWithModifiers(Modifier.STATIC, Modifier.TRANSIENT);

            // DateTime
            gsonBuilder.registerTypeAdapter(DateTime.class, new DateTimeSerialization());
            gsonBuilder.registerTypeAdapter(LocalDateTime.class, new LocalDateTimeSerialization());
            gsonBuilder.registerTypeAdapter(Duration.class, new DurationSerialization());
            gsonBuilder.registerTypeAdapter(DateTimeZone.class, new DateTimeZoneSerialization());

            BandsInTownPageInfo result = gsonBuilder.create().fromJson(json, BandsInTownPageInfo.class);
            logger.info(StrUtil.formatAsJson(json));
            return result;
        }
        catch (Exception e)
        {
            logger.info(StrUtil.stack2String(e));
        }

        return null;
    }

    public EventGhettoData toData()
    {
        EventGhettoData event;
        if (eventView != null)
        {
            event = eventView.toData();
        }
        else
        {
            event = new EventGhettoData();
        }
        EventGhettoData jsonEvent;
        if (jsonLdContainer != null)
        {
            jsonEvent = jsonLdContainer.toData();
            if (event != null && jsonEvent != null)
            {
               return mergeEvents (event, jsonEvent);
            }
            else
            {
                return jsonEvent;
            }
        }
        else
        {
            return event;
        }
    }

    private EventGhettoData mergeEvents(EventGhettoData lhs, EventGhettoData rhs)
    {
        lhs.setTitle(rhs.getTitle());
        lhs.setStatus(rhs.getStatus());
        if (lhs.getBestImage() == null || lhs.getBestImage().isEmpty())
        {
            lhs.setBestImage(rhs.getBestImage());
        }
        if (lhs == null)
        {
            return rhs;
        }
        if (rhs == null)
        {
            return lhs;
        }
        if (lhs.getVenue() == null)
        {
            lhs.setVenue(rhs.getVenue());
        }
        else
        {
            VenueData venue = lhs.getVenue();
            venue = mergeVenue(venue, rhs.getVenue());
            if (venue.getTimezone() == null)
            {
                venue.setTimezone(lhs.getEventTimezone());
            }
            lhs.setVenue(venue);
        }

        return lhs;
    }

    private VenueData mergeVenue(VenueData lhs, VenueData rhs)
    {
        if (lhs == null)
        {
            return rhs;
        }
        if (rhs == null)
        {
            return lhs;
        }
        if (lhs.getLocationID() == null)
        {
            lhs.setLocationID(rhs.getLocationID());
        }
        if (lhs.getLocationPoint() == null)
        {
            lhs.setLocationPoint(rhs.getLocationPoint());
        }
        if (lhs.getCity() == null || lhs.getCity().isEmpty())
        {
            lhs.setZipAndCity(rhs.getZip(), rhs.getCity());
        }
        if (lhs.getCountryCode() == null && rhs.getCountryCode() != null)
        {
            lhs.setCountry(rhs.getCountryCode().toString());
        }
        return lhs;
    }


    public String getTitle()
    {
        return title;
    }
}
