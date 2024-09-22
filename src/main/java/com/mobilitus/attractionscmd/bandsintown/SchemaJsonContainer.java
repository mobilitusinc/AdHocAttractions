package com.mobilitus.attractionscmd.bandsintown;

import com.mobilitus.util.data.schema.SchemaEvent;
import com.mobilitus.util.data.ticketMaster.EventGhettoData;

/**
 * @author helgaw
 * @todo add class description.
 * @since 9/18/23 10:14
 */
public class SchemaJsonContainer
{
    private SchemaEvent eventJsonLd;


    public EventGhettoData toData()
    {
        if (eventJsonLd != null)
        {
            return eventJsonLd.toData();
        }
        return null;
    }

}
