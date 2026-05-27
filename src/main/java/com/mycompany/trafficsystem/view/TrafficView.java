/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.trafficsystem.view;

import com.mycompany.trafficsystem.controller.TrafficController;
import com.mycompany.trafficsystem.model.Traffic;

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
 * Giao diện quản lý lưu lượng giao thông.
 * Dữ liệu bảng TRAFFIC chỉ cho sửa, xóa và tra cứu.
 * Không có chức năng thêm vì dữ liệu sẽ được nạp tự động bằng tiến trình khác.
 */
public class TrafficView {

    private static final String BG = "#EEF3F8";
    private static final String CARD_BG = "#FFFFFF";
    private static final String TEXT_MUTED = "#5E7EA5";
    private static final String PRIMARY = "#123B63";
    private static final String BORDER = "#DDE5EF";

    private final TrafficController trafficController;
    private final TableView<Traffic> table;
    private final TextField searchField;

    public TrafficView() {
        this.trafficController = new TrafficController();
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
        TableView<Traffic> trafficTable = createTable();

        card.getChildren().addAll(toolbar, trafficTable);
        VBox.setVgrow(trafficTable, Priority.ALWAYS);

        root.getChildren().add(card);
        VBox.setVgrow(card, Priority.ALWAYS);

        loadData();

        return root;
    }

    private HBox createToolbar() {
        HBox toolbar = new HBox(10);
        toolbar.setAlignment(Pos.CENTER_LEFT);
        toolbar.setPadding(new Insets(14, 16, 14, 16));

        Label title = new Label("Quản lý lưu lượng");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        title.setTextFill(Color.web("#111827"));

        Label note = new Label("Chỉ sửa, xóa và tra cứu. Dữ liệu thêm tự động từ tiến trình khác.");
        note.setFont(Font.font("Arial", 11));
        note.setTextFill(Color.web(TEXT_MUTED));

        VBox titleBox = new VBox(2, title, note);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        searchField.setPromptText("Tìm mã trạng thái, đoạn đường, tốc độ...");
        searchField.setPrefWidth(300);
        searchField.setStyle(
                "-fx-background-radius: 8;" +
                "-fx-border-radius: 8;" +
                "-fx-border-color: #CBD5E1;" +
                "-fx-padding: 8 10;" +
                "-fx-background-color: white;"
        );

        searchField.textProperty().addListener((obs, oldValue, newValue) -> {
            table.setItems(FXCollections.observableArrayList(trafficController.searchTraffic(newValue)));
        });

        Button refreshButton = new Button("↻  Làm mới");
        refreshButton.setStyle(
                "-fx-background-color: " + PRIMARY + ";" +
                "-fx-text-fill: white;" +
                "-fx-font-weight: bold;" +
                "-fx-background-radius: 7;" +
                "-fx-padding: 8 14;"
        );
        refreshButton.setOnAction(e -> {
            searchField.clear();
            loadData();
        });

        toolbar.getChildren().addAll(titleBox, spacer, searchField, refreshButton);

        return toolbar;
    }

    private TableView<Traffic> createTable() {
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);
        table.setPrefHeight(460);
        table.setStyle(
                "-fx-background-color: white;" +
                "-fx-border-color: transparent;"
        );

