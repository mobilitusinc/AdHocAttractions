package com.mobilitus.attractionscmd.bandsintown.data.eventView;


import java.util.List;

/**
 * @author helgaw
 * @todo add class description.
 * @since 9/17/23 14:12
 */
public class TicketList
{
    private List<Ticket> tickets;

    public Boolean hasTickets()
    {
        if (tickets == null || tickets.isEmpty())
        {
            return false;
        }
        return true;
    }

    public List<Ticket> getTickets()
    {
        return tickets;
    }
}
