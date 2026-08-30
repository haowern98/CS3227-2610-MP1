package com.possessionmanager.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.possessionmanager.model.Possession;
import com.possessionmanager.model.PossessionCategory;
import com.possessionmanager.model.PossessionInput;
import com.possessionmanager.model.PossessionStatus;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class PossessionServiceTest {

    @Test
    void addsPossessionWithNormalizedFields() {
        PossessionService service = new PossessionService();

        Possession possession = service.addPossession(input("  Camera  ", PossessionCategory.ELECTRONICS,
                PossessionStatus.IN_USE, Set.of(" travel ", "camera")));

        assertEquals("Camera", possession.name());
        assertEquals(Set.of("travel", "camera"), possession.tags());
        assertEquals(1, service.listAll().size());
    }

    @Test
    void rejectsBlankPossessionName() {
        PossessionService service = new PossessionService();

        assertThrows(ValidationException.class, () -> service.addPossession(
                input("   ", PossessionCategory.OTHER, PossessionStatus.IN_USE, Set.of())));
    }

    @Test
    void updatesExistingPossession() {
        PossessionService service = new PossessionService();
        Possession possession = service.addPossession(input("Camera", PossessionCategory.ELECTRONICS,
                PossessionStatus.IN_USE, Set.of("travel")));

        Possession updated = service.updatePossession(possession.id(), input("Sony Camera",
                PossessionCategory.ELECTRONICS, PossessionStatus.RETIRED, Set.of("travel", "mirrorless")));

        assertEquals("Sony Camera", updated.name());
        assertEquals(PossessionStatus.RETIRED, updated.status());
        assertTrue(updated.tags().contains("mirrorless"));
    }

    @Test
    void archivesPossessionInsteadOfRemovingIt() {
        PossessionService service = new PossessionService();
        Possession possession = service.addPossession(input("Camera", PossessionCategory.ELECTRONICS,
                PossessionStatus.IN_USE, Set.of()));

        service.archivePossession(possession.id());

        assertEquals(PossessionStatus.ARCHIVED, service.findById(possession.id()).orElseThrow().status());
        assertFalse(service.listAll().contains(possession));
        assertEquals(1, service.listArchived().size());
    }

    @Test
    void deletesExistingPossession() {
        PossessionService service = new PossessionService();
        Possession possession = service.addPossession(input("Camera", PossessionCategory.ELECTRONICS,
                PossessionStatus.IN_USE, Set.of()));

        service.deletePossession(possession.id());

        assertFalse(service.findById(possession.id()).isPresent());
        assertEquals(List.of(), service.listAll());
        assertEquals(List.of(), service.toAppData().possessions());
    }

    @Test
    void rejectsDeletingMissingPossession() {
        PossessionService service = new PossessionService();

        assertThrows(ValidationException.class, () -> service.deletePossession(UUID.randomUUID()));
    }

    @Test
    void searchesNamesAndTagsIgnoringCase() {
        PossessionService service = new PossessionService();
        service.addPossession(input("Sony Camera", PossessionCategory.ELECTRONICS,
                PossessionStatus.IN_USE, Set.of("travel")));
        service.addPossession(input("Book", PossessionCategory.BOOKS, PossessionStatus.IN_USE, Set.of("reading")));

        assertEquals(1, service.search("CAMERA").size());
        assertEquals("Sony Camera", service.search("TRAVEL").getFirst().name());
    }

    @Test
    void filtersOnlyActivePossessionsByCategoryAndStatus() {
        PossessionService service = new PossessionService();
        service.addPossession(input("Camera", PossessionCategory.ELECTRONICS, PossessionStatus.IN_USE, Set.of()));
        service.addPossession(input("Adapter", PossessionCategory.ELECTRONICS, PossessionStatus.RETIRED, Set.of()));
        Possession archived = service.addPossession(input("Old Book", PossessionCategory.BOOKS,
                PossessionStatus.IN_USE, Set.of()));
        service.archivePossession(archived.id());

        assertEquals(2, service.filterByCategory(PossessionCategory.ELECTRONICS).size());
        assertEquals(1, service.filterByStatus(PossessionStatus.RETIRED).size());
        assertEquals(2, service.listAll().size());
    }

    @Test
    void combinesSearchCategoryAndStatusFilters() {
        PossessionService service = new PossessionService();
        service.addPossession(input("Camera", PossessionCategory.ELECTRONICS, PossessionStatus.IN_USE,
                Set.of("travel")));
        service.addPossession(input("Camera Strap", PossessionCategory.ACCESSORIES, PossessionStatus.IN_USE,
                Set.of("travel")));
        service.addPossession(input("Spare Camera", PossessionCategory.ELECTRONICS, PossessionStatus.RETIRED,
                Set.of()));

        assertEquals(List.of("Camera"), service.query("camera", PossessionCategory.ELECTRONICS,
                PossessionStatus.IN_USE).stream().map(Possession::name).toList());
    }

    private PossessionInput input(String name, PossessionCategory category, PossessionStatus status, Set<String> tags) {
        return new PossessionInput(name, category, "Desk", status, tags, "Useful item");
    }
}
