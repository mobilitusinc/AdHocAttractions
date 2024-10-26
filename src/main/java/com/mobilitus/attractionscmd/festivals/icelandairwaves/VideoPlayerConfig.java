package com.mobilitus.attractionscmd.festivals.icelandairwaves;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.google.gson.annotations.SerializedName;
import com.mobilitus.util.hexia.json.serialization.DateTimeSerialization;
import com.mobilitus.util.hexia.json.serialization.DurationSerialization;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.Duration;

import java.lang.reflect.Modifier;
import java.util.Map;

/**
 * @author helgaw
 * @todo add class description.
 * @since 10/18/23 15:36
 */
public class VideoPlayerConfig

{
    /*{
  "youtube_url": "https://www.youtube.com/watch?v\u003dxMr1foBnzow",
  "show_image_overlay": "yes",
  "lightbox": "yes",
  "lightbox_content_animation": "fadeIn",
  "video_type": "youtube",
  "controls": "yes",
  "image_overlay": {
    "id": 18800,
    "url": "https://icelandairwaves.is/wp-content/uploads/2023/09/AGGRASOPPAR-1.png"
  }
}

     */
    @SerializedName("youtube_url")
    private String url;
    @SerializedName("show_image_overlay")
    private String showImageOverlay;
    private String lightbox;
    @SerializedName("lightbox_content_animation")
    private String lightboxContentAnimation;
    @SerializedName("video_type")
    private String videoType;
    private String controls;
    private Map<String, Object> image_overlay;

    public static VideoPlayerConfig create (String json)
    {
        GsonBuilder gsonBuilder = new GsonBuilder().excludeFieldsWithModifiers(Modifier.STATIC, Modifier.TRANSIENT);

        try
        {
            // DateTime
            gsonBuilder.registerTypeAdapter(DateTime.class, new DateTimeSerialization(DateTimeZone.UTC));
            gsonBuilder.registerTypeAdapter(Duration.class, new DurationSerialization());
            //            gsonBuilder.setLenient();
           VideoPlayerConfig data = gsonBuilder.create().fromJson(json, VideoPlayerConfig.class);
            if (data == null )
            {

                return null;
            }

            return data;
        }
        catch (JsonSyntaxException e)
        {
            return null;

        }

    }

    public String getUrl()
    {
        return url;
    }
}
