package com.mobilitus.attractionscmd.importqueue;

import com.mobilitus.gogo.Cache;
import com.mobilitus.gogo.attractions.EventWorker;
import com.mobilitus.gogo.search.AttractionSearch;
import com.mobilitus.gogo.search.EventSearch;
import com.mobilitus.persisted.imports.EventImportQueuePersisted;
import com.mobilitus.persisted.imports.ImportProcessPersisted;
import com.mobilitus.persisted.utils.EntryStatus;
import com.mobilitus.util.data.attractions.DataSource;
import com.mobilitus.util.data.gogo.PromoGhettoData;
import com.mobilitus.util.data.schema.SchemaEvent;
import com.mobilitus.util.data.ticketMaster.EventGhettoData;
import com.mobilitus.util.distributed.aws.cloudsearch.DefaultSearchConfig;
import com.mobilitus.util.distributed.aws.cloudsearch.SearchConfig;
import com.mobilitus.util.distributed.aws.memcached.ElastiCacheAdministrator;
import com.mobilitus.util.distributed.dynamodb.AWSUtils;
import com.mobilitus.util.hexia.StrUtil;
import com.mobilitus.util.hexia.location.CountryCode;
import com.mobilitus.util.hexia.pagination.Page;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.core.async.SdkPublisher;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedAsyncClient;

import java.time.Duration;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author helgaw
 * @since 8/1/24 11:40
 */
public class QueueUtils
{
    private static ElastiCacheAdministrator cacheAdministrator = null;
    private final AwsCredentialsProvider credentials;
    private final DynamoDbEnhancedAsyncClient mapper;
    private static final Logger logger = Logger.getLogger(com.mobilitus.attractionscmd.attractions.AttractionUtil.class);
    private final AttractionSearch gogoSearch;
    private final EventWorker eventWorker;
    private final EventSearch eventSearch;

    AtomicReference<Integer> count = new AtomicReference<>(0);


    public QueueUtils(AwsCredentialsProvider credentials, String memcache)
    {
        this.credentials = credentials;

        if (cacheAdministrator == null)
        {
            Cache.create(credentials.resolveCredentials(), memcache);
            cacheAdministrator = new ElastiCacheAdministrator();
        }
        mapper = AWSUtils.getMapper(credentials);

        SearchConfig searchConfig = new DefaultSearchConfig(credentials);

        eventSearch = new EventSearch(searchConfig.getCredentialsProvider(), searchConfig.getEventSearchURL());

        gogoSearch = new AttractionSearch(searchConfig.getCredentialsProvider(), searchConfig.getArtistSearchURL());

        eventWorker = new EventWorker(null, null, mapper, AWSUtils.getS3(), searchConfig);
    }

