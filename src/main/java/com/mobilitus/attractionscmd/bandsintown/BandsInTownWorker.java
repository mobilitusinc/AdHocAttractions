package com.mobilitus.attractionscmd.bandsintown;

import com.mobilitus.gogo.Cache;
import com.mobilitus.gogo.attractions.ArtistWorker;
import com.mobilitus.gogo.attractions.EventWorker;
import com.mobilitus.gogo.attractions.VenueWorker;
import com.mobilitus.gogo.search.AttractionSearch;
import com.mobilitus.gogo.search.EventSearch;
import com.mobilitus.persisted.attractions.artists.ArtistSourcePersisted;
import com.mobilitus.util.data.attractions.ArtistData;
import com.mobilitus.util.data.attractions.DataSource;
import com.mobilitus.util.data.aws.kinesis.KinesisStream;
import com.mobilitus.util.data.face.FaceData;
import com.mobilitus.util.data.pusher.AttributeName;
import com.mobilitus.util.data.pusher.MessageType;
import com.mobilitus.util.data.pusher.PusherMessage;
import com.mobilitus.util.data.schema.SchemaArtist;
import com.mobilitus.util.distributed.aws.cloudsearch.DefaultSearchConfig;
import com.mobilitus.util.distributed.aws.cloudsearch.SearchConfig;
import com.mobilitus.util.distributed.aws.kinesis.Producer;
import com.mobilitus.util.cache.MemcachedAdministrator;

import com.mobilitus.util.distributed.aws.s3.S3;
import com.mobilitus.util.distributed.dynamodb.AWSUtils;
import com.mobilitus.util.hexia.ID;
import org.apache.log4j.Logger;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedAsyncClient;

import java.util.ArrayList;
import java.util.List;

/**
 * @author helgaw
 * @todo add class description.
 * @since 8/31/22 11:46
 */
public class BandsInTownWorker
{
    private final String scrapingbeeApiKey;
    private static MemcachedAdministrator cacheAdministrator;
    private final ID id;
    private BandsInTownArtistScraper artistScraper;
    private BandsInTownVenueScraper venueScraper;

    private final DynamoDbEnhancedAsyncClient mapper;
    private S3 s3;
    private SearchConfig searchConfig;
    private AttractionSearch gogoSearch;
    private EventSearch eventSearch;
    private EventWorker eventWorker;
    private VenueWorker venueWorker;
    private ArtistWorker artistWorker;


    private Producer toAttractionWorker;
    private final Producer toImporter;

    private static final Logger logger = org.apache.log4j.Logger.getLogger(BandsInTownWorker.class);

    //    public SchemaArtist getArtist (String artistID)
//    {
//        BandsInTownArtistDocument artistDocument = new BandsInTownArtistDocument();
//        return artistDocument.getArtist(artistID);
//
//    }


    public BandsInTownWorker(String scrapingbeeApiKey)
    {
        this.scrapingbeeApiKey = scrapingbeeApiKey;
        artistScraper = new BandsInTownArtistScraper(scrapingbeeApiKey);
//        venueScraper = new BandsInTownVenueScraper(scrapingbeeApiKey);

        AwsCredentialsProvider credentialsProvider = AWSUtils.getCredentialsProvider();

        Cache.create(credentialsProvider.resolveCredentials(), "localhost:11211");

        mapper = AWSUtils.getMapper(credentialsProvider);


        SearchConfig searchConfig = new DefaultSearchConfig(credentialsProvider);

        eventSearch = new EventSearch(searchConfig.getCredentialsProvider(), searchConfig.getEventSearchURL());

        gogoSearch = new AttractionSearch(searchConfig.getCredentialsProvider(), searchConfig.getArtistSearchURL());
        id = new ID();
        eventWorker = new EventWorker(null, null, mapper, AWSUtils.getS3(), searchConfig);
        artistWorker = new ArtistWorker(null, null, mapper, AWSUtils.getS3(), searchConfig);
        venueWorker = new VenueWorker(id, new FaceData(), mapper, s3, searchConfig);
        artistWorker = new ArtistWorker(id, new FaceData(), mapper, s3, searchConfig);
        eventWorker = new EventWorker(id, new FaceData(), mapper, s3, searchConfig);

        cacheAdministrator = new MemcachedAdministrator();



        //        toAttractionWorker = new Producer(KinesisStream.promogogoData, credentials.getAWSAccessKeyId(), credentials.getAWSSecretKey());
        toImporter = new Producer(KinesisStream.imports, credentialsProvider);

    }

    public Boolean scrapeArtist(ArtistData artist)
    {
        Boolean added = false;
        List < ArtistSourcePersisted> bandsInTownArtists = getSources(artist, DataSource.bandsInTown);
        if (bandsInTownArtists.isEmpty())
        {
            SchemaArtist schemaArtist  = artistScraper.searchForArtist(artist.getName());
            if (schemaArtist != null)
            {
                logger.info("Found bit artist " + schemaArtist.getName() + " " + schemaArtist.getUrl());
                String remoteID = schemaArtist.getId();
                artistWorker.addSource(artist.getArtistID(), DataSource.bandsInTown, remoteID, DataSource.bandsInTown.name(), schemaArtist.getUrl());
                importArtist(artist.getArtistID(), remoteID, artist.getName());
                added = true;
            }
            else
            {
                logger.info("no bit artist found for " + artist.getName());
            }
        }
        else
        {
            for (ArtistSourcePersisted bandsInTownArtist : bandsInTownArtists)
            {
                importArtist(artist.getArtistID(), bandsInTownArtist.getThirdPartyID(), artist.getName());
            }

        }
        return added;
    }

    public Boolean hasBandsInTownArtist(ArtistData artist)
    {
        List < ArtistSourcePersisted> bandsInTownArtists = getSources(artist, DataSource.bandsInTown);
        return !bandsInTownArtists.isEmpty();
    }

    private List<ArtistSourcePersisted> getSources(ArtistData artist, DataSource dataSource)
    {
        List<ArtistSourcePersisted> sources = artistWorker.getUniqueSources(artist.getArtistID());
        List<ArtistSourcePersisted> result =  new ArrayList<>(sources.size());
        for (ArtistSourcePersisted source : sources)
        {
            if (source.getDataSource().equals(dataSource))
            {
                result.add(source);
            }
        }
        return result;
    }


    private void importArtist(String artistID, String remoteID, String artistName)
    {
        PusherMessage msg = new PusherMessage();
        msg.addUnique(AttributeName.artistID, artistID);
        msg.addUnique("bandsintown.artistID", remoteID);
        msg.addUnique("name", artistName);
        msg.addUnique("scope", "all");

        //        logger.info("Sending songkick message " +remoteID + " " + artistName +" to KinesisStream.imports '" + KinesisStream.imports + "'");

        toImporter.send(artistID, MessageType.importBandsInTownArtist.name(), msg.toJson());
    }



    //    public List<EventGhettoData> getVenue(String bandsInTownID)
//    {
//        return  venueScraper.getVenue(bandsInTownID);
//    }
//
//    public List<EventGhettoData> getFestival(String bandsInTownID)
//    {
//        return  venueScraper.getVenue(bandsInTownID);
//    }

}
