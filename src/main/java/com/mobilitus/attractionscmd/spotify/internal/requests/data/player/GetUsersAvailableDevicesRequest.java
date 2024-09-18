package com.mobilitus.attractionscmd.spotify.internal.requests.data.player;

import com.mobilitus.attractionscmd.spotify.internal.exceptions.SpotifyWebApiException;
import com.mobilitus.attractionscmd.spotify.internal.model_objects.miscellaneous.Device;
import com.mobilitus.attractionscmd.spotify.internal.requests.data.AbstractDataRequest;

import java.io.IOException;


/**
 * Get information about a user’s available devices.
 */
public class GetUsersAvailableDevicesRequest extends AbstractDataRequest
{

    /**
     * The private {@link GetUsersAvailableDevicesRequest} constructor.
     *
     * @param builder A {@link Builder}.
     */
    private GetUsersAvailableDevicesRequest(final Builder builder)
    {
        super(builder);
    }


    /**
     * Get an user's available devices.
     *
     * @return An user's available devices.
     *
     * @throws IOException            In case of networking issues.
     * @throws SpotifyWebApiException The Web API returned an error further specified in this exception's root cause.
     */
    public Device[] execute() throws
                              IOException,
                              SpotifyWebApiException
    {
        return new Device.JsonUtil().createModelObjectArray(getJson(), "devices");
    }


    /**
     * Builder class for building a {@link GetUsersAvailableDevicesRequest}.
     */
    public static final class Builder extends AbstractDataRequest.Builder<Builder>
    {

        /**
         * Create a new {@link Builder}.
         * <p>
         * Your access token must have the {@code user-read-playback-state} scope authorized in order to read information.
         *
         * @param accessToken Required. A valid access token from the Spotify Accounts service.
         *
         * @see <a href="https://developer.spotify.com/web-api/using-scopes/">Spotify: Using Scopes</a>
         */
        public Builder(final String accessToken)
        {
            super(accessToken);
        }


        /**
         * The request build method.
         *
         * @return A custom {@link GetUsersAvailableDevicesRequest}.
         */
        @Override
        public GetUsersAvailableDevicesRequest build()
        {
            setPath("/v1/me/player/devices");
            return new GetUsersAvailableDevicesRequest(this);
        }
    }
}
