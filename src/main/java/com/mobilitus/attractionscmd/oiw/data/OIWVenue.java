package com.mobilitus.attractionscmd.oiw.data;

import com.mobilitus.util.data.schema.SchemaAddress;
import com.mobilitus.util.data.schema.SchemaLocation;
import com.mobilitus.util.hexia.location.Point;
import com.mobilitus.util.data.attractions.DataSource;


import java.io.IOException;
import java.net.URLDecoder;
import java.util.Arrays;
import java.util.List;

/**
 * @author helgaw
 * @since 9/23/24 17:21
 */
public class OIWVenue
{
    /**
     *      "venue": {
     *      "_id": "PUn43gDAws84BhCBDZ4OsM",
     *      "title": "Mesh Youngstorget",
     *      "_updatedAt": "2024-08-09T08:54:13Z",
     *      "owner": {
     *      "_ref": "5f657894-98b4-4008-b8dc-d787fa2b618e",
     *      "_type": "reference"
     *      },
     *      "web": "https://maps.app.goo.gl/Bn2iz48rhzjQDSsM8",
     *      "_createdAt": "2024-04-21T21:56:22Z",
     *      "_rev": "v3431n7IAaIx7DOZmW7DNN",
     *      "_type": "venue"
     *      },
     */

    private String _id;
    private String title;
    private String _updatedAt;
    private String web;
    private String _createdAt;
    private String _rev;
    private String _type;


    public SchemaLocation toSchemaVenue()
    {
        SchemaLocation schemaLocation = new SchemaLocation();
        schemaLocation.setId(_id);
        schemaLocation.setName(title);
        SchemaLocation  loc = parseGoogleMapsUrl(web);

        if (loc != null && loc.getAddress() != null && loc.getAddress().getStreetAddress() == null)
        {
            String[] parts = title.split(",");
            if (parts.length > 1)
            {
                schemaLocation.setName(parts[0]);
                loc.getAddress().setStreetAddress(parts[1]);
            }
            schemaLocation.setAddress(loc.getAddress());
            if (loc.getLocationPoint() != null)
            {
                schemaLocation.setLocationPoint(loc.getLocationPoint());
            }
            schemaLocation.addHint("location", "approximate");
        }
        else
        {
            schemaLocation.setAddress(loc.getAddress());
            if (loc.getLocationPoint() != null)
            {
                schemaLocation.setLocationPoint(loc.getLocationPoint());
            }


            schemaLocation.addHint("location", "approximate");
        }
        schemaLocation.addImportSource(DataSource.osloInnovationWeek.name());

        return schemaLocation;

    }

    private SchemaLocation parseGoogleMapsUrl(String web)
    {
        try
        {
            String resolvedUrl = UrlResolver.resolveRedirect(web);
            // https://www.google.com/maps/place/Mesh+Renationalisation+-+Mesh+Community/@59.9179493,10.7295194,4888m/
            // data=!3m2!1e3!5s0x46416e877e7281cd:0xa65c7a9646b54934!4m6!3m5!1s0x46416e7d879df3a9:0x2363c32e960440a0!8m2!3d59.9130099!4d10.7367458!16s%2Fg%2F12mkqykdp?
            // entry=ttu&g_ep=EgoyMDI0MDkxOC4xIKXMDSoASAFQAw%3D%3D
            List<String> urls = Arrays.asList(resolvedUrl.split("/"));
            for (int i = 0; i < urls.size(); i++)
            {
                if (urls.get(i).contains("place"))
                {
                    String place = urls.get(i + 1);
                    String geo = urls.get(i + 2);
                    SchemaLocation address =  toAddress(place, geo);
                    return address;
                }
            }

        }
        catch (IOException e)
        {
//            return null;
        }
        SchemaAddress schemaAddress = new SchemaAddress();
        schemaAddress.setAddressCountry("no");
        schemaAddress.setAddressLocality("Oslo");
        schemaAddress.setTimezone("Europe/Oslo");

        SchemaLocation schemaLocation = new SchemaLocation();
        schemaLocation.setAddress(schemaAddress);
        return schemaLocation;

    }

    private SchemaLocation toAddress(String name, String pointString)
    {
        //https://www.google.com/maps/place/
        // %C3%98vre+Slottsgate+11,+0157+Oslo/
        // @59.9124694,10.7407586,157m/
        // data=!3m1!1e3!4m6!3m5!1s0x46416e87fc1dfbfb:0x1ae962a2526075e0!8m2!3d59.9123112!4d10.7412999!16s%2Fg%2F11bw3yw_nj?entry=ttu&g_ep=EgoyMDI0MDkxOC4xIKXMDSoASAFQAw%3D%3D
        // or
        // https://www.google.com/maps/place/Mesh+Nationaltheatret+-+Mesh+Community/@59.9179493,10.7295194,4888m/
        // data=!3m2!1e3!5s0x46416e877e7281cd:0xa65c7a9646b54934!4m6!3m5!1s0x46416e7d879df3a9:0x2363c32e960440a0!8m2!3d59.9130099!4d10.7367458!16s%2Fg%2F12mkqykdp?
        // entry=ttu&g_ep=EgoyMDI0MDkxOC4xIKXMDSoASAFQAw%3D%3D

        name = URLDecoder.decode(name);
        name = name.replaceAll("\\+", " ");
        String[] nameParts = name.split(",");

        String address = "";
        if (nameParts.length <= 1)
        {
            name = nameParts[0];
        }
        else if (nameParts.length <= 2)
        {
            name = nameParts[0].trim();
            if (addressIsOK(nameParts[1]))
            {
                address = nameParts[1];
            }
        }

       String pnt = URLDecoder.decode(pointString);
        Point p = new Point(cleanGeoString(pnt));
        SchemaLocation loc = new SchemaLocation();
        loc.setName(name);

        SchemaAddress schemaAddress = new SchemaAddress();
        schemaAddress.setStreetAddress(address);
        schemaAddress.setAddressCountry("no");
        schemaAddress.setAddressLocality("Oslo");
        schemaAddress.setTimezone("Europe/Oslo");
        loc.setAddress(schemaAddress);
        loc.setLocationPoint(p);
        return loc;
    }

    private boolean addressIsOK(String namePart)
    {
        if (namePart.trim().equalsIgnoreCase("oslo"))
        {
            return false;
        }
        return true;
    }

    private String cleanGeoString(String pointString)
    {
        String str = pointString.replace("@", "");
        String[] parts = str.split(",");
        if (parts.length < 2)
        {
            return null;
        }
        return parts[0] + "," + parts[1];
    }

}
