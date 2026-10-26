package sg.edu.rp.sandbox.events;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import sg.edu.rp.sandbox.common.event.DomainEvent;
import sg.edu.rp.sandbox.common.event.EventPublisher;

/**
 * In-process {@link EventPublisher} backed by Spring's
 * {@link ApplicationEventPublisher}. Lets the sandbox publish and handle domain
 * events on a laptop with no message broker.
 *
 * <p>Registered as a bean when {@code provider.events=in-process} (the v1
 * default). A Kafka/Redis publisher can replace it later via config - publishers
 * only ever see the {@link EventPublisher} contract.
 */
public class InProcessEventPublisher implements EventPublisher {

    private static final Logger log = LoggerFactory.getLogger(InProcessEventPublisher.class);

    private final ApplicationEventPublisher delegate;

    public InProcessEventPublisher(ApplicationEventPublisher delegate) {
        this.delegate = delegate;
    }

    @Override
    public void publish(DomainEvent event) {
        log.info("Publishing domain event: {} at {}", event.eventType(), event.occurredAt());
        delegate.publishEvent(event);
    }
}
