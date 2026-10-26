package sg.edu.rp.sandbox.gateway;

import org.junit.jupiter.api.Test;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.http.HttpStatus;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Drives the edge {@link ApiKeyRateLimitFilter} directly with mock exchanges, so
 * behaviour is deterministic and needs no running backends:
 * <ul>
 *   <li>missing/unknown key -> 401,</li>
 *   <li>valid key within quota -> passes to the chain,</li>
 *   <li>exceeding the per-key quota -> 429,</li>
 *   <li>public paths (actuator) bypass the key check.</li>
 * </ul>
 */
class ApiKeyRateLimitFilterIntegrationTest {

    private final GatewayProperties properties = buildProps();
    private final ApiKeyRateLimitFilter filter = new ApiKeyRateLimitFilter(properties);

    private static GatewayProperties buildProps() {
        GatewayProperties p = new GatewayProperties();
        p.setApiKeyHeader("X-Sandbox-Key");
        p.setApiKeys(java.util.List.of("test-key"));
        p.getRateLimit().setRequestsPerMinute(3);
        return p;
    }

    /** A chain that records invocation and completes successfully. */
    private static class RecordingChain implements GatewayFilterChain {
        boolean invoked = false;

        @Override
        public Mono<Void> filter(ServerWebExchange exchange) {
            invoked = true;
            return Mono.empty();
        }
    }

    private int run(String path, String key) {
        MockServerHttpRequest.BaseBuilder<?> builder = MockServerHttpRequest.get(path);
        if (key != null) {
            builder.header("X-Sandbox-Key", key);
        }
        MockServerWebExchange exchange = MockServerWebExchange.from(builder);
        RecordingChain chain = new RecordingChain();
        filter.filter(exchange, chain).block();
        HttpStatus status = (HttpStatus) exchange.getResponse().getStatusCode();
        // If the filter let the request through, the chain was invoked and no error
        // status was set; represent that as 200 for assertion purposes.
        if (chain.invoked && status == null) {
            return 200;
        }
        return status == null ? 200 : status.value();
    }

    @Test
    void missingKeyIsUnauthorized() {
        assertThat(run("/api/v1/customers/me", null)).isEqualTo(401);
    }

    @Test
    void unknownKeyIsUnauthorized() {
        assertThat(run("/api/v1/customers/me", "not-a-real-key")).isEqualTo(401);
    }

    @Test
    void validKeyWithinQuotaPasses() {
        assertThat(run("/api/v1/customers/me", "test-key")).isEqualTo(200);
    }

    @Test
    void publicPathBypassesKeyCheck() {
        assertThat(run("/actuator/health", null)).isEqualTo(200);
    }

    @Test
    void exceedingRateLimitReturns429() {
        // Limit is 3/min. First three pass; the fourth is throttled.
        assertThat(run("/api/v1/customers/me", "test-key")).isEqualTo(200);
        assertThat(run("/api/v1/customers/me", "test-key")).isEqualTo(200);
        assertThat(run("/api/v1/customers/me", "test-key")).isEqualTo(200);
        assertThat(run("/api/v1/customers/me", "test-key")).isEqualTo(429);
    }
}
