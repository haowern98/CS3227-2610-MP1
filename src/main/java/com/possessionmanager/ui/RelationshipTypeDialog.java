package com.possessionmanager.ui;

import com.possessionmanager.model.RelationshipKind;
import com.possessionmanager.model.RelationshipType;
import com.possessionmanager.model.RelationshipTypeInput;
import java.util.Optional;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;

/**
 * Collects the editable labels and direction setting for a relationship type.
 */
public final class RelationshipTypeDialog {
    private RelationshipTypeDialog() {
    }

    /**
     * Shows an add or edit dialog for a relationship type.
     *
     * @param type type to edit, or {@code null} when adding one.
     * @return entered type details when the user saves the dialog.
     */
    public static Optional<RelationshipTypeInput> show(RelationshipType type) {
        Dialog<RelationshipTypeInput> dialog = new Dialog<>();
        boolean isEditing = type != null;
        dialog.setTitle(isEditing ? "Edit Relationship Type" : "Add Relationship Type");
        dialog.setHeaderText(isEditing ? "Update the controlled relationship labels."
                : "Define controlled labels for future possession relationships.");
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.CANCEL, ButtonType.OK);

        Fields fields = new Fields(type);
        dialog.getDialogPane().setContent(fields.createForm());
        preventBlankFields(dialog, fields.nameField, fields.forwardLabelField);
        dialog.setResultConverter(button -> button == ButtonType.OK ? fields.toInput() : null);
        return dialog.showAndWait();
    }

    private static void preventBlankFields(Dialog<?> dialog, TextField nameField, TextField forwardLabelField) {
        Button saveButton = (Button) dialog.getDialogPane().lookupButton(ButtonType.OK);
        saveButton.addEventFilter(ActionEvent.ACTION, event -> {
            if (nameField.getText().trim().isEmpty()) {
                event.consume();
                nameField.requestFocus();
            } else if (forwardLabelField.getText().trim().isEmpty()) {
                event.consume();
                forwardLabelField.requestFocus();
            }
        });
    }

    private static final class Fields {
        private final TextField nameField = new TextField();
        private final ComboBox<RelationshipKind> kindBox = new ComboBox<>();
        private final TextField forwardLabelField = new TextField();
        private final TextField inverseLabelField = new TextField();
        private final Label inverseLabel = new Label("Inverse Label *");

        private Fields(RelationshipType type) {
            kindBox.getItems().setAll(RelationshipKind.values());
            kindBox.valueProperty().addListener(this::updateInverseField);
            populate(type);
        }

        private GridPane createForm() {
            GridPane form = new GridPane();
            form.setHgap(10);
            form.setVgap(10);
            form.setPadding(new Insets(8));
            form.addRow(0, new Label("Name *"), nameField);
            form.addRow(1, new Label("Kind"), kindBox);
            form.addRow(2, new Label("Forward Label *"), forwardLabelField);
            form.addRow(3, inverseLabel, inverseLabelField);
            return form;
        }

        private void populate(RelationshipType type) {
            if (type == null) {
                kindBox.setValue(RelationshipKind.DIRECTED);
                return;
            }
            nameField.setText(type.name());
            kindBox.setValue(type.kind());
            forwardLabelField.setText(type.forwardLabel());
            inverseLabelField.setText(type.inverseLabel());
            updateInverseField(null, null, type.kind());
        }

        private void updateInverseField(ObservableValue<? extends RelationshipKind> observable,
                RelationshipKind oldKind, RelationshipKind newKind) {
            boolean isSymmetric = newKind == RelationshipKind.SYMMETRIC;
            inverseLabelField.setDisable(isSymmetric);
            inverseLabel.setText(isSymmetric ? "Inverse Label (uses forward label)" : "Inverse Label *");
        }

        private RelationshipTypeInput toInput() {
            return new RelationshipTypeInput(nameField.getText(), forwardLabelField.getText(),
                    inverseLabelField.getText(), kindBox.getValue());
        }
    }
}
