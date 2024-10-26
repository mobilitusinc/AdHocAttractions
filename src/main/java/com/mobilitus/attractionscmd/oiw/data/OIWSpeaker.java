package com.mobilitus.attractionscmd.oiw.data;

import com.mobilitus.util.data.attractions.AttractionType;
import com.mobilitus.util.data.attractions.DataSource;
import com.mobilitus.util.data.schema.SchemaArtist;

import java.util.List;

/**
 * @author helgaw
 * @since 9/23/24 17:21
 */
public class OIWSpeaker
{
    /**
     "firstName": "Niloufar",
     "poster": {
     "_type": "image",
     "asset": {
     "_ref": "image-edeae5a395ec966af9f17b1dcd50d0898f5187c6-828x885-jpg",
     "_type": "reference"
     }
     },
     "gender": "she/her",
     "jobTitle": "Secretary General",
     "company2": "Desinova Studios",
     "_createdAt": "2024-05-06T10:10:44Z",
     "_rev": "FyAHGzCGYvVljtOszjzMBg",
     "_id": "azBm0ox0gTIvJxtHPsslHq",
     "_type": "speaker",
     "star": false,
     "_updatedAt": "2024-05-06T10:17:10Z",
     "lastName": "Gharavi",
     "about": [
     {
     "children": [
     {
     "_type": "span",
     "marks": [],
     "text": "Niloufar Gharavi, known as Nilu, is a nomad entrepreneur and systemic designer, pioneering \"Design-Driven Entrepreneurship\" (DDE). She runs Desinova Studios, a Design-Driven Venture Studio, and streamlines access to grant funding for impact startups and businesses through ENFA.",
     "_key": "azBm0ox0gTIvJxtHPsslRA"
     }
     ],
     "_type": "block",
     "_key": "azBm0ox0gTIvJxtHPsslMV",
     "markDefs": []
     }
     ],
     "company": "Euro Nordic Funding Alliance",
     "slug": {
     "current": "niloufar-gharavi",
     "_type": "slug"
     },
     "owner": {
     "_ref": "be73beb7-4126-4c28-b9cd-7a38b54dd682",
     "_type": "reference"
     }
     }

     */

    private String _id;
    private String firstName;
    private String lastName;
    private String gender;
    private String jobTitle;
    private String company;
    private String company2;
    private String _createdAt;
    private String _rev;
    private String _type;
    private boolean star;
    private String _updatedAt;
    private OIWSlug slug;
    private OIWPoster poster;
    private List<OIWAbout> about;
    private OIWNode owner;
     private OIWSocial social;




    public SchemaArtist toSchemaArtist()
    {
        SchemaArtist schemaArtist = new SchemaArtist();
        schemaArtist.setName(firstName.trim() + " " + lastName.trim());
        schemaArtist.setId(_id);
        schemaArtist.setUrl("https://oiw.no/speaker/" + slug.getSlug());
        schemaArtist.addInternalType(_type);
        schemaArtist.addInternal("jobTitle", jobTitle);
        schemaArtist.addInternal("company", company);
        schemaArtist.addInternal("company2", company2);
        schemaArtist.addInternal("gender", gender);
        if (social != null)
        {
            if (social.getLinkedin() != null && social.getLinkedin().length() > 0)
            {
                schemaArtist.addSameAs(social.getLinkedin());
            }
        }
        schemaArtist.setArtistType(AttractionType.lecture);
        if (about != null)
        {
            StringBuilder sb = new StringBuilder();
            for (OIWAbout a : about)
            {
                if (sb.length() > 0)
                {
                    sb.append("\n");
                }
                sb.append(a.getAbout());
            }
            schemaArtist.setDescription(sb.toString());
        }
        if (poster != null)
        {
            schemaArtist.setImage(poster.getImage());
//            schemaArtist.addImage(poster.getImage());
        }
        schemaArtist.setSource(DataSource.osloInnovationWeek.toString());
        return schemaArtist;

    }
}
