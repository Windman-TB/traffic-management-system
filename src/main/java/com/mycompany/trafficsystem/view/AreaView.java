/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.trafficsystem.view;

/**
 *
 * @author engineer
 */
import com.mycompany.trafficsystem.controller.AreaController;
import com.mycompany.trafficsystem.model.Area;

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

public class AreaView {

    private static final String BG = "#EEF3F8";
    private static final String CARD_BG = "#FFFFFF";
    private static final String TEXT_MUTED = "#5E7EA5";
    private static final String PRIMARY = "#123B63";
    private static final String BORDER = "#DDE5EF";

    private final AreaController areaController;
    private final TableView<Area> table;
    private final TextField searchField;

    public AreaView() {
        this.areaController = new AreaController();
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
        TableView<Area> areaTable = createTable();

        card.getChildren().addAll(toolbar, areaTable);
        VBox.setVgrow(areaTable, Priority.ALWAYS);

        root.getChildren().add(card);
        VBox.setVgrow(card, Priority.ALWAYS);

        loadData();

        return root;
    }

    private HBox createToolbar() {
        HBox toolbar = new HBox(10);
        toolbar.setAlignment(Pos.CENTER_LEFT);
        toolbar.setPadding(new Insets(14, 16, 14, 16));

        Label title = new Label("Quản lý khu vực");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        title.setTextFill(Color.web("#111827"));

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        searchField.setPromptText("Tìm kiếm...");
        searchField.setPrefWidth(220);
        searchField.setStyle(
                "-fx-background-radius: 8;" +
                "-fx-border-radius: 8;" +
                "-fx-border-color: #CBD5E1;" +
                "-fx-padding: 8 10;" +
                "-fx-background-color: white;"
        );

        searchField.textProperty().addListener((obs, oldValue, newValue) -> {
            table.setItems(FXCollections.observableArrayList(areaController.searchAreas(newValue)));
        });

        Button addButton = new Button("+  Thêm khu vực");
        addButton.setStyle(
                "-fx-background-color: " + PRIMARY + ";" +
                "-fx-text-fill: white;" +
                "-fx-font-weight: bold;" +
                "-fx-background-radius: 7;" +
                "-fx-padding: 8 14;"
        );

        addButton.setOnAction(e -> showAddAreaDialog());

        toolbar.getChildren().addAll(title, spacer, searchField, addButton);

        return toolbar;
    }

