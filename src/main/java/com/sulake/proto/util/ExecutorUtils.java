package com.sulake.proto.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinWorkerThread;
import java.util.concurrent.TimeUnit;

/**
 * @author Johno Crawford (johno@sulake.com)
 */
public class ExecutorUtils {

    private static final Logger logger = LogManager.getLogger(ExecutorUtils.class.getName());

    public static ForkJoinPool newScalingThreadPool(String prefix, int parallelism, boolean asyncMode) {
        final ForkJoinPool.ForkJoinWorkerThreadFactory factory = pool ->
        {
            final ForkJoinWorkerThread worker = ForkJoinPool.defaultForkJoinWorkerThreadFactory.newThread(pool);
            worker.setName(prefix + worker.getPoolIndex());
            return worker;
        };
        logger.info("Creating FJP with prefix {}, parallelism {}, asyncMode {}", prefix, parallelism, asyncMode);
        // async-mode tasks get executed in the order they are submitted
        return new ForkJoinPool(parallelism, factory, (t, e) -> logger.error("Uncaught exception", e), asyncMode);
    }

    public static void shutdown(ExecutorService executorService, Logger logger, Runnable cleanup) {
        if (executorService == null) {
            return;
        }
        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(10, TimeUnit.SECONDS)) {
                logger.info("Timeout elapsed before termination, forcing shutdown");
                List<Runnable> tasksAwaitingExecution = executorService.shutdownNow();
                logger.info("Tasks awaiting execution after forced shutdown: " + tasksAwaitingExecution.size());
                if (!executorService.awaitTermination(10, TimeUnit.SECONDS)) {
                    logger.error("Pool did not terminate");
                }
            }
        } catch (InterruptedException e) {
            logger.error("Exception occurred while shutting down thread pool", e);
            executorService.shutdownNow();
            Thread.currentThread().interrupt();
        } finally {
            try {
                cleanup.run();
            } catch (Exception e) {
                logger.error("Error invoking cleanup runnable", e);
            }
        }
    }
}
