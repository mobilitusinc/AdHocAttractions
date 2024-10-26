package com.mobilitus.attractionscmd.tix.data;

import org.apache.log4j.Logger;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author helgaw
 * @todo add class description.
 * @since 7/2/21 16:01
 */
public class NotArtists
{  private static Map<String, String> map = new HashMap<>(100);

    private static final Logger logger = Logger.getLogger(CatMap.class);

    static
    {
        map.put("mozart", "");
        map.put("beethoven", "");
        map.put("brahms", "");
        map.put("mendelssohn", "");
        map.put("chopin", "");
        map.put("schubert", "");
        map.put("jazz", "");
        map.put("lög", "");
        map.put("ljóð", "");
        map.put("lygasögur", "");
        map.put("sögur", "");
        map.put("veitingar", "");
        map.put("gran partíta", "");
        map.put("gran partita", "");
        map.put("dvorák", "");
        map.put("dvorak", "");
        map.put("mahler", "");
        map.put("shostakovitsj", "");
        map.put("strauss", "");
        map.put("prokofíev", "");
        map.put("brahms", "");
        map.put("wagner", "");
        map.put("shumann", "");
        map.put("händel", "");
        map.put("bach", "");
        map.put("schumann", "");
        map.put("sumar", "");
        map.put("sveitaball", "");
        map.put("grill", "");
        map.put("stæði", "");
        map.put("stúka", "");
        map.put("laugardagur", "");
        map.put("sunnudagur", "");
        map.put("tónleikar", "");
        map.put("matur", "");
        map.put("gallerí fold", "");
        map.put("friður", "");
        map.put("minningartónleikar", "");

        map.put("afmælistónleikar", "");
        map.put("endurkoma", "");
        map.put("matur + tónleikar", "");
        map.put("frestað", "");
        map.put("gjafabréf", "");
        map.put("á spot", "");
        map.put("uppselt", "");
        map.put("nýtt í sölu", "");
        map.put("fáir miðar", "");
        map.put("hannesarholt", "");
        map.put("jólafjör", "");
        map.put("örfáir miðar lausir", "");
        map.put("örfá sæti laus", "");
        map.put("örfá sæti", "");
        map.put("örfáir miðar eftir", "");
        map.put("norræna húsið", "");
        map.put("hljómahöll", "");
        map.put("sæti við borð", "");
        map.put("support", "");
        map.put("hátíðartónleikar", "");
        map.put("jólatónleikar", "");
        map.put("viðburður hefur verið fluttur", "");
        map.put("laugardalshöll", "");
        map.put("í laugardalshöll", "");
        map.put("hljómsveit", "");
        map.put("tsjajkovskíj", "");
        map.put("þýsk sálumessa", "");
        map.put("níunda sinfónía beethovens", "");
        map.put("sellókonsert dvoráks", "");
        map.put("jazzpassi", "");
        map.put("beethoven í 250 ár", "");
        map.put("salurinn", "");
        map.put("live from reykjavik", "");
        map.put("ísafirði", "");
        map.put("á húrra", "");
        map.put("í sjallanum", "");
        map.put("valkyrja wagners", "");
        map.put("tónleikar á gauknum", "");
        map.put("græni hatturinn", "");
        map.put("í valaskjálf", "");
        map.put("reykjavík", "");
        map.put("reykjavik", "");
        map.put("iðnó", "");
        map.put("fyrri hluti", "");
        map.put("seinni hluti", "");
        map.put("píanótónleikar", "");
        map.put("klippikort", "");
        map.put("árskort", "");
        map.put("með gleði", "");
        map.put("vortónleikar", "");
        map.put("sumartónleikar", "");
        map.put("standandi salur", "");
        map.put("sitjandi salur", "");
        map.put("sitjandi", "");
        map.put("ókeypis viðburður", "");
        map.put("jólafjör", "");
        map.put("opin æfing", "");
        map.put("vínartónleikar", "");
        map.put("bíó paradís", "");
        map.put("gamla bíó", "");
        map.put("fríkirkjan", "");
        map.put("hveragerði", "");
        map.put("mosfellsbær", "");
        map.put("klassík", "");
        map.put("streymi", "");
        map.put("(ónúmerað)", "");
        map.put("akureyri", "");
        map.put("vestmannaeyjar", "");
        map.put("gólf (ónúmerað)", "");
        map.put("gólf (númeruð sæti)", "");
        map.put("salur", "");
        map.put("stólar", "");
        map.put("album release concert", "");
        map.put("dagtónleikar", "");
        map.put("kvöldtónleikar", "");
        map.put("jólatónleikar", "");
        map.put("fyrri tónleikar", "");
        map.put("seinni tónleikar", "");

        map.put("IV.", "");
        map.put("livestream", "");
        map.put("gestir", "");
        map.put("20th anniversary", "");
        map.put("aðventa", "");
        map.put("góðum gestum", "");



    }


    public static List  getAll()
    {
        return  Arrays.asList(map.keySet().toArray());
    }


    public static String get(String key)
    {
        if (key == null || key.isEmpty())
            return null;
        String result = map.get(key.toLowerCase());
        if (result != null)
            return key;

        return null;
    }
}
