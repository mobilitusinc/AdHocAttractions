package com.mobilitus.attractionscmd.attractions;

import com.mobilitus.gogo.Cache;
import com.mobilitus.gogo.attractions.ArtistWorker;
import com.mobilitus.gogo.attractions.EventWorker;
import com.mobilitus.gogo.search.AttractionSearch;
import com.mobilitus.gogo.search.EventSearch;
import com.mobilitus.persisted.attractions.RemoteTypesPersisted;
import com.mobilitus.util.cache.MemcachedAdministrator;
import com.mobilitus.util.distributed.aws.cloudsearch.DefaultSearchConfig;
import com.mobilitus.util.distributed.aws.cloudsearch.SearchConfig;
import com.mobilitus.util.distributed.dynamodb.AWSUtils;
import com.mobilitus.util.hexia.StrUtil;
import org.apache.log4j.Logger;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedAsyncClient;
import software.amazon.awssdk.enhanced.dynamodb.model.Page;
import software.amazon.awssdk.enhanced.dynamodb.model.PagePublisher;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author helgaw
 * @since 7/31/24 10:06
 */
public class RemoteTypeList
{
    private static MemcachedAdministrator cacheAdministrator = null;
    private final AwsCredentialsProvider credentials;
    private final DynamoDbEnhancedAsyncClient mapper;
    private static final Logger logger = Logger.getLogger(RemoteTypeList.class);
    private final AttractionSearch gogoSearch;
    private final EventWorker eventWorker;
    private final EventSearch eventSearch;
    private final ArtistWorker artistWorker;


    public RemoteTypeList(AwsCredentialsProvider credentials, String memcache)
    {
        this.credentials = credentials;

        if (cacheAdministrator == null)
        {
            Cache.create(credentials.resolveCredentials(), memcache);
            cacheAdministrator = new MemcachedAdministrator();
        }
        mapper = AWSUtils.getMapper(credentials);

        SearchConfig searchConfig = new DefaultSearchConfig(credentials);

        eventSearch = new EventSearch(searchConfig.getCredentialsProvider(), searchConfig.getEventSearchURL());

        gogoSearch = new AttractionSearch(searchConfig.getCredentialsProvider(), searchConfig.getArtistSearchURL());

        eventWorker = new EventWorker(null, null, mapper, AWSUtils.getS3(), searchConfig);
        artistWorker = new ArtistWorker(null, null, mapper, AWSUtils.getS3(), searchConfig);
    }

    public void getAllRemoteTypes()
    {
        PagePublisher<RemoteTypesPersisted> all = RemoteTypesPersisted.findAll(mapper);

        AtomicReference<Integer> reProcess = new AtomicReference<>(0);
        AtomicReference<Integer> deleted = new AtomicReference<>(0);
        AtomicReference<Integer> counter = new AtomicReference<>(0);

        CompletableFuture<Void> future = new CompletableFuture<>();
         Map<String,RemoteTypesPersisted> remoteTypes = new ConcurrentHashMap<>(500);
        all.subscribe(new Subscriber<>()
        {

            @Override
            public void onSubscribe(Subscription s)
            {
                s.request(50000);
            }

            /**
             * Data notification sent by the {@link } in response to requests to {@link Subscription#request(long)}.
             *
             * @param remoteTypesPage the element signaled
             */
            @Override public void onNext(Page<RemoteTypesPersisted> remoteTypesPage)
            {
                   remoteTypesPage.items().forEach(remoteType -> {
                       remoteTypes.put(remoteType.getId(), remoteType);
                   });
            }


            @Override
            public void onError(Throwable t)
            {
                logger.error("Error getting items", t);
                future.completeExceptionally(t);
            }

            @Override
            public void onComplete()
            {
                future.complete(null);
            }
        });
        try
        {
            future.get(); // This will block until the operation is complete
            StringBuilder builder = new StringBuilder(10000);
            for (Map.Entry<String, RemoteTypesPersisted> entry : remoteTypes.entrySet())
            {
                RemoteTypesPersisted remoteType = entry.getValue();
                logger.info(remoteType.getType() + "\t" + remoteType.getSource() + "\t" + remoteType.getCount() +"\t" + remoteType.getCreated());

                builder.append(remoteType.getType() + "\t" + remoteType.getSource() + "\t" + remoteType.getCount() +"\t" + remoteType.getCreated() + "\n");
            }

            logger.info("\n" + builder.toString());
        }
        catch (InterruptedException e)
        {
            logger.error(StrUtil.stack2String(e));

        }
        catch (ExecutionException e)
        {
            logger.error(StrUtil.stack2String(e));

        }
    }


}
