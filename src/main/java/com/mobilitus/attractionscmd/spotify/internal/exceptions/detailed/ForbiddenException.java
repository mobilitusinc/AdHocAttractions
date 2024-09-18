package com.mobilitus.attractionscmd.spotify.internal.exceptions.detailed;


import com.mobilitus.attractionscmd.spotify.internal.exceptions.SpotifyWebApiException;

/**
 * The server understood the request, but is refusing to fulfill it.
 */
public class ForbiddenException extends SpotifyWebApiException
{

    public ForbiddenException()
    {
        super();
    }


    public ForbiddenException(String message)
    {
        super(message);
    }


    public ForbiddenException(String message, Throwable cause)
    {
        super(message, cause);
    }

}
