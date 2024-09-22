package com.mobilitus.attractionscmd.scrapingbee;

import com.google.gson.annotations.SerializedName;

import java.util.Map;

/**
 * @author helgaw
 * @todo add class description.
 * @since 9/12/23 12:03
 */
public class SubDocument
{
    private String url;
    @SerializedName("status_code")
    private Integer statusCode;
    private String method;
    private Map<String, String> headers;

    @SerializedName("request_headers")
    private Map<String, String> requestHeaders;
    private String body;


    public String getUrl()
    {
        return url;
    }

    public Integer getStatusCode()
    {
        return statusCode;
    }

    public String getMethod()
    {
        return method;
    }

    public Map<String, String> getHeaders()
    {
        return headers;
    }

    public Map<String, String> getRequestHeaders()
    {
        return requestHeaders;
    }

    public String getBody()
    {
        return body;
    }
}