    private TableView<Area> createTable() {
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
        table.setPrefHeight(460);
        table.setStyle(
                "-fx-background-color: white;" +
                "-fx-border-color: transparent;"
        );

        TableColumn<Area, String> idCol = new TableColumn<>("MÃ");
        idCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getDisplayAreaId()));

        TableColumn<Area, String> nameCol = new TableColumn<>("TÊN KHU VỰC");
        nameCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getAreaName()));

        TableColumn<Area, String> typeCol = new TableColumn<>("LOẠI");
        typeCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getAreaType()));

        TableColumn<Area, String> oldProvinceCol = new TableColumn<>("TỈNH CŨ");
        oldProvinceCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getOldProvince()));

        TableColumn<Area, String> createdAtCol = new TableColumn<>("THỜI GIAN TẠO");
        createdAtCol.setCellValueFactory(data -> {
            if (data.getValue().getCreatedAt() == null) {
                return new SimpleStringProperty("");
            }

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
            return new SimpleStringProperty(data.getValue().getCreatedAt().format(formatter));
        });

        TableColumn<Area, String> updatedAtCol = new TableColumn<>("THỜI GIAN CẬP NHẬT");
        updatedAtCol.setCellValueFactory(data -> {
            if (data.getValue().getUpdatedAt() == null) {
                return new SimpleStringProperty("");
            }

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
            return new SimpleStringProperty(data.getValue().getUpdatedAt().format(formatter));
        });

        TableColumn<Area, Void> actionCol = new TableColumn<>("");
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
                    Area area = getTableView().getItems().get(getIndex());
                    showAreaDetail(area);
                });

                editButton.setOnAction(e -> {
                    Area area = getTableView().getItems().get(getIndex());
                    showEditAreaDialog(area);
                });

                deleteButton.setOnAction(e -> {
                    Area area = getTableView().getItems().get(getIndex());
                    handleDelete(area);
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
                oldProvinceCol,
                createdAtCol,
                updatedAtCol,
                actionCol
        ));

        return table;
    }

    private void loadData() {
        table.setItems(FXCollections.observableArrayList(areaController.getAllAreas()));
    }

    private void showAreaDetail(Area area) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

        String createdAtText = area.getCreatedAt() == null
                ? ""
                : area.getCreatedAt().format(formatter);

        String updatedAtText = area.getUpdatedAt() == null
                ? ""
                : area.getUpdatedAt().format(formatter);

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Chi tiết khu vực");
        alert.setHeaderText(area.getAreaName());
        alert.setContentText(
                "Mã: " + area.getDisplayAreaId() + "\n" +
                "Tên: " + area.getAreaName() + "\n" +
                "Loại: " + area.getAreaType() + "\n" +
                "Tỉnh cũ: " + area.getOldProvince() + "\n" +
                "Thời gian tạo: " + createdAtText + "\n" +
                "Thời gian cập nhật: " + updatedAtText
        );
        alert.showAndWait();
    }

    private void handleDelete(Area area) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Xác nhận xóa");
        confirm.setHeaderText(null);
        confirm.setContentText("Bạn có chắc chắn muốn xóa khu vực " + area.getAreaName() + " không?");

        Optional<ButtonType> result = confirm.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            boolean success = areaController.deleteArea(area.getAreaId());

            if (success) {
                loadData();
                showInfoAlert("Xóa khu vực thành công.");
            } else {
                showErrorAlert(areaController.getDeleteRestrictionMessage(area.getAreaId()));
            }
        }
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
    
    private void showAddAreaDialog() {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Thêm khu vực");
        dialog.setHeaderText("Nhập thông tin khu vực mới");

        ButtonType saveButtonType = new ButtonType("Lưu", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelButtonType = new ButtonType("Hủy", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, cancelButtonType);

        GridPane form = new GridPane();
        form.setHgap(12);
        form.setVgap(12);
        form.setPadding(new Insets(16));

        TextField areaIdField = new TextField();
        String nextAreaId = areaController.generateNextAreaId();
        areaIdField.setText(formatDisplayAreaId(nextAreaId));
        areaIdField.setEditable(false);

        TextField areaNameField = new TextField();
        areaNameField.setPromptText("Ví dụ: Quận 1");

        ComboBox<String> areaTypeBox = new ComboBox<>();
        areaTypeBox.getItems().addAll(
                "Xã",
                "Phường",
                "Đặc khu"
        );
        areaTypeBox.setPromptText("Chọn loại khu vực");
        areaTypeBox.setMaxWidth(Double.MAX_VALUE);

        ComboBox<String> oldProvinceBox = new ComboBox<>();
        oldProvinceBox.getItems().addAll(
                "TP.HCM",
                "Bình Dương",
                "Bà Rịa - Vũng Tàu"
        );
        oldProvinceBox.setPromptText("Chọn tỉnh cũ");
        oldProvinceBox.setMaxWidth(Double.MAX_VALUE);

        form.add(new Label("Mã khu vực:"), 0, 0);
        form.add(areaIdField, 1, 0);

        form.add(new Label("Tên khu vực:"), 0, 1);
        form.add(areaNameField, 1, 1);

        form.add(new Label("Loại:"), 0, 2);
        form.add(areaTypeBox, 1, 2);

        form.add(new Label("Tỉnh cũ:"), 0, 3);
        form.add(oldProvinceBox, 1, 3);

        applyFormLayout(form);
        dialog.getDialogPane().setContent(form);

        Button saveButton = (Button) dialog.getDialogPane().lookupButton(saveButtonType);
        saveButton.addEventFilter(javafx.event.ActionEvent.ACTION, event -> {
            String areaName = areaNameField.getText();
            String areaType = areaTypeBox.getValue();
            String oldProvince = oldProvinceBox.getValue();

            String errorMessage = validateAreaInput(areaName, areaType, oldProvince);

            if (errorMessage != null) {
                showErrorAlert(errorMessage);
                event.consume();
                return;
            }

            boolean success = areaController.addArea(areaName, areaType, oldProvince);

            if (success) {
                loadData();
                showInfoAlert("Thêm khu vực thành công.");
            } else {
                showErrorAlert("Không thể thêm khu vực.");
                event.consume();
            }
        });

        dialog.showAndWait();
    }
    
    private void showEditAreaDialog(Area area) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Sửa khu vực");
        dialog.setHeaderText("Cập nhật thông tin khu vực");

        ButtonType saveButtonType = new ButtonType("Cập nhật", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelButtonType = new ButtonType("Hủy", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, cancelButtonType);

        GridPane form = new GridPane();
        form.setHgap(12);
        form.setVgap(12);
        form.setPadding(new Insets(16));

        TextField areaIdField = new TextField();
        areaIdField.setText(area.getDisplayAreaId());
        areaIdField.setEditable(false);

        TextField areaNameField = new TextField();
        areaNameField.setText(area.getAreaName());

        ComboBox<String> areaTypeBox = new ComboBox<>();
        areaTypeBox.getItems().addAll(
                "Xã",
                "Phường",
                "Đặc khu"
        );
        areaTypeBox.setValue(area.getAreaType());
        areaTypeBox.setMaxWidth(Double.MAX_VALUE);

        ComboBox<String> oldProvinceBox = new ComboBox<>();
        oldProvinceBox.getItems().addAll(
                "TP.HCM",
                "Bình Dương",
                "Bà Rịa - Vũng Tàu"
        );
        oldProvinceBox.setValue(area.getOldProvince());
        oldProvinceBox.setMaxWidth(Double.MAX_VALUE);

        form.add(new Label("Mã khu vực:"), 0, 0);
        form.add(areaIdField, 1, 0);

        form.add(new Label("Tên khu vực:"), 0, 1);
        form.add(areaNameField, 1, 1);

        form.add(new Label("Loại:"), 0, 2);
        form.add(areaTypeBox, 1, 2);

        form.add(new Label("Tỉnh cũ:"), 0, 3);
        form.add(oldProvinceBox, 1, 3);

        applyFormLayout(form);
        dialog.getDialogPane().setContent(form);

        Button saveButton = (Button) dialog.getDialogPane().lookupButton(saveButtonType);
        saveButton.addEventFilter(javafx.event.ActionEvent.ACTION, event -> {
            String areaName = areaNameField.getText();
            String areaType = areaTypeBox.getValue();
            String oldProvince = oldProvinceBox.getValue();

            String errorMessage = validateAreaInput(areaName, areaType, oldProvince);

            if (errorMessage != null) {
                showErrorAlert(errorMessage);
                event.consume();
                return;
            }

            boolean success = areaController.updateArea(
                    area.getAreaId(),
                    areaName,
                    areaType,
                    oldProvince
            );

            if (success) {
                loadData();
                showInfoAlert("Cập nhật khu vực thành công.");
            } else {
                showErrorAlert("Không thể cập nhật khu vực.");
                event.consume();
            }
        });

        dialog.showAndWait();
    }
    
    private String validateAreaInput(String areaName, String areaType, String oldProvince) {
        if (areaName == null || areaName.trim().isEmpty()) {
            return "Tên khu vực không được để trống.";
        }

        if (areaName.trim().length() < 2) {
            return "Tên khu vực phải có ít nhất 2 ký tự.";
        }

        if (areaType == null || areaType.trim().isEmpty()) {
            return "Vui lòng chọn loại khu vực.";
        }

        if (oldProvince == null || oldProvince.trim().isEmpty()) {
            return "Vui lòng chọn tỉnh cũ.";
        }

        return null;
    }

    private void applyFormLayout(GridPane form) {
        ColumnConstraints labelCol = new ColumnConstraints();
        labelCol.setMinWidth(120);

        ColumnConstraints inputCol = new ColumnConstraints();
        inputCol.setMinWidth(280);

        form.getColumnConstraints().setAll(labelCol, inputCol);
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

    private String formatDisplayAreaId(String areaId) {
        if (areaId == null || areaId.trim().isEmpty()) {
            return "";
        }

        try {
            int number = Integer.parseInt(areaId.trim());
            return String.format("KV%03d", number);
        } catch (NumberFormatException e) {
            return areaId;
        }
    }
    
    
}
