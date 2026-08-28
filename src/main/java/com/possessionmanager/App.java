package com.possessionmanager;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * Starts the Possession Manager desktop application.
 */
public final class App extends Application {
    private static final String APPLICATION_TITLE = "Possession Manager";
    private static final double INITIAL_WINDOW_WIDTH = 960;
    private static final double INITIAL_WINDOW_HEIGHT = 640;

    @Override
    public void start(Stage stage) {
        Label heading = new Label("Possession Manager");
        heading.getStyleClass().add("placeholder-title");

        Label description = new Label("Your possession ecosystem will appear here.");
        description.getStyleClass().add("placeholder-copy");

        VBox root = new VBox(8, heading, description);
        root.setPadding(new Insets(24));

        Scene scene = new Scene(root, INITIAL_WINDOW_WIDTH, INITIAL_WINDOW_HEIGHT);
        scene.getStylesheets().add(App.class.getResource("app.css").toExternalForm());
        stage.setTitle(APPLICATION_TITLE);
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Launches the JavaFX application.
     *
     * @param args command-line arguments supplied at startup.
     */
    public static void main(String[] args) {
        launch(args);
    }
}
