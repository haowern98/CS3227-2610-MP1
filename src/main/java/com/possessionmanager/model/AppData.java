package com.possessionmanager.model;

import java.util.List;

/**
 * Represents the complete set of data persisted by the current application phase.
 *
 * @param possessions possessions known to the application.
 * @param lifecycleEvents lifecycle events linked to possessions.
 */
public record AppData(List<Possession> possessions, List<LifecycleEvent> lifecycleEvents) {

    /**
     * Creates an immutable snapshot of the supplied possessions.
     */
    public AppData {
        possessions = possessions == null ? List.of() : List.copyOf(possessions);
        lifecycleEvents = lifecycleEvents == null ? List.of() : List.copyOf(lifecycleEvents);
    }

    /**
     * Creates a possession-only snapshot compatible with the first persisted format.
     *
     * @param possessions possessions known to the application.
     */
    public AppData(List<Possession> possessions) {
        this(possessions, List.of());
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
