package com.mobilitus.attractionscmd.bandsintown.data.eventView;

import com.google.gson.annotations.SerializedName;
import com.mobilitus.util.data.attractions.VenueData;

import java.util.Map;

/**
 * @author helgaw
 * @todo add class description.
 * @since 9/17/23 14:17
 */
public class VenueInfo
{
    private String name;
    private String streetAddress;
    private String location;
    private String phone;
    private Map<String, String>homepage;
    private String venueLink;
    @SerializedName("venueImageUrl")
    private String imageUrl;
    private String cityUrl;
    private Long venueId;

    public String getName()
    {
        return name;
    }

    public String getStreetAddress()
    {
        return streetAddress;
    }

    public String getLocation()
    {
        return location;
    }

    public String getPhone()
    {
        return phone;
    }

    public String getHomepage()
    {
        if (homepage == null)
        {
            return null;
        }
        return homepage.get("url");
    }

    public String getImageUrl()
    {
        return imageUrl;
    }

    public String getCityUrl()
    {
        return cityUrl;
    }

    public Long getVenueId()
    {
        return venueId;
    }

    public VenueData toData()
    {
        // here we don't get the location coord, and we don't get a zip or a good city / country description
        VenueData data = new VenueData();
        data.setName(name);
        if (venueId != null)
        {
            data.setLocationID(venueId + "");
        }
        data.setAddress(streetAddress);
        data.addAttribute ("phone", phone);
        data.setLocationID("" + venueId);
        data.setWebpage(getHomepage());
        if (imageUrl != null && !imageUrl.toLowerCase().contains("placeholder"))
        {
            data.setBestImage(imageUrl);
        }
        data.setBestImage(imageUrl);
        if (venueLink != null)
        {
            if (venueLink.contains("?"))
            {
                data.setUrl(venueLink.substring(0, venueLink.indexOf("?")));
            }
            else
            {
                data.setUrl(venueLink);
            }
        }
        data.setUrl(venueLink);
        return data;
    }
}
