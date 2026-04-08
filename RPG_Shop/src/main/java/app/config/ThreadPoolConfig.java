package app.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.*;

public class ThreadPoolConfig {

    private static final Logger LOGGER = LoggerFactory.getLogger(ThreadPoolConfig.class);

    private static final int POOL_SIZE = Math.max(2, Runtime.getRuntime().availableProcessors());
    private static final int QUEUE_CAPACITY = 100;

    private static final ThreadPoolExecutor EXECUTOR = new ThreadPoolExecutor(

            POOL_SIZE,
            POOL_SIZE,
            0L,
            TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<>(QUEUE_CAPACITY),
            new ThreadPoolExecutor.AbortPolicy()
    );

    private ThreadPoolConfig() {
    }

    public static ThreadPoolExecutor getExecutor() {

        return EXECUTOR;
    }

    public static <T> Future<T> submit(Callable<T> task) {

        return EXECUTOR.submit(task);
    }

    public static Future<?> submit(Runnable task) {

        return EXECUTOR.submit(task);
    }

    public static void shutdown() {

        LOGGER.info("Shutting down thread pool...");
        EXECUTOR.shutdown();

        try {

            if (!EXECUTOR.awaitTermination(5, TimeUnit.SECONDS)) {

                LOGGER.warn("Pool did not terminate in time, forcing shutdown");
                EXECUTOR.shutdownNow();
            }
        } catch (InterruptedException e) {

            LOGGER.error("Shutdown interrupted: {}", e.getMessage());
            EXECUTOR.shutdownNow();

            Thread.currentThread().interrupt();
        }
    }
}