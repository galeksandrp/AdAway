package org.adaway.model.source;

import org.adaway.db.dao.HostListItemDao;
import org.adaway.db.entity.HostListItem;
import org.adaway.db.entity.HostsSource;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.stream.Stream;

/**
 * This class is a tool to do batch update into the database.<br>
 * It allows faster insertion by using only one transaction for a batch insert.
 *
 * @author Bruce BUJON (bruce.bujon(at)gmail(dot)com)
 */
class SourceBatchUpdater {
    private static final int BATCH_SIZE = 100;
    private final HostListItemDao hostListItemDao;

    SourceBatchUpdater(HostListItemDao hostListItemDao) {
        this.hostListItemDao = hostListItemDao;
    }

    void updateSource(HostsSource source, Stream<HostListItem> items) {
        // Clear current hosts
        int sourceId = source.getId();
        this.hostListItemDao.clearSourceHosts(sourceId);
        // Create batch

        ExecutorService executorService = Executors.newFixedThreadPool(5, r -> new Thread(r, "SourceUpdater"));


//        HostListItem[] batch = new HostListItem[BATCH_SIZE];
//        int cacheSize = 0;
        // Insert parsed items
//        ForkJoinPool.ForkJoinWorkerThreadFactory forkJoinWorkerThreadFactory =
//                pool -> {
//                    ForkJoinWorkerThread thread = new ForkJoinWorkerThread(pool) {
//
//                    };
//                    thread.setName("SourceUpdater");
//                    thread.setPriority(Process.THREAD_PRIORITY_LESS_FAVORABLE);
//                    return thread;
//                };
//                ForkJoinPool.defaultForkJoinWorkerThreadFactory;
//        ForkJoinPool pool = new ForkJoinPool(4, forkJoinWorkerThreadFactory, null, false);
//
//        try {
//            pool.submit(() -> BatchingIterator.batchedStreamOf(items, BATCH_SIZE)
//                    .forEach(this.hostListItemDao::insert))
//                    .get();
//        } catch (ExecutionException e) {
//            e.printStackTrace();
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//        pool.shutdown();

//        BatchingIterator.batchedStreamOf(items, BATCH_SIZE)
//                .forEach(this.hostListItemDao::insert);

//        Iterator<HostListItem> iterator = items.iterator();
//        while (iterator.hasNext()) {
//            HostListItem item = iterator.next();
//            batch[cacheSize++] = item;
//            if (cacheSize >= batch.length) {
//                this.hostListItemDao.insert(batch);
//                cacheSize = 0;
//            }
//        }
//        for (HostListItem item : items.iterator()) {
//            batch[cacheSize++] = item;
//            if (cacheSize >= batch.length) {
//                this.hostListItemDao.insert(batch);
//                cacheSize = 0;
//            }
//        }
//        // Flush current batch
//        HostListItem[] remaining = new HostListItem[cacheSize];
//        System.arraycopy(batch, 0, remaining, 0, remaining.length);
//        this.hostListItemDao.insert(remaining);
    }
}
