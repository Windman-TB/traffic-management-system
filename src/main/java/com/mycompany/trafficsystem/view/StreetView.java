/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.trafficsystem.view;

import com.mycompany.trafficsystem.controller.StreetController;
import com.mycompany.trafficsystem.model.Street;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.time.format.DateTimeFormatter;
import java.util.Optional;

public class StreetView {

    private static final String BG = "#EEF3F8";
    private static final String CARD_BG = "#FFFFFF";
    private static final String TEXT_MUTED = "#5E7EA5";
    private static final String PRIMARY = "#123B63";
    private static final String BORDER = "#DDE5EF";

    private final StreetController streetController;
    private final TableView<Street> table;
    private final TextField searchField;

    public StreetView() {
        this.streetController = new StreetController();
        this.table = new TableView<>();
        this.searchField = new TextField();
    }

    public VBox getView() {
        VBox root = new VBox();
        root.setPadding(new Insets(22, 28, 28, 28));
        root.setStyle("-fx-background-color: " + BG + ";");

        VBox card = new VBox();
        card.setStyle(createCardStyle());

        HBox toolbar = createToolbar();
        TableView<Street> streetTable = createTable();

        card.getChildren().addAll(toolbar, streetTable);
        VBox.setVgrow(streetTable, Priority.ALWAYS);

        root.getChildren().add(card);
        VBox.setVgrow(card, Priority.ALWAYS);

        loadData();

        return root;
    }

    private HBox createToolbar() {
        HBox toolbar = new HBox(10);
        toolbar.setAlignment(Pos.CENTER_LEFT);
        toolbar.setPadding(new Insets(14, 16, 14, 16));

        Label title = new Label("Quản lý tuyến đường");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        title.setTextFill(Color.web("#111827"));

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        searchField.setPromptText("Tìm theo mã, tên, loại, cấp đường...");
        searchField.setPrefWidth(260);
        searchField.setStyle(
                "-fx-background-radius: 8;" +
                "-fx-border-radius: 8;" +
                "-fx-border-color: #CBD5E1;" +
                "-fx-padding: 8 10;" +
                "-fx-background-color: white;"
        );

        searchField.textProperty().addListener((obs, oldValue, newValue) -> {
            table.setItems(FXCollections.observableArrayList(streetController.searchStreets(newValue)));
        });

        Button addButton = new Button("+  Thêm tuyến đường");
        addButton.setStyle(
                "-fx-background-color: " + PRIMARY + ";" +
                "-fx-text-fill: white;" +
                "-fx-font-weight: bold;" +
                "-fx-background-radius: 7;" +
                "-fx-padding: 8 14;"
        );

        addButton.setOnAction(e -> showAddStreetDialog());

        toolbar.getChildren().addAll(title, spacer, searchField, addButton);

        return toolbar;
    }

    private TableView<Street> createTable() {
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);
        table.setPrefHeight(460);
        table.setStyle(
                "-fx-background-color: white;" +
                "-fx-border-color: transparent;"
        );

