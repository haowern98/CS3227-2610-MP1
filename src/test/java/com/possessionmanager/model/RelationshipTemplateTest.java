package com.possessionmanager.model;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import org.junit.jupiter.api.Test;

class RelationshipTemplateTest {

    @Test
    void listsOnlyTemplatesForSelectedCategory() {
        List<RelationshipTemplate> chargingTemplates = RelationshipTemplate.forCategory(
                RelationshipCategory.CHARGING);

        assertEquals(List.of(RelationshipTemplate.CHARGED_BY, RelationshipTemplate.POWERED_BY),
                chargingTemplates);
    }

    @Test
    void usesSamePhraseForCompatibilityInBothDirections() {
        RelationshipTemplate compatibility = RelationshipTemplate.COMPATIBLE_WITH;

        assertEquals("compatible with", compatibility.forwardLabel());
        assertEquals("compatible with", compatibility.inverseLabel());
    }
}