        TableColumn<Traffic, String> statusIdCol = new TableColumn<>("MÃ TRẠNG THÁI");
        statusIdCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getDisplayStatusId()));

        TableColumn<Traffic, String> segmentIdCol = new TableColumn<>("MÃ ĐOẠN ĐƯỜNG");
        segmentIdCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getDisplaySegmentId()));

        TableColumn<Traffic, String> velocityCol = new TableColumn<>("TỐC ĐỘ");
        velocityCol.setCellValueFactory(data -> new SimpleStringProperty(formatVelocity(data.getValue().getVelocity())));

        TableColumn<Traffic, String> statusCol = new TableColumn<>("TÌNH TRẠNG");
        statusCol.setCellValueFactory(data -> new SimpleStringProperty(getTrafficStatus(data.getValue().getVelocity())));
        statusCol.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String status, boolean empty) {
                super.updateItem(status, empty);

                if (empty || status == null) {
                    setText(null);
                    setGraphic(null);
                    return;
                }

                Label badge = new Label(status);
                badge.setPadding(new Insets(4, 9, 4, 9));
                badge.setFont(Font.font("Arial", FontWeight.BOLD, 11));

                if (status.equals("Tắc nghẽn")) {
                    badge.setTextFill(Color.web("#DC2626"));
                    badge.setStyle("-fx-background-color: #FEF2F2; -fx-border-color: #FCA5A5; -fx-background-radius: 5; -fx-border-radius: 5;");
                } else if (status.equals("Đông đúc")) {
                    badge.setTextFill(Color.web("#D97706"));
                    badge.setStyle("-fx-background-color: #FFFBEB; -fx-border-color: #FCD34D; -fx-background-radius: 5; -fx-border-radius: 5;");
                } else {
                    badge.setTextFill(Color.web("#059669"));
                    badge.setStyle("-fx-background-color: #ECFDF5; -fx-border-color: #6EE7B7; -fx-background-radius: 5; -fx-border-radius: 5;");
                }

                setGraphic(badge);
            }
        });

        TableColumn<Traffic, String> createdAtCol = new TableColumn<>("THỜI GIAN TẠO");
        createdAtCol.setCellValueFactory(data -> {
            if (data.getValue().getCreatedAt() == null) {
                return new SimpleStringProperty("");
            }

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
            return new SimpleStringProperty(data.getValue().getCreatedAt().format(formatter));
        });

        TableColumn<Traffic, Void> actionCol = new TableColumn<>("");
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
                    Traffic traffic = getTableView().getItems().get(getIndex());
                    showTrafficDetail(traffic);
                });

                editButton.setOnAction(e -> {
                    Traffic traffic = getTableView().getItems().get(getIndex());
                    showEditTrafficDialog(traffic);
                });

                deleteButton.setOnAction(e -> {
                    Traffic traffic = getTableView().getItems().get(getIndex());
                    handleDelete(traffic);
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
                statusIdCol,
                segmentIdCol,
                velocityCol,
                statusCol,
                createdAtCol,
                actionCol
        ));

        return table;
    }

    private void loadData() {
        table.setItems(FXCollections.observableArrayList(trafficController.getAllTraffic()));
    }

    private void showTrafficDetail(Traffic traffic) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

        String createdAtText = traffic.getCreatedAt() == null
                ? ""
                : traffic.getCreatedAt().format(formatter);

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Chi tiết lưu lượng");
        alert.setHeaderText(traffic.getDisplayStatusId());
        alert.setContentText(
                "Mã trạng thái: " + traffic.getDisplayStatusId() + "\n" +
                "Mã đoạn đường: " + traffic.getDisplaySegmentId() + "\n" +
                "Tốc độ: " + formatVelocity(traffic.getVelocity()) + "\n" +
                "Tình trạng: " + getTrafficStatus(traffic.getVelocity()) + "\n" +
                "Thời gian tạo: " + createdAtText
        );
        alert.showAndWait();
    }

    private void handleDelete(Traffic traffic) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Xác nhận xóa");
        confirm.setHeaderText(null);
        confirm.setContentText("Bạn có chắc chắn muốn xóa dữ liệu lưu lượng " + traffic.getDisplayStatusId() + " không?");

        Optional<ButtonType> result = confirm.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            boolean success = trafficController.deleteTraffic(traffic.getStatusId());

            if (success) {
                loadData();
                showInfoAlert("Xóa dữ liệu lưu lượng thành công.");
            } else {
                showErrorAlert("Không thể xóa dữ liệu lưu lượng.");
            }
        }
    }

    private void showEditTrafficDialog(Traffic traffic) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Sửa lưu lượng");
        dialog.setHeaderText("Cập nhật dữ liệu lưu lượng giao thông");

        ButtonType saveButtonType = new ButtonType("Cập nhật", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelButtonType = new ButtonType("Hủy", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, cancelButtonType);

        GridPane form = new GridPane();
        form.setHgap(12);
        form.setVgap(12);
        form.setPadding(new Insets(16));

        TextField statusIdField = new TextField();
        statusIdField.setText(traffic.getDisplayStatusId());
        statusIdField.setDisable(true);

        TextField segmentIdField = new TextField();
        segmentIdField.setText(traffic.getDisplaySegmentId());
        segmentIdField.setPromptText("Ví dụ: SEG001 hoặc 1");

        TextField velocityField = new TextField();
        velocityField.setText(formatVelocity(traffic.getVelocity()));
        velocityField.setPromptText("Ví dụ: 45.50");

        form.add(new Label("Mã trạng thái:"), 0, 0);
        form.add(statusIdField, 1, 0);

        form.add(new Label("Mã đoạn đường:"), 0, 1);
        form.add(segmentIdField, 1, 1);

        form.add(new Label("Tốc độ:"), 0, 2);
        form.add(velocityField, 1, 2);

        dialog.getDialogPane().setContent(form);

        Button saveButton = (Button) dialog.getDialogPane().lookupButton(saveButtonType);
        saveButton.addEventFilter(javafx.event.ActionEvent.ACTION, event -> {
            String segmentId = segmentIdField.getText();
            String velocity = velocityField.getText();

            String errorMessage = validateTrafficInput(segmentId, velocity);

            if (errorMessage != null) {
                showErrorAlert(errorMessage);
                event.consume();
                return;
            }

            boolean success = trafficController.updateTraffic(
                    traffic.getStatusId(),
                    segmentId,
                    velocity
            );

            if (success) {
                loadData();
                showInfoAlert("Cập nhật dữ liệu lưu lượng thành công.");
            } else {
                showErrorAlert("Không thể cập nhật dữ liệu lưu lượng.");
                event.consume();
            }
        });

        dialog.showAndWait();
    }

    private String validateTrafficInput(String segmentIdText, String velocityText) {
        String segmentId = trafficController.normalizeSegmentId(segmentIdText);
        Double velocity = parseDouble(velocityText);

        if (segmentId == null || segmentId.trim().isEmpty()) {
            return "Mã đoạn đường không được để trống. Ví dụ: SEG001 hoặc 1.";
        }

        if (velocity == null) {
            return "Tốc độ phải là số. Ví dụ: 45.50";
        }

        if (velocity < 0) {
            return "Tốc độ phải lớn hơn hoặc bằng 0.";
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

    private String formatVelocity(double value) {
        return String.format(java.util.Locale.US, "%.2f", value);
    }

    private String getTrafficStatus(double velocity) {
        if (velocity <= 15) {
            return "Tắc nghẽn";
        }

        if (velocity <= 35) {
            return "Đông đúc";
        }

        return "Thông thoáng";
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
