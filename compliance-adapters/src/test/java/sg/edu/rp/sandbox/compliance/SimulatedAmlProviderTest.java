package sg.edu.rp.sandbox.compliance;

import org.junit.jupiter.api.Test;
import sg.edu.rp.sandbox.common.aml.AmlDecision;
import sg.edu.rp.sandbox.common.aml.AmlRequest;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class SimulatedAmlProviderTest {

    private final ComplianceProperties properties = new ComplianceProperties();
    private final ComplianceOverrides overrides = new ComplianceOverrides();
    private final SimulatedAmlProvider provider = new SimulatedAmlProvider(properties, overrides);

    private AmlRequest transfer(String amount) {
        return new AmlRequest("ACC-1", "ACC-2", new BigDecimal(amount), "SGD");
    }

    @Test
    void clearsSmallAmount() {
        assertThat(provider.screen(transfer("100.00")).decision()).isEqualTo(AmlDecision.CLEAR);
    }

    @Test
    void reviewsMediumAmount() {
        // Default review threshold is 5000.00
        assertThat(provider.screen(transfer("5000.00")).decision()).isEqualTo(AmlDecision.REVIEW);
    }

    @Test
    void flagsLargeAmount() {
        // Default flag threshold is 10000.00
        assertThat(provider.screen(transfer("10000.00")).decision()).isEqualTo(AmlDecision.FLAGGED);
    }

    @Test
    void runtimeThresholdOverrideApplies() {
        overrides.setAmlFlagThresholdOverride(new BigDecimal("200.00"));
        assertThat(provider.screen(transfer("250.00")).decision()).isEqualTo(AmlDecision.FLAGGED);
    }
}
