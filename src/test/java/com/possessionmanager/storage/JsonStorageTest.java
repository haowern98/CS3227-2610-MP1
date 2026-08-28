package com.possessionmanager.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
import com.possessionmanager.service.PossessionService;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class JsonStorageTest {

    @TempDir
    Path temporaryDirectory;

    @Test
    void savesAndLoadsPossessions() {
        Path dataFile = temporaryDirectory.resolve("data.json");
        PossessionService service = new PossessionService();
        Possession possession = service.addPossession(new PossessionInput("Camera", PossessionCategory.ELECTRONICS,
                "Camera bag", PossessionStatus.IN_USE, Set.of("travel"), "Main camera"));
        JsonStorage storage = new JsonStorage(dataFile);

        storage.save(service.toAppData());
        AppData restoredData = storage.load();

        assertEquals(1, restoredData.possessions().size());
        assertEquals(possession.id(), restoredData.possessions().getFirst().id());
        assertEquals("Camera bag", restoredData.possessions().getFirst().location());
    }

    @Test
    void returnsEmptyDataWhenFileDoesNotExist() {
        JsonStorage storage = new JsonStorage(temporaryDirectory.resolve("data.json"));

        assertTrue(storage.load().possessions().isEmpty());
    }

    @Test
    void savesAndLoadsLifecycleEvents() {
        Path dataFile = temporaryDirectory.resolve("data.json");
        Possession possession = new PossessionService().addPossession(new PossessionInput("Camera",
                PossessionCategory.ELECTRONICS, "Desk", PossessionStatus.IN_USE, Set.of(), ""));
        LifecycleEvent event = LifecycleEvent.create(possession.id(), new LifecycleEventInput(
                LifecycleEventType.MAINTENANCE, LocalDate.of(2025, 1, 10), "Lens cleaned", ""));
        JsonStorage storage = new JsonStorage(dataFile);

        storage.save(new AppData(java.util.List.of(possession), java.util.List.of(event)));

        assertEquals(event, storage.load().lifecycleEvents().getFirst());
    }

    @Test
    void loadsPossessionOnlyDataFromEarlierVersion() throws IOException {
        Path dataFile = temporaryDirectory.resolve("data.json");
        Files.writeString(dataFile, "{\"possessions\":[]}");
        JsonStorage storage = new JsonStorage(dataFile);

        assertTrue(storage.load().lifecycleEvents().isEmpty());
    }

    @Test
    void preservesCorruptFileBeforeReportingLoadFailure() throws IOException {
        Path dataFile = temporaryDirectory.resolve("data.json");
        Files.writeString(dataFile, "not valid JSON");
        JsonStorage storage = new JsonStorage(dataFile);

        assertThrows(StorageException.class, storage::load);

        try (var files = Files.list(temporaryDirectory)) {
            assertTrue(files.anyMatch(path -> path.getFileName().toString().startsWith("data.corrupt-")));
        }
    }
}
