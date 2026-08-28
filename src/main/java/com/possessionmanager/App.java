package com.possessionmanager;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import com.possessionmanager.service.PossessionService;
import com.possessionmanager.storage.AppDataFile;
import com.possessionmanager.storage.JsonStorage;
import com.possessionmanager.storage.StorageException;
import com.possessionmanager.ui.DashboardView;

/**
 * Starts the Possession Manager desktop application.
 */
public final class App extends Application {
    private static final String APPLICATION_TITLE = "Possession Manager";
    private static final double INITIAL_WINDOW_WIDTH = 960;
    private static final double INITIAL_WINDOW_HEIGHT = 640;

    @Override
    public void start(Stage stage) {
        JsonStorage storage = new JsonStorage(AppDataFile.getDataFilePath());
        DashboardView dashboard = new DashboardView(loadPossessions(storage), storage);
        Scene scene = new Scene(dashboard.createRoot(), INITIAL_WINDOW_WIDTH, INITIAL_WINDOW_HEIGHT);
        scene.getStylesheets().add(App.class.getResource("app.css").toExternalForm());
        stage.setTitle(APPLICATION_TITLE);
        stage.setScene(scene);
        stage.show();
    }

    private PossessionService loadPossessions(JsonStorage storage) {
        try {
            return new PossessionService(storage.load());
        } catch (StorageException exception) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle(APPLICATION_TITLE);
            alert.setHeaderText("Saved data could not be loaded");
            alert.setContentText(exception.getMessage());
            alert.showAndWait();
            return new PossessionService();
        }
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
