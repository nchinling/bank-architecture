package sg.edu.rp.sandbox.common.event;

import java.time.Instant;

/**
 * Marker/base type for all domain events published through {@link EventPublisher}.
 * Keeping a common base lets the event layer stay provider-neutral.
 */
public interface DomainEvent {

    /** A stable event type name, e.g. "PaymentCompleted". */
    String eventType();

    /** When the event occurred. */
    Instant occurredAt();
}
