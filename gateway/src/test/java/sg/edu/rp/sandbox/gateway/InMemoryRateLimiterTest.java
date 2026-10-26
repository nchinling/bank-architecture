package sg.edu.rp.sandbox.gateway;

import org.junit.jupiter.api.Test;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;

class InMemoryRateLimiterTest {

    @Test
    void allowsUpToTheLimitThenBlocks() {
        InMemoryRateLimiter limiter = new InMemoryRateLimiter(3, Duration.ofMinutes(1));

        assertThat(limiter.tryAcquire("k1")).isTrue();
        assertThat(limiter.tryAcquire("k1")).isTrue();
        assertThat(limiter.tryAcquire("k1")).isTrue();
        // 4th request in the window is blocked.
        assertThat(limiter.tryAcquire("k1")).isFalse();
    }

    @Test
    void limitsAreIndependentPerKey() {
        InMemoryRateLimiter limiter = new InMemoryRateLimiter(1, Duration.ofMinutes(1));

        assertThat(limiter.tryAcquire("a")).isTrue();
        assertThat(limiter.tryAcquire("a")).isFalse();
        // A different key has its own quota.
        assertThat(limiter.tryAcquire("b")).isTrue();
    }

    @Test
    void windowExpiryAllowsAgain() throws InterruptedException {
        InMemoryRateLimiter limiter = new InMemoryRateLimiter(1, Duration.ofMillis(50));

        assertThat(limiter.tryAcquire("k")).isTrue();
        assertThat(limiter.tryAcquire("k")).isFalse();
        Thread.sleep(80);
        // Window has passed; the old hit is evicted.
        assertThat(limiter.tryAcquire("k")).isTrue();
    }
}
