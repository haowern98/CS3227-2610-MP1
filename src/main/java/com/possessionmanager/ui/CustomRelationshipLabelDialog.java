package com.possessionmanager.ui;

import com.possessionmanager.model.RelationshipKind;
import com.possessionmanager.model.RelationshipType;
import com.possessionmanager.model.RelationshipTypeInput;
import java.util.Optional;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.OverrunStyle;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;

/**
 * Collects custom two-sided wording for a relationship label.
 */
final class CustomRelationshipLabelDialog {
    private static final double DIALOG_WIDTH = 860;
    private static final double PREVIEW_WIDTH = 480;

    private CustomRelationshipLabelDialog() {
    }

    /**
     * Shows a fixed-size custom-label dialog.
     *
     * @param type relationship label to edit, or {@code null} when adding one.
     * @return entered label details when the user saves the dialog.
     */
    static Optional<RelationshipTypeInput> show(RelationshipType type) {
        Dialog<RelationshipTypeInput> dialog = new Dialog<>();
        dialog.setTitle(type == null ? "Add Custom Relationship Label" : "Edit Relationship Label");
        dialog.setHeaderText("Describe how each item reads the relationship.");
        dialog.setResizable(false);
        dialog.getDialogPane().setPrefWidth(DIALOG_WIDTH);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.CANCEL, ButtonType.OK);

        Fields fields = new Fields(type);
        dialog.getDialogPane().setContent(fields.createForm());
        preventBlankFields(dialog, fields);
        dialog.setResultConverter(button -> button == ButtonType.OK ? fields.toInput() : null);
        return dialog.showAndWait();
    }

    private static void preventBlankFields(Dialog<?> dialog, Fields fields) {
        Button saveButton = (Button) dialog.getDialogPane().lookupButton(ButtonType.OK);
        saveButton.addEventFilter(ActionEvent.ACTION, event -> {
            if (fields.nameField.getText().trim().isEmpty()) {
                event.consume();
                fields.nameField.requestFocus();
            } else if (fields.firstPhraseField.getText().trim().isEmpty()) {
                event.consume();
                fields.firstPhraseField.requestFocus();
            } else if (!fields.samePhraseBox.isSelected() && fields.secondPhraseField.getText().trim().isEmpty()) {
                event.consume();
                fields.secondPhraseField.requestFocus();
            }
        });
    }

    private static final class Fields {
        private final TextField nameField = new TextField();
        private final TextField firstPhraseField = new TextField();
        private final CheckBox samePhraseBox = new CheckBox("Use the same wording when viewing either item");
        private final TextField secondPhraseField = new TextField();
        private final Label previewLabel = new Label();

        private Fields(RelationshipType type) {
            samePhraseBox.selectedProperty().addListener((observable, wasSelected, isSelected) -> {
                secondPhraseField.setDisable(isSelected);
                refreshPreview();
            });
            firstPhraseField.textProperty().addListener((observable, oldText, newText) -> refreshPreview());
            secondPhraseField.textProperty().addListener((observable, oldText, newText) -> refreshPreview());
            populate(type);
        }

        private GridPane createForm() {
            previewLabel.setMinWidth(0);
            previewLabel.setPrefWidth(PREVIEW_WIDTH);
            previewLabel.setMaxWidth(PREVIEW_WIDTH);
            previewLabel.setTextOverrun(OverrunStyle.ELLIPSIS);
            GridPane form = new GridPane();
            form.setHgap(10);
            form.setVgap(10);
            form.setPadding(new Insets(8));
            form.addRow(0, new Label("Custom label name *"), nameField);
            form.addRow(1, new Label("Phrase after \"Item A is\" *"), firstPhraseField);
            form.add(samePhraseBox, 1, 2);
            form.addRow(3, new Label("How Item B relates to Item A *"), secondPhraseField);
            form.add(previewLabel, 1, 4);
            return form;
        }

        private void populate(RelationshipType type) {
            if (type == null) {
                return;
            }
            nameField.setText(type.name());
            firstPhraseField.setText(type.forwardLabel());
            samePhraseBox.setSelected(type.forwardLabel().equals(type.inverseLabel()));
            secondPhraseField.setText(type.inverseLabel());
        }

        private void refreshPreview() {
            String firstPhrase = firstPhraseField.getText();
            String secondPhrase = samePhraseBox.isSelected() ? firstPhrase : secondPhraseField.getText();
            previewLabel.setText("Example: " + RelationshipTypeDialog.formatExample(firstPhrase, secondPhrase));
        }

        private RelationshipTypeInput toInput() {
            String inverseLabel = samePhraseBox.isSelected() ? firstPhraseField.getText()
                    : secondPhraseField.getText();
            RelationshipKind kind = samePhraseBox.isSelected() ? RelationshipKind.SYMMETRIC
                    : RelationshipKind.DIRECTED;
            return new RelationshipTypeInput(nameField.getText(), firstPhraseField.getText(), inverseLabel, kind);
        }
    }
}
