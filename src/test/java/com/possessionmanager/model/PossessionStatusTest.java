package com.possessionmanager.model;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import org.junit.jupiter.api.Test;

class PossessionStatusTest {

    @Test
    void providesOnlyUserAssignableStatuses() {
        assertEquals(List.of(PossessionStatus.IN_USE, PossessionStatus.LENT_OUT, PossessionStatus.RETIRED),
                List.of(PossessionStatus.values()));
    }
}
