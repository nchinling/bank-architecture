package sg.edu.rp.sandbox.compliance;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * Auto-configuration that wires the simulated compliance adapters into any
 * service depending on this module. Listed in
 * {@code META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports}.
 */
@Configuration
@EnableConfigurationProperties(ComplianceProperties.class)
@ComponentScan(basePackageClasses = ComplianceAutoConfiguration.class)
public class ComplianceAutoConfiguration {
}
