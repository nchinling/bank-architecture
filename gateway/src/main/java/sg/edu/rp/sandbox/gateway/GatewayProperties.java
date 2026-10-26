package sg.edu.rp.sandbox.gateway;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.List;

/**
 * Gateway configuration for sandbox API keys and rate limiting.
 *
 * <pre>
 * sandbox:
 *   gateway:
 *     api-key-header: X-Sandbox-Key
 *     api-keys:
 *       - demo-key-123
 *       - student-key-456
 *     rate-limit:
 *       requests-per-minute: 60
 * </pre>
 */
@ConfigurationProperties(prefix = "sandbox.gateway")
public class GatewayProperties {

    /** Header that carries the sandbox API key. */
    private String apiKeyHeader = "X-Sandbox-Key";

    /** Valid sandbox API keys (self-service keys handed to students). */
    private List<String> apiKeys = new ArrayList<>();

    private final RateLimit rateLimit = new RateLimit();

    public String getApiKeyHeader() {
        return apiKeyHeader;
    }

    public void setApiKeyHeader(String apiKeyHeader) {
        this.apiKeyHeader = apiKeyHeader;
    }

    public List<String> getApiKeys() {
        return apiKeys;
    }

    public void setApiKeys(List<String> apiKeys) {
        this.apiKeys = apiKeys;
    }

    public RateLimit getRateLimit() {
        return rateLimit;
    }

    public static class RateLimit {
        /** Max requests allowed per API key per minute. */
        private int requestsPerMinute = 60;

        public int getRequestsPerMinute() {
            return requestsPerMinute;
        }

        public void setRequestsPerMinute(int requestsPerMinute) {
            this.requestsPerMinute = requestsPerMinute;
        }
    }
}
