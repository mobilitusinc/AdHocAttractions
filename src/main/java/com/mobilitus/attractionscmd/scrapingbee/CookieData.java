package com.mobilitus.attractionscmd.scrapingbee;

/**
 * @author helgaw
 * @todo add class description.
 * @since 9/12/23 12:00
 */
public class CookieData
{
    private String name;
    private String value;
    private String domain;
    private String path;
    private String expires;

    private Integer size;
    private Boolean httpOnly;
    private Boolean secure;
    private String sameSite;

    private Boolean sameParty;
    private String sourceScheme;
    private Long sourcePort;

    public String getName()
    {
        return name;
    }

    public String getValue()
    {
        return value;
    }

    public String getDomain()
    {
        return domain;
    }

    public String getPath()
    {
        return path;
    }

    public String getExpires()
    {
        return expires;
    }

    public Boolean getHttpOnly()
    {
        return httpOnly;
    }

    public Boolean getSecure()
    {
        return secure;
    }

    public String getSameSite()
    {
        return sameSite;
    }

    public Integer getSize()
    {
        return size;
    }

    public Boolean getSameParty()
    {
        return sameParty;
    }

    public String getSourceScheme()
    {
        return sourceScheme;
    }

    public Long getSourcePort()
    {
        return sourcePort;
    }
}
