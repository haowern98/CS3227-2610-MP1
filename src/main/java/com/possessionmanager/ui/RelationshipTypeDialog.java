package com.possessionmanager.ui;

import com.possessionmanager.model.RelationshipCategory;
import com.possessionmanager.model.RelationshipKind;
import com.possessionmanager.model.RelationshipTemplate;
import com.possessionmanager.model.RelationshipType;
import com.possessionmanager.model.RelationshipTypeInput;
import java.util.Arrays;
import java.util.Optional;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.OverrunStyle;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;

/**
 * Collects a built-in or custom label for a possession relationship.
 */
public final class RelationshipTypeDialog {
    private static final double DIALOG_WIDTH = 820;
    private static final double DIALOG_HEIGHT = 550;
    private static final double PREVIEW_WIDTH = 480;

    private RelationshipTypeDialog() {
    }

    /**
     * Shows an add or edit dialog for a relationship label.
     *
     * @param type relationship label to edit, or {@code null} when adding one.
     * @return entered label details when the user saves the dialog.
     */
    public static Optional<RelationshipTypeInput> show(RelationshipType type) {
        Dialog<RelationshipTypeInput> dialog = new Dialog<>();
        boolean isEditing = type != null;
        dialog.setTitle(isEditing ? "Edit Relationship Label" : "Add Relationship Label");
        dialog.setHeaderText("Choose a built-in label or create a custom one.");
        dialog.setResizable(false);
        dialog.getDialogPane().setPrefSize(DIALOG_WIDTH, DIALOG_HEIGHT);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.CANCEL, ButtonType.OK);

        Fields fields = new Fields(type);
        dialog.getDialogPane().setContent(fields.createForm());
        preventBlankCustomFields(dialog, fields);
        dialog.setResultConverter(button -> button == ButtonType.OK ? fields.toInput() : null);
        return dialog.showAndWait();
    }

    private static void preventBlankCustomFields(Dialog<?> dialog, Fields fields) {
        Button saveButton = (Button) dialog.getDialogPane().lookupButton(ButtonType.OK);
        saveButton.addEventFilter(ActionEvent.ACTION, event -> {
            if (fields.categoryBox.getValue() != RelationshipCategory.CUSTOM) {
                return;
            }
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
        private final ComboBox<RelationshipCategory> categoryBox = new ComboBox<>();
        private final TextField nameField = new TextField();
        private final TextField firstPhraseField = new TextField();
        private final CheckBox samePhraseBox = new CheckBox("Use the same wording when viewing either item");
        private final TextField secondPhraseField = new TextField();
        private final Label nameLabel = new Label("Custom label name *");
        private final Label firstPhraseLabel = new Label("Phrase after \"Item A is\" *");
        private final Label secondPhraseLabel = new Label("How Item B relates to Item A *");
        private final Label previewLabel = new Label();

        private Fields(RelationshipType type) {
            categoryBox.getItems().setAll(RelationshipCategory.values());
            categoryBox.valueProperty().addListener((observable, oldCategory, newCategory) -> {
                updateCategoryControls(newCategory);
            });
            samePhraseBox.selectedProperty().addListener((observable, wasSelected, isSelected) -> {
                updateSecondPhraseControl();
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
            form.addRow(0, new Label("Relationship label"), categoryBox);
            form.addRow(1, nameLabel, nameField);
            form.addRow(2, firstPhraseLabel, firstPhraseField);
            form.add(samePhraseBox, 1, 3);
            form.addRow(4, secondPhraseLabel, secondPhraseField);
            form.add(previewLabel, 1, 5);
            return form;
        }

        private void populate(RelationshipType type) {
            if (type == null) {
                categoryBox.setValue(RelationshipCategory.STORAGE);
                return;
            }
            RelationshipTemplate template = findTemplate(type);
            if (template != null) {
                categoryBox.setValue(template.category());
                return;
            }
            categoryBox.setValue(RelationshipCategory.CUSTOM);
            nameField.setText(type.name());
            firstPhraseField.setText(type.forwardLabel());
            samePhraseBox.setSelected(type.forwardLabel().equals(type.inverseLabel()));
            secondPhraseField.setText(type.inverseLabel());
        }

        private RelationshipTemplate findTemplate(RelationshipType type) {
            return Arrays.stream(RelationshipTemplate.values())
                    .filter(template -> template.forwardLabel().equals(type.forwardLabel()))
                    .filter(template -> template.inverseLabel().equals(type.inverseLabel()))
                    .findFirst()
                    .orElse(null);
        }

        private void updateCategoryControls(RelationshipCategory category) {
            boolean isCustom = category == RelationshipCategory.CUSTOM;
            setVisibleAndManaged(nameLabel, isCustom);
            setVisibleAndManaged(nameField, isCustom);
            setVisibleAndManaged(firstPhraseLabel, isCustom);
            setVisibleAndManaged(firstPhraseField, isCustom);
            setVisibleAndManaged(samePhraseBox, isCustom);
            setVisibleAndManaged(secondPhraseLabel, isCustom);
            setVisibleAndManaged(secondPhraseField, isCustom);
            if (isCustom) {
                updateSecondPhraseControl();
            }
            refreshPreview();
        }

        private void updateSecondPhraseControl() {
            boolean usesSamePhrase = samePhraseBox.isSelected();
            secondPhraseField.setDisable(usesSamePhrase);
            secondPhraseLabel.setText(usesSamePhrase ? "How Item B relates to Item A (uses Item A wording)"
                    : "How Item B relates to Item A *");
        }

        private void refreshPreview() {
            if (categoryBox.getValue() != RelationshipCategory.CUSTOM) {
                previewLabel.setText("Example: Item A is " + selectedTemplate().forwardLabel() + " Item B");
                return;
            }
            String firstPhrase = firstPhraseField.getText();
            String secondPhrase = samePhraseBox.isSelected() ? firstPhrase : secondPhraseField.getText();
            previewLabel.setText("Example: Item A is " + firstPhrase
                    + " Item B; Item B " + secondPhrase + " Item A");
        }

        private RelationshipTemplate selectedTemplate() {
            return RelationshipTemplate.forCategory(categoryBox.getValue()).getFirst();
        }

        private RelationshipTypeInput toInput() {
            if (categoryBox.getValue() == RelationshipCategory.CUSTOM) {
                String inverseLabel = samePhraseBox.isSelected() ? firstPhraseField.getText()
                        : secondPhraseField.getText();
                RelationshipKind kind = samePhraseBox.isSelected() ? RelationshipKind.SYMMETRIC
                        : RelationshipKind.DIRECTED;
                return new RelationshipTypeInput(nameField.getText(), firstPhraseField.getText(), inverseLabel, kind);
            }
            RelationshipTemplate template = selectedTemplate();
            RelationshipKind kind = template.forwardLabel().equals(template.inverseLabel())
                    ? RelationshipKind.SYMMETRIC : RelationshipKind.DIRECTED;
            return new RelationshipTypeInput(template.category().toString(), template.forwardLabel(),
                    template.inverseLabel(), kind);
        }

        private void setVisibleAndManaged(javafx.scene.Node node, boolean isVisible) {
            node.setVisible(isVisible);
            node.setManaged(isVisible);
        }
    }
}
