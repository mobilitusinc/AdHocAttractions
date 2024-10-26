package com.mobilitus.attractionscmd.tix.data;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.mobilitus.util.hexia.StrUtil;
import com.mobilitus.util.hexia.json.serialization.DateTimeSerialization;
import com.mobilitus.util.hexia.json.serialization.DurationSerialization;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.Duration;

import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.List;

/**
 * @author helgaw
 * @todo add class description.
 * @since 9/10/20 14:23
 */
public class TixEventList
{

    private static List<TixEvent> events;

    public static List<TixEvent> create(String json)
    {
        GsonBuilder gsonBuilder = new GsonBuilder().excludeFieldsWithModifiers(Modifier.STATIC, Modifier.TRANSIENT);

        try
        {
            // DateTime
            gsonBuilder.registerTypeAdapter(DateTime.class, new DateTimeSerialization(DateTimeZone.UTC));
            gsonBuilder.registerTypeAdapter(Duration.class, new DurationSerialization());
            TixEvent[] d = gsonBuilder.create().fromJson(json, TixEvent[].class);

            if (d != null)
            {
                return Arrays.asList(d);
            }
        }
        catch (JsonSyntaxException e)
        {
            Logger logger = Logger.getLogger(TixEventList.class);
            logger.error(StrUtil.stack2String(e));
            logger.error(StrUtil.formatAsJson(json));
            return null;
        }
        return null;
    }

}
