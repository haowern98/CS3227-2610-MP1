package com.possessionmanager.ui;

import com.possessionmanager.model.LifecycleEvent;
import com.possessionmanager.model.LifecycleEventInput;
import com.possessionmanager.model.LifecycleEventType;
import java.time.LocalDate;
import java.util.Optional;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;

/**
 * Collects lifecycle-event details for a possession.
 */
public final class LifecycleEventDialog {
    private LifecycleEventDialog() {
    }

    /**
     * Shows an add or edit dialog for a lifecycle event.
     *
     * @param event event to edit, or {@code null} when adding one.
     * @return entered event details when the user saves the dialog.
     */
    public static Optional<LifecycleEventInput> show(LifecycleEvent event) {
        Dialog<LifecycleEventInput> dialog = new Dialog<>();
        boolean isEditing = event != null;
        dialog.setTitle(isEditing ? "Edit Lifecycle Event" : "Add Lifecycle Event");
        dialog.setHeaderText(isEditing ? "Update the event details." : "Record a possession event.");
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.CANCEL, ButtonType.OK);

        Fields fields = new Fields(event);
        dialog.getDialogPane().setContent(fields.createForm());
        preventBlankDescription(dialog, fields.descriptionField);
        dialog.setResultConverter(button -> button == ButtonType.OK ? fields.toInput() : null);
        return dialog.showAndWait();
    }

    private static void preventBlankDescription(Dialog<?> dialog, TextField descriptionField) {
        Button saveButton = (Button) dialog.getDialogPane().lookupButton(ButtonType.OK);
        saveButton.addEventFilter(ActionEvent.ACTION, event -> {
            if (descriptionField.getText().trim().isEmpty()) {
                event.consume();
                descriptionField.requestFocus();
            }
        });
    }

    private static final class Fields {
        private final ComboBox<LifecycleEventType> typeBox = new ComboBox<>();
        private final DatePicker datePicker = new DatePicker();
        private final TextField descriptionField = new TextField();
        private final TextArea notesArea = new TextArea();

        private Fields(LifecycleEvent event) {
            typeBox.getItems().setAll(LifecycleEventType.values());
            datePicker.setEditable(false);
            datePicker.getEditor().setOnMouseClicked(mouseEvent -> datePicker.show());
            notesArea.setPrefRowCount(3);
            populate(event);
        }

        private GridPane createForm() {
            GridPane form = new GridPane();
            form.setHgap(10);
            form.setVgap(10);
            form.setPadding(new Insets(8));
            form.addRow(0, new Label("Event Type"), typeBox);
            form.addRow(1, new Label("Date"), datePicker);
            form.addRow(2, new Label("Description *"), descriptionField);
            form.addRow(3, new Label("Notes"), notesArea);
            return form;
        }

        private void populate(LifecycleEvent event) {
            if (event == null) {
                typeBox.setValue(LifecycleEventType.ADDED);
                datePicker.setValue(LocalDate.now());
                return;
            }
            typeBox.setValue(event.type());
            datePicker.setValue(event.date());
            descriptionField.setText(event.description());
            notesArea.setText(event.notes());
        }

        private LifecycleEventInput toInput() {
            return new LifecycleEventInput(typeBox.getValue(), datePicker.getValue(), descriptionField.getText(),
                    notesArea.getText());
        }
    }
}
