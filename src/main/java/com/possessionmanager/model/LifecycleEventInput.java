package com.possessionmanager.model;

import java.time.LocalDate;

/**
 * Holds the user-editable details of a lifecycle event.
 *
 * @param type kind of event.
 * @param date calendar date on which the event occurred.
 * @param description short explanation of the event.
 * @param notes optional additional notes.
 */
public record LifecycleEventInput(
        LifecycleEventType type,
        LocalDate date,
        String description,
        String notes) {
}
