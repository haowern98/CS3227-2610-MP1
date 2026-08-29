package com.possessionmanager.model;

/**
 * Holds user-editable labels and direction settings for a relationship type.
 *
 * @param name unique display name for the type.
 * @param forwardLabel label shown from source possession to target possession.
 * @param inverseLabel label shown from target possession to source possession.
 * @param kind whether the type is directed or symmetric.
 */
public record RelationshipTypeInput(
        String name,
        String forwardLabel,
        String inverseLabel,
        RelationshipKind kind) {
}
