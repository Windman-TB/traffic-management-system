/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.trafficsystem.view;

import com.mycompany.trafficsystem.controller.NodeController;
import com.mycompany.trafficsystem.model.Node;

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

/**
 * Giao diện quản lý nút giao.
 */
public class NodeView {

    private static final String BG = "#EEF3F8";
    private static final String CARD_BG = "#FFFFFF";
    private static final String TEXT_MUTED = "#5E7EA5";
    private static final String PRIMARY = "#123B63";
    private static final String BORDER = "#DDE5EF";

    private final NodeController nodeController;
    private final TableView<Node> table;
    private final TextField searchField;

    public NodeView() {
        this.nodeController = new NodeController();
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
        TableView<Node> nodeTable = createTable();

        card.getChildren().addAll(toolbar, nodeTable);
        VBox.setVgrow(nodeTable, Priority.ALWAYS);

        root.getChildren().add(card);
        VBox.setVgrow(card, Priority.ALWAYS);

        loadData();

        return root;
    }

    private HBox createToolbar() {
        HBox toolbar = new HBox(10);
        toolbar.setAlignment(Pos.CENTER_LEFT);
        toolbar.setPadding(new Insets(14, 16, 14, 16));

        Label title = new Label("Quản lý nút giao");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        title.setTextFill(Color.web("#111827"));

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        searchField.setPromptText("Tìm mã, vĩ độ, kinh độ...");
        searchField.setPrefWidth(240);
        searchField.setStyle(
                "-fx-background-radius: 8;" +
                "-fx-border-radius: 8;" +
                "-fx-border-color: #CBD5E1;" +
                "-fx-padding: 8 10;" +
                "-fx-background-color: white;"
        );

        searchField.textProperty().addListener((obs, oldValue, newValue) -> {
            table.setItems(FXCollections.observableArrayList(nodeController.searchNodes(newValue)));
        });

        Button addButton = new Button("+  Thêm nút giao");
        addButton.setStyle(
                "-fx-background-color: " + PRIMARY + ";" +
                "-fx-text-fill: white;" +
                "-fx-font-weight: bold;" +
                "-fx-background-radius: 7;" +
                "-fx-padding: 8 14;"
        );

        addButton.setOnAction(e -> showAddNodeDialog());

        toolbar.getChildren().addAll(title, spacer, searchField, addButton);

        return toolbar;
    }

    private TableView<Node> createTable() {
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);
        table.setPrefHeight(460);
        table.setStyle(
                "-fx-background-color: white;" +
                "-fx-border-color: transparent;"
        );