        TableColumn<Street, String> idCol = new TableColumn<>("MÃ");
        idCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getDisplayStreetId()));

        TableColumn<Street, String> nameCol = new TableColumn<>("TÊN TUYẾN ĐƯỜNG");
        nameCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getStreetName()));

        TableColumn<Street, String> typeCol = new TableColumn<>("LOẠI ĐƯỜNG");
        typeCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getStreetType()));

        TableColumn<Street, String> roadLevelCol = new TableColumn<>("CẤP ĐƯỜNG");
        roadLevelCol.setCellValueFactory(data -> new SimpleStringProperty(String.valueOf(data.getValue().getRoadLevel())));

        TableColumn<Street, String> createdAtCol = new TableColumn<>("THỜI GIAN TẠO");
        createdAtCol.setCellValueFactory(data -> {
            if (data.getValue().getCreatedAt() == null) {
                return new SimpleStringProperty("");
            }

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
            return new SimpleStringProperty(data.getValue().getCreatedAt().format(formatter));
        });

        TableColumn<Street, String> updatedAtCol = new TableColumn<>("THỜI GIAN CẬP NHẬT");
        updatedAtCol.setCellValueFactory(data -> {
            if (data.getValue().getUpdatedAt() == null) {
                return new SimpleStringProperty("");
            }

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
            return new SimpleStringProperty(data.getValue().getUpdatedAt().format(formatter));
        });

        TableColumn<Street, Void> actionCol = new TableColumn<>("");
        actionCol.setPrefWidth(120);
        actionCol.setCellFactory(col -> new TableCell<>() {

            private final Button viewButton = new Button("👁");
            private final Button editButton = new Button("✎");
            private final Button deleteButton = new Button("🗑");
            private final HBox box = new HBox(8, viewButton, editButton, deleteButton);

            {
                box.setAlignment(Pos.CENTER);

                styleActionButton(viewButton);
                styleActionButton(editButton);
                styleActionButton(deleteButton);

                viewButton.setOnAction(e -> {
                    Street street = getTableView().getItems().get(getIndex());
                    showStreetDetail(street);
                });

                editButton.setOnAction(e -> {
                    Street street = getTableView().getItems().get(getIndex());
                    showEditStreetDialog(street);
                });

                deleteButton.setOnAction(e -> {
                    Street street = getTableView().getItems().get(getIndex());
                    handleDelete(street);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);

                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(box);
                }
            }
        });

        table.getColumns().setAll(java.util.List.of(
                idCol,
                nameCol,
                typeCol,
                roadLevelCol,
                createdAtCol,
                updatedAtCol,
                actionCol
        ));

        return table;
    }

    private void loadData() {
        table.setItems(FXCollections.observableArrayList(streetController.getAllStreets()));
    }

    private void showStreetDetail(Street street) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

        String createdAtText = street.getCreatedAt() == null
                ? ""
                : street.getCreatedAt().format(formatter);

        String updatedAtText = street.getUpdatedAt() == null
                ? ""
                : street.getUpdatedAt().format(formatter);

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Chi tiết tuyến đường");
        alert.setHeaderText(street.getStreetName());
        alert.setContentText(
                "Mã: " + street.getDisplayStreetId() + "\n" +
                "Tên: " + street.getStreetName() + "\n" +
                "Loại đường: " + street.getStreetType() + "\n" +
                "Cấp đường: " + street.getRoadLevel() + "\n" +
                "Thời gian tạo: " + createdAtText + "\n" +
                "Thời gian cập nhật: " + updatedAtText
        );
        alert.showAndWait();
    }

    private void handleDelete(Street street) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Xác nhận xóa");
        confirm.setHeaderText(null);
        confirm.setContentText("Bạn có chắc chắn muốn xóa tuyến đường " + street.getStreetName() + " không?");

        Optional<ButtonType> result = confirm.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            boolean success = streetController.deleteStreet(street.getStreetId());

            if (success) {
                loadData();
                showInfoAlert("Xóa tuyến đường thành công.");
            } else {
                showErrorAlert("Không thể xóa tuyến đường.");
            }
        }
    }

    private void showAddStreetDialog() {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Thêm tuyến đường");
        dialog.setHeaderText("Nhập thông tin tuyến đường mới");

        ButtonType saveButtonType = new ButtonType("Lưu", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelButtonType = new ButtonType("Hủy", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, cancelButtonType);

        GridPane form = createStreetForm();

        TextField streetIdField = new TextField();
        streetIdField.setText(streetController.generateNextStreetId());
        streetIdField.setEditable(false);

        TextField streetNameField = new TextField();
        streetNameField.setPromptText("Ví dụ: Đường số 1");

        ComboBox<String> streetTypeBox = createStreetTypeBox();
        ComboBox<Integer> roadLevelBox = createRoadLevelBox();

        addStreetFieldsToForm(form, streetIdField, streetNameField, streetTypeBox, roadLevelBox);
        dialog.getDialogPane().setContent(form);

        Button saveButton = (Button) dialog.getDialogPane().lookupButton(saveButtonType);
        saveButton.addEventFilter(javafx.event.ActionEvent.ACTION, event -> {
            String streetName = streetNameField.getText();
            String streetType = streetTypeBox.getValue();
            Integer roadLevel = roadLevelBox.getValue();

            String errorMessage = validateStreetInput(streetName, streetType, roadLevel);

            if (errorMessage != null) {
                showErrorAlert(errorMessage);
                event.consume();
                return;
            }

            boolean success = streetController.addStreet(streetName, streetType, roadLevel);

            if (success) {
                loadData();
                showInfoAlert("Thêm tuyến đường thành công.");
            } else {
                showErrorAlert("Không thể thêm tuyến đường.");
                event.consume();
            }
        });

        dialog.showAndWait();
    }

    private void showEditStreetDialog(Street street) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Sửa tuyến đường");
        dialog.setHeaderText("Cập nhật thông tin tuyến đường");

        ButtonType saveButtonType = new ButtonType("Cập nhật", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelButtonType = new ButtonType("Hủy", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, cancelButtonType);

        GridPane form = createStreetForm();

        TextField streetIdField = new TextField();
        streetIdField.setText(street.getDisplayStreetId());
        streetIdField.setEditable(false);

        TextField streetNameField = new TextField();
        streetNameField.setText(street.getStreetName());

        ComboBox<String> streetTypeBox = createStreetTypeBox();
        streetTypeBox.setValue(street.getStreetType());

        ComboBox<Integer> roadLevelBox = createRoadLevelBox();
        roadLevelBox.setValue(street.getRoadLevel());

        addStreetFieldsToForm(form, streetIdField, streetNameField, streetTypeBox, roadLevelBox);
        dialog.getDialogPane().setContent(form);

        Button saveButton = (Button) dialog.getDialogPane().lookupButton(saveButtonType);
        saveButton.addEventFilter(javafx.event.ActionEvent.ACTION, event -> {
            String streetName = streetNameField.getText();
            String streetType = streetTypeBox.getValue();
            Integer roadLevel = roadLevelBox.getValue();

            String errorMessage = validateStreetInput(streetName, streetType, roadLevel);

            if (errorMessage != null) {
                showErrorAlert(errorMessage);
                event.consume();
                return;
            }

            boolean success = streetController.updateStreet(
                    street.getStreetId(),
                    streetName,
                    streetType,
                    roadLevel
            );

            if (success) {
                loadData();
                showInfoAlert("Cập nhật tuyến đường thành công.");
            } else {
                showErrorAlert("Không thể cập nhật tuyến đường.");
                event.consume();
            }
        });

        dialog.showAndWait();
    }

    private GridPane createStreetForm() {
        GridPane form = new GridPane();
        form.setHgap(12);
        form.setVgap(12);
        form.setPadding(new Insets(16));

        ColumnConstraints labelCol = new ColumnConstraints();
        labelCol.setMinWidth(130);

        ColumnConstraints inputCol = new ColumnConstraints();
        inputCol.setMinWidth(280);

        form.getColumnConstraints().addAll(labelCol, inputCol);
        return form;
    }

    private ComboBox<String> createStreetTypeBox() {
        ComboBox<String> streetTypeBox = new ComboBox<>();
        streetTypeBox.getItems().addAll(
                "motorway",
                "trunk",
                "primary",
                "secondary",
                "tertiary",
                "residential",
                "unclassified"
        );
        streetTypeBox.setPromptText("Chọn loại đường");
        streetTypeBox.setMaxWidth(Double.MAX_VALUE);
        return streetTypeBox;
    }

    private ComboBox<Integer> createRoadLevelBox() {
        ComboBox<Integer> roadLevelBox = new ComboBox<>();
        roadLevelBox.getItems().addAll(1, 2, 3, 4, 5, 6);
        roadLevelBox.setPromptText("Chọn cấp đường");
        roadLevelBox.setMaxWidth(Double.MAX_VALUE);
        return roadLevelBox;
    }

    private void addStreetFieldsToForm(GridPane form,
                                       TextField streetIdField,
                                       TextField streetNameField,
                                       ComboBox<String> streetTypeBox,
                                       ComboBox<Integer> roadLevelBox) {
        form.add(new Label("Mã tuyến đường:"), 0, 0);
        form.add(streetIdField, 1, 0);

        form.add(new Label("Tên tuyến đường:"), 0, 1);
        form.add(streetNameField, 1, 1);

        form.add(new Label("Loại đường:"), 0, 2);
        form.add(streetTypeBox, 1, 2);

        form.add(new Label("Cấp đường:"), 0, 3);
        form.add(roadLevelBox, 1, 3);
    }

    private String validateStreetInput(String streetName, String streetType, Integer roadLevel) {
        if (streetName == null || streetName.trim().isEmpty()) {
            return "Tên tuyến đường không được để trống.";
        }

        if (streetName.trim().length() < 2) {
            return "Tên tuyến đường phải có ít nhất 2 ký tự.";
        }

        if (streetType == null || streetType.trim().isEmpty()) {
            return "Vui lòng chọn loại đường.";
        }

        if (roadLevel == null) {
            return "Vui lòng chọn cấp đường.";
        }

        if (roadLevel < 1 || roadLevel > 6) {
            return "Cấp đường phải từ 1 đến 6.";
        }

        return null;
    }

    private void styleActionButton(Button button) {
        button.setStyle(
                "-fx-background-color: transparent;" +
                "-fx-text-fill: " + TEXT_MUTED + ";" +
                "-fx-font-size: 13;" +
                "-fx-cursor: hand;"
        );
    }

    private String createCardStyle() {
        return "-fx-background-color: " + CARD_BG + ";" +
                "-fx-background-radius: 12;" +
                "-fx-border-color: " + BORDER + ";" +
                "-fx-border-radius: 12;" +
                "-fx-effect: dropshadow(gaussian, rgba(15, 23, 42, 0.12), 8, 0.2, 0, 2);";
    }

    private void showInfoAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Thông báo");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showErrorAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Lỗi");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
