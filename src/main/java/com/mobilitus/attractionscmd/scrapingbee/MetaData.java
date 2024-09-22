package com.mobilitus.attractionscmd.scrapingbee;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * @author helgaw
 * @todo add class description.
 * @since 9/12/23 12:05
 */
public class MetaData
{
    private List microdata;
    @SerializedName("json_ld")
    private List jsonld;
    private List<OpenGraphData> opengraph;
    private List dublincore;


    public List<OpenGraphData> getOpengraph()
    {
        return opengraph;
    }

    public String getTitle()
    {
        if (opengraph != null && opengraph.size() > 0)
        {
            return opengraph.get(0).getTitle();
        }
        return null;
    }

    public String getImage()
    {
        if (opengraph != null && opengraph.size() > 0)
        {
            return opengraph.get(0).getImage();
        }
        return null;
    }
}