        TableColumn<Node, String> idCol = new TableColumn<>("MÃ");
        idCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getDisplayNodeId()));

        TableColumn<Node, String> latitudeCol = new TableColumn<>("VĨ ĐỘ");
        latitudeCol.setCellValueFactory(data -> new SimpleStringProperty(formatCoordinate(data.getValue().getLatitude())));

        TableColumn<Node, String> longitudeCol = new TableColumn<>("KINH ĐỘ");
        longitudeCol.setCellValueFactory(data -> new SimpleStringProperty(formatCoordinate(data.getValue().getLongitude())));

        TableColumn<Node, String> createdAtCol = new TableColumn<>("THỜI GIAN TẠO");
        createdAtCol.setCellValueFactory(data -> {
            if (data.getValue().getCreatedAt() == null) {
                return new SimpleStringProperty("");
            }

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
            return new SimpleStringProperty(data.getValue().getCreatedAt().format(formatter));
        });

        TableColumn<Node, String> updatedAtCol = new TableColumn<>("THỜI GIAN CẬP NHẬT");
        updatedAtCol.setCellValueFactory(data -> {
            if (data.getValue().getUpdatedAt() == null) {
                return new SimpleStringProperty("");
            }

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
            return new SimpleStringProperty(data.getValue().getUpdatedAt().format(formatter));
        });

        TableColumn<Node, Void> actionCol = new TableColumn<>("");
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
                    Node node = getTableView().getItems().get(getIndex());
                    showNodeDetail(node);
                });

                editButton.setOnAction(e -> {
                    Node node = getTableView().getItems().get(getIndex());
                    showEditNodeDialog(node);
                });

                deleteButton.setOnAction(e -> {
                    Node node = getTableView().getItems().get(getIndex());
                    handleDelete(node);
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
                latitudeCol,
                longitudeCol,
                createdAtCol,
                updatedAtCol,
                actionCol
        ));

        return table;
    }

    private void loadData() {
        table.setItems(FXCollections.observableArrayList(nodeController.getAllNodes()));
    }

    private void showNodeDetail(Node node) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

        String createdAtText = node.getCreatedAt() == null
                ? ""
                : node.getCreatedAt().format(formatter);

        String updatedAtText = node.getUpdatedAt() == null
                ? ""
                : node.getUpdatedAt().format(formatter);

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Chi tiết nút giao");
        alert.setHeaderText(node.getDisplayNodeId());
        alert.setContentText(
                "Mã: " + node.getDisplayNodeId() + "\n" +
                "Vĩ độ: " + formatCoordinate(node.getLatitude()) + "\n" +
                "Kinh độ: " + formatCoordinate(node.getLongitude()) + "\n" +
                "Thời gian tạo: " + createdAtText + "\n" +
                "Thời gian cập nhật: " + updatedAtText
        );
        alert.showAndWait();
    }

    private void handleDelete(Node node) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Xác nhận xóa");
        confirm.setHeaderText(null);
        confirm.setContentText("Bạn có chắc chắn muốn xóa nút giao " + node.getDisplayNodeId() + " không?");

        Optional<ButtonType> result = confirm.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            boolean success = nodeController.deleteNode(node.getNodeId());

            if (success) {
                loadData();
                showInfoAlert("Xóa nút giao thành công.");
            } else {
                showErrorAlert("Không thể xóa nút giao.");
            }
        }
    }

    private void showAddNodeDialog() {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Thêm nút giao");
        dialog.setHeaderText("Nhập tọa độ nút giao mới");

        ButtonType saveButtonType = new ButtonType("Lưu", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelButtonType = new ButtonType("Hủy", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, cancelButtonType);

        GridPane form = new GridPane();
        form.setHgap(12);
        form.setVgap(12);
        form.setPadding(new Insets(16));

        TextField nodeIdField = new TextField();
        String nextNodeId = nodeController.generateNextNodeId();
        nodeIdField.setText(formatDisplayNodeId(nextNodeId));
        nodeIdField.setEditable(false);

        TextField latitudeField = new TextField();
        latitudeField.setPromptText("Ví dụ: 10.8418178");

        TextField longitudeField = new TextField();
        longitudeField.setPromptText("Ví dụ: 106.7784875");

        form.add(new Label("Mã nút giao:"), 0, 0);
        form.add(nodeIdField, 1, 0);

        form.add(new Label("Vĩ độ:"), 0, 1);
        form.add(latitudeField, 1, 1);

        form.add(new Label("Kinh độ:"), 0, 2);
        form.add(longitudeField, 1, 2);

        applyFormLayout(form);
        dialog.getDialogPane().setContent(form);

        Button saveButton = (Button) dialog.getDialogPane().lookupButton(saveButtonType);
        saveButton.addEventFilter(javafx.event.ActionEvent.ACTION, event -> {
            String latitude = latitudeField.getText();
            String longitude = longitudeField.getText();

            String errorMessage = validateNodeInput(latitude, longitude);

            if (errorMessage != null) {
                showErrorAlert(errorMessage);
                event.consume();
                return;
            }

            boolean success = nodeController.addNode(latitude, longitude);

            if (success) {
                loadData();
                showInfoAlert("Thêm nút giao thành công.");
            } else {
                showErrorAlert("Không thể thêm nút giao.");
                event.consume();
            }
        });

        dialog.showAndWait();
    }

    private void showEditNodeDialog(Node node) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Sửa nút giao");
        dialog.setHeaderText("Cập nhật tọa độ nút giao");

        ButtonType saveButtonType = new ButtonType("Cập nhật", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelButtonType = new ButtonType("Hủy", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, cancelButtonType);

        GridPane form = new GridPane();
        form.setHgap(12);
        form.setVgap(12);
        form.setPadding(new Insets(16));

        TextField nodeIdField = new TextField();
        nodeIdField.setText(node.getDisplayNodeId());
        nodeIdField.setEditable(false);

        TextField latitudeField = new TextField();
        latitudeField.setText(formatCoordinate(node.getLatitude()));

        TextField longitudeField = new TextField();
        longitudeField.setText(formatCoordinate(node.getLongitude()));

        form.add(new Label("Mã nút giao:"), 0, 0);
        form.add(nodeIdField, 1, 0);

        form.add(new Label("Vĩ độ:"), 0, 1);
        form.add(latitudeField, 1, 1);

        form.add(new Label("Kinh độ:"), 0, 2);
        form.add(longitudeField, 1, 2);

        applyFormLayout(form);
        dialog.getDialogPane().setContent(form);

        Button saveButton = (Button) dialog.getDialogPane().lookupButton(saveButtonType);
        saveButton.addEventFilter(javafx.event.ActionEvent.ACTION, event -> {
            String latitude = latitudeField.getText();
            String longitude = longitudeField.getText();

            String errorMessage = validateNodeInput(latitude, longitude);

            if (errorMessage != null) {
                showErrorAlert(errorMessage);
                event.consume();
                return;
            }

            boolean success = nodeController.updateNode(
                    node.getNodeId(),
                    latitude,
                    longitude
            );

            if (success) {
                loadData();
                showInfoAlert("Cập nhật nút giao thành công.");
            } else {
                showErrorAlert("Không thể cập nhật nút giao.");
                event.consume();
            }
        });

        dialog.showAndWait();
    }

    private String validateNodeInput(String latitudeText, String longitudeText) {
        Double latitude = parseDouble(latitudeText);
        Double longitude = parseDouble(longitudeText);

        if (latitude == null) {
            return "Vĩ độ phải là số. Ví dụ: 10.8418178";
        }

        if (longitude == null) {
            return "Kinh độ phải là số. Ví dụ: 106.7784875";
        }

        if (latitude < -90 || latitude > 90) {
            return "Vĩ độ phải nằm trong khoảng từ -90 đến 90.";
        }

        if (longitude < -180 || longitude > 180) {
            return "Kinh độ phải nằm trong khoảng từ -180 đến 180.";
        }

        return null;
    }

    private Double parseDouble(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }

        try {
            return Double.parseDouble(value.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private String formatCoordinate(double value) {
        return String.format(java.util.Locale.US, "%.7f", value);
    }

    private String formatDisplayNodeId(String nodeId) {
        if (nodeId == null || nodeId.trim().isEmpty()) {
            return "";
        }

        try {
            int number = Integer.parseInt(nodeId.trim());
            return String.format("NG%03d", number);
        } catch (NumberFormatException e) {
            return nodeId;
        }
    }

    private void applyFormLayout(GridPane form) {
        ColumnConstraints labelCol = new ColumnConstraints();
        labelCol.setMinWidth(120);

        ColumnConstraints inputCol = new ColumnConstraints();
        inputCol.setMinWidth(280);

        form.getColumnConstraints().setAll(labelCol, inputCol);
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
