package com.possessionmanager.model;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

/**
 * Represents one physical possession and its current metadata.
 *
 * @param id stable identifier for the possession.
 * @param name display name of the possession.
 * @param category fixed category of the possession.
 * @param location physical location of the possession.
 * @param status current lifecycle status of the possession.
 * @param tags search labels associated with the possession.
 * @param notes free-form notes about the possession.
 * @param createdAt time at which the possession was first recorded.
 * @param updatedAt time at which the possession was last changed.
 */
public record Possession(
        UUID id,
        String name,
        PossessionCategory category,
        String location,
        PossessionStatus status,
        Set<String> tags,
        String notes,
        LocalDateTime createdAt,
        LocalDateTime updatedAt) {

    /**
     * Creates a possession with defensive ownership of its tags.
     */
    public Possession {
        Objects.requireNonNull(id, "id must not be null");
        Objects.requireNonNull(name, "name must not be null");
        Objects.requireNonNull(category, "category must not be null");
        Objects.requireNonNull(location, "location must not be null");
        Objects.requireNonNull(status, "status must not be null");
        Objects.requireNonNull(tags, "tags must not be null");
        Objects.requireNonNull(notes, "notes must not be null");
        Objects.requireNonNull(createdAt, "createdAt must not be null");
        Objects.requireNonNull(updatedAt, "updatedAt must not be null");
        tags = Collections.unmodifiableSet(new LinkedHashSet<>(tags));
    }

    /**
     * Creates a new possession from validated input.
     *
     * @param input normalized details supplied by the user.
     * @return the new possession.
     */
    public static Possession create(PossessionInput input) {
        LocalDateTime now = LocalDateTime.now();
        return new Possession(UUID.randomUUID(), input.name(), input.category(), input.location(), input.status(),
                input.tags(), input.notes(), now, now);
    }

    /**
     * Returns a copy with the supplied validated details.
     *
     * @param input normalized replacement details.
     * @return the updated possession.
     */
    public Possession updateDetails(PossessionInput input) {
        return new Possession(id, input.name(), input.category(), input.location(), input.status(), input.tags(),
                input.notes(), createdAt, LocalDateTime.now());
    }
}
