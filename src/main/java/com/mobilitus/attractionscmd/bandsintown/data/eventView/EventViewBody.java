package com.mobilitus.attractionscmd.bandsintown.data.eventView;

import com.google.gson.annotations.SerializedName;
import com.mobilitus.util.data.attractions.ArtistData;
import com.mobilitus.util.data.attractions.VenueData;
import com.mobilitus.util.data.attributes.AttributeData;
import com.mobilitus.util.data.ticketMaster.EventGhettoData;

import java.util.List;

/**
 * @author helgaw
 * @todo add class description.
 * @since 9/17/23 11:51
 */
public class EventViewBody
{
    private ArtistBio artistBio;
    private ArtistAndEventInfo artistAndEventInfo;
    @SerializedName("eventInfoContainer")
    private EventInfo eventInfoContainer;
    private TicketList detailedTicketList;

    private SimilarEvents similarEvents;
    private VenueInfo venueSectionInfo;

    public EventGhettoData toData()
    {
        EventGhettoData data = new EventGhettoData();
        data.setEventID(artistAndEventInfo.getId() + "");
        data.setTitle(artistAndEventInfo.getArtistName());
        data.setShowTime(artistAndEventInfo.startAt());
        data.setEventTimezone(artistAndEventInfo.timezone());
        VenueData venue = venueSectionInfo.toData();
        // we have decent venue info here, including the venueId and a link to the homepage but we don't
        // get the geolocation and the zip + city code from this part

        data.setVenue(venue);
        if (detailedTicketList != null && detailedTicketList.hasTickets())
        {
            for (Ticket ticketInfo : detailedTicketList.getTickets())
            {
                data.addAttribute("purchaseLink", ticketInfo.getDirectTicketUrl());
            }
        }
        ArtistData mainArtist = artistBio.toData();
        List<ArtistData> artists = eventInfoContainer.getLineupAsArtistData();
        Boolean first = true;
        if (artists != null && !artists.isEmpty())
        {
            for (ArtistData artist : artists)
            {
                if (first)
                {
                    ArtistData merged = merge(artist, mainArtist);
                    data.addArtist(merged);
                    if (merged.getMajorType() != null)
                    {
                        data.setMajorType(merged.getMajorType());
                    }
                    if (merged.getGogoGenre() != null)
                    {
                        data.setGogoGenre(merged.getGogoGenre());
                    }
                    first = false;
                }
                else
                {
                    data.addArtist(artist);
                    if (data.getMajorType() == null && artist.getMajorType() != null)
                    {
                        data.setMajorType(artist.getMajorType());
                    }
                    if (data.getGogoGenre() == null && artist.getGogoGenre() != null)
                    {
                        data.setGogoGenre(artist.getGogoGenre());
                    }

                }
            }
        }
        return data;
    }

    private ArtistData merge(ArtistData mainArtist, ArtistData rhs)
    {
        for (AttributeData attrib : rhs.getAttributes())
        {
            mainArtist.addAttribute(attrib.getKey(), attrib.getValue());
        }
        if (mainArtist.getMajorType() == null)
        {
            mainArtist.setMajorType(rhs.getMajorType());
        }
        if (mainArtist.getGogoGenre() == null)
        {
            mainArtist.setGogoGenre(rhs.getGogoGenre());
        }
        return mainArtist;
    }
}
