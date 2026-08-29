package com.possessionmanager.model;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

/**
 * Defines the controlled labels used by a possession relationship.
 *
 * @param id stable identifier for the relationship type.
 * @param name unique display name for the type.
 * @param forwardLabel label shown from source possession to target possession.
 * @param inverseLabel label shown from target possession to source possession.
 * @param kind whether the type is directed or symmetric.
 * @param createdAt time at which the type was first recorded.
 * @param updatedAt time at which the type was last changed.
 */
public record RelationshipType(
        UUID id,
        String name,
        String forwardLabel,
        String inverseLabel,
        RelationshipKind kind,
        LocalDateTime createdAt,
        LocalDateTime updatedAt) {

    /**
     * Creates a relationship type with required identifiers, labels, and timestamps.
     */
    public RelationshipType {
        Objects.requireNonNull(id, "id must not be null");
        Objects.requireNonNull(name, "name must not be null");
        Objects.requireNonNull(forwardLabel, "forwardLabel must not be null");
        Objects.requireNonNull(inverseLabel, "inverseLabel must not be null");
        Objects.requireNonNull(kind, "kind must not be null");
        Objects.requireNonNull(createdAt, "createdAt must not be null");
        Objects.requireNonNull(updatedAt, "updatedAt must not be null");
    }

    /**
     * Creates a new relationship type from validated input.
     *
     * @param input validated relationship-type details.
     * @return the new relationship type.
     */
    public static RelationshipType create(RelationshipTypeInput input) {
        Objects.requireNonNull(input, "input must not be null");
        LocalDateTime now = LocalDateTime.now();
        return new RelationshipType(UUID.randomUUID(), input.name(), input.forwardLabel(), input.inverseLabel(),
                input.kind(), now, now);
    }

    /**
     * Returns a copy with the supplied validated details.
     *
     * @param input validated replacement details.
     * @return the updated relationship type.
     */
    public RelationshipType update(RelationshipTypeInput input) {
        Objects.requireNonNull(input, "input must not be null");
        return new RelationshipType(id, input.name(), input.forwardLabel(), input.inverseLabel(), input.kind(),
                createdAt, LocalDateTime.now());
    }
}
