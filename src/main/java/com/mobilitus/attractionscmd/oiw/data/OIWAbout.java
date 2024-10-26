package com.mobilitus.attractionscmd.oiw.data;

import java.util.List;

/**
 * @author helgaw
 * @since 9/23/24 18:53
 */
public class OIWAbout
{
    /**
     * "children": [
     *      {
     *      "_type": "span",
     *      "marks": [],
     *      "text": "Niloufar Gharavi, known as Nilu, is a nomad entrepreneur and systemic designer, pioneering \"Design-Driven Entrepreneurship\" (DDE). She runs Desinova Studios, a Design-Driven Venture Studio, and streamlines access to grant funding for impact startups and businesses through ENFA.",
     *      "_key": "azBm0ox0gTIvJxtHPsslRA"
     *      }
     *      ],
     *      "_type": "block",
     *      "_key": "azBm0ox0gTIvJxtHPsslMV",
     *      "markDefs": []
     */
    private List<OIWBlock> children;

    public String getAbout()
    {
        StringBuilder buf = new StringBuilder();

        for (OIWBlock block : children)
        {
            if (buf.length() > 0)
            {
                buf.append("\n");
            }
            buf.append(block.getText());
        }
        return buf.toString();
    }
}
