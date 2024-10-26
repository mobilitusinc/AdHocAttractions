package com.mobilitus.attractionscmd.oiw.data;

import com.mobilitus.util.data.schema.SchemaArtist;

/**
 * @author helgaw
 * @since 9/23/24 17:21
 */
public class OIWHost
{
    /**
     "owner": {
     "_ref": "be73beb7-4126-4c28-b9cd-7a38b54dd682",
     "_type": "reference"
     },
     "_createdAt": "2024-05-06T10:05:00Z",
     "_type": "host",
     "_id": "azBm0ox0gTIvJxtHPsqhFf",
     "logo": {
     "asset": {
     "_ref": "image-048588232fe7576d4d93f97818f358e30417ac55-2522x986-png",
     "_type": "reference"
     },
     "_type": "image"
     },
     "title": "Euro Nordic Funding Alliance",
     "_rev": "75hF6u0k3iHYTKl66OIrxC",
     "_updatedAt": "2024-05-06T10:16:31Z",
     "web": "https://www.en-fa.org/"     */

    private String _id;
    private String title;
    private String _updatedAt;
    private String web;
    private OIWPoster logo;
    private String _createdAt;
    private String _rev;
    private String _type;

    public SchemaArtist toSchemaArtist()
    {
        SchemaArtist schemaArtist = new SchemaArtist();
        schemaArtist.setName(title);
        schemaArtist.setUrl(web);
        schemaArtist.setWebpage(web);
        schemaArtist.setId(_id);
        if (logo != null)
        {
            schemaArtist.setImage(logo.getImage());
        }
        schemaArtist.addInternalType(_type);
        return schemaArtist;

    }
}
