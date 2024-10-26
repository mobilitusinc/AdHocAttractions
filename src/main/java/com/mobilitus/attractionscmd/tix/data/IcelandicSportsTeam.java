package com.mobilitus.attractionscmd.tix.data;

import java.util.HashMap;

/**
 * @author helgaw
 * @todo add class description.
 * @since 8/10/21 16:40
 */
public class IcelandicSportsTeam
{
    private static HashMap<String, String> map = new HashMap<>(100);

    static
    {
        map.put("afturelding", "");

        map.put("breiðablik", "");
        map.put("fh", "");
        map.put("fram", "");
        map.put("fylkir", "");
        map.put("fjölnir", "");

        map.put("grótta", "");
        map.put("grindavík", "");
        map.put("haukar", "");

        map.put("hk", "");
        map.put("ía", "");
        map.put("íbv", "");
        map.put("keflavík", "");
        map.put("ka", "");
        map.put("kr", "");
        map.put("kórdrengir", "");

        map.put("leiknir r.", "");
        map.put("selfoss", "");
        map.put("stjarnan", "");
        map.put("tindastóll", "");
        map.put("valur", "");
        map.put("vestri", "");

        map.put("víkingur", "");
        map.put("víkingur r.", "");
        map.put("víkingur ó.", "");
        map.put("þór/ka", "");
        map.put("þróttur r.", "");
        map.put("þór", "");
        map.put("ísland", "");
        map.put("grikkland", "");
        map.put("rúmenía", "");
        map.put("makedónía", "");

    }

    public static Boolean has (String str)
    {
        if (str == null || str.isEmpty())
            return false;

        if (map.get(str.trim().toLowerCase()) != null)
            return true;

        return false;
    }
}
