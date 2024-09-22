package com.mobilitus.attractionscmd.bandsintown.data.eventView;

import com.mobilitus.util.data.attractions.ArtistData;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author helgaw
 * @todo add class description.
 * @since 9/17/23 14:09
 */
public class Lineup
{
    private List<LineupItem> lineupItems;

    public List<ArtistData> getLineupItemsAsArtistData()
    {
        if (lineupItems == null)
        {
            return Collections.emptyList();
        }

        List<ArtistData> result = new ArrayList<>(lineupItems.size());
        for (LineupItem item : lineupItems)
        {
            result.add(item.toArtistData());
        }
        return result;
    }
}
