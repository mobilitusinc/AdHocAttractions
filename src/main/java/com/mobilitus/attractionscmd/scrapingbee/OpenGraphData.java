package com.mobilitus.attractionscmd.scrapingbee;

import com.google.gson.annotations.SerializedName;

/**
 * @author helgaw
 * @todo add class description.
 * @since 9/12/23 12:07
 */
public class OpenGraphData
{
    @SerializedName("og:title")
    private String title;
    @SerializedName("og:description")
    private String description;
    @SerializedName("og:image")
    private String image;
    @SerializedName("og:url")
    private String url;
    @SerializedName("og:locale")
    private String locale;
    @SerializedName("@type")
    private String type;

    public String getTitle()
    {
        return title;
    }

    public String getDescription()
    {
        return description;
    }

    public String getImage()
    {
        return image;
    }

    public String getUrl()
    {
        return url;
    }

    public String getLocale()
    {
        return locale;
    }

    public String getType()
    {
        return type;
    }
}
