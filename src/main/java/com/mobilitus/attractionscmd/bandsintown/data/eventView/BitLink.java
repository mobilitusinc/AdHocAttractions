package com.mobilitus.attractionscmd.bandsintown.data.eventView;

/**
 * @author helgaw
 * @todo add class description.
 * @since 9/17/23 12:28
 */
public class BitLink
{
    private String url;
    private String svg;
    private String classAttribute;

    public String getUrl()
    {
        return url;
    }

    public String getType()
    {
        if (classAttribute == null)
        {
            return null;
        }

        return classAttribute;
    }

    public boolean isOfficial()
    {
        if (classAttribute == null)
        {
            return false;
        }
        else if (classAttribute.equalsIgnoreCase("officialWebsiteIcon"))
        {
            return true;
        }
        return false;
    }
}
