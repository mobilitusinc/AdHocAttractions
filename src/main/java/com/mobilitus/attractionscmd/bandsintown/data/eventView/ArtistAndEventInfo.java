package com.mobilitus.attractionscmd.bandsintown.data.eventView;

import com.google.gson.annotations.SerializedName;
import com.mobilitus.util.hexia.StrUtil;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import java.time.LocalDateTime;

/**
 * @author helgaw
 * @todo add class description.
 * @since 9/17/23 14:02
 */
public class ArtistAndEventInfo
{
    private Long id;
    private Long artistId;
    private String artistName;
    private String startsAt;
    private String timezone;
    private Boolean streamingEvent;

    @SerializedName("rsvpCountInt")
    private Integer rsvpCount;
    private String cityText;
    private String cityUrl;

    private static final Logger logger = Logger.getLogger(ArtistAndEventInfo.class);

    public Long getId()
    {
        return id;
    }

    public Long getArtistId()
    {
        return artistId;
    }

    public String getArtistName()
    {
        return artistName;
    }

    public DateTime startAt()
    {
        if (startsAt != null && timezone != null)
        {
            try
            {
                DateTime dt = new DateTime(startsAt).withZone(DateTimeZone.forID(timezone));
                return dt;
            }
            catch (Exception e)
            {
            }
        }
        if (startsAt != null)
        {
            return new DateTime(startsAt);
        }
        return null;
    }

    public LocalDateTime getStartsAt()
    {
        try
        {
            LocalDateTime dt = LocalDateTime.parse(startsAt);
            return dt;
        }
        catch (Exception e)
        {
            logger.error(StrUtil.stack2String(e));

        }
        return null;
    }

    public String getTimezone()
    {
        return timezone;
    }

    public Boolean getStreamingEvent()
    {
        return streamingEvent;
    }

    public Integer getRsvpCount()
    {
        return rsvpCount;
    }

    public String getCityText()
    {
        return cityText;
    }

    public String getCityUrl()
    {
        return cityUrl;
    }

    public DateTimeZone timezone()
    {
        if (timezone != null)
        {
            return DateTimeZone.forID(timezone);
        }
        return null;
    }
}
