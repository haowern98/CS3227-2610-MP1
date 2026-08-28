package com.possessionmanager.model;

import java.util.Set;

/**
 * Holds the user-editable details for a possession.
 *
 * @param name display name of the possession.
 * @param category fixed category of the possession.
 * @param location optional physical location of the possession.
 * @param status current lifecycle status of the possession.
 * @param tags optional labels used for searching.
 * @param notes optional free-form notes.
 */
public record PossessionInput(
        String name,
        PossessionCategory category,
        String location,
        PossessionStatus status,
        Set<String> tags,
        String notes) {
}
