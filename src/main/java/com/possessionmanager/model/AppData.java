package com.possessionmanager.model;

import java.util.List;

/**
 * Represents the complete set of data persisted by the current application phase.
 *
 * @param possessions possessions known to the application.
 * @param lifecycleEvents lifecycle events linked to possessions.
 * @param relationshipTypes controlled labels available for possession relationships.
 */
public record AppData(
        List<Possession> possessions,
        List<LifecycleEvent> lifecycleEvents,
        List<RelationshipType> relationshipTypes) {

    /**
     * Creates an immutable snapshot of the supplied application data.
     */
    public AppData {
        possessions = possessions == null ? List.of() : List.copyOf(possessions);
        lifecycleEvents = lifecycleEvents == null ? List.of() : List.copyOf(lifecycleEvents);
        relationshipTypes = relationshipTypes == null ? List.of() : List.copyOf(relationshipTypes);
    }

    /**
     * Creates a snapshot containing possessions and their lifecycle events.
     *
     * @param possessions possessions known to the application.
     * @param lifecycleEvents lifecycle events linked to possessions.
     */
    public AppData(List<Possession> possessions, List<LifecycleEvent> lifecycleEvents) {
        this(possessions, lifecycleEvents, List.of());
    }

    /**
     * Creates a possession-only snapshot compatible with the first persisted format.
     *
     * @param possessions possessions known to the application.
     */
    public AppData(List<Possession> possessions) {
        this(possessions, List.of(), List.of());
    }

    /**
     * Returns an empty application-data snapshot.
     *
     * @return an empty snapshot.
     */
    public static AppData empty() {
        return new AppData(List.of());
    }
}
