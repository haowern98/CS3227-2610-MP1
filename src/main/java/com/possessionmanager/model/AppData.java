package com.possessionmanager.model;

import java.util.List;
import java.util.Objects;

/**
 * Represents the complete set of data persisted by the current application phase.
 *
 * @param possessions possessions known to the application.
 */
public record AppData(List<Possession> possessions) {

    /**
     * Creates an immutable snapshot of the supplied possessions.
     */
    public AppData {
        Objects.requireNonNull(possessions, "possessions must not be null");
        possessions = List.copyOf(possessions);
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
