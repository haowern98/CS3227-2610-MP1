package com.possessionmanager.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.possessionmanager.model.AppData;
import com.possessionmanager.model.LifecycleEvent;
import com.possessionmanager.model.LifecycleEventInput;
import com.possessionmanager.model.LifecycleEventType;
import com.possessionmanager.model.Possession;
import com.possessionmanager.model.PossessionCategory;
import com.possessionmanager.model.PossessionInput;
import com.possessionmanager.model.PossessionStatus;
import com.possessionmanager.storage.JsonStorage;
import com.possessionmanager.storage.StorageException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class PersistentChangeTest {

    @TempDir
    Path temporaryDirectory;

    private PossessionService possessions;
    private LifecycleEventService events;
    private Possession camera;
    private LifecycleEvent maintenance;

    @BeforeEach
    void setUp() {
        possessions = new PossessionService();
        camera = possessions.addPossession(possessionInput("Camera", "Desk"));
        events = new LifecycleEventService(possessions);
        maintenance = events.addEvent(camera.id(), eventInput("Lens cleaned"));
    }

    @Test
    void failedSaveRollsBackAddedPossession() throws IOException {
        PersistentChange change = failingChange();

        assertThrows(StorageException.class,
                () -> change.run(() -> possessions.addPossession(possessionInput("Laptop", "Shelf"))));

        assertEquals(Set.of("Camera"), possessionNames());
    }

    @Test
    void failedSaveRollsBackEditedPossession() throws IOException {
        PersistentChange change = failingChange();

        assertThrows(StorageException.class,
                () -> change.run(() -> possessions.updatePossession(camera.id(),
                        possessionInput("Updated Camera", "Cupboard"))));

        assertEquals(camera, possessions.findById(camera.id()).orElseThrow());
    }

    @Test
    void failedSaveRollsBackPossessionAndEventDeletion() throws IOException {
        PersistentChange change = failingChange();

        assertThrows(StorageException.class, () -> change.run(() -> {
            events.deleteForPossession(camera.id());
            possessions.deletePossession(camera.id());
        }));

        assertEquals(camera, possessions.findById(camera.id()).orElseThrow());
        assertEquals(Set.of(maintenance), Set.copyOf(events.listForPossession(camera.id())));
    }

    @Test
    void failedSaveRollsBackAddedEvent() throws IOException {
        PersistentChange change = failingChange();

        assertThrows(StorageException.class,
                () -> change.run(() -> events.addEvent(camera.id(), eventInput("Battery replaced"))));

        assertEquals(Set.of(maintenance), Set.copyOf(events.listForPossession(camera.id())));
    }

    @Test
    void failedSaveRollsBackEditedEvent() throws IOException {
        PersistentChange change = failingChange();

        assertThrows(StorageException.class,
                () -> change.run(() -> events.updateEvent(maintenance.id(), eventInput("Updated event"))));

        assertEquals(Set.of(maintenance), Set.copyOf(events.listForPossession(camera.id())));
    }

    @Test
    void failedSaveRollsBackDeletedEvent() throws IOException {
        PersistentChange change = failingChange();

        assertThrows(StorageException.class, () -> change.run(() -> events.deleteEvent(maintenance.id())));

        assertEquals(Set.of(maintenance), Set.copyOf(events.listForPossession(camera.id())));
    }

    @Test
    void successfulSaveAfterFailureDoesNotPersistFailedChange() throws IOException {
        PersistentChange failedChange = failingChange();
        assertThrows(StorageException.class,
                () -> failedChange.run(() -> possessions.addPossession(possessionInput("Failed Laptop", "Shelf"))));

        Path dataFile = temporaryDirectory.resolve("saved-data.json");
        new PersistentChange(possessions, events, new JsonStorage(dataFile))
                .run(() -> possessions.addPossession(possessionInput("Phone", "Drawer")));

        AppData savedData = new JsonStorage(dataFile).load();
        assertEquals(Set.of("Camera", "Phone"), savedData.possessions().stream()
                .map(Possession::name)
                .collect(java.util.stream.Collectors.toSet()));
        assertFalse(savedData.possessions().stream().anyMatch(item -> item.name().equals("Failed Laptop")));
    }

    @Test
    void validationFailureRollsBackEarlierMutation() throws IOException {
        PersistentChange change = failingChange();

        ValidationException exception = assertThrows(ValidationException.class, () -> change.run(() -> {
            events.deleteForPossession(camera.id());
            possessions.deletePossession(UUID.randomUUID());
        }));

        assertEquals("Possession was not found.", exception.getMessage());
        assertEquals(Set.of(maintenance), Set.copyOf(events.listForPossession(camera.id())));
    }

    private PersistentChange failingChange() throws IOException {
        Path blockedParent = temporaryDirectory.resolve("blocked-parent");
        Files.writeString(blockedParent, "not a directory");
        return new PersistentChange(possessions, events, new JsonStorage(blockedParent.resolve("data.json")));
    }

    private Set<String> possessionNames() {
        return possessions.listAll().stream().map(Possession::name).collect(java.util.stream.Collectors.toSet());
    }

    private PossessionInput possessionInput(String name, String location) {
        return new PossessionInput(name, PossessionCategory.ELECTRONICS, location, PossessionStatus.IN_USE,
                Set.of(), "");
    }

    private LifecycleEventInput eventInput(String description) {
        return new LifecycleEventInput(LifecycleEventType.MAINTENANCE, LocalDate.of(2026, 1, 1), description, "");
    }
}
