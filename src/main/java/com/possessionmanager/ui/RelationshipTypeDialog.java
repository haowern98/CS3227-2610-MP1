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
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;

/**
 * Collects predefined or custom wording for a saved possession relationship.
 */
public final class RelationshipTypeDialog {
    private RelationshipTypeDialog() {
    }

    /**
     * Shows an add or edit dialog for a saved relationship.
     *
     * @param type relationship to edit, or {@code null} when adding one.
     * @return entered relationship details when the user saves the dialog.
     */
    public static Optional<RelationshipTypeInput> show(RelationshipType type) {
        Dialog<RelationshipTypeInput> dialog = new Dialog<>();
        boolean isEditing = type != null;
        dialog.setTitle(isEditing ? "Edit Saved Relationship" : "Add Saved Relationship");
        dialog.setHeaderText("Choose ready-made wording or create your own.");
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
        private final ComboBox<RelationshipTemplate> templateBox = new ComboBox<>();
        private final TextField nameField = new TextField();
        private final TextField firstPhraseField = new TextField();
        private final CheckBox samePhraseBox = new CheckBox("Use the same phrase both ways");
        private final TextField secondPhraseField = new TextField();
        private final Label templateLabel = new Label("Predefined wording");
        private final Label nameLabel = new Label("Saved relationship name *");
        private final Label firstPhraseLabel = new Label("Phrase for Item A *");
        private final Label secondPhraseLabel = new Label("Phrase for Item B *");
        private final Label previewLabel = new Label();

        private Fields(RelationshipType type) {
            categoryBox.getItems().setAll(RelationshipCategory.values());
            categoryBox.valueProperty().addListener((observable, oldCategory, newCategory) -> {
                updateCategoryControls(newCategory);
            });
            templateBox.valueProperty().addListener((observable, oldTemplate, newTemplate) -> refreshPreview());
            samePhraseBox.selectedProperty().addListener((observable, wasSelected, isSelected) -> {
                updateSecondPhraseControl();
                refreshPreview();
            });
            firstPhraseField.textProperty().addListener((observable, oldText, newText) -> refreshPreview());
            secondPhraseField.textProperty().addListener((observable, oldText, newText) -> refreshPreview());
            populate(type);
        }

        private GridPane createForm() {
            GridPane form = new GridPane();
            form.setHgap(10);
            form.setVgap(10);
            form.setPadding(new Insets(8));
            form.addRow(0, new Label("Relationship category"), categoryBox);
            form.addRow(1, templateLabel, templateBox);
            form.addRow(2, nameLabel, nameField);
            form.addRow(3, firstPhraseLabel, firstPhraseField);
            form.add(samePhraseBox, 1, 4);
            form.addRow(5, secondPhraseLabel, secondPhraseField);
            form.add(previewLabel, 1, 6);
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
                templateBox.setValue(template);
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
            setVisibleAndManaged(templateLabel, !isCustom);
            setVisibleAndManaged(templateBox, !isCustom);
            setVisibleAndManaged(nameLabel, isCustom);
            setVisibleAndManaged(nameField, isCustom);
            setVisibleAndManaged(firstPhraseLabel, isCustom);
            setVisibleAndManaged(firstPhraseField, isCustom);
            setVisibleAndManaged(samePhraseBox, isCustom);
            setVisibleAndManaged(secondPhraseLabel, isCustom);
            setVisibleAndManaged(secondPhraseField, isCustom);
            if (isCustom) {
                updateSecondPhraseControl();
            } else {
                templateBox.getItems().setAll(RelationshipTemplate.forCategory(category));
                templateBox.setValue(templateBox.getItems().getFirst());
            }
            refreshPreview();
        }

        private void updateSecondPhraseControl() {
            boolean usesSamePhrase = samePhraseBox.isSelected();
            secondPhraseField.setDisable(usesSamePhrase);
            secondPhraseLabel.setText(usesSamePhrase ? "Phrase for Item B (uses Item A phrase)"
                    : "Phrase for Item B *");
        }

        private void refreshPreview() {
            String firstPhrase = currentFirstPhrase();
            String secondPhrase = currentSecondPhrase(firstPhrase);
            previewLabel.setText("Preview: Item A " + firstPhrase + " Item B; Item B " + secondPhrase + " Item A");
        }

        private String currentFirstPhrase() {
            RelationshipTemplate template = templateBox.getValue();
            return categoryBox.getValue() == RelationshipCategory.CUSTOM ? firstPhraseField.getText()
                    : template == null ? "" : template.forwardLabel();
        }

        private String currentSecondPhrase(String firstPhrase) {
            RelationshipTemplate template = templateBox.getValue();
            if (categoryBox.getValue() != RelationshipCategory.CUSTOM) {
                return template == null ? "" : template.inverseLabel();
            }
            return samePhraseBox.isSelected() ? firstPhrase : secondPhraseField.getText();
        }

        private RelationshipTypeInput toInput() {
            if (categoryBox.getValue() == RelationshipCategory.CUSTOM) {
                String inverseLabel = samePhraseBox.isSelected() ? firstPhraseField.getText()
                        : secondPhraseField.getText();
                RelationshipKind kind = samePhraseBox.isSelected() ? RelationshipKind.SYMMETRIC
                        : RelationshipKind.DIRECTED;
                return new RelationshipTypeInput(nameField.getText(), firstPhraseField.getText(), inverseLabel, kind);
            }
            RelationshipTemplate template = templateBox.getValue();
            RelationshipKind kind = template.forwardLabel().equals(template.inverseLabel())
                    ? RelationshipKind.SYMMETRIC : RelationshipKind.DIRECTED;
            String name = template.category() + ": " + template.forwardLabel();
            return new RelationshipTypeInput(name, template.forwardLabel(), template.inverseLabel(), kind);
        }

        private void setVisibleAndManaged(javafx.scene.Node node, boolean isVisible) {
            node.setVisible(isVisible);
            node.setManaged(isVisible);
        }
    }
}
