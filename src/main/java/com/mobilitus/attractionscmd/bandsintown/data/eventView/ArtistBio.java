package com.mobilitus.attractionscmd.bandsintown.data.eventView;

import com.google.gson.annotations.SerializedName;
import com.mobilitus.util.data.attractions.ArtistData;
import com.mobilitus.util.data.attractions.AttractionType;
import com.mobilitus.util.data.attractions.Genre;

import java.util.List;

/**
 * @author helgaw
 * @todo add class description.
 * @since 9/17/23 12:23
 */
public class ArtistBio
{
    @SerializedName("bandMembersContent")
    private String bandmembers;
    @SerializedName("bioText")
    private String bio;
    @SerializedName("shortBioText")
    private String shortBio;
    @SerializedName("genreContent")
    private List<String> genres;

    @SerializedName("hometownContent")
    private String hometown;
    private BitLinks artistLinks;

    public String getBandmembers()
    {
        return bandmembers;
    }

    public String getBio()
    {
        return bio;
    }

    public String getShortBio()
    {
        return shortBio;
    }

    public List<String> getGenres()
    {
        return genres;
    }

    public String getHometown()
    {
        return hometown;
    }

    public BitLinks getArtistLinks()
    {
        return artistLinks;
    }

    public ArtistData toData()
    {
        ArtistData artist = new ArtistData();
        artist.addAttribute("description", bio);
        artist.addAttribute("bandmembers", bandmembers);
        artist.setMajorType(AttractionType.music);
        if (genres != null)
        {
            for (String genre : genres)
            {
                Genre g = Genre.create(genre);
                if (artist.getGogoGenre() == null && g != null && g != Genre.unknown)
                {
                    artist.setGogoGenre(g);
                }

                artist.addAttribute("genre", genre);
            }
        }
        if (hometown != null)
        {
            artist.addAttribute("hometown", hometown);
        }
        if (getArtistLinks() != null)
        {

            artist.setWebpage(getArtistLinks().getOfficialUrl());
            artist.setFacebook(getArtistLinks().getFacebook());
        }
        return artist;
    }
}
