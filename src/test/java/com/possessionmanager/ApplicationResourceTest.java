package com.possessionmanager;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

class ApplicationResourceTest {

    @Test
    void includesApplicationStylesheet() {
        assertNotNull(getClass().getResource("/com/possessionmanager/app.css"));
    }
}
