package com.mobilitus.attractionscmd.tix.data;

import com.mobilitus.util.hexia.StrUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * @author helgaw
 * @todo add class description.
 * @since 7/2/21 12:10
 */
public class CatMap
{
    private static Map<String, String> map = new HashMap<>(100);
    private static Map<String, Integer> seen = new HashMap<>(100);

    private static final Logger logger = Logger.getLogger(CatMap.class);

    static
    {
        map.put("leikhús", "Arts & Theater");
        map.put("tónlist", "Music");
        map.put("tónleikar", "Music");
        map.put("grín", "Comedy");
        map.put("hátíðir", "Festival");
        map.put("dans", "dance");
        map.put("sinfóníutónleikar", "Classical");
        map.put("annað", "Other");
        map.put("sýning", "Arts & Theater");
        map.put("barna", "Family");
        map.put("börn", "Family");
        map.put("klassík", "Classical");
        map.put("jazz og blús", "Jazz");
        map.put("standup", "Standup");
        map.put("uppistand", "Standup");
        map.put("söngleikur", "Musical");
        map.put("sviðslist", "Arts & Theater");
        map.put("námskeið", "Course");
        map.put("börn og fjölskyldan", "Family");
        map.put("sport", "Sport");
        map.put("jólatónleikar", "Christmas");
        map.put("málstofa", "Seminar");
        map.put("ópera", "Classical");
        map.put("samtímatónlist", "Classical");
        map.put("jólasýning", "Christmas");
        map.put("rokk og popp", "Pop");
        map.put("kántrítónlist", "Country");
        map.put("bíótónleikar", "Arts & Theater");
        map.put("leiklist", "Arts & Theater");
        map.put("jazz", "Jazz");
        map.put("popp", "Pop");
        map.put("þjóðlagatónlist", "Folk");
        map.put("kammertónlist", "Classical");
        map.put("ráðstefnur", "Conferences");
        map.put("rokk", "Rock");
        map.put("bíó", "Film");

    }

    public static String get(String key)
    {
        if (key == null || key.isEmpty())
        {
            return "";
        }
        addToSeen(key);
        String result = map.get(key.toLowerCase());
        if (result != null)
        {
            return result;
        }

//        logger.error("No translation found for category '" + key + "'");
        return "";
    }

    private static void addToSeen(String key)
    {
        if (seen.containsKey(key.trim().toLowerCase()))
        {
            seen.put(key.trim().toLowerCase(), seen.get(key.trim().toLowerCase()) + 1);
        }
        else
        {
            seen.put(key.trim().toLowerCase(), 1);
        }
    }

    public static void logFrequencies()
    {
        HashMap<String, Integer>  sorted = sortByValues(seen);
        StringBuilder buf = new StringBuilder(1000);

        for (Map.Entry<String, Integer> item : sorted.entrySet())
        {
            logger.info(item.getKey() + ":\t" + StringUtils.leftPad(StrUtil.formatNumber(item.getValue()), 5));
            buf.append(item.getKey() + ":\t" + StringUtils.leftPad(StrUtil.formatNumber(item.getValue()), 5) + "\n");
        }

        logger.info("\n\n\n" + buf);
    }


    private  static HashMap sortByValues(Map map)
    {
        List list = new LinkedList(map.entrySet());
        // Defined Custom Comparator here
        Collections.sort(list, new Comparator()
        {
            public int compare(Object o1, Object o2)
            {
                return -1 * ((Comparable) ((Map.Entry) (o1)).getValue()).compareTo(((Map.Entry) (o2)).getValue());
            }
        });

        // Here I am copying the sorted list in HashMap
        // using LinkedHashMap to preserve the insertion order
        HashMap sortedHashMap = new LinkedHashMap();
        for (Iterator it = list.iterator(); it.hasNext();)
        {
            Map.Entry entry = (Map.Entry) it.next();
            sortedHashMap.put(entry.getKey(), entry.getValue());
        }
        return sortedHashMap;
    }
}
