package com.mobilitus.attractionscmd.attractions;

import com.mobilitus.util.data.attractions.ArtistType;
import com.mobilitus.util.data.attractions.AttractionType;
import com.mobilitus.util.data.attractions.MajorVenueCategory;
import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author helgaw
 * @since 9/16/24 15:38
 */
public class RemoteTypeMap
{
    private Map<String, RemoteType> remoteTypeMap = new HashMap<>(1000);
    private static Logger logger = Logger.getLogger(RemoteTypeMap.class);

    public RemoteTypeMap()
    {
    }

    public void createFromFile(String filePath)
    {
        String line = "";
        String csvSplitBy = ",";
        Boolean firstLine = true;
        int index = 0;
        StringBuffer codeGen = new StringBuffer();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath)))
        {

            while ((line = br.readLine()) != null)
            {
                // Use comma as separator
                String[] values = line.split(csvSplitBy, -1);
//                logger.info (line);
                index++;
                if (firstLine)
                {
                    firstLine = false;
                    continue;
                }

                if (values.length < 10)
                {
                    logger.error("Line " + index + " has less than 10 values: " + line);
                    continue;
                }
                // format is
                //  0         1       2               3           4
                // remoteType,source,venueCategory,artistType,attractionType
                String remoteType = values[0];
                String source = values[1];
                String venueCategory = values[9];
                String artistType = values[10];
                String attractionType = values[8];
//                logger.info("remoteType: " + remoteType + " venueCategory: " + venueCategory + " artistType: " + artistType + " attractionType: " + attractionType);
                // create the map
                if (!artistType.isEmpty() || !attractionType.isEmpty())
                {
                    AttractionType type = null;
                    String typeStr = "null";
                    if (!attractionType.isEmpty())
                    {
                        type = AttractionType.create(attractionType);
                        if (type == null || type == AttractionType.unknown)
                        {
                             logger.error("AttractionType is null for " + remoteType + " " + attractionType);
                        }
                        else
                        {
                            typeStr = "AttractionType." + type.name();
                        }
                    }
                    ArtistType artType = null;
                    String artTypeStr = "null";
                    if (!artistType.isEmpty())
                    {
                        artType = ArtistType.create(artistType);
                        if (artType == null || artType == ArtistType.na)
                        {
                            logger.error("ArtistType is null for " + remoteType + " " + artistType);
                        }
                        else
                        {
                            artTypeStr = "ArtistType." + artType.name();
                        }
                    }

                    RemoteType map = RemoteType.createAttraction(remoteType, artType, type);
                    logger.info(index + "\tremoteTypeMap.put(\"" + remoteType + "\", RemoteType.createAttraction(\"" + remoteType + "\", " + artTypeStr +  ", " + typeStr + "));");
                    codeGen.append("remoteTypeMap.put(\"" + remoteType + "\", RemoteType.createAttraction(\"" + remoteType + "\", " + artTypeStr +", " + typeStr +"));\n");
                }
                else if (!venueCategory.isEmpty())
                {
                    MajorVenueCategory cat =  MajorVenueCategory.create(venueCategory);
                    RemoteType map = new RemoteType(remoteType, null, cat, null, null);
                    logger.info("remoteTypeMap.put(\"" + remoteType + "\", RemoteType.createVenue(\"" + remoteType + "\", MajorVenueCategory." + cat.name() + "));");
                    codeGen.append("remoteTypeMap.put(\"" + remoteType + "\", RemoteType.createVenue(\"" + remoteType + "\", MajorVenueCategory." + cat.name() + "));\n");
                }
                else
                {
                    logger.error(index +"\tskipping " + line);
                }

            }

        } catch (IOException e)
        {
            e.printStackTrace();
        }
        System.out.println(codeGen.toString());
    }

    public void createMap(String json)
    {
        remoteTypeMap.put("foodAndDrink", RemoteType.createVenue("beer garden", MajorVenueCategory.foodAndDrink));
    }
}
