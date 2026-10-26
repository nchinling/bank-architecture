package sg.edu.rp.sandbox.events;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import sg.edu.rp.sandbox.common.event.EventPublisher;

/**
 * Auto-configuration registering the in-process event publisher. Listed in
 * {@code META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports}.
 */
@Configuration
public class EventsAutoConfiguration {

    @Bean
    @ConditionalOnProperty(name = "provider.events", havingValue = "in-process", matchIfMissing = true)
    EventPublisher inProcessEventPublisher(ApplicationEventPublisher delegate) {
        return new InProcessEventPublisher(delegate);
    }
}
