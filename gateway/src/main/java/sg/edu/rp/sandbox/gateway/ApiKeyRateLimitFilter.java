package sg.edu.rp.sandbox.gateway;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Edge filter enforcing two sandbox concerns before a request is routed:
 * <ol>
 *   <li><b>API key</b>: requests must carry a valid key in the configured header,</li>
 *   <li><b>Rate limit</b>: each key is capped at N requests/minute (in-memory).</li>
 * </ol>
 *
 * <p>Public paths (actuator health, aggregated OpenAPI/Swagger) are exempt so the
 * docs and health checks stay reachable without a key.
 */
public class ApiKeyRateLimitFilter implements GlobalFilter, Ordered {

    private static final List<String> PUBLIC_PREFIXES = List.of(
            "/actuator", "/v3/api-docs", "/swagger-ui", "/webjars");

    private final Set<String> validKeys;
    private final String apiKeyHeader;
    private final InMemoryRateLimiter rateLimiter;

    public ApiKeyRateLimitFilter(GatewayProperties properties) {
        this.validKeys = new HashSet<>(properties.getApiKeys());
        this.apiKeyHeader = properties.getApiKeyHeader();
        this.rateLimiter = new InMemoryRateLimiter(
                properties.getRateLimit().getRequestsPerMinute(), java.time.Duration.ofMinutes(1));
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getPath().value();

        // Let public paths through without a key.
        if (isPublic(path)) {
            return chain.filter(exchange);
        }

        String key = request.getHeaders().getFirst(apiKeyHeader);
        if (key == null || key.isBlank() || !validKeys.contains(key)) {
            return reject(exchange, HttpStatus.UNAUTHORIZED);
        }

        if (!rateLimiter.tryAcquire(key)) {
            return reject(exchange, HttpStatus.TOO_MANY_REQUESTS);
        }

        return chain.filter(exchange);
    }

    private boolean isPublic(String path) {
        return PUBLIC_PREFIXES.stream().anyMatch(path::startsWith);
    }

    private Mono<Void> reject(ServerWebExchange exchange, HttpStatus status) {
        exchange.getResponse().setStatusCode(status);
        return exchange.getResponse().setComplete();
    }

    @Override
    public int getOrder() {
        // Run early, before routing and token validation.
        return -100;
    }
}
