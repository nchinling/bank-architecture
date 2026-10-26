package sg.edu.rp.sandbox.common.event;

/**
 * Pluggable event publisher.
 *
 * <p>Detachable contract: v1 ships an in-process implementation (backed by
 * Spring's {@code ApplicationEventPublisher}) so the sandbox runs on a laptop
 * with no message broker. A Kafka/Redis implementation can be added later
 * behind this same interface and selected via the {@code provider.events}
 * configuration property - no changes to publishing code.
 */
public interface EventPublisher {

    /**
     * Publishes a domain event.
     *
     * @param event the event payload (an immutable value object)
     */
    void publish(DomainEvent event);
}
