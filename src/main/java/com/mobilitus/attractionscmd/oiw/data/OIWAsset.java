package com.mobilitus.attractionscmd.oiw.data;

/**
 * @author helgaw
 * @since 9/23/24 18:42
 */
public class OIWAsset
{
    private static String root = "https://cdn.sanity.io/images/h1jmcyiv/production/";
    /**
     * https://cdn.sanity.io/images/h1jmcyiv/production/5f7bcedf075b07d0a82ba928349237be0a99bddd-1500x788.png?w=200
     * "_ref": "image-edeae5a395ec966af9f17b1dcd50d0898f5187c6-828x885-jpg",
     * "_type": "reference"
     *
     * https://cdn.sanity.io/images/h1jmcyiv/production/c872a58672f17a21b4b5b0cee1dc2aacc0c168ba-500x500.png
     * https://cdn.sanity.io/images/h1jmcyiv/production/c872a58672f17a21b4b5b0cee1dc2aacc0c168ba-500x500.png
     *
     */

    private String _ref;


    String getImage()
    {
        String asset = _ref;
        String imageUri = asset.substring(asset.indexOf("-") + 1);

        int lastDot = imageUri.lastIndexOf("-");
        if (lastDot > 0)
        {
            String first = imageUri.substring(0, lastDot);
            String last = imageUri.substring(lastDot + 1);
            imageUri = first + "." + last;
        }

        String all =  root + imageUri;
        return all;
    }

}
