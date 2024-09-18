package com.mobilitus.attractionscmd.spotify;

import com.mobilitus.attractionscmd.spotify.internal.SpotifyApi;
import com.mobilitus.attractionscmd.spotify.internal.exceptions.SpotifyWebApiException;
import com.mobilitus.attractionscmd.spotify.internal.model_objects.credentials.ClientCredentials;
import com.mobilitus.attractionscmd.spotify.internal.requests.authorization.client_credentials.ClientCredentialsRequest;
import com.mobilitus.util.hexia.StrUtil;
import org.apache.log4j.Logger;

import java.io.IOException;

/**
 * @author helgaw
 * @todo add class description.
 * @since 2019-01-23 09:37
 */
public class SpotifyAuthorize
{
    private static final Logger logger = Logger.getLogger(SpotifyAuthorize.class);

    public static SpotifyApi authorize(String clientId, String clientSecret)
    {
        try
        {

           SpotifyApi spotifyApi = new SpotifyApi.Builder()
                                                                 .setClientId(clientId)
                                                                 .setClientSecret(clientSecret)
                                                                 .build();

            ClientCredentialsRequest clientCredentialsRequest = spotifyApi.clientCredentials()
                                                                          .build();

            final ClientCredentials clientCredentials = clientCredentialsRequest.execute();
            spotifyApi.setAccessToken(clientCredentials.getAccessToken());

            logger.info("Spotify access token Expires in: " + clientCredentials.getExpiresIn());

            return spotifyApi;
        }
        catch (IOException | SpotifyWebApiException e)
        {
           logger.error("Error: " + e.getMessage() + StrUtil.stack2String(e));
        }
        return null;
    }

}
