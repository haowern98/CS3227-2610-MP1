package com.possessionmanager;

import javafx.application.Application;

/**
 * Launches the JavaFX application from the packaged JAR.
 */
public final class Launcher {
    private Launcher() {
    }

    /**
     * Launches Possession Manager.
     *
     * @param args command-line arguments supplied at startup.
     */
    public static void main(String[] args) {
        Application.launch(App.class, args);
    }
}
