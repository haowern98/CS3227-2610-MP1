package com.possessionmanager.ui;

import com.possessionmanager.model.Possession;
import com.possessionmanager.model.PossessionCategory;
import com.possessionmanager.model.PossessionInput;
import com.possessionmanager.model.PossessionStatus;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.Set;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;

/**
 * Collects the editable fields for adding or changing a possession.
 */
public final class PossessionDialog {
    private PossessionDialog() {
    }

    /**
     * Shows an add or edit dialog for a possession.
     *
     * @param possession possession to edit, or {@code null} when adding one.
     * @return entered possession details when the user saves the dialog.
     */
    public static Optional<PossessionInput> show(Possession possession) {
        Dialog<PossessionInput> dialog = new Dialog<>();
        boolean isEditing = possession != null;
        dialog.setTitle(isEditing ? "Edit Possession" : "Add Possession");
        dialog.setHeaderText(isEditing ? "Update the possession details." : "Record a physical possession.");
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.CANCEL, ButtonType.OK);

        Fields fields = new Fields(possession);
        dialog.getDialogPane().setContent(fields.createForm());
        preventBlankName(dialog, fields.nameField);
        dialog.setResultConverter(button -> button == ButtonType.OK ? fields.toInput() : null);
        return dialog.showAndWait();
    }

    private static void preventBlankName(Dialog<?> dialog, TextField nameField) {
        Button saveButton = (Button) dialog.getDialogPane().lookupButton(ButtonType.OK);
        saveButton.addEventFilter(ActionEvent.ACTION, event -> {
            if (nameField.getText().trim().isEmpty()) {
                event.consume();
                nameField.requestFocus();
            }
        });
    }

    private static final class Fields {
        private final TextField nameField = new TextField();
        private final ComboBox<PossessionCategory> categoryBox = new ComboBox<>();
        private final TextField locationField = new TextField();
        private final ComboBox<PossessionStatus> statusBox = new ComboBox<>();
        private final TextField tagsField = new TextField();
        private final TextArea notesArea = new TextArea();

        private Fields(Possession possession) {
            categoryBox.getItems().setAll(PossessionCategory.values());
            statusBox.getItems().setAll(PossessionStatus.values());
            notesArea.setPrefRowCount(3);
            populate(possession);
        }

        private GridPane createForm() {
            GridPane form = new GridPane();
            form.setHgap(10);
            form.setVgap(10);
            form.setPadding(new Insets(8));
            form.addRow(0, new Label("Name *"), nameField);
            form.addRow(1, new Label("Category"), categoryBox);
            form.addRow(2, new Label("Location"), locationField);
            form.addRow(3, new Label("Status"), statusBox);
            form.addRow(4, new Label("Tags"), tagsField);
            form.addRow(5, new Label("Notes"), notesArea);
            return form;
        }

        private void populate(Possession possession) {
            if (possession == null) {
                categoryBox.setValue(PossessionCategory.OTHER);
                statusBox.setValue(PossessionStatus.IN_USE);
                return;
            }
            nameField.setText(possession.name());
            categoryBox.setValue(possession.category());
            locationField.setText(possession.location());
            statusBox.setValue(possession.status());
            tagsField.setText(String.join(", ", possession.tags()));
            notesArea.setText(possession.notes());
        }

        private PossessionInput toInput() {
            return new PossessionInput(nameField.getText(), categoryBox.getValue(), locationField.getText(),
                    statusBox.getValue(), parseTags(tagsField.getText()), notesArea.getText());
        }

        private Set<String> parseTags(String text) {
            return Arrays.stream(text.split(","))
                    .map(String::trim)
                    .filter(tag -> !tag.isEmpty())
                    .collect(java.util.stream.Collectors.toCollection(LinkedHashSet::new));
        }
    }
}
