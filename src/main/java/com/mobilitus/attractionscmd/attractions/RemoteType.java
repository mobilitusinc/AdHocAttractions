package com.mobilitus.attractionscmd.attractions;

import com.mobilitus.util.data.attractions.ArtistType;
import com.mobilitus.util.data.attractions.AttractionType;
import com.mobilitus.util.data.attractions.DataSource;
import com.mobilitus.util.data.attractions.MajorVenueCategory;

/**
 * @author helgaw
 * @since 9/16/24 15:32
 */
public class RemoteType
{
    private String remoteType;
    private DataSource source;
    private MajorVenueCategory venueCategory;
    private ArtistType artistType;
    private AttractionType attractionType;

    public RemoteType(String remoteType, DataSource source, MajorVenueCategory venueCategory, ArtistType artistType, AttractionType attractionType)
    {
        this.remoteType = remoteType;
        this.source = source;
        this.venueCategory = venueCategory;
        this.artistType = artistType;
        this.attractionType = attractionType;
    }

    public static RemoteType createVenue(String remoteType, MajorVenueCategory category)
    {
        RemoteType map = new RemoteType(remoteType.toLowerCase().trim(), DataSource.facebook, category, null, null);
        return map;
    }


    public static RemoteType createAttraction(String remoteType, ArtistType artistType, AttractionType attractionType)
    {
        RemoteType map = new RemoteType(remoteType.toLowerCase().trim(), DataSource.facebook, null, artistType, attractionType);
        return map;
    }

}
