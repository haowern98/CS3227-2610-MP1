package com.possessionmanager.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class RelationshipTypeDialogTest {

    @Test
    void formatsBothReadingsForDirectionalLabel() {
        String example = RelationshipTypeDialog.formatExample("stored in", "contains");

        assertEquals("Item A is stored in Item B, Item B contains Item A", example);
    }

    @Test
    void formatsBothReadingsForSameWordingLabel() {
        String example = RelationshipTypeDialog.formatExample("compatible with", "compatible with");

        assertEquals("Item A is compatible with Item B, Item B is compatible with Item A", example);
    }
}
