package com.possessionmanager.model;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import org.junit.jupiter.api.Test;

class RelationshipTemplateTest {

    @Test
    void listsOneClearTemplateForCharging() {
        List<RelationshipTemplate> chargingTemplates = RelationshipTemplate.forCategory(
                RelationshipCategory.CHARGING);

        assertEquals(List.of(RelationshipTemplate.CHARGED_BY), chargingTemplates);
    }

    @Test
    void listsPartAndAccessoryTemplate() {
        List<RelationshipTemplate> partTemplates = RelationshipTemplate.forCategory(
                RelationshipCategory.PART_ACCESSORY);

        assertEquals(List.of(RelationshipTemplate.PART_OF), partTemplates);
    }

    @Test
    void usesSamePhraseForCompatibilityInBothDirections() {
        RelationshipTemplate compatibility = RelationshipTemplate.COMPATIBLE_WITH;

        assertEquals("compatible with", compatibility.forwardLabel());
        assertEquals("compatible with", compatibility.inverseLabel());
    }
}
