package com.mobilitus.attractionscmd.oiw.data;

import java.util.List;

/**
 * @author helgaw
 * @since 9/23/24 18:54
 */
public class OIWBlock
{
    /**
     *      "_type": "span",
     *      "marks": [],
     *      "text": "Niloufar Gharavi, known as Nilu, is a nomad entrepreneur and systemic designer, pioneering
     *      \"Design-Driven Entrepreneurship\" (DDE). She runs Desinova Studios, a Design-Driven Venture Studio,
     *      and streamlines access to grant funding for impact startups and businesses through ENFA.",
     *      "_key": "azBm0ox0gTIvJxtHPsslRA"
     *      }
     *      ],
     *      "_type": "block",
     *      "_key": "azBm0ox0gTIvJxtHPsslMV",
     *      "markDefs": []

     */

    private String _type;
    private List<String> marks;
    private String text;
    private String _key;

    public String getText()
    {
        return text;
    }

}

