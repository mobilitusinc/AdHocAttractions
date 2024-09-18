package com.mobilitus.attractionscmd.spotify;

import com.mobilitus.attractionscmd.spotify.internal.SpotifyApi;
import com.mobilitus.attractionscmd.spotify.internal.enums.ModelObjectType;
import com.mobilitus.attractionscmd.spotify.internal.exceptions.SpotifyWebApiException;
import com.mobilitus.attractionscmd.spotify.internal.model_objects.special.SearchResult;
import com.mobilitus.attractionscmd.spotify.internal.model_objects.specification.Artist;
import com.mobilitus.attractionscmd.spotify.internal.model_objects.specification.ArtistSimplified;
import com.mobilitus.attractionscmd.spotify.internal.model_objects.specification.Image;
import com.mobilitus.attractionscmd.spotify.internal.model_objects.specification.Paging;
import com.mobilitus.attractionscmd.spotify.internal.model_objects.specification.PlaylistTrack;
import com.mobilitus.attractionscmd.spotify.internal.model_objects.specification.Track;
import com.mobilitus.attractionscmd.spotify.internal.requests.data.artists.GetArtistRequest;
import com.mobilitus.attractionscmd.spotify.internal.requests.data.artists.GetArtistsRelatedArtistsRequest;
import com.mobilitus.attractionscmd.spotify.internal.requests.data.artists.GetArtistsTopTracksRequest;
import com.mobilitus.attractionscmd.spotify.internal.requests.data.artists.GetSeveralArtistsRequest;
import com.mobilitus.attractionscmd.spotify.internal.requests.data.playlists.GetPlaylistsTracksRequest;
import com.mobilitus.attractionscmd.spotify.internal.requests.data.search.SearchItemRequest;
import com.mobilitus.util.data.attractions.ArtistData;
import com.mobilitus.util.data.attractions.DataSource;
import com.mobilitus.util.data.attractions.SourcedAttributeData;
import com.mobilitus.util.hexia.StrUtil;
import com.mobilitus.util.hexia.location.CountryCode;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Connect to the Spotify API and return data from it
 */
public class Spotify
{
    private   String clientId;
    private String clientSecret ;

    private SpotifyApi spotifyApi;


    private static final Logger logger = Logger.getLogger(Spotify.class);


    public Spotify()
    {
        clientId = "0d4001f9e2f94cbe8779a75cd097039f";
        clientSecret = "d5cebd766bcd469fbff89f09a2009e89";
        spotifyApi = SpotifyAuthorize.authorize(clientId, clientSecret);
    }


    public List<String> getArtistsFromPlaylist(String playlistUrl)
    {
        String playlistId = playlistUrl.substring(playlistUrl.lastIndexOf("/") + 1);

        List<String> artistIDs = new ArrayList<>();
        GetPlaylistsTracksRequest request = spotifyApi.getPlaylistsTracks(playlistId).build();
        try
        {
            Paging<PlaylistTrack> tracks = request.execute();
            for (PlaylistTrack track : tracks.getItems())
            {
                if (track == null || track.getTrack() == null)
                {
                    continue;
                }
                System.out.println(track.getTrack().getName());
                ArtistSimplified[] artists = track.getTrack().getArtists();
                for (ArtistSimplified artist : artists)
                {
                    System.out.println("  " + artist.getName());
                    if (!artistIDs.contains(artist.getId()))
                    {
                        artistIDs.add(artist.getId());
                    }
                }
            }

            return artistIDs;

        }
        catch (IOException e)
        {
            /**
             * @todo improve error handling
             *
             **/
            logger.error(StrUtil.stack2String(e));

        }
        catch (SpotifyWebApiException e)
        {
            /**
             * @todo improve error handling
             *
             **/
            logger.error(StrUtil.stack2String(e));

        }
        return Collections.emptyList();
    }

    public Artist getArtist(String spotifyID)
    {
        if (spotifyID == null)
        {
            return null;
        }
        try
        {

            GetArtistRequest getArtistRequest = spotifyApi.getArtist(spotifyID).build();

            Artist artist = getArtistRequest.execute();

            return artist;
        }
        catch (NullPointerException | IOException | SpotifyWebApiException e)
        {
            logger.info("Error: " + e.getMessage());
        }
        return null;
    }

    public List<Artist> getArtists(List<String> spotifyIDs)
    {
        try
        {
            if (spotifyIDs == null || spotifyIDs.isEmpty())
            {
                return Collections.emptyList();
            }
            else if (spotifyIDs.size() > 50)
            {
                Integer half = spotifyIDs.size() / 2;
                List<String> firstHalf = spotifyIDs.subList(0, half);
                List<String> secondHalf = spotifyIDs.subList(half, spotifyIDs.size());

                List<Artist> first = getArtists(firstHalf);
                List<Artist> second = getArtists(secondHalf);
                List<Artist> result = new ArrayList<>(first.size() + second.size());
                result.addAll(first);
                result.addAll(second);
                return result;
            }
            String[] ids= new String[spotifyIDs.size()];
            spotifyIDs.toArray(ids);

            GetSeveralArtistsRequest getArtistRequest = spotifyApi.getSeveralArtists(ids).build();

            Artist[] artists = getArtistRequest.execute();
            List<Artist> result = Arrays.asList(artists);

            return result;
        }
        catch (IOException | SpotifyWebApiException e)
        {
            logger.info("Error: " + e.getMessage());
            if (e.getMessage().toLowerCase().contains("expired"))
            {
                spotifyApi = SpotifyAuthorize.authorize(clientId, clientSecret);
                return getArtists(spotifyIDs);
            }
        }
        return null;
    }


