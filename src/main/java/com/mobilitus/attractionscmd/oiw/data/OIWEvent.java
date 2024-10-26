package com.mobilitus.attractionscmd.oiw.data;


import com.mobilitus.util.data.attractions.AttractionType;
import com.mobilitus.util.data.attractions.DataSource;
import com.mobilitus.util.data.schema.SchemaArtist;
import com.mobilitus.util.data.schema.SchemaEvent;
import com.mobilitus.util.data.schema.SchemaLocation;
import com.mobilitus.util.hexia.location.CountryCode;
import org.joda.time.DateTimeZone;
import org.joda.time.LocalDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * @author helgaw
 * @since 9/23/24 17:16
 */
public class OIWEvent
{
    /**
     "title": "GENERATION INNOVATION: startups of the future",
     "slug": {
     "current": "sefio24",
     "_type": "slug"
     },
     "externalUrl": null,
     "ingress": "Join us for an exhilarating pitch contest during Oslo Innovation Week, where students will compete by presenting their innovative ideas to a panel of experts. After the competition the event transitions into a vibrant networking and afterparty session, offering a perfect opportunity to connect, discuss, and enjoy a festive atmosphere. Don't miss this unique blend of competition and celebration—where ideas meet opportunities!",
     "venue": {
     "_id": "PUn43gDAws84BhCBDZ4OsM",
     "title": "Mesh Youngstorget",
     "_updatedAt": "2024-08-09T08:54:13Z",
     "owner": {
     "_ref": "5f657894-98b4-4008-b8dc-d787fa2b618e",
     "_type": "reference"
     },
     "web": "https://maps.app.goo.gl/Bn2iz48rhzjQDSsM8",
     "_createdAt": "2024-04-21T21:56:22Z",
     "_rev": "v3431n7IAaIx7DOZmW7DNN",
     "_type": "venue"
     },
     "speakers": null,
     "_id": "0737b3fc-aa7d-4cfc-991d-0096d59983ae",
     "startDate": "2024-09-24T17:00",
     "endDate": "2024-09-24T23:30",
     "hosts": [
     {
     "title": "SEFiO",
     "_rev": "EID7htTIDezvC7ki0X8yGI",
     "_createdAt": "2024-04-21T22:03:02Z",
     "logo": {
     "_type": "image",
     "asset": {
     "_ref": "image-c872a58672f17a21b4b5b0cee1dc2aacc0c168ba-500x500-png",
     "_type": "reference"
     }
     },
     "_type": "host",
     "_id": "ir5Pa3JqTeBfgyynGTW50F",
     "web": "h",
     "_updatedAt": "2024-05-03T06:53:48Z",
     "owner": {
     "_type": "reference",
     "_ref": "5f657894-98b4-4008-b8dc-d787fa2b618e"
     }
     }
     ],
     "format": [
     {
     "_id": "18c79885-a226-45e6-8431-a1b3085d7ac1",
     "_updatedAt": "2022-03-22T10:26:28Z",
     "_createdAt": "2022-03-22T10:26:28Z",
     "_rev": "7yes0qfWfSyjOODV0cjUrp",
     "_type": "format",
     "name": "Pitch"
     }
     ],
     "labels": [
     {
     "_id": "26d5c8be-af97-4914-9701-f67a14219664",
     "_updatedAt": "2022-05-20T06:41:00Z",
     "_createdAt": "2022-05-20T06:41:00Z",
     "_rev": "WCjzzUnhYGi23btu7Yhh2C",
     "_type": "label",
     "name": "Talent"
     },
     {
     "name": "Networking",
     "_id": "6b5f2ca1-556e-4e14-9650-47e72fbd8ec6",
     "_updatedAt": "2023-03-22T14:19:30Z",
     "_createdAt": "2023-03-22T14:19:30Z",
     "_rev": "2qkeg4s3dkr1FKUl7I0gdc",
     "_type": "label"
     }
     ]
     },
     */
    private String title;
    private OIWSlug slug;
    private String externalUrl;
    private String ingress;
    private OIWVenue venue;
    private List<OIWSpeaker> speakers;
    private String id;
    private String startDate;
    private String endDate;
    private List<OIWHost> hosts;
    private List<OIWNode> format;
    private List<OIWNode> labels;

    private static final Logger logger = LoggerFactory.getLogger(OIWEvent.class);

