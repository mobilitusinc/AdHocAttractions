package com.mobilitus.attractionscmd.bandsintown.data.eventView;

import com.google.gson.annotations.SerializedName;
import com.mobilitus.util.data.attractions.ArtistData;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @author helgaw
 * @todo add class description.
 * @since 9/17/23 14:07
 */
public class EventInfo
{
    @SerializedName("shouldRenderEventInfoContainer")
    private String description;
    @SerializedName("lineupContainer")
    private Lineup lineup;

    private Map<String, Object> eventInfo;

    public String getDescription()
    {
        return description;
    }

    public Lineup getLineup()
    {
        return lineup;
    }

    public Map<String, Object> getEventInfo()
    {
        return eventInfo;
    }

    public List<ArtistData> getLineupAsArtistData()
    {
        if (lineup == null)
            return Collections.emptyList();

        return lineup.getLineupItemsAsArtistData();
    }
}