    public Integer updateStaleInSchemaWorker()
    {
        SdkPublisher<ImportProcessPersisted> all = ImportProcessPersisted.findAll(mapper);

        AtomicReference<Integer> reProcess = new AtomicReference<>(0);
        AtomicReference<Integer> deleted = new AtomicReference<>(0);
        AtomicReference<Integer> allentries = new AtomicReference<>(0);
        AtomicReference<Integer> stale = new AtomicReference<>(0);

        CompletableFuture<Void> future = new CompletableFuture<>();
        all.subscribe(new Subscriber<>()
        {

            @Override
            public void onSubscribe(Subscription s)
            {
                s.request(50000);
            }

            @Override public void onNext(ImportProcessPersisted importProcess)
            {
                if (importProcess == null)
                {
                    return;
                }
                //                 results.add(eventImportQueuePersisted);
//                Integer duplicates = removeDuplicates(importProcess);

//                if (duplicates > 0)
//                {
//                    logger.info (reProcess.get() + "/" + count.get() +"/" + allentries.get() + " found  " + duplicates.intValue() + " of " + importProcess.toString());
//                    reProcess.set(reProcess.get() + 1);
//                    deleted.set(deleted.get() + duplicates);
//                }
//                else
//                {
                    if (!importProcess.isDone())
                    {
                        SchemaEvent event =importProcess.getSchemaEvent();
                        if (importProcess.getCreated().plusMinutes(5).isAfter(ZonedDateTime.now()))
                        {
                            return;
                        }

                        if (event.getStartDate().isAfterNow())
                        {
                            logger.info(reProcess.get() + "/" + count.get() + "  nudging " +  event.getName() + " a:" + importProcess.artistStatus() + " v:" + importProcess.venueStatus() +
                                        " e:" + importProcess.eventStatus() + " " + getAgeStr(Duration.between(importProcess.getUpdated(), ZonedDateTime.now())));
                            if (importProcess.isArtistDone() && importProcess.isVenueDone())
                            {
                                importProcess.setEventStatus(EntryStatus.pending.value());
                                importProcess.setUpdated(ZonedDateTime.now());
                                ImportProcessPersisted.getTable(mapper).putItem(importProcess);
                                reProcess.set(reProcess.get() + 1);
                            }
                        }
                    }
//                }
//                allentries.set(allentries.get() + 1 + duplicates);
                count.set(count.get() + 1);
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
                System.out.println("Fixed duplicates removed " + deleted.get()  + " of " + count.get());
                System.out.println("Kicked  " + stale.get()  + " entries " );
                future.complete(null);
            }
        });
        try
        {
            future.get(); // This will block until the operation is complete
        }
        catch (InterruptedException e)
        {
            /**
             * @todo improve error handling
             *
             **/
            logger.error(StrUtil.stack2String(e));

        }
        catch (ExecutionException e)
        {
            /**
             * @todo improve error handling
             *
             **/
            logger.error(StrUtil.stack2String(e));

        }

        return count.get();

    }

//    private Integer removeDuplicates(ImportProcessPersisted importProcess)
//    {
//        List<ImportProcessPersisted> allForEvent = ImportProcessPersisted.findByRemoteEventID(mapper, importProcess.getRemoteEventID());
//        Map<String, String> venues = new HashMap<>();
//        Map<String, String> artists = new HashMap<>();
//        venues.put(importProcess.getRemoteVenueID(), importProcess.getRemoteVenueID());
//        artists.put(importProcess.getRemoteArtistID(), importProcess.getRemoteArtistID());
//        Integer duplicates = 0;
//
//        for (ImportProcessPersisted process : allForEvent)
//        {
//            if (process.getId().equalsIgnoreCase(importProcess.getId()))
//            {
//                continue;
//            }
//
//            if (venues.get(process.getRemoteVenueID()) != null && artists.get(process.getRemoteArtistID()) != null)
//            {
//                duplicates++;
//
//                ImportProcessPersisted.getTable(mapper).deleteItem(process);
//            }
//        }
//        return duplicates;
//    }

