package com.possessionmanager.ui;

import com.possessionmanager.model.LifecycleEvent;
import com.possessionmanager.model.LifecycleEventInput;
import com.possessionmanager.model.Possession;
import com.possessionmanager.service.LifecycleEventService;
import com.possessionmanager.service.PersistentChange;
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
import javafx.geometry.VPos;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

/**
 * Displays one possession and its lifecycle history.
 */
public final class PossessionDetailView {
    private static final double NOTES_HEIGHT = 84;

    private final PossessionService possessionService;
    private final LifecycleEventService lifecycleEventService;
    private final PersistentChange persistentChange;
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
        persistentChange = new PersistentChange(possessionService, lifecycleEventService, storage);
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
        root.setCenter(createContent());
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

    private VBox createContent() {
        Label lifecycleHeading = new Label("Lifecycle History");
        lifecycleHeading.getStyleClass().add("section-title");
        VBox lifecycleHistory = createLifecycleHistory();
        VBox content = new VBox(16, createPossessionDetails(), lifecycleHeading, lifecycleHistory);
        content.setPadding(new Insets(0, 24, 24, 24));
        VBox.setVgrow(lifecycleHistory, Priority.ALWAYS);
        return content;
    }

    private GridPane createPossessionDetails() {
        GridPane details = new GridPane();
        details.setHgap(16);
        details.setVgap(10);
        details.getColumnConstraints().addAll(createLabelColumn(), createValueColumn());
        details.addRow(0, new Label("Location"), new Label(emptyFallback(possession.location())));
        details.addRow(1, new Label("Tags"),
                new Label(emptyFallback(String.join(", ", possession.tags()))));

        Label notesLabel = new Label("Notes");
        GridPane.setValignment(notesLabel, VPos.TOP);
        TextArea notesArea = createNotesArea();
        details.addRow(2, notesLabel, notesArea);
        return details;
    }

    private ColumnConstraints createLabelColumn() {
        ColumnConstraints labelColumn = new ColumnConstraints();
        labelColumn.setMinWidth(64);
        return labelColumn;
    }

    private ColumnConstraints createValueColumn() {
        ColumnConstraints valueColumn = new ColumnConstraints();
        valueColumn.setHgrow(Priority.ALWAYS);
        return valueColumn;
    }

    private TextArea createNotesArea() {
        TextArea notesArea = new TextArea(emptyFallback(possession.notes()));
        notesArea.setEditable(false);
        notesArea.setFocusTraversable(false);
        notesArea.setWrapText(true);
        notesArea.setMinHeight(NOTES_HEIGHT);
        notesArea.setPrefHeight(NOTES_HEIGHT);
        notesArea.setMaxHeight(NOTES_HEIGHT);
        GridPane.setHgrow(notesArea, Priority.ALWAYS);
        return notesArea;
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
            persistentChange.run(change);
        } catch (ValidationException exception) {
            showError(exception.getMessage());
        } catch (StorageException exception) {
            showError(exception.getMessage() + " No changes were kept.");
        } finally {
            refreshEvents();
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
