package com.possessionmanager.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.possessionmanager.model.LifecycleEvent;
import com.possessionmanager.model.LifecycleEventInput;
import com.possessionmanager.model.LifecycleEventType;
import com.possessionmanager.model.Possession;
import com.possessionmanager.model.PossessionCategory;
import com.possessionmanager.model.PossessionInput;
import com.possessionmanager.model.PossessionStatus;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class LifecycleEventServiceTest {

    @Test
    void listsEventsForPossessionNewestFirst() {
        PossessionService possessions = new PossessionService();
        Possession camera = addPossession(possessions, "Camera");
        LifecycleEventService events = new LifecycleEventService(possessions);

        events.addEvent(camera.id(), event(LocalDate.of(2025, 1, 10), "Lens cleaned"));
        events.addEvent(camera.id(), event(LocalDate.of(2025, 2, 4), "Lens repaired"));

        assertEquals(List.of("Lens repaired", "Lens cleaned"), events.listForPossession(camera.id()).stream()
                .map(LifecycleEvent::description)
                .toList());
    }

    @Test
    void rejectsEventForMissingPossession() {
        LifecycleEventService events = new LifecycleEventService(new PossessionService());

        assertThrows(ValidationException.class, () -> events.addEvent(UUID.randomUUID(),
                event(LocalDate.of(2025, 1, 10), "Lens cleaned")));
    }

    @Test
    void rejectsFutureEventDate() {
        PossessionService possessions = new PossessionService();
        Possession camera = addPossession(possessions, "Camera");
        LifecycleEventService events = new LifecycleEventService(possessions);

        assertThrows(ValidationException.class, () -> events.addEvent(camera.id(),
                event(LocalDate.now().plusDays(1), "Scheduled maintenance")));
    }

    @Test
    void updatesAndDeletesEvent() {
        PossessionService possessions = new PossessionService();
        Possession camera = addPossession(possessions, "Camera");
        LifecycleEventService events = new LifecycleEventService(possessions);
        LifecycleEvent created = events.addEvent(camera.id(), event(LocalDate.of(2025, 1, 10), "Lens cleaned"));

        LifecycleEvent updated = events.updateEvent(created.id(), event(LocalDate.of(2025, 1, 11), "Sensor cleaned"));
        events.deleteEvent(created.id());

        assertEquals("Sensor cleaned", updated.description());
        assertEquals(List.of(), events.listForPossession(camera.id()));
    }

    private Possession addPossession(PossessionService possessions, String name) {
        return possessions.addPossession(new PossessionInput(name, PossessionCategory.ELECTRONICS, "Desk",
                PossessionStatus.IN_USE, Set.of(), ""));
    }

    private LifecycleEventInput event(LocalDate date, String description) {
        return new LifecycleEventInput(LifecycleEventType.MAINTENANCE, date, description, "");
    }
}
