package com.possessionmanager.service;

import com.possessionmanager.model.LifecycleEvent;
import com.possessionmanager.model.LifecycleEventInput;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * Manages lifecycle events while preserving their ownership by possessions.
 */
public final class LifecycleEventService {
    private final PossessionService possessionService;
    private final Map<UUID, LifecycleEvent> events = new LinkedHashMap<>();

    /**
     * Creates an empty lifecycle-event service.
     *
     * @param possessionService service used to validate event ownership.
     */
    public LifecycleEventService(PossessionService possessionService) {
        this(possessionService, List.of());
    }

    /**
     * Creates a lifecycle-event service from persisted events.
     *
     * @param possessionService service used to validate event ownership.
     * @param events persisted lifecycle events.
     */
    public LifecycleEventService(PossessionService possessionService, List<LifecycleEvent> events) {
        this.possessionService = Objects.requireNonNull(possessionService, "possessionService must not be null");
        for (LifecycleEvent event : events) {
            requirePossession(event.possessionId());
            if (this.events.putIfAbsent(event.id(), event) != null) {
                throw new ValidationException("Saved data contains duplicate lifecycle event IDs.");
            }
        }
    }

    /**
     * Adds an event to an existing possession.
     *
     * @param possessionId identifier of the associated possession.
     * @param input event details.
     * @return created lifecycle event.
     */
    public LifecycleEvent addEvent(UUID possessionId, LifecycleEventInput input) {
        requirePossession(possessionId);
        LifecycleEvent event = LifecycleEvent.create(possessionId, normalize(input));
        events.put(event.id(), event);
        return event;
    }

    /**
     * Updates one existing lifecycle event.
     *
     * @param eventId identifier of the event to update.
     * @param input replacement event details.
     * @return updated lifecycle event.
     */
    public LifecycleEvent updateEvent(UUID eventId, LifecycleEventInput input) {
        LifecycleEvent updated = requireEvent(eventId).update(normalize(input));
        events.put(eventId, updated);
        return updated;
    }

    /**
     * Deletes one lifecycle event.
     *
     * @param eventId identifier of the event to delete.
     */
    public void deleteEvent(UUID eventId) {
        if (events.remove(eventId) == null) {
            throw new ValidationException("Lifecycle event was not found.");
        }
    }

    /**
     * Lists one possession's events from newest to oldest.
     *
     * @param possessionId identifier of the possession.
     * @return lifecycle events owned by the possession.
     */
    public List<LifecycleEvent> listForPossession(UUID possessionId) {
        requirePossession(possessionId);
        return events.values().stream()
                .filter(event -> event.possessionId().equals(possessionId))
                .sorted(Comparator.comparing(LifecycleEvent::date).reversed())
                .toList();
    }

    /**
     * Returns all events for persistence.
     *
     * @return immutable event snapshot.
     */
    public List<LifecycleEvent> listAll() {
        return List.copyOf(events.values());
    }

    private void requirePossession(UUID possessionId) {
        if (possessionId == null || possessionService.findById(possessionId).isEmpty()) {
            throw new ValidationException("Possession was not found.");
        }
    }

    private LifecycleEvent requireEvent(UUID eventId) {
        LifecycleEvent event = events.get(eventId);
        if (event == null) {
            throw new ValidationException("Lifecycle event was not found.");
        }
        return event;
    }

    private LifecycleEventInput normalize(LifecycleEventInput input) {
        Objects.requireNonNull(input, "input must not be null");
        if (input.type() == null || input.date() == null) {
            throw new ValidationException("Event type and date are required.");
        }
        if (input.date().isAfter(LocalDate.now())) {
            throw new ValidationException("Event date cannot be in the future.");
        }
        String description = normalizeText(input.description());
        if (description.isEmpty()) {
            throw new ValidationException("Event description is required.");
        }
        return new LifecycleEventInput(input.type(), input.date(), description, normalizeText(input.notes()));
    }

    private String normalizeText(String text) {
        return text == null ? "" : text.trim();
    }
}
