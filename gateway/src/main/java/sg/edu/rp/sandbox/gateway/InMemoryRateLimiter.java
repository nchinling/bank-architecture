package sg.edu.rp.sandbox.gateway;

import java.time.Duration;
import java.time.Instant;
import java.util.Deque;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;

/**
 * Simple per-key sliding-window rate limiter held in memory.
 *
 * <p>Deliberately dependency-free so the sandbox needs no Redis to run natively.
 * In a clustered deployment you would swap this for a distributed limiter (e.g.
 * Redis) - the gateway filter depends only on the {@link #tryAcquire(String)}
 * method, so that swap is localised.
 */
public class InMemoryRateLimiter {

    private final int maxRequests;
    private final Duration window;
    private final Map<String, Deque<Long>> hits = new ConcurrentHashMap<>();

    public InMemoryRateLimiter(int maxRequests, Duration window) {
        this.maxRequests = maxRequests;
        this.window = window;
    }

    /**
     * Records a request for the key and reports whether it is within the limit.
     *
     * @return true if allowed, false if the key has exceeded its quota
     */
    public boolean tryAcquire(String key) {
        long now = Instant.now().toEpochMilli();
        long cutoff = now - window.toMillis();
        Deque<Long> timestamps = hits.computeIfAbsent(key, k -> new ConcurrentLinkedDeque<>());

        synchronized (timestamps) {
            // Drop timestamps outside the window.
            while (!timestamps.isEmpty() && timestamps.peekFirst() < cutoff) {
                timestamps.pollFirst();
            }
            if (timestamps.size() >= maxRequests) {
                return false;
            }
            timestamps.addLast(now);
            return true;
        }
    }
}
