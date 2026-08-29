package com.possessionmanager.ui;

import com.possessionmanager.model.AppData;
import com.possessionmanager.model.Possession;
import com.possessionmanager.model.PossessionCategory;
import com.possessionmanager.model.PossessionInput;
import com.possessionmanager.model.PossessionStatus;
import com.possessionmanager.service.LifecycleEventService;
import com.possessionmanager.service.PossessionService;
import com.possessionmanager.service.RelationshipTypeService;
import com.possessionmanager.service.ValidationException;
import com.possessionmanager.storage.JsonStorage;
import com.possessionmanager.storage.StorageException;
import java.util.List;
import java.util.function.Consumer;
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
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

/**
 * Displays the possession dashboard and its CRUD actions.
 */
public final class DashboardView {
    private final PossessionService possessionService;
    private final LifecycleEventService lifecycleEventService;
    private final RelationshipTypeService relationshipTypeService;
    private final JsonStorage storage;
    private final Consumer<java.util.UUID> showPossessionDetail;
    private final Runnable showRelationshipTypeManager;
    private final TableView<Possession> possessionTable = new TableView<>();
    private final ObservableList<Possession> displayedPossessions = FXCollections.observableArrayList();
    private final TextField searchField = new TextField();
    private final ComboBox<PossessionCategory> categoryFilter = new ComboBox<>();
    private final ComboBox<PossessionStatus> statusFilter = new ComboBox<>();
    private final Label countLabel = new Label();

    /**
     * Creates a dashboard backed by the supplied service and local storage.
     *
     * @param possessionService service that owns possession data.
     * @param lifecycleEventService service that owns possession lifecycle events.
     * @param relationshipTypeService service that owns relationship types.
     * @param storage local JSON storage used after each successful change.
     * @param showPossessionDetail action that opens one possession's detail screen.
     * @param showRelationshipTypeManager action that opens the relationship-type manager.
     */
    public DashboardView(PossessionService possessionService, LifecycleEventService lifecycleEventService,
            RelationshipTypeService relationshipTypeService, JsonStorage storage,
            Consumer<java.util.UUID> showPossessionDetail, Runnable showRelationshipTypeManager) {
        this.possessionService = possessionService;
        this.lifecycleEventService = lifecycleEventService;
        this.relationshipTypeService = relationshipTypeService;
        this.storage = storage;
        this.showPossessionDetail = showPossessionDetail;
        this.showRelationshipTypeManager = showRelationshipTypeManager;
        configureFilters();
        configureTable();
        refreshTable();
    }

    /**
     * Creates the dashboard's root node.
     *
     * @return dashboard root node.
     */
    public Parent createRoot() {
        BorderPane root = new BorderPane();
        root.setTop(createHeader());
        root.setCenter(createContent());
        return root;
    }

    private VBox createHeader() {
        Label title = new Label("Possession Manager");
        title.getStyleClass().add("page-title");
        Label subtitle = new Label("Track physical possessions, their location, and current status.");
        subtitle.getStyleClass().add("page-subtitle");
        VBox header = new VBox(4, title, subtitle, createActions(), createTypeManagementAction());
        header.setPadding(new Insets(24, 24, 16, 24));
        return header;
    }

    private HBox createActions() {
        searchField.setPromptText("Search names or tags");
        searchField.setPrefWidth(240);
        categoryFilter.setPromptText("All categories");
        statusFilter.setPromptText("All statuses");
        Button clearFiltersButton = new Button("Clear Filters");
        clearFiltersButton.setOnAction(event -> clearFilters());
        Button addButton = new Button("+ Add Possession");
        addButton.getStyleClass().add("primary-button");
        addButton.setOnAction(event -> addPossession());
        HBox actions = new HBox(10, searchField, categoryFilter, statusFilter, clearFiltersButton, addButton);
        actions.setPadding(new Insets(16, 0, 0, 0));
        return actions;
    }

    private HBox createTypeManagementAction() {
        Button manageTypesButton = new Button("Manage Saved Relationships");
        manageTypesButton.setOnAction(event -> showRelationshipTypeManager.run());
        HBox managementAction = new HBox(manageTypesButton);
        managementAction.setPadding(new Insets(8, 0, 0, 0));
        return managementAction;
    }

