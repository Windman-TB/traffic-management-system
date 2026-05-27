/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.trafficsystem.view;

import com.mycompany.trafficsystem.controller.SegmentController;
import com.mycompany.trafficsystem.model.Segment;

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
import java.util.Locale;
import java.util.Optional;

/**
 * Giao diện quản lý đoạn đường.
 */
public class SegmentView {

    private static final String BG = "#EEF3F8";
    private static final String CARD_BG = "#FFFFFF";
    private static final String TEXT_MUTED = "#5E7EA5";
    private static final String PRIMARY = "#123B63";
    private static final String BORDER = "#DDE5EF";

    private final SegmentController segmentController;
    private final TableView<Segment> table;
    private final TextField searchField;

    public SegmentView() {
        this.segmentController = new SegmentController();
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
        TableView<Segment> segmentTable = createTable();

        card.getChildren().addAll(toolbar, segmentTable);
        VBox.setVgrow(segmentTable, Priority.ALWAYS);

        root.getChildren().add(card);
        VBox.setVgrow(card, Priority.ALWAYS);

        loadData();

        return root;
    }

    private HBox createToolbar() {
        HBox toolbar = new HBox(10);
        toolbar.setAlignment(Pos.CENTER_LEFT);
        toolbar.setPadding(new Insets(14, 16, 14, 16));

        Label title = new Label("Quản lý đoạn đường");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        title.setTextFill(Color.web("#111827"));

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        searchField.setPromptText("Tìm mã đoạn, tuyến, khu vực, nút...");
        searchField.setPrefWidth(300);
        searchField.setStyle(
                "-fx-background-radius: 8;" +
                "-fx-border-radius: 8;" +
                "-fx-border-color: #CBD5E1;" +
                "-fx-padding: 8 10;" +
                "-fx-background-color: white;"
        );

        searchField.textProperty().addListener((obs, oldValue, newValue) -> {
            table.setItems(FXCollections.observableArrayList(segmentController.searchSegments(newValue)));
        });

        Button refreshButton = new Button("↻  Làm mới");
        refreshButton.setStyle(
                "-fx-background-color: white;" +
                "-fx-text-fill: " + PRIMARY + ";" +
                "-fx-font-weight: bold;" +
                "-fx-border-color: " + PRIMARY + ";" +
                "-fx-border-radius: 7;" +
                "-fx-background-radius: 7;" +
                "-fx-padding: 8 14;"
        );
        refreshButton.setOnAction(e -> {
            searchField.clear();
            loadData();
        });

        Button addButton = new Button("+  Thêm đoạn đường");
        addButton.setStyle(
                "-fx-background-color: " + PRIMARY + ";" +
                "-fx-text-fill: white;" +
                "-fx-font-weight: bold;" +
                "-fx-background-radius: 7;" +
                "-fx-padding: 8 14;"
        );
        addButton.setOnAction(e -> showAddSegmentDialog());

        toolbar.getChildren().addAll(title, spacer, searchField, refreshButton, addButton);

        return toolbar;
    }