    public Integer reImport()
    {
        SdkPublisher<EventImportQueuePersisted> all = EventImportQueuePersisted.findAll(mapper);

        AtomicReference<Integer> reProcess = new AtomicReference<>(0);
        AtomicReference<Integer> doneCount = new AtomicReference<>(0);

        CompletableFuture<Void> future = new CompletableFuture<>();
        Map<String, String> existing = new ConcurrentHashMap<>(10000);

        all.subscribe(new Subscriber<>()
        {

            @Override
            public void onSubscribe(Subscription s)
            {
                s.request(50000);
            }

            @Override public void onNext(EventImportQueuePersisted eventImportQueuePersisted)
            {
                //                 results.add(eventImportQueuePersisted);
//                logger.info (  count.get() + " looking at  " + eventImportQueuePersisted.toString());

                if (existing.containsKey(eventImportQueuePersisted.getUrl()))
                {
                    logger.info (  " deleting dupliates " + eventImportQueuePersisted.toString());
                    EventImportQueuePersisted.getTable(mapper).deleteItem(eventImportQueuePersisted);
                    return;
                }
                else
                {
                    existing.put(eventImportQueuePersisted.getId(), eventImportQueuePersisted.getId());
                }
                if (eventImportQueuePersisted.status() == EntryStatus.failed)
                {
                    logger.info (reProcess.get() + "/" + count.get() + " " + eventImportQueuePersisted.toString());
                    reProcess.set(reProcess.get() + 1);
                    reImport(eventImportQueuePersisted);
                    return;

                }
                else if (eventImportQueuePersisted.getCreated().plusDays(7).isBefore(ZonedDateTime.now()))
                {
                    count.set(count.get() + 1);

                    return;
                }
                if (eventImportQueuePersisted.getUpdated().plusMinutes(5).isAfter(ZonedDateTime.now()))
                {
                    // somebody is working on this
                    count.set(count.get() + 1);

                    return;
                }
                Boolean done = false;

                EventGhettoData event = getEvent(eventImportQueuePersisted);
                if (event == null ||  event.getVenue() == null || event.getArtists().isEmpty())
                {
//                    logger.info (reProcess.get() + "/" + count.get() + " " + eventImportQueuePersisted.toString());
//                    reProcess.set(reProcess.get() + 1);
//                    reImport(eventImportQueuePersisted);
                }
                else if (event != null)
                {
                    if (!eventImportQueuePersisted.isDone())
                    {
//                        if (event.getShowTime().isBeforeNow())
                        {
                            logger.info(doneCount.get() + "/" + count.get() + "done with " +  " " + eventImportQueuePersisted.toString());
                            doneCount.set(doneCount.get() + 1);
                            eventImportQueuePersisted.setStatus(EntryStatus.success);
                            EventImportQueuePersisted.getTable(mapper).putItem(eventImportQueuePersisted);
                            done = true;
                        }
//                        else if (eventImportQueuePersisted.getUpdated().plusMinutes(60).isBefore(ZonedDateTime.now()))
//                        {
//                            Duration updateAge = Duration.between(eventImportQueuePersisted.getCreated(), ZonedDateTime.now());
//
//                            logger.info(reProcess.get() + "/" + count.get() + " pending  " + getAgeStr(updateAge) +  " " + eventImportQueuePersisted.toString());
//                            reProcess.set(reProcess.get() + 1);
//                            reImport(eventImportQueuePersisted);
//                        }
                    }
                    else
                    {
                        done = true;
                    }
                }
                //                pending.add(eventImportQueuePersisted);
//                Boolean bOK = handleQueueEntry(eventImportQueuePersisted);
//                if (bOK)
//                {
//                    logger.info (reProcess.get() + "/" + count.get() + " " + eventImportQueuePersisted.toString());
//                    reProcess.set(reProcess.get() + 1);
//                }
                if (!done)
                {
                    logger.info(reProcess.get() + "/" + count.get() + " not done with " +  " " + eventImportQueuePersisted.toString());
                    reProcess.set(reProcess.get() + 1);
                }
                count.set(count.get() + 1);
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
                System.out.println("Got all items");
                future.complete(null);
            }
        });
        try
        {
            future.get(); // This will block until the operation is complete
        }
        catch (InterruptedException e)
        {
            /**
             * @todo improve error handling
             *
             **/
            logger.error(StrUtil.stack2String(e));

        }
        catch (ExecutionException e)
        {
            /**
             * @todo improve error handling
             *
             **/
            logger.error(StrUtil.stack2String(e));
        }

        return count.get();
    }


    public Integer updateStaleEntries()
    {
        SdkPublisher<EventImportQueuePersisted> all = EventImportQueuePersisted.findAll(mapper);

        AtomicReference<Integer> reProcess = new AtomicReference<>(0);

        CompletableFuture<Void> future = new CompletableFuture<>();
        all.subscribe(new Subscriber<>()
        {

            @Override
            public void onSubscribe(Subscription s)
            {
                s.request(50000);
            }

            @Override public void onNext(EventImportQueuePersisted eventImportQueuePersisted)
            {
//                 results.add(eventImportQueuePersisted);
//                logger.info (  count.get() + " looking at  " + eventImportQueuePersisted.toString());
                Boolean bOK = handleQueueEntry(eventImportQueuePersisted);
                if (bOK)
                {
                    logger.info (reProcess.get() + "/" + count.get() + " " + eventImportQueuePersisted.toString());
                    reProcess.set(reProcess.get() + 1);
                }
                count.set(count.get() + 1);
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
                System.out.println("Got all items");
                future.complete(null);
            }
        });
        try
        {
            future.get(); // This will block until the operation is complete
        }
        catch (InterruptedException e)
        {
            /**
             * @todo improve error handling
             *
             **/
            logger.error(StrUtil.stack2String(e));

        }
        catch (ExecutionException e)
        {
            /**
             * @todo improve error handling
             *
             **/
            logger.error(StrUtil.stack2String(e));

        }

        return count.get();
    }

