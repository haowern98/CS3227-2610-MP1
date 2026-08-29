package com.possessionmanager.ui;

import com.possessionmanager.model.RelationshipCategory;
import com.possessionmanager.model.RelationshipKind;
import com.possessionmanager.model.RelationshipTemplate;
import com.possessionmanager.model.RelationshipType;
import com.possessionmanager.model.RelationshipTypeInput;
import java.util.Arrays;
import java.util.Optional;
import javafx.geometry.Insets;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;

/**
 * Collects a built-in relationship label.
 */
public final class RelationshipTypeDialog {
    private static final double DIALOG_WIDTH = 520;

    private RelationshipTypeDialog() {
    }

    /**
     * Shows a built-in-label chooser or the custom-label dialog.
     *
     * @param type relationship label to edit, or {@code null} when adding one.
     * @return entered label details when the user saves a dialog.
     */
    public static Optional<RelationshipTypeInput> show(RelationshipType type) {
        if (type != null && findTemplate(type) == null) {
            return CustomRelationshipLabelDialog.show(type);
        }
        return showBuiltInChooser(type);
    }

    private static Optional<RelationshipTypeInput> showBuiltInChooser(RelationshipType type) {
        Dialog<RelationshipCategory> dialog = new Dialog<>();
        dialog.setTitle(type == null ? "Add Relationship Label" : "Edit Relationship Label");
        dialog.setHeaderText("Choose a built-in label or create a custom one.");
        dialog.setResizable(false);
        dialog.getDialogPane().setPrefWidth(DIALOG_WIDTH);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.CANCEL, ButtonType.OK);

        ComboBox<RelationshipCategory> categoryBox = new ComboBox<>();
        categoryBox.getItems().setAll(RelationshipCategory.values());
        categoryBox.setValue(type == null ? RelationshipCategory.STORAGE : findTemplate(type).category());
        Label exampleLabel = new Label();
        categoryBox.valueProperty().addListener((observable, oldCategory, newCategory) -> {
            updateExample(exampleLabel, newCategory);
        });
        updateExample(exampleLabel, categoryBox.getValue());
        dialog.getDialogPane().setContent(createChooserForm(categoryBox, exampleLabel));
        dialog.setResultConverter(button -> button == ButtonType.OK ? categoryBox.getValue() : null);
        return dialog.showAndWait().flatMap(RelationshipTypeDialog::toInput);
    }

    private static GridPane createChooserForm(ComboBox<RelationshipCategory> categoryBox, Label exampleLabel) {
        GridPane form = new GridPane();
        form.setHgap(10);
        form.setVgap(10);
        form.setPadding(new Insets(8));
        form.addRow(0, new Label("Relationship label"), categoryBox);
        form.add(exampleLabel, 1, 1);
        return form;
    }

    private static void updateExample(Label exampleLabel, RelationshipCategory category) {
        if (category == RelationshipCategory.CUSTOM) {
            exampleLabel.setText("Choose OK to enter your own wording.");
            return;
        }
        RelationshipTemplate template = RelationshipTemplate.forCategory(category).getFirst();
        exampleLabel.setText("Example: " + formatExample(template.forwardLabel(), template.inverseLabel()));
    }

    static String formatExample(String forwardLabel, String inverseLabel) {
        String reverseSubject = forwardLabel.equals(inverseLabel) ? "Item B is " : "Item B ";
        return "Item A is " + forwardLabel + " Item B, " + reverseSubject + inverseLabel + " Item A";
    }

    private static Optional<RelationshipTypeInput> toInput(RelationshipCategory category) {
        if (category == RelationshipCategory.CUSTOM) {
            return CustomRelationshipLabelDialog.show(null);
        }
        RelationshipTemplate template = RelationshipTemplate.forCategory(category).getFirst();
        RelationshipKind kind = template.forwardLabel().equals(template.inverseLabel())
                ? RelationshipKind.SYMMETRIC : RelationshipKind.DIRECTED;
        return Optional.of(new RelationshipTypeInput(template.category().toString(), template.forwardLabel(),
                template.inverseLabel(), kind));
    }

    private static RelationshipTemplate findTemplate(RelationshipType type) {
        return Arrays.stream(RelationshipTemplate.values())
                .filter(template -> template.forwardLabel().equals(type.forwardLabel()))
                .filter(template -> template.inverseLabel().equals(type.inverseLabel()))
                .findFirst()
                .orElse(null);
    }
}