    private TableView<Segment> createTable() {
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.setPrefHeight(460);
        table.setStyle(
                "-fx-background-color: white;" +
                "-fx-border-color: transparent;"
        );

        TableColumn<Segment, String> segmentIdCol = new TableColumn<>("MÃ ĐOẠN");
        segmentIdCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getDisplaySegmentId()));

        TableColumn<Segment, String> streetIdCol = new TableColumn<>("MÃ TUYẾN");
        streetIdCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getDisplayStreetId()));

        TableColumn<Segment, String> areaIdCol = new TableColumn<>("KHU VỰC");
        areaIdCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getDisplayAreaId()));

        TableColumn<Segment, String> startNodeCol = new TableColumn<>("NÚT ĐẦU");
        startNodeCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getDisplayStartNodeId()));

        TableColumn<Segment, String> endNodeCol = new TableColumn<>("NÚT CUỐI");
        endNodeCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getDisplayEndNodeId()));

        TableColumn<Segment, String> lengthCol = new TableColumn<>("CHIỀU DÀI");
        lengthCol.setCellValueFactory(data -> new SimpleStringProperty(formatDouble(data.getValue().getSegmentLength())));

        TableColumn<Segment, String> maxVelocityCol = new TableColumn<>("TỐC ĐỘ TỐI ĐA");
        maxVelocityCol.setCellValueFactory(data -> new SimpleStringProperty(formatInteger(data.getValue().getMaxVelocity())));

        TableColumn<Segment, String> createdAtCol = new TableColumn<>("THỜI GIAN TẠO");
        createdAtCol.setCellValueFactory(data -> {
            if (data.getValue().getCreatedAt() == null) {
                return new SimpleStringProperty("");
            }

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
            return new SimpleStringProperty(data.getValue().getCreatedAt().format(formatter));
        });

        TableColumn<Segment, String> updatedAtCol = new TableColumn<>("THỜI GIAN CẬP NHẬT");
        updatedAtCol.setCellValueFactory(data -> {
            if (data.getValue().getUpdatedAt() == null) {
                return new SimpleStringProperty("");
            }

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
            return new SimpleStringProperty(data.getValue().getUpdatedAt().format(formatter));
        });

        TableColumn<Segment, Void> actionCol = new TableColumn<>("");
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
                    Segment segment = getTableView().getItems().get(getIndex());
                    showSegmentDetail(segment);
                });

                editButton.setOnAction(e -> {
                    Segment segment = getTableView().getItems().get(getIndex());
                    showEditSegmentDialog(segment);
                });

                deleteButton.setOnAction(e -> {
                    Segment segment = getTableView().getItems().get(getIndex());
                    handleDelete(segment);
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

        table.getColumns().addAll(
                segmentIdCol,
                streetIdCol,
                areaIdCol,
                startNodeCol,
                endNodeCol,
                lengthCol,
                maxVelocityCol,
                createdAtCol,
                updatedAtCol,
                actionCol
        );

        return table;
    }

    private void loadData() {
        table.setItems(FXCollections.observableArrayList(segmentController.getAllSegments()));
    }

    private void showSegmentDetail(Segment segment) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

        String createdAtText = segment.getCreatedAt() == null
                ? ""
                : segment.getCreatedAt().format(formatter);

        String updatedAtText = segment.getUpdatedAt() == null
                ? ""
                : segment.getUpdatedAt().format(formatter);

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Chi tiết đoạn đường");
        alert.setHeaderText(segment.getDisplaySegmentId());
        alert.setContentText(
                "Mã đoạn đường: " + segment.getDisplaySegmentId() + "\n" +
                "Mã tuyến đường: " + segment.getDisplayStreetId() + "\n" +
                "Mã khu vực: " + segment.getDisplayAreaId() + "\n" +
                "Nút đầu: " + segment.getDisplayStartNodeId() + "\n" +
                "Nút cuối: " + segment.getDisplayEndNodeId() + "\n" +
                "Chiều dài: " + formatDouble(segment.getSegmentLength()) + "\n" +
                "Tốc độ tối đa: " + formatInteger(segment.getMaxVelocity()) + "\n" +
                "Thời gian tạo: " + createdAtText + "\n" +
                "Thời gian cập nhật: " + updatedAtText
        );
        alert.showAndWait();
    }

    private void handleDelete(Segment segment) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Xác nhận xóa");
        confirm.setHeaderText(null);
        confirm.setContentText("Bạn có chắc chắn muốn xóa đoạn đường " + segment.getDisplaySegmentId() + " không?");

        Optional<ButtonType> result = confirm.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            boolean success = segmentController.deleteSegment(segment.getSegmentId());

            if (success) {
                loadData();
                showInfoAlert("Xóa đoạn đường thành công.");
            } else {
                showErrorAlert("Không thể xóa đoạn đường.");
            }
        }
    }

    private void showAddSegmentDialog() {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Thêm đoạn đường");
        dialog.setHeaderText("Nhập thông tin đoạn đường mới");

        ButtonType saveButtonType = new ButtonType("Lưu", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelButtonType = new ButtonType("Hủy", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, cancelButtonType);

        GridPane form = createSegmentForm();

        TextField segmentIdField = new TextField(formatDisplaySegmentId(segmentController.generateNextSegmentId()));
        segmentIdField.setEditable(false);

        TextField streetIdField = new TextField();
        streetIdField.setPromptText("Ví dụ: 8");

        TextField areaIdField = new TextField();
        areaIdField.setPromptText("Ví dụ: 76 hoặc KV076");

        TextField startNodeIdField = new TextField();
        startNodeIdField.setPromptText("Ví dụ: 455427 hoặc NG455427");

        TextField endNodeIdField = new TextField();
        endNodeIdField.setPromptText("Ví dụ: 455453 hoặc NG455453");

        TextField segmentLengthField = new TextField();
        segmentLengthField.setPromptText("Ví dụ: 19.58");

        TextField maxVelocityField = new TextField();
        maxVelocityField.setPromptText("Có thể để trống. Ví dụ: 60");

        addFieldsToForm(form, segmentIdField, streetIdField, areaIdField, startNodeIdField,
                endNodeIdField, segmentLengthField, maxVelocityField);

        dialog.getDialogPane().setContent(form);

        Button saveButton = (Button) dialog.getDialogPane().lookupButton(saveButtonType);
        saveButton.addEventFilter(javafx.event.ActionEvent.ACTION, event -> {
            String errorMessage = validateSegmentInput(
                    streetIdField.getText(),
                    startNodeIdField.getText(),
                    endNodeIdField.getText(),
                    segmentLengthField.getText(),
                    maxVelocityField.getText()
            );

            if (errorMessage != null) {
                showErrorAlert(errorMessage);
                event.consume();
                return;
            }

            boolean success = segmentController.addSegment(
                    streetIdField.getText(),
                    areaIdField.getText(),
                    startNodeIdField.getText(),
                    endNodeIdField.getText(),
                    segmentLengthField.getText(),
                    maxVelocityField.getText()
            );

            if (success) {
                loadData();
                showInfoAlert("Thêm đoạn đường thành công.");
            } else {
                showErrorAlert("Không thể thêm đoạn đường.");
                event.consume();
            }
        });

        dialog.showAndWait();
    }

    private void showEditSegmentDialog(Segment segment) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Sửa đoạn đường");
        dialog.setHeaderText("Cập nhật thông tin đoạn đường");

        ButtonType saveButtonType = new ButtonType("Cập nhật", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelButtonType = new ButtonType("Hủy", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, cancelButtonType);

        GridPane form = createSegmentForm();

        TextField segmentIdField = new TextField(segment.getDisplaySegmentId());
        segmentIdField.setEditable(false);

        TextField streetIdField = new TextField(segment.getDisplayStreetId());
        TextField areaIdField = new TextField(segment.getDisplayAreaId());
        TextField startNodeIdField = new TextField(segment.getDisplayStartNodeId());
        TextField endNodeIdField = new TextField(segment.getDisplayEndNodeId());
        TextField segmentLengthField = new TextField(formatDouble(segment.getSegmentLength()));
        TextField maxVelocityField = new TextField(formatInteger(segment.getMaxVelocity()));

        addFieldsToForm(form, segmentIdField, streetIdField, areaIdField, startNodeIdField,
                endNodeIdField, segmentLengthField, maxVelocityField);

        dialog.getDialogPane().setContent(form);

        Button saveButton = (Button) dialog.getDialogPane().lookupButton(saveButtonType);
        saveButton.addEventFilter(javafx.event.ActionEvent.ACTION, event -> {
            String errorMessage = validateSegmentInput(
                    streetIdField.getText(),
                    startNodeIdField.getText(),
                    endNodeIdField.getText(),
                    segmentLengthField.getText(),
                    maxVelocityField.getText()
            );

            if (errorMessage != null) {
                showErrorAlert(errorMessage);
                event.consume();
                return;
            }

            boolean success = segmentController.updateSegment(
                    segment.getSegmentId(),
                    streetIdField.getText(),
                    areaIdField.getText(),
                    startNodeIdField.getText(),
                    endNodeIdField.getText(),
                    segmentLengthField.getText(),
                    maxVelocityField.getText()
            );

            if (success) {
                loadData();
                showInfoAlert("Cập nhật đoạn đường thành công.");
            } else {
                showErrorAlert("Không thể cập nhật đoạn đường.");
                event.consume();
            }
        });

        dialog.showAndWait();
    }

    private GridPane createSegmentForm() {
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

    private void addFieldsToForm(GridPane form, TextField segmentIdField, TextField streetIdField,
                                 TextField areaIdField, TextField startNodeIdField,
                                 TextField endNodeIdField, TextField segmentLengthField,
                                 TextField maxVelocityField) {
        form.add(new Label("Mã đoạn đường:"), 0, 0);
        form.add(segmentIdField, 1, 0);

        form.add(new Label("Mã tuyến đường:"), 0, 1);
        form.add(streetIdField, 1, 1);

        form.add(new Label("Mã khu vực:"), 0, 2);
        form.add(areaIdField, 1, 2);

        form.add(new Label("Nút đầu:"), 0, 3);
        form.add(startNodeIdField, 1, 3);

        form.add(new Label("Nút cuối:"), 0, 4);
        form.add(endNodeIdField, 1, 4);

        form.add(new Label("Chiều dài:"), 0, 5);
        form.add(segmentLengthField, 1, 5);

        form.add(new Label("Tốc độ tối đa:"), 0, 6);
        form.add(maxVelocityField, 1, 6);
    }

    private String validateSegmentInput(String streetIdText, String startNodeIdText, String endNodeIdText,
                                        String segmentLengthText, String maxVelocityText) {
        if (isBlank(streetIdText)) {
            return "Mã tuyến đường không được để trống.";
        }

        if (isBlank(startNodeIdText)) {
            return "Mã nút đầu không được để trống.";
        }

        if (isBlank(endNodeIdText)) {
            return "Mã nút cuối không được để trống.";
        }

        if (!isBlank(segmentLengthText)) {
            Double segmentLength = parseDouble(segmentLengthText);
            if (segmentLength == null || segmentLength < 0) {
                return "Chiều dài đoạn đường phải là số lớn hơn hoặc bằng 0. Ví dụ: 19.58";
            }
        }

        if (!isBlank(maxVelocityText)) {
            Integer maxVelocity = parseInteger(maxVelocityText);
            if (maxVelocity == null || maxVelocity < 0) {
                return "Tốc độ tối đa phải là số nguyên lớn hơn hoặc bằng 0. Ví dụ: 60";
            }
        }

        return null;
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    private Double parseDouble(String value) {
        try {
            return Double.parseDouble(value.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private Integer parseInteger(String value) {
        try {
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private String formatDouble(Double value) {
        if (value == null) {
            return "";
        }
        return String.format(Locale.US, "%.2f", value);
    }

    private String formatInteger(Integer value) {
        if (value == null) {
            return "";
        }
        return String.valueOf(value);
    }

    private String formatDisplaySegmentId(String segmentId) {
        if (segmentId == null || segmentId.trim().isEmpty()) {
            return "";
        }

        try {
            int number = Integer.parseInt(segmentId.trim());
            return String.format("SEG%03d", number);
        } catch (NumberFormatException e) {
            return segmentId;
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