    public List<Track> getTopTracks(String spotifyID, CountryCode code)
    {
        try
        {
            GetArtistsTopTracksRequest artistsTopTracks = spotifyApi.getArtistsTopTracks(spotifyID, code).build();
            Track[] tracks = artistsTopTracks.execute();
            List<Track> result = Arrays.asList(tracks);

            return result;
        }
        catch (IOException | SpotifyWebApiException e)
        {
            logger.info("Error: " + e.getMessage());
        }
        return null;
    }


    public List<Artist> findArtist(String name)
    {
        String type = ModelObjectType.ARTIST.getType();

        SearchItemRequest searchItemRequest = spotifyApi.searchItem(name, type)
                                                        .market(CountryCode.se)
                                                        .limit(10)
                                                        .offset(0)
                                                        .build();

        try
        {
            SearchResult searchResult = searchItemRequest.execute();

            if (logger.isDebugEnabled())
            {
                logger.debug("Total artists similar to " + name + " : " + searchResult.getArtists().getTotal());
            }

            List<Artist> artists = new ArrayList<>(searchResult.getArtists().getTotal());
            Collections.addAll(artists,  searchResult.getArtists().getItems());
            return artists;
        }
        catch (IOException | SpotifyWebApiException e)
        {
            if (e.getMessage().equalsIgnoreCase("The access token Expired"))
            {
                spotifyApi = SpotifyAuthorize.authorize(clientId, clientSecret);
                return findArtist(name);
            }
            else
            {
                logger.warn("Error: " + e.getMessage());
            }
        }
        return Collections.emptyList();
    }


    public List<Artist> findRelatedArtists(String spotifyArtistID)
    {
        GetArtistsRelatedArtistsRequest getArtistsRelatedArtistsRequest = spotifyApi
                                                                                  .getArtistsRelatedArtists(spotifyArtistID)
                                                                                  .build();

        try
        {
             Artist[] result = getArtistsRelatedArtistsRequest.execute();

            List<Artist> artists = new ArrayList<>(result.length);
            Collections.addAll(artists, result);
            return artists;
        }
        catch (IOException | SpotifyWebApiException e)
        {
            if (e.getMessage().equalsIgnoreCase("The access token Expired"))
            {
                spotifyApi = SpotifyAuthorize.authorize(clientId, clientSecret);
                return findRelatedArtists(spotifyArtistID);
            }
            else
            {
                logger.warn("Error: " + e.getMessage());
            }
        }
        return Collections.emptyList();
    }


    public static List<ArtistData> toArtistDataList(List<Artist> artists)
    {
        List<ArtistData>result = new ArrayList<>(artists.size());
        for (Artist artist : artists)
        {
            result.add(toArtistData(artist));
        }

        return result;
    }

    public static ArtistData  toArtistData(Artist artist)
    {
        ArtistData artistData = new ArtistData();
        artistData.setName(artist.getName());
        artistData.setArtistID(artist.getId());

        artistData.setBestImage(getBestImage(artist.getImages()));
        artistData.setBestThumb(getBestThumb(artist.getImages()));

        artistData.addAttribute(new SourcedAttributeData(null, DataSource.spotify, "followers", artist.getFollowers().getTotal() + ""));
        artistData.addAttribute( new SourcedAttributeData(null, DataSource.spotify, "popularity", artist.getPopularity() + ""));

        for (String genre : artist.getGenres())
        {
            artistData.addAttribute( new SourcedAttributeData(null, DataSource.spotify, "genre", genre));
        }
        return artistData;
    }


    private static String getBestImage(Image[] images)
    {
        if (images == null)
        {
            return "";
        }

        if (images.length == 0)
        {
            return "";
        }
        if (images.length == 1)
        {
            return  images[0].getUrl();
        }

        Image best = null;
        for (Image image : images)
        {
            if (best == null)
            {
                best = image;
            }
            else if (best.getWidth() < image.getWidth())
            {
                best = image;
            }
        }
        if (best == null)
        {
            return "";
        }

        return best.getUrl();
    }


    private static String getBestThumb(Image[] images)
    {
        if (images == null)
        {
            return "";
        }

        if (images.length == 0)
        {
            return "";
        }
        if (images.length == 1)
        {
            return  images[0].getUrl();
        }

        Image best = null;
        for (Image image : images)
        {
            if (best == null)
            {
                best = image;
            }
            else if (image.getWidth() < 50)
            {
                continue;
            }
            else if (best.getWidth() > image.getWidth())
            {
                best = image;
            }
        }
        if (best == null)
        {
            return "";
        }

        return best.getUrl();
    }


}
