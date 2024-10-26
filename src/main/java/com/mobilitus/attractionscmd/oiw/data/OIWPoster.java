package com.mobilitus.attractionscmd.oiw.data;

/**
 * @author helgaw
 * @since 9/23/24 18:41
 */
public class OIWPoster
{
    private static String root = "https://cdn.sanity.io/images/h1jmcyiv/production/";
    // https://cdn.sanity.io/images/h1jmcyiv/production/
    // edeae5a395ec966af9f17b1dcd50d0898f5187c6-828x885.jpg
    // ?rect=0,29,828,828&w=480&h=480
    /**
     "poster": {
     "_type": "image",
     "asset": {

     "_ref": "image-edeae5a395ec966af9f17b1dcd50d0898f5187c6-828x885-jpg",
     "_type": "reference"
     }
     },
     */

    private String _type;
    private OIWAsset asset;


    String getImage()
    {
        if (asset != null)
        {
            return asset.getImage();
        }
        return null;
    }
}
