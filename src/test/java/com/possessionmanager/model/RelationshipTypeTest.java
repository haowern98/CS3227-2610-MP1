package com.possessionmanager.model;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDateTime;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class RelationshipTypeTest {

    @Test
    void createsTypeWithSuppliedLabelsAndKind() {
        RelationshipTypeInput input = new RelationshipTypeInput("Storage", "stored in", "contains",
                RelationshipKind.DIRECTED);

        RelationshipType type = RelationshipType.create(input);

        assertEquals("Storage", type.name());
        assertEquals("stored in", type.forwardLabel());
        assertEquals("contains", type.inverseLabel());
        assertEquals(RelationshipKind.DIRECTED, type.kind());
    }

    @Test
    void updatePreservesTypeIdentityAndCreationTime() {
        UUID id = UUID.randomUUID();
        LocalDateTime createdAt = LocalDateTime.of(2026, 8, 29, 12, 0);
        RelationshipType existing = new RelationshipType(id, "Storage", "stored in", "contains",
                RelationshipKind.DIRECTED, createdAt, createdAt);
        RelationshipTypeInput replacement = new RelationshipTypeInput("Charging", "charged by", "charges",
                RelationshipKind.DIRECTED);

        RelationshipType updated = existing.update(replacement);

        assertEquals(id, updated.id());
        assertEquals(createdAt, updated.createdAt());
        assertEquals("Charging", updated.name());
        assertEquals("charged by", updated.forwardLabel());
        assertEquals("charges", updated.inverseLabel());
    }
}
