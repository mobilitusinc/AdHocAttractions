package com.mobilitus.attractionscmd.bandsintown.data.eventView;

/**
 * @author helgaw
 * @todo add class description.
 * @since 9/17/23 14:14
 */
public class Ticket
{
    private String ticketSellerName;
    private String vendor;
    private String ticketText;
    private String directTicketUrl;
    private Boolean typeIsSoldOut;
    private Boolean isVIP;

    public String getTicketSellerName()
    {
        return ticketSellerName;
    }

    public String getVendor()
    {
        return vendor;
    }

    public String getTicketText()
    {
        return ticketText;
    }

    public String getDirectTicketUrl()
    {
        return directTicketUrl;
    }

    public Boolean getTypeIsSoldOut()
    {
        return typeIsSoldOut;
    }

    public Boolean getVIP()
    {
        return isVIP;
    }
}
