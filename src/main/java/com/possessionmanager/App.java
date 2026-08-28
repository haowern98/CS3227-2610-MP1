package com.possessionmanager;

import com.possessionmanager.model.AppData;
import com.possessionmanager.service.LifecycleEventService;
import com.possessionmanager.service.PossessionService;
import com.possessionmanager.storage.AppDataFile;
import com.possessionmanager.storage.JsonStorage;
import com.possessionmanager.storage.StorageException;
import com.possessionmanager.ui.DashboardView;
import com.possessionmanager.ui.PossessionDetailView;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

/**
 * Starts the Possession Manager desktop application.
 */
public final class App extends Application {
    private static final String APPLICATION_TITLE = "Possession Manager";
    private static final double INITIAL_WINDOW_WIDTH = 960;
    private static final double INITIAL_WINDOW_HEIGHT = 640;

    private JsonStorage storage;
    private PossessionService possessionService;
    private LifecycleEventService lifecycleEventService;
    private Scene scene;

    @Override
    public void start(Stage stage) {
        storage = new JsonStorage(AppDataFile.getDataFilePath());
        AppData data = loadData(storage);
        possessionService = new PossessionService(data);
        lifecycleEventService = new LifecycleEventService(possessionService, data.lifecycleEvents());
        scene = new Scene(new javafx.scene.layout.Pane(), INITIAL_WINDOW_WIDTH, INITIAL_WINDOW_HEIGHT);
        scene.getStylesheets().add(App.class.getResource("app.css").toExternalForm());
        stage.setTitle(APPLICATION_TITLE);
        stage.setScene(scene);
        showDashboard();
        stage.show();
    }

    private void showDashboard() {
        DashboardView dashboard = new DashboardView(possessionService, lifecycleEventService, storage,
                this::showPossessionDetail);
        scene.setRoot(dashboard.createRoot());
    }

    private void showPossessionDetail(java.util.UUID possessionId) {
        PossessionDetailView detail = new PossessionDetailView(possessionService, lifecycleEventService, storage,
                this::showDashboard);
        scene.setRoot(detail.createRoot(possessionId));
    }

    private AppData loadData(JsonStorage storage) {
        try {
            return storage.load();
        } catch (StorageException exception) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle(APPLICATION_TITLE);
            alert.setHeaderText("Saved data could not be loaded");
            alert.setContentText(exception.getMessage());
            alert.showAndWait();
            return AppData.empty();
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
