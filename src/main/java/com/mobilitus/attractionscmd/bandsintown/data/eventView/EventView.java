package com.mobilitus.attractionscmd.bandsintown.data.eventView;

import com.mobilitus.util.data.ticketMaster.EventGhettoData;

/**
 * @author helgaw
 * @todo add class description.
 * @since 9/17/23 11:49
 */
public class EventView
{
    private EventViewBody body;

    public EventGhettoData toData()
    {
        return body.toData();
    }

}
