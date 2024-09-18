package com.mobilitus.attractionscmd.spotify.internal.requests.data.tracks;

import com.mobilitus.attractionscmd.spotify.internal.exceptions.SpotifyWebApiException;
import com.mobilitus.attractionscmd.spotify.internal.model_objects.miscellaneous.AudioAnalysis;
import com.mobilitus.attractionscmd.spotify.internal.requests.data.AbstractDataRequest;

import java.io.IOException;


/**
 * Get a detailed audio analysis for a single track identified by its unique Spotify ID.
 */
public class GetAudioAnalysisForTrackRequest extends AbstractDataRequest
{

    /**
     * The private {@link GetAudioAnalysisForTrackRequest} constructor.
     *
     * @param builder A {@link Builder}.
     */
    private GetAudioAnalysisForTrackRequest(final Builder builder)
    {
        super(builder);
    }


    /**
     * Get an audio analyis for a track.
     *
     * @return An {@link AudioAnalysis}.
     *
     * @throws IOException            In case of networking issues.
     * @throws SpotifyWebApiException The Web API returned an error further specified in this exception's root cause.
     */
    @SuppressWarnings("unchecked")
    public AudioAnalysis execute() throws
                                   IOException,
                                   SpotifyWebApiException
    {
        return new AudioAnalysis.JsonUtil().createModelObject(getJson());
    }


    /**
     * Builder class for building a {@link GetAudioAnalysisForTrackRequest}.
     */
    public static final class Builder extends AbstractDataRequest.Builder<Builder>
    {

        /**
         * Create a new {@link Builder}.
         *
         * @param accessToken Required. A valid access token from the Spotify Accounts service.
         */
        public Builder(final String accessToken)
        {
            super(accessToken);
        }


        /**
         * The track ID setter.
         *
         * @param id Required. The Spotify ID for the track.
         *
         * @return A {@link Builder}.
         *
         * @see <a href="https://developer.spotify.com/web-api/user-guide/#spotify-uris-and-ids">Spotify: URIs &amp; IDs</a>
         */
        public Builder id(final String id)
        {
            assert (id != null);
            assert (!id.equals(""));
            return setPathParameter("id", id);
        }


        /**
         * The request build method.
         *
         * @return A custom {@link GetAudioAnalysisForTrackRequest}.
         */
        @Override
        public GetAudioAnalysisForTrackRequest build()
        {
            setPath("/v1/audio-analysis/{id}");
            return new GetAudioAnalysisForTrackRequest(this);
        }
    }
}
