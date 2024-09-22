package com.mobilitus.attractionscmd.bandsintown.data.eventView;

import com.mobilitus.util.data.attractions.ArtistData;

/**
 * @author helgaw
 * @todo add class description.
 * @since 9/17/23 14:10
 */
public class LineupItem
{
    private Long id;
    private String imageUrl;
    private String name;
    private String trackerCount;
    private String artistPageUrl;

    public ArtistData toArtistData()
    {
        ArtistData artist = new ArtistData();
        artist.setArtistID("" + id);
        artist.setName(name);
        artist.setBestImage(imageUrl);
        if (artistPageUrl != null)
        {
            if (artistPageUrl.contains("?"))
            {
                artist.setUrl(artistPageUrl.substring(0, artistPageUrl.indexOf("?")));
            }
            else
            {
                artist.setUrl(artistPageUrl);
            }
        }
        artist.addAttribute("followers", getFollowers() + "");
        return artist;
    }

    private String getFollowers()
    {
        if (trackerCount == null)
        {
            return "0";
        }
        if (!trackerCount.contains(" "))
        {
            return trackerCount;
        }
        return trackerCount.substring(0, trackerCount.indexOf(" "));
    }
}
