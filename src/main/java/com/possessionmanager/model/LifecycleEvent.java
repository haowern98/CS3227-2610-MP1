package com.possessionmanager.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

/**
 * Records a dated event in the history of one possession.
 *
 * @param id stable identifier for the event.
 * @param possessionId identifier of the associated possession.
 * @param type kind of lifecycle event.
 * @param date date on which the event occurred.
 * @param description short explanation of the event.
 * @param notes additional notes.
 * @param createdAt time at which the event was recorded.
 * @param updatedAt time at which the event was last changed.
 */
public record LifecycleEvent(
        UUID id,
        UUID possessionId,
        LifecycleEventType type,
        LocalDate date,
        String description,
        String notes,
        LocalDateTime createdAt,
        LocalDateTime updatedAt) {

    /**
     * Creates a lifecycle event with required identifiers and dates.
     */
    public LifecycleEvent {
        Objects.requireNonNull(id, "id must not be null");
        Objects.requireNonNull(possessionId, "possessionId must not be null");
        Objects.requireNonNull(type, "type must not be null");
        Objects.requireNonNull(date, "date must not be null");
        Objects.requireNonNull(description, "description must not be null");
        Objects.requireNonNull(notes, "notes must not be null");
        Objects.requireNonNull(createdAt, "createdAt must not be null");
        Objects.requireNonNull(updatedAt, "updatedAt must not be null");
    }

    /**
     * Creates a new event from validated input.
     *
     * @param possessionId identifier of the associated possession.
     * @param input validated event details.
     * @return new lifecycle event.
     */
    public static LifecycleEvent create(UUID possessionId, LifecycleEventInput input) {
        LocalDateTime now = LocalDateTime.now();
        return new LifecycleEvent(UUID.randomUUID(), possessionId, input.type(), input.date(),
                input.description(), input.notes(), now, now);
    }

    /**
     * Returns a copy with the supplied validated details.
     *
     * @param input validated replacement details.
     * @return updated lifecycle event.
     */
    public LifecycleEvent update(LifecycleEventInput input) {
        return new LifecycleEvent(id, possessionId, input.type(), input.date(), input.description(), input.notes(),
                createdAt, LocalDateTime.now());
    }
}
