package com.possessionmanager.service;

import com.possessionmanager.model.AppData;
import com.possessionmanager.storage.JsonStorage;
import java.util.Objects;

/**
 * Saves an application change atomically with its in-memory service state.
 */
public final class PersistentChange {
    private final PossessionService possessions;
    private final LifecycleEventService events;
    private final JsonStorage storage;

    /**
     * Creates a persistent-change boundary for the shared application services.
     *
     * @param possessions service that owns possessions.
     * @param events service that owns lifecycle events.
     * @param storage destination for complete application snapshots.
     */
    public PersistentChange(PossessionService possessions, LifecycleEventService events, JsonStorage storage) {
        this.possessions = Objects.requireNonNull(possessions, "possessions must not be null");
        this.events = Objects.requireNonNull(events, "events must not be null");
        this.storage = Objects.requireNonNull(storage, "storage must not be null");
    }

    /**
     * Applies and saves a change, restoring the prior state if either step fails.
     *
     * @param change in-memory change to apply.
     * @throws RuntimeException if the change is invalid or the updated snapshot cannot be saved.
     */
    public void run(Runnable change) {
        Objects.requireNonNull(change, "change must not be null");
        AppData originalData = snapshot();
        try {
            change.run();
            storage.save(snapshot());
        } catch (RuntimeException exception) {
            possessions.restore(originalData.possessions());
            events.restore(originalData.lifecycleEvents());
            throw exception;
        }
    }

    private AppData snapshot() {
        return new AppData(possessions.snapshot(), events.listAll());
    }
}
