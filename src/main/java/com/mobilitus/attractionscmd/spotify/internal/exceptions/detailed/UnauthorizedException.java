package com.mobilitus.attractionscmd.spotify.internal.exceptions.detailed;


import com.mobilitus.attractionscmd.spotify.internal.exceptions.SpotifyWebApiException;

/**
 * The request requires user authorization or, if the request included authorization credentials, authorization has been
 * refused for those credentials.
 */
public class UnauthorizedException extends SpotifyWebApiException
{

    public UnauthorizedException()
    {
        super();
    }


    public UnauthorizedException(String message)
    {
        super(message);
    }


    public UnauthorizedException(String message, Throwable cause)
    {
        super(message, cause);
    }

}
