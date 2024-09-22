package com.mobilitus.attractionscmd.bandsintown.data.eventView;

import java.util.List;

/**
 * @author helgaw
 * @todo add class description.
 * @since 9/17/23 12:27
 */
public class BitLinks
{
    private List<BitLink> links;

    public String getOfficialUrl()
    {
        if (links == null)
        {
            return null;
        }
        for (BitLink link : links)
        {
            if (link.isOfficial())
            {
                return link.getUrl();
            }
        }
        return null;
    }

    public String getFacebook()
    {
        if (links == null)
        {
            return null;
        }
        for (BitLink link : links)
        {
            if (link.isOfficial())
            {
                return link.getUrl();
            }
        }
        return null;
    }
}