    private Boolean handleQueueEntry(EventImportQueuePersisted eventImportQueuePersisted)
    {
        try
        {
            if (eventImportQueuePersisted.getId().equalsIgnoreCase("promogogo:promogogo:ymsirvidburdir"))
            {
                EventImportQueuePersisted newEvent = new EventImportQueuePersisted(eventImportQueuePersisted);
                EventImportQueuePersisted.getTable(mapper).putItem(eventImportQueuePersisted);

                return true;
            }
            if (eventImportQueuePersisted.isDone())
            {
                if (eventImportQueuePersisted.getEventID() == null)
                {
                    EventGhettoData event = getEvent(eventImportQueuePersisted);
                    if (event != null && !event.getArtists().isEmpty() && event.getVenue() != null)
                    {
                        //            logger.info ("\t\tEvent is OK");
                        eventImportQueuePersisted.success();

                        eventImportQueuePersisted.setEventID(event.getEventID());
                        eventImportQueuePersisted.setTitle(event.getTitle());
                        eventImportQueuePersisted.setShowTime(ZonedDateTime.ofInstant(event.getShowTime().toDateTime(DateTimeZone.UTC)
                                                                                           .toGregorianCalendar().toInstant(), ZoneId.of("Z")));
                        EventImportQueuePersisted.getTable(mapper).putItem(eventImportQueuePersisted);
                        return true;
                    }
                    else
                    {
                        eventImportQueuePersisted.setStatus(EntryStatus.pending);
                        eventImportQueuePersisted.addMessage("Event should be reprocessed");
                        EventImportQueuePersisted.getTable(mapper).putItem(eventImportQueuePersisted);
                        return true;
                    }
                }

    //            logger.info ("\t\tdone");
                return false;
            }
            EventGhettoData event = getEvent(eventImportQueuePersisted);
            if (event != null)
            {
                eventImportQueuePersisted.success();

                if (eventImportQueuePersisted.getEventID() == null)
                {
                    eventImportQueuePersisted.setEventID(event.getEventID());
                    eventImportQueuePersisted.setTitle(event.getTitle());
                    eventImportQueuePersisted.setShowTime(ZonedDateTime.ofInstant(event.getShowTime().toDateTime(DateTimeZone.UTC)
                                                                                       .toGregorianCalendar().toInstant(), ZoneId.of("Z")));
                }
                EventImportQueuePersisted.getTable(mapper).putItem(eventImportQueuePersisted);
                return true;
            }
            else if (eventImportQueuePersisted.getCreated().isBefore(ZonedDateTime.now().minusDays(1)))
            {
                eventImportQueuePersisted.setStatus(EntryStatus.pending);
                eventImportQueuePersisted.addMessage("Event should be reprocessed..");
                EventImportQueuePersisted.getTable(mapper).putItem(eventImportQueuePersisted);
                return true;
            }
            else
            {
    //            eventImportQueuePersisted.setStatus(EntryStatus.failed);
    //            eventImportQueuePersisted.addMessage("Event not found");
    //            EventImportQueuePersisted.getTable(mapper).putItem(eventImportQueuePersisted);
                return false;
            }
        }
        catch (Exception e)
        {
            /**
             * @todo improve error handling
             *
             **/
            logger.error(StrUtil.stack2String(e));
            return false;
        }
    }

    private String checkIfEventIsOK(EventImportQueuePersisted importRequest)
    {
        Page<PromoGhettoData> events = eventWorker.getEventsByAlternativeID(DataSource.create(importRequest.getSource()),  importRequest.getRemoteID());
        if (events == null || events.isEmpty())
        {
            return null;
        }
        return events.get(0).getEventID();
    }

    private EventGhettoData getEvent(EventImportQueuePersisted importRequest)
    {
        Page<PromoGhettoData> events = eventWorker.getEventsByAlternativeID(DataSource.create(importRequest.getSource()),  importRequest.getRemoteID());
        if (events == null || events.isEmpty())
        {
            return null;
        }
        return events.get(0).getGig();
    }

