package com.mobilitus.attractionscmd.bandsintown.data;

import com.mobilitus.util.data.schema.SchemaArtist;

/**
 * @author helgaw
 * @since 9/18/24 18:48
 */
public class BandsInTownArtist
{
    /**
     *       "name" : "06 Boys",
     *       "id" : 15539038,
     *       "trackerText" : "85 Followers",
     *       "imageSrc" : "https://photos.bandsintown.com/thumb/16562693.jpeg",
     *       "properlySizedImageURL" : "https://media.bandsintown.com/300x300/16562693.webp",
     *       "verifiedSrc" : null,
     *       "verified" : false,
     *       "href" : "https://www.bandsintown.com/a/15539038-06-boys?came_from=257&utm_medium=web&utm_source=artist_page&utm_campaign=search_bar"
     * /
     * */
    private String name;
    private int id;
    private String trackerText;
    private String imageSrc;
    private String properlySizedImageURL;
    private String verifiedSrc;
    private boolean verified;
    private String href;

    public String getName()
    {
        return name;
    }

    public int getId()
    {
        return id;
    }

    public String getTrackerText()
    {
        return trackerText;
    }

    public String getImageSrc()
    {
        return imageSrc;
    }

    public String getProperlySizedImageURL()
    {
        return properlySizedImageURL;
    }

    public String getVerifiedSrc()
    {
        return verifiedSrc;
    }

    public boolean isVerified()
    {
        return verified;
    }

    public String getHref()
    {
        return href;
    }

    public SchemaArtist toSchemaArtist()
    {
        SchemaArtist schemaArtist = new SchemaArtist();
        schemaArtist.setName(name);
        schemaArtist.setId(String.valueOf(id));
        schemaArtist.setImage(imageSrc);
        schemaArtist.setUrl(cleanURL(href));
        return schemaArtist;
    }

    private String cleanURL(String href)
    {
        if (href == null)
        {
            return null;
        }
        int index = href.indexOf("?");
        if (index > 0)
        {
            return href.substring(0, index);
        }
        return href;
    }
}
