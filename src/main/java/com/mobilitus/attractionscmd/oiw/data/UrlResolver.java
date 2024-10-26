package com.mobilitus.attractionscmd.oiw.data;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * @author helgaw
 * @since 9/23/24 17:52
 */
public class UrlResolver
{

    public static String resolveRedirect(String url) throws IOException
    {
        HttpURLConnection connection = null;
        try
        {
            URL obj = new URL(url);
            connection = (HttpURLConnection) obj.openConnection();
            connection.setInstanceFollowRedirects(false);

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_MOVED_TEMP ||
                responseCode == HttpURLConnection.HTTP_MOVED_PERM)
            {
                String redirectUrl = connection.getHeaderField("Location");
                return resolveRedirect(redirectUrl);
            }
        }
        finally
        {
            if (connection != null)
            {
                connection.disconnect();
            }
        }
        return url;
    }
}