    private VBox createContent() {
        Button detailsButton = new Button("View Details");
        detailsButton.disableProperty().bind(
                Bindings.isNull(possessionTable.getSelectionModel().selectedItemProperty()));
        detailsButton.setOnAction(event -> openSelectedPossession());
        Button editButton = new Button("Edit Selected");
        editButton.disableProperty().bind(
                Bindings.isNull(possessionTable.getSelectionModel().selectedItemProperty()));
        editButton.setOnAction(event -> editSelectedPossession());
        Button archiveButton = new Button("Archive Selected");
        archiveButton.disableProperty().bind(
                Bindings.isNull(possessionTable.getSelectionModel().selectedItemProperty()));
        archiveButton.setOnAction(event -> archiveSelectedPossession());
        HBox tableActions = new HBox(10, countLabel, detailsButton, editButton, archiveButton);
        HBox.setHgrow(countLabel, Priority.ALWAYS);
        VBox content = new VBox(12, tableActions, possessionTable);
        content.setPadding(new Insets(8, 24, 24, 24));
        VBox.setVgrow(possessionTable, Priority.ALWAYS);
        return content;
    }

    private void configureFilters() {
        categoryFilter.getItems().setAll(PossessionCategory.values());
        statusFilter.getItems().setAll(PossessionStatus.values());
        categoryFilter.getSelectionModel().clearSelection();
        statusFilter.getSelectionModel().clearSelection();
        searchField.textProperty().addListener((observable, oldText, newText) -> refreshTable());
        categoryFilter.valueProperty().addListener((observable, oldValue, newValue) -> refreshTable());
        statusFilter.valueProperty().addListener((observable, oldValue, newValue) -> refreshTable());
    }

    private void configureTable() {
        possessionTable.setItems(displayedPossessions);
        possessionTable.getColumns().setAll(List.of(
                textColumn("Name", Possession::name),
                textColumn("Category", possession -> format(possession.category())),
                textColumn("Location", Possession::location),
                textColumn("Status", possession -> format(possession.status())),
                textColumn("Tags", possession -> String.join(", ", possession.tags()))));
        possessionTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);
        possessionTable.setPlaceholder(new Label("No active possessions yet. Add your first item."));
        possessionTable.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2) {
                openSelectedPossession();
            }
        });
    }

    private TableColumn<Possession, String> textColumn(String title, Function<Possession, String> value) {
        TableColumn<Possession, String> column = new TableColumn<>(title);
        column.setCellValueFactory(cell -> new SimpleStringProperty(value.apply(cell.getValue())));
        return column;
    }

    private void addPossession() {
        PossessionDialog.show(null).ifPresent(input -> applyChange(() -> possessionService.addPossession(input)));
    }

    private void editSelectedPossession() {
        Possession selected = possessionTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            PossessionDialog.show(selected).ifPresent(input -> updatePossession(selected, input));
        }
    }

    private void openSelectedPossession() {
        Possession selected = possessionTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            showPossessionDetail.accept(selected.id());
        }
    }

    private void updatePossession(Possession selected, PossessionInput input) {
        applyChange(() -> possessionService.updatePossession(selected.id(), input));
    }

    private void archiveSelectedPossession() {
        Possession selected = possessionTable.getSelectionModel().getSelectedItem();
        if (selected == null || !confirmArchive(selected)) {
            return;
        }
        applyChange(() -> possessionService.archivePossession(selected.id()));
    }

    private boolean confirmArchive(Possession possession) {
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Archive Possession");
        confirmation.setHeaderText("Archive " + possession.name() + "?");
        confirmation.setContentText("The record remains saved but no longer appears in active results.");
        return confirmation.showAndWait().filter(ButtonType.OK::equals).isPresent();
    }

    private void applyChange(Runnable change) {
        try {
            change.run();
            storage.save(new AppData(possessionService.toAppData().possessions(), lifecycleEventService.listAll(),
                    relationshipTypeService.listTypes()));
            refreshTable();
        } catch (ValidationException | StorageException exception) {
            showError(exception.getMessage());
        }
    }

    private void refreshTable() {
        displayedPossessions.setAll(possessionService.query(searchField.getText(), categoryFilter.getValue(),
                statusFilter.getValue()));
        countLabel.setText(displayedPossessions.size() + " active possession(s)");
    }

    private void clearFilters() {
        searchField.clear();
        categoryFilter.getSelectionModel().clearSelection();
        statusFilter.getSelectionModel().clearSelection();
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
