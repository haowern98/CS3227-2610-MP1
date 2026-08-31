package com.possessionmanager.ui;

import com.possessionmanager.model.AppData;
import com.possessionmanager.model.LifecycleEvent;
import com.possessionmanager.model.LifecycleEventInput;
import com.possessionmanager.model.Possession;
import com.possessionmanager.service.LifecycleEventService;
import com.possessionmanager.service.PossessionService;
import com.possessionmanager.service.ValidationException;
import com.possessionmanager.storage.JsonStorage;
import com.possessionmanager.storage.StorageException;
import java.util.List;
import java.util.function.Function;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

/**
 * Displays one possession and its lifecycle history.
 */
public final class PossessionDetailView {
    private final PossessionService possessionService;
    private final LifecycleEventService lifecycleEventService;
    private final JsonStorage storage;
    private final Runnable showDashboard;
    private final TableView<LifecycleEvent> eventTable = new TableView<>();
    private final ObservableList<LifecycleEvent> displayedEvents = FXCollections.observableArrayList();

    private Possession possession;

    /**
     * Creates a detail view with navigation back to the dashboard.
     *
     * @param possessionService service that owns possession records.
     * @param lifecycleEventService service that owns lifecycle events.
     * @param storage local JSON storage used after each successful change.
     * @param showDashboard action that returns to the dashboard.
     */
    public PossessionDetailView(PossessionService possessionService, LifecycleEventService lifecycleEventService,
            JsonStorage storage, Runnable showDashboard) {
        this.possessionService = possessionService;
        this.lifecycleEventService = lifecycleEventService;
        this.storage = storage;
        this.showDashboard = showDashboard;
    }

    /**
     * Creates the detail view for one existing possession.
     *
     * @param possessionId identifier of the possession to display.
     * @return detail view root node.
     */
    public Parent createRoot(java.util.UUID possessionId) {
        possession = possessionService.findById(possessionId).orElseThrow();
        configureEventTable();
        refreshEvents();

        BorderPane root = new BorderPane();
        root.setTop(createHeader());
        root.setCenter(createTabs());
        return root;
    }

    private VBox createHeader() {
        Button backButton = new Button("Back to Dashboard");
        backButton.setOnAction(event -> showDashboard.run());
        Label title = new Label(possession.name());
        title.getStyleClass().add("page-title");
        Label subtitle = new Label(format(possession.category()) + " · " + format(possession.status()));
        subtitle.getStyleClass().add("page-subtitle");
        VBox header = new VBox(6, backButton, title, subtitle);
        header.setPadding(new Insets(24, 24, 16, 24));
        return header;
    }

    private TabPane createTabs() {
        Tab overviewTab = new Tab("Overview", createOverview());
        Tab lifecycleTab = new Tab("Lifecycle History", createLifecycleHistory());
        overviewTab.setClosable(false);
        lifecycleTab.setClosable(false);
        TabPane tabs = new TabPane(overviewTab, lifecycleTab);
        tabs.getStyleClass().add("detail-tabs");
        return tabs;
    }

    private VBox createOverview() {
        VBox overview = new VBox(10,
                detailLabel("Location", emptyFallback(possession.location())),
                detailLabel("Status", format(possession.status())),
                detailLabel("Tags", emptyFallback(String.join(", ", possession.tags()))),
                detailLabel("Notes", emptyFallback(possession.notes())));
        overview.setPadding(new Insets(20));
        return overview;
    }

    private Label detailLabel(String name, String value) {
        return new Label(name + ": " + value);
    }

    private VBox createLifecycleHistory() {
        Button addButton = new Button("+ Add Event");
        addButton.getStyleClass().add("primary-button");
        addButton.setOnAction(event -> addEvent());
        Button editButton = new Button("Edit Selected");
        editButton.disableProperty().bind(Bindings.isNull(eventTable.getSelectionModel().selectedItemProperty()));
        editButton.setOnAction(event -> editSelectedEvent());
        Button deleteButton = new Button("Delete Selected");
        deleteButton.disableProperty().bind(Bindings.isNull(eventTable.getSelectionModel().selectedItemProperty()));
        deleteButton.setOnAction(event -> deleteSelectedEvent());
        HBox actions = new HBox(10, addButton, editButton, deleteButton);
        VBox content = new VBox(12, actions, eventTable);
        content.setPadding(new Insets(20));
        VBox.setVgrow(eventTable, Priority.ALWAYS);
        return content;
    }

    private void configureEventTable() {
        eventTable.setItems(displayedEvents);
        eventTable.getColumns().setAll(List.of(
                textColumn("Date", event -> event.date().toString()),
                textColumn("Event Type", event -> format(event.type())),
                textColumn("Description", LifecycleEvent::description),
                textColumn("Notes", LifecycleEvent::notes)));
        eventTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);
        eventTable.setPlaceholder(new Label("No lifecycle events recorded yet."));
    }

    private TableColumn<LifecycleEvent, String> textColumn(String title, Function<LifecycleEvent, String> value) {
        TableColumn<LifecycleEvent, String> column = new TableColumn<>(title);
        column.setCellValueFactory(cell -> new SimpleStringProperty(value.apply(cell.getValue())));
        return column;
    }

    private void addEvent() {
        LifecycleEventDialog.show(null).ifPresent(input -> applyChange(() -> lifecycleEventService.addEvent(
                possession.id(), input)));
    }

    private void editSelectedEvent() {
        LifecycleEvent selected = eventTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            LifecycleEventDialog.show(selected).ifPresent(input -> updateEvent(selected, input));
        }
    }

    private void updateEvent(LifecycleEvent selected, LifecycleEventInput input) {
        applyChange(() -> lifecycleEventService.updateEvent(selected.id(), input));
    }

    private void deleteSelectedEvent() {
        LifecycleEvent selected = eventTable.getSelectionModel().getSelectedItem();
        if (selected == null || !confirmDeletion(selected)) {
            return;
        }
        applyChange(() -> lifecycleEventService.deleteEvent(selected.id()));
    }

    private boolean confirmDeletion(LifecycleEvent event) {
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Delete Lifecycle Event");
        confirmation.setHeaderText("Delete " + event.description() + "?");
        confirmation.setContentText("This event will be removed from the possession history.");
        return confirmation.showAndWait().filter(ButtonType.OK::equals).isPresent();
    }

    private void applyChange(Runnable change) {
        try {
            change.run();
            storage.save(new AppData(possessionService.toAppData().possessions(), lifecycleEventService.listAll()));
            refreshEvents();
        } catch (ValidationException | StorageException exception) {
            showError(exception.getMessage());
        }
    }

    private void refreshEvents() {
        displayedEvents.setAll(lifecycleEventService.listForPossession(possession.id()));
    }

    private String emptyFallback(String text) {
        return text.isBlank() ? "Not recorded" : text;
    }

    private String format(Enum<?> value) {
        String words = value.name().toLowerCase().replace('_', ' ');
        return Character.toUpperCase(words.charAt(0)) + words.substring(1);
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Possession Manager");
        alert.setHeaderText("The change could not be saved");
        alert.setContentText(message);
        alert.showAndWait();
    }
}
