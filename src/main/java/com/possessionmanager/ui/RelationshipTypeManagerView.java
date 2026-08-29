package com.possessionmanager.ui;

import com.possessionmanager.model.AppData;
import com.possessionmanager.model.RelationshipType;
import com.possessionmanager.model.RelationshipTypeInput;
import com.possessionmanager.service.LifecycleEventService;
import com.possessionmanager.service.PossessionService;
import com.possessionmanager.service.RelationshipTypeService;
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
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

/**
 * Displays and manages reusable labels for possession links.
 */
public final class RelationshipTypeManagerView {
    private final PossessionService possessionService;
    private final LifecycleEventService lifecycleEventService;
    private final RelationshipTypeService relationshipTypeService;
    private final JsonStorage storage;
    private final Runnable showDashboard;
    private final TableView<RelationshipType> typeTable = new TableView<>();
    private final ObservableList<RelationshipType> displayedTypes = FXCollections.observableArrayList();

    /**
     * Creates a relationship-label manager with navigation back to the dashboard.
     *
     * @param possessionService service that owns possession records.
     * @param lifecycleEventService service that owns lifecycle events.
     * @param relationshipTypeService service that owns reusable relationship labels.
     * @param storage local JSON storage used after each successful change.
     * @param showDashboard action that returns to the dashboard.
     */
    public RelationshipTypeManagerView(PossessionService possessionService,
            LifecycleEventService lifecycleEventService, RelationshipTypeService relationshipTypeService,
            JsonStorage storage, Runnable showDashboard) {
        this.possessionService = possessionService;
        this.lifecycleEventService = lifecycleEventService;
        this.relationshipTypeService = relationshipTypeService;
        this.storage = storage;
        this.showDashboard = showDashboard;
        configureTable();
        refreshTypes();
    }

    /**
     * Creates the relationship-label manager root node.
     *
     * @return manager root node.
     */
    public Parent createRoot() {
        BorderPane root = new BorderPane();
        root.setTop(createHeader());
        root.setCenter(createContent());
        return root;
    }

    private VBox createHeader() {
        Button backButton = new Button("Back to Dashboard");
        backButton.setOnAction(event -> showDashboard.run());
        Label title = new Label("Relationship Labels");
        title.getStyleClass().add("page-title");
        Label subtitle = new Label("Create reusable labels for links between possessions.");
        subtitle.getStyleClass().add("page-subtitle");
        VBox header = new VBox(6, backButton, title, subtitle);
        header.setPadding(new Insets(24, 24, 16, 24));
        return header;
    }

    private VBox createContent() {
        Button addButton = new Button("+ Add Relationship Label");
        addButton.getStyleClass().add("primary-button");
        addButton.setOnAction(event -> addType());
        Button editButton = new Button("Edit Selected");
        editButton.disableProperty().bind(
                Bindings.isNull(typeTable.getSelectionModel().selectedItemProperty()));
        editButton.setOnAction(event -> editSelectedType());
        Button deleteButton = new Button("Delete Selected");
        deleteButton.disableProperty().bind(
                Bindings.isNull(typeTable.getSelectionModel().selectedItemProperty()));
        deleteButton.setOnAction(event -> deleteSelectedType());
        HBox actions = new HBox(10, addButton, editButton, deleteButton);
        VBox content = new VBox(12, actions, typeTable);
        content.setPadding(new Insets(8, 24, 24, 24));
        VBox.setVgrow(typeTable, Priority.ALWAYS);
        return content;
    }

    private void configureTable() {
        typeTable.setItems(displayedTypes);
        typeTable.getColumns().setAll(List.of(
                textColumn("Relationship Label", RelationshipType::name),
                textColumn("Example", this::formatExample)));
        typeTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);
        typeTable.setPlaceholder(new Label("No relationship labels yet. Add a built-in or custom label."));
    }

    private TableColumn<RelationshipType, String> textColumn(String title,
            Function<RelationshipType, String> value) {
        TableColumn<RelationshipType, String> column = new TableColumn<>(title);
        column.setCellValueFactory(cell -> new SimpleStringProperty(value.apply(cell.getValue())));
        return column;
    }

    private String formatExample(RelationshipType type) {
        return "Item A is " + type.forwardLabel() + " Item B";
    }

    private void addType() {
        RelationshipTypeDialog.show(null).ifPresent(input -> applyChange(() -> relationshipTypeService.addType(input)));
    }

    private void editSelectedType() {
        RelationshipType selected = typeTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            RelationshipTypeDialog.show(selected).ifPresent(input -> updateType(selected, input));
        }
    }

    private void updateType(RelationshipType selected, RelationshipTypeInput input) {
        applyChange(() -> relationshipTypeService.updateType(selected.id(), input));
    }

    private void deleteSelectedType() {
        RelationshipType selected = typeTable.getSelectionModel().getSelectedItem();
        if (selected == null || !confirmDeletion(selected)) {
            return;
        }
        applyChange(() -> relationshipTypeService.deleteType(selected.id()));
    }

    private boolean confirmDeletion(RelationshipType type) {
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Delete Relationship Label");
        confirmation.setHeaderText("Delete " + type.name() + "?");
        confirmation.setContentText("This reusable label will be removed.");
        return confirmation.showAndWait().filter(ButtonType.OK::equals).isPresent();
    }

    private void applyChange(Runnable change) {
        try {
            change.run();
            storage.save(new AppData(possessionService.toAppData().possessions(), lifecycleEventService.listAll(),
                    relationshipTypeService.listTypes()));
            refreshTypes();
        } catch (ValidationException | StorageException exception) {
            showError(exception.getMessage());
        }
    }

    private void refreshTypes() {
        displayedTypes.setAll(relationshipTypeService.listTypes());
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Possession Manager");
        alert.setHeaderText("Relationship label could not be saved");
        alert.setContentText(message);
        alert.showAndWait();
    }
}