    public SchemaEvent toSchemaEvent()
    {
        SchemaEvent schemaEvent = new SchemaEvent();
        schemaEvent.setName(title);
        schemaEvent.setUrl("https://oiw.no/event/" + slug.getSlug());
        schemaEvent.setDescription(ingress);
        schemaEvent.setStartDate(toDateString(startDate));
        schemaEvent.setLocalStartDate(startDate);
//        schemaEvent.setLocalEndDate(endDate);
        schemaEvent.setEndDate(toDateString(endDate));
        schemaEvent.setId(slug.getSlug());

        if (venue != null)
        {
            SchemaLocation schemaVenue = venue.toSchemaVenue();
            schemaVenue.addImportSource(DataSource.osloInnovationWeek.name());
            schemaEvent.setLocation(schemaVenue);

        }

        schemaEvent.addArtist(oiw());
        schemaEvent.addImage("https://media.promogogo.com/ymsirvidburdir/2024-09-23/ymsirvidburdir-078aa700-1974-4ce8-84f7-34bb0e2c3c6c-oiw.jpg");
        for (OIWHost host : hosts)
        {
            SchemaArtist schemaArtist = host.toSchemaArtist();

            schemaArtist.addImportSource(DataSource.osloInnovationWeek.name());
            schemaEvent.addArtist(schemaArtist);
        }
        if (speakers != null)
        {
            for (OIWSpeaker speaker : speakers)
            {
                SchemaArtist schemaArtist = speaker.toSchemaArtist();
                schemaArtist.addImportSource(DataSource.osloInnovationWeek.name());
                schemaEvent.addArtist(schemaArtist);
            }
        }
        schemaEvent.setType(getBestType());
        if (labels != null)
        {
            for (OIWNode label : labels)
            {
                schemaEvent.addInternalType(label.getName());
            }
        }
        if (format != null)
        {
            for (OIWNode form : format)
            {
                schemaEvent.addInternalType(form.getName());
            }
        }

        schemaEvent.setSource(DataSource.osloInnovationWeek.name());
        schemaEvent.addImportSource(DataSource.osloInnovationWeek.name());
        if (externalUrl != null)
        {
            schemaEvent.addWebsite(externalUrl);
        }
        return schemaEvent;
    }

    private AttractionType getBestType()
    {
        for (OIWNode label : labels)
        {
            logger.info("Label: " + label.getName());
            if (label.getName().toLowerCase().contains("conference"))
            {
                return AttractionType.conference;
            }
            if (label.getName().equalsIgnoreCase("networking"))
            {
                return AttractionType.meetup;
            }
            if (label.getName().toLowerCase().contains("party"))
            {
                return AttractionType.party;
            }
            if (label.getName().toLowerCase().contains("talk"))
            {
                return AttractionType.lecture;
            }
            if (label.getName().toLowerCase().contains("sport"))
            {
                return AttractionType.sports;
            }
            if (label.getName().toLowerCase().contains("seminar"))
            {
                return AttractionType.lecture;
            }
            if (label.getName().equalsIgnoreCase("community"))
            {
                return AttractionType.community;
            }
            if (label.getName().toLowerCase().contains("fitness"))
            {
                return AttractionType.exercise;
            }
            if (label.getName().toLowerCase().contains("wellbeing"))
            {
                return AttractionType.wellness;
            }
        }
        return AttractionType.conference;
    }

    private SchemaArtist oiw()
    {
        SchemaArtist schemaArtist = new SchemaArtist();
        schemaArtist.setName("Oslo Innovation Week");
        schemaArtist.setId("oiw");
        schemaArtist.setWebpage("https://oiw.no");
        schemaArtist.setArtistType(AttractionType.conference);
        schemaArtist.addInternalType("conference");
        schemaArtist.addImportSource(DataSource.osloInnovationWeek.name());
        schemaArtist.setSource(DataSource.osloInnovationWeek.name());
        schemaArtist.setLocation(CountryCode.no);
        return schemaArtist;

    }

    private String toDateString(String dateStr)
    {
        if (dateStr == null)
        {
            return null;
        }
        if (dateStr.contains("Z"))
        {
            return dateStr;
        }

        LocalDateTime local = LocalDateTime.parse(dateStr);
        return local.toDateTime(DateTimeZone.forID("Europe/Oslo")).toDateTime(DateTimeZone.UTC).toString();
    }

}
