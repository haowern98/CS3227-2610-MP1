package com.possessionmanager.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.possessionmanager.model.RelationshipKind;
import com.possessionmanager.model.RelationshipType;
import com.possessionmanager.model.RelationshipTypeInput;
import org.junit.jupiter.api.Test;

class RelationshipTypeServiceTest {

    @Test
    void addsSymmetricTypeWithOneSharedLabel() {
        RelationshipTypeService types = new RelationshipTypeService();

        RelationshipType created = types.addType(input("Compatibility", "compatible with", "",
                RelationshipKind.SYMMETRIC));

        assertEquals("compatible with", created.forwardLabel());
        assertEquals("compatible with", created.inverseLabel());
        assertEquals(RelationshipKind.SYMMETRIC, created.kind());
    }

    @Test
    void rejectsDuplicateNameIgnoringCaseAndWhitespace() {
        RelationshipTypeService types = new RelationshipTypeService();
        types.addType(input("Storage", "stored in", "contains", RelationshipKind.DIRECTED));

        assertThrows(ValidationException.class, () -> types.addType(
                input(" storage ", "kept in", "keeps", RelationshipKind.DIRECTED)));
    }

    @Test
    void rejectsDirectedTypeWithSameLabels() {
        RelationshipTypeService types = new RelationshipTypeService();

        assertThrows(ValidationException.class, () -> types.addType(
                input("Compatibility", "compatible with", "compatible with", RelationshipKind.DIRECTED)));
    }

    @Test
    void updatesAndDeletesExistingType() {
        RelationshipTypeService types = new RelationshipTypeService();
        RelationshipType created = types.addType(input("Storage", "stored in", "contains", RelationshipKind.DIRECTED));

        RelationshipType updated = types.updateType(created.id(),
                input("Charging", "charged by", "charges", RelationshipKind.DIRECTED));
        types.deleteType(created.id());

        assertEquals("Charging", updated.name());
        assertEquals(0, types.listTypes().size());
    }

    private RelationshipTypeInput input(String name, String forwardLabel, String inverseLabel, RelationshipKind kind) {
        return new RelationshipTypeInput(name, forwardLabel, inverseLabel, kind);
    }
}
