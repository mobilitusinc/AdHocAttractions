package com.mobilitus.attractionscmd.oiw.data;

import com.google.gson.GsonBuilder;
import com.mobilitus.util.data.schema.SchemaEvent;
import com.mobilitus.util.hexia.StrUtil;
import org.apache.log4j.Logger;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

/**
 * @author helgaw
 * @since 9/23/24 17:15
 */
public class EventList
{
    /**
     * https://h1jmcyiv.apicdn.sanity.io/v2021-08-31/data/query/production?query=*%5B_type+%3D%3D+%27event%27%5D%7B+_id%2C+title%2C+slug%2C+startDate%2C+endDate%2C+externalUrl%2Cingress%2Cvenue-%3E%7B...%7D%2C+speakers%5B%5D-%3E%7B...%7D%2C+hosts%5B%5D-%3E%7B...%7D%2Cformat%5B%5D-%3E%7B...%7D%2C+labels%5B%5D-%3E%7B...%7D%7D
     */
    List<OIWEvent> result;
    private static final Logger logger = Logger.getLogger(EventList.class);

    public List<SchemaEvent> toSchemaEvents()
    {
        List<SchemaEvent> theList = new ArrayList<>();
        for (OIWEvent event : result)
        {
//            logger.info (StrUtil.formatAsJson(event.toSchemaEvent().toJson()));
            theList.add(event.toSchemaEvent());
        }
        return theList;
    }

    public static EventList create(String json)
    {
        if (json == null)
        {
            return null;
        }
        GsonBuilder gsonBuilder = new GsonBuilder().excludeFieldsWithModifiers(Modifier.STATIC, Modifier.TRANSIENT);

        try
        {
            EventList result = gsonBuilder.create().fromJson(json, EventList.class);
            if (result != null)
            {
                return result;
            }
        }
        catch (Exception e)
        {
            logger.error("exception " + StrUtil.stack2String(e) + "\n json is " + StrUtil.formatAsJson(json));
        }
        return null;

    }
}