    public Integer fixEventDatesForIndexes()
    {
        SdkPublisher<EventImportQueuePersisted> all = EventImportQueuePersisted.findAll(mapper);

        AtomicReference<Integer> reProcess = new AtomicReference<>(0);
        AtomicReference<Integer> deleted = new AtomicReference<>(0);
        AtomicReference<Integer> counter = new AtomicReference<>(0);

        CompletableFuture<Void> future = new CompletableFuture<>();
        DateTime aug15 = new DateTime(2024, 8, 15, 0, 0, DateTimeZone.UTC);

        all.subscribe(new Subscriber<>()
        {

            @Override
            public void onSubscribe(Subscription s)
            {
                s.request(50000);
            }

            @Override public void onNext(EventImportQueuePersisted eventImportEntry)
            {
                if (counter.get() < 600)
                {
                    counter.set(counter.get() + 1);
                    return;
                }

                if (eventImportEntry == null)
                {
                    return;
                }
                if (eventImportEntry.getEventID() == null)
                {
                    return;
                }
                PromoGhettoData promoGhettoData = eventWorker.getEventFresh(eventImportEntry.getEventID());
                if (promoGhettoData == null)
                {
                    reImport(eventImportEntry);
                }
                else if (promoGhettoData.getCreated().isBefore(aug15) && promoGhettoData.getShowTime().isAfterNow() &&
                         (promoGhettoData.getVenue() == null || promoGhettoData.getVenue().getCountryCode() == CountryCode.is))
                {
//                    if (promoGhettoData.getVenue() != null)
//                    {
//                        logger.info(reProcess.get() +"/" +counter.get() + " Fixing " + promoGhettoData.getTitle() + " " + promoGhettoData.getShowTime() + " " + promoGhettoData.getVenue().getPrettyAddress());
//                    }
//                    eventWorker.remove(eventImportEntry.getEventID());
//                    reProcess.set(reProcess.get() + 1);
//                    reImport(eventImportEntry);
                }
                else if (promoGhettoData.getCreated().isBefore(aug15))
                {
                    if (promoGhettoData.getVenue() != null)
                    {
                        logger.info(reProcess.get() +"/" +counter.get() + " Fixing " + promoGhettoData.getTitle() + " " + promoGhettoData.getShowTime() + " " + promoGhettoData.getVenue().getPrettyAddress());
                    }
                    else
                    {
                        logger.info(reProcess.get() +"/" +counter.get() + " Fixing " + promoGhettoData.getTitle() + " " + promoGhettoData.getShowTime());
                    }
                    eventWorker.remove(eventImportEntry.getEventID());
                    reProcess.set(reProcess.get() + 1);
                    reImport(eventImportEntry);
                }
                else if (counter.get() % 100 == 0)
                {
                    logger.info("     " +counter.get() + " skipping " + promoGhettoData.getTitle() + " " + promoGhettoData.getShowTime());
                }
                //                logger.info(counter.get() + " Saved " + event.getTitle() + " https://dev.promogogo.com/go/promogogo/createmoment.do?event=" + event.getEventID());
                counter.set(counter.get() + 1);
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
                System.out.println("Fixed duplicates removed " + deleted.get()  + " of " + count.get());
                future.complete(null);
            }
        });
        try
        {
            future.get(); // This will block until the operation is complete
        }
        catch (InterruptedException e)
        {
            /**
             * @todo improve error handling
             *
             **/
            logger.error(StrUtil.stack2String(e));

        }
        catch (ExecutionException e)
        {
            /**
             * @todo improve error handling
             *
             **/
            logger.error(StrUtil.stack2String(e));

        }

        return count.get();


    }

    private void reImport(EventImportQueuePersisted eventImport)
    {
//        try
//        {
//            ImportProcessPersisted.getTable(mapper).deleteItem(importProcess).join();
//        }
//        catch (Exception e)
//        {
//            /**
//             * @todo improve error handling
//             *
//             **/
//            logger.error(StrUtil.stack2String(e));
//
//        }
//
        eventImport.setStatus(EntryStatus.pending);
        eventImport.setEventID(null);
        eventImport.addMessage("Reimporting event");
        EventImportQueuePersisted.getTable(mapper).updateItem(eventImport);
    }

    private static   String getAgeStr(Duration duration)
    {
        if (duration.toMinutes() > 119)
        {
            return duration.toHours() + " hours old";
        }
        if (duration.toMinutes() > 15)
        {
            return duration.toMinutes() + " minutes old";
        }
        return duration.toSeconds() + " seconds old";
    }

}
