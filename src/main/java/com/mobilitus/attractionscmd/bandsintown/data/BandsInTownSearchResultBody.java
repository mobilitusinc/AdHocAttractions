package com.mobilitus.attractionscmd.bandsintown.data;

import com.mobilitus.util.data.schema.SchemaArtist;

import java.util.Collections;
import java.util.List;

/**
 * @author helgaw
 * @since 9/18/24 18:46
 */
public class BandsInTownSearchResultBody
{
    /**
     *     "artists" : [ {
     *       "name" : "06 Boys",
     *       "id" : 15539038,
     *       "trackerText" : "85 Followers",
     *       "imageSrc" : "https://photos.bandsintown.com/thumb/16562693.jpeg",
     *       "properlySizedImageURL" : "https://media.bandsintown.com/300x300/16562693.webp",
     *       "verifiedSrc" : null,
     *       "verified" : false,
     *       "href" : "https://www.bandsintown.com/a/15539038-06-boys?came_from=257&utm_medium=web&utm_source=artist_page&utm_campaign=search_bar"
     *     } ],
     *     "events" : [ ],
     *     "festivals" : [ ],
     *     "venues" : [ ]
     */
    private List<BandsInTownArtist> artists;
//    private List<BandsInTownEvent> events;
//    private List<BandsInTownFestival> festivals;
//    private List<BandsInTownVenue> venues;


    public List<SchemaArtist> getArtists()
    {
        if (artists == null)
        {
            return Collections.emptyList();
        }
        List<SchemaArtist> schemaArtists = new java.util.ArrayList<>();
        for (BandsInTownArtist artist : artists)
        {
            schemaArtists.add(artist.toSchemaArtist());
        }
        return schemaArtists;
    }

    public boolean hasArtists()
    {
        if (artists == null)
        {
            return false;
        }
        if (artists.isEmpty())
        {
            return false;
        }
        return true;
    }
}
