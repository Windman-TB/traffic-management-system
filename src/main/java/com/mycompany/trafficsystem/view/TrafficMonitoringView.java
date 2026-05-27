package com.mycompany.trafficsystem.view;

import com.mycompany.trafficsystem.controller.TrafficAnalyticsController;
import com.mycompany.trafficsystem.model.Traffic;
import com.mycompany.trafficsystem.model.TrafficMonitoringRow;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.FileChooser;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class TrafficMonitoringView {

    private static final String BG = "#EEF3F8";
    private static final String CARD_BG = "#FFFFFF";
    private static final String TEXT_MUTED = "#5E7EA5";
    private static final String PRIMARY = "#123B63";

    private final TrafficAnalyticsController controller;
    private final TableView<TrafficMonitoringRow> table;
    private final ComboBox<String> areaBox;
    private final ComboBox<String> streetBox;
    private final ComboBox<String> statusBox;
    private final TextField minVelocityField;
    private final TextField maxVelocityField;
    private final TextField keywordField;
    private final Label totalSegmentsLabel;
    private final Label avgVelocityLabel;
    private final Label clearLabel;
    private final Label crowdedLabel;
    private final Label congestedLabel;
    private final Label alertLabel;
    private List<TrafficMonitoringRow> currentRows;

    public TrafficMonitoringView() {
        this.controller = new TrafficAnalyticsController();
        this.table = new TableView<>();
        this.areaBox = new ComboBox<>();
        this.streetBox = new ComboBox<>();
        this.statusBox = new ComboBox<>();
        this.minVelocityField = new TextField();
        this.maxVelocityField = new TextField();
        this.keywordField = new TextField();
        this.totalSegmentsLabel = new Label("0");
        this.avgVelocityLabel = new Label("0");
        this.clearLabel = new Label("0");
        this.crowdedLabel = new Label("0");
        this.congestedLabel = new Label("0");
        this.alertLabel = new Label("0");
        this.currentRows = new ArrayList<>();
    }

    public VBox getView() {
        VBox root = new VBox(16);
        root.setPadding(new Insets(22, 28, 28, 28));
        root.setStyle("-fx-background-color: " + BG + ";");

        GridPane cards = createDashboard();
        HBox filters = createFilters();
        TableView<TrafficMonitoringRow> trafficTable = createTable();

        root.getChildren().addAll(cards, filters, trafficTable);
        VBox.setVgrow(trafficTable, Priority.ALWAYS);

        loadData();
        return root;
    }

    private GridPane createDashboard() {
        GridPane cards = new GridPane();
        cards.setHgap(12);
        cards.setVgap(12);

        for (int i = 0; i < 6; i++) {
            ColumnConstraints column = new ColumnConstraints();
            column.setPercentWidth(100.0 / 6);
            cards.getColumnConstraints().add(column);
        }

        cards.add(createStatCard("Đoạn theo dõi", totalSegmentsLabel, "Bản ghi mới nhất"), 0, 0);
        cards.add(createStatCard("Tốc độ TB", avgVelocityLabel, "km/h hiện tại"), 1, 0);
        cards.add(createStatCard("Thông thoáng", clearLabel, "velocity ổn định"), 2, 0);
        cards.add(createStatCard("Đông đúc", crowdedLabel, "cần theo dõi"), 3, 0);
        cards.add(createStatCard("Ùn tắc", congestedLabel, "velocity thấp"), 4, 0);
        cards.add(createStatCard("Cảnh báo", alertLabel, "đoạn ùn tắc"), 5, 0);
        return cards;
    }

    private VBox createStatCard(String titleText, Label valueLabel, String noteText) {
        VBox card = new VBox(8);
        card.setPadding(new Insets(14));
        card.setMinHeight(98);
        card.setStyle(createCardStyle());

        Label title = new Label(titleText);
        title.setTextFill(Color.web(TEXT_MUTED));
        title.setFont(Font.font("Arial", 12));

        valueLabel.setTextFill(Color.web("#111827"));
        valueLabel.setFont(Font.font("Arial", FontWeight.BOLD, 22));

        Label note = new Label(noteText);
        note.setTextFill(Color.web(TEXT_MUTED));
        note.setFont(Font.font("Arial", 11));

        card.getChildren().addAll(title, valueLabel, note);
        return card;
    }

    private HBox createFilters() {
        HBox filters = new HBox(10);
        filters.setAlignment(Pos.CENTER_LEFT);
        filters.setPadding(new Insets(14, 16, 14, 16));
        filters.setStyle(createCardStyle());

        areaBox.setPrefWidth(160);
        areaBox.setPromptText("Khu vực");

        streetBox.setPrefWidth(190);
        streetBox.setPromptText("Tuyến đường");

        statusBox.setItems(FXCollections.observableArrayList("Tất cả", "Thông thoáng", "Đông đúc", "Ùn tắc"));
        statusBox.setValue("Tất cả");
        statusBox.setPrefWidth(130);

        minVelocityField.setPromptText("Tốc độ từ");
        minVelocityField.setPrefWidth(95);

        maxVelocityField.setPromptText("đến");
        maxVelocityField.setPrefWidth(75);

        keywordField.setPromptText("Tìm đoạn/tuyến/khu vực");
        keywordField.setPrefWidth(180);

        Button searchButton = createPrimaryButton("Lọc");
        searchButton.setOnAction(event -> loadData());

        Button resetButton = createSecondaryButton("Làm mới");
        resetButton.setOnAction(event -> resetFilters());

        Button exportButton = createPrimaryButton("Xuất CSV");
        exportButton.setOnAction(event -> exportCsv());

        filters.getChildren().addAll(areaBox, streetBox, statusBox, minVelocityField, maxVelocityField,
                keywordField, searchButton, resetButton, exportButton);
        return filters;
    }

    private TableView<TrafficMonitoringRow> createTable() {
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);
        table.setStyle("-fx-background-color: white; -fx-border-color: transparent;");

        TableColumn<TrafficMonitoringRow, String> segmentCol = new TableColumn<>("Đoạn đường");
        segmentCol.setCellValueFactory(data -> new SimpleStringProperty(nullToEmpty(data.getValue().getSegmentId())));

        TableColumn<TrafficMonitoringRow, String> streetCol = new TableColumn<>("Tuyến đường");
        streetCol.setCellValueFactory(data -> new SimpleStringProperty(nullToEmpty(data.getValue().getStreetName())));

        TableColumn<TrafficMonitoringRow, String> areaCol = new TableColumn<>("Khu vực");
        areaCol.setCellValueFactory(data -> new SimpleStringProperty(nullToEmpty(data.getValue().getAreaName())));

        TableColumn<TrafficMonitoringRow, String> velocityCol = new TableColumn<>("Tốc độ");
        velocityCol.setCellValueFactory(data -> new SimpleStringProperty(controller.formatNumber(data.getValue().getVelocity())));

        TableColumn<TrafficMonitoringRow, String> maxCol = new TableColumn<>("Tối đa");
        maxCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getMaxVelocity() == null
                ? "" : String.valueOf(data.getValue().getMaxVelocity())));

        TableColumn<TrafficMonitoringRow, String> ratioCol = new TableColumn<>("Tỷ lệ");
        ratioCol.setCellValueFactory(data -> new SimpleStringProperty(controller.formatNumber(data.getValue().getVelocityRatio())));

        TableColumn<TrafficMonitoringRow, String> statusCol = new TableColumn<>("Trạng thái");
        statusCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getStatus()));
        statusCol.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String status, boolean empty) {
                super.updateItem(status, empty);
                if (empty || status == null) {
                    setText(null);
                    setStyle("");
                    return;
                }
                setText(status);
                if (status.equals("Ùn tắc")) {
                    setTextFill(Color.web("#DC2626"));
                } else if (status.equals("Đông đúc")) {
                    setTextFill(Color.web("#D97706"));
                } else {
                    setTextFill(Color.web("#059669"));
                }
            }
        });

        TableColumn<TrafficMonitoringRow, String> createdCol = new TableColumn<>("Thời gian tạo");
        createdCol.setCellValueFactory(data -> new SimpleStringProperty(controller.formatDateTime(data.getValue().getCreatedAt())));

        table.getColumns().setAll(java.util.List.of(
                segmentCol,
                streetCol,
                areaCol,
                velocityCol,
                maxCol,
                ratioCol,
                statusCol,
                createdCol
        ));
        table.setRowFactory(tv -> {
            TableRow<TrafficMonitoringRow> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && !row.isEmpty()) {
                    showDetail(row.getItem());
                }
            });
            return row;
        });
        return table;
    }

    private void loadData() {
        currentRows = controller.getCurrentTraffic(
                selectedId(areaBox.getValue()),
                selectedId(streetBox.getValue()),
                statusBox.getValue(),
                parseDouble(minVelocityField.getText()),
                parseDouble(maxVelocityField.getText()),
                keywordField.getText()
        );
        table.setItems(FXCollections.observableArrayList(currentRows));
        updateDashboard();
        loadFilterValues(currentRows);
    }

    private void updateDashboard() {
        int clear = 0;
        int crowded = 0;
        int congested = 0;
        double velocityTotal = 0;
        int velocityCount = 0;

        for (TrafficMonitoringRow row : currentRows) {
            if (row.getVelocity() != null) {
                velocityTotal += row.getVelocity();
                velocityCount++;
            }

            if (row.getStatus().equals("Thông thoáng")) {
                clear++;
            } else if (row.getStatus().equals("Đông đúc")) {
                crowded++;
            } else if (row.getStatus().equals("Ùn tắc")) {
                congested++;
            }
        }

        totalSegmentsLabel.setText(String.valueOf(currentRows.size()));
        avgVelocityLabel.setText(velocityCount == 0 ? "0" : String.format("%.2f", velocityTotal / velocityCount));
        clearLabel.setText(String.valueOf(clear));
        crowdedLabel.setText(String.valueOf(crowded));
        congestedLabel.setText(String.valueOf(congested));
        alertLabel.setText(String.valueOf(congested));
    }

    private void loadFilterValues(List<TrafficMonitoringRow> rows) {
        String selectedArea = areaBox.getValue();
        String selectedStreet = streetBox.getValue();
        Set<String> areas = new LinkedHashSet<>();
        Set<String> streets = new LinkedHashSet<>();
        areas.add("Tất cả");
        streets.add("Tất cả");

        for (TrafficMonitoringRow row : rows) {
            if (row.getAreaId() != null) {
                areas.add(row.getAreaId() + " - " + nullToEmpty(row.getAreaName()));
            }
            if (row.getStreetId() != null) {
                streets.add(row.getStreetId() + " - " + nullToEmpty(row.getStreetName()));
            }
        }

        areaBox.setItems(FXCollections.observableArrayList(areas));
        streetBox.setItems(FXCollections.observableArrayList(streets));
        areaBox.setValue(selectedArea == null ? "Tất cả" : selectedArea);
        streetBox.setValue(selectedStreet == null ? "Tất cả" : selectedStreet);
    }

    private void showDetail(TrafficMonitoringRow row) {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Chi tiết đoạn đường");
        dialog.setHeaderText("Đoạn đường: " + row.getSegmentId());
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);

        VBox content = new VBox(14);
        content.setPadding(new Insets(12));
        content.setPrefWidth(900);

        Label detailLabel = new Label(
                "Tuyến đường: " + nullToEmpty(row.getStreetName()) + "\n" +
                "Khu vực: " + nullToEmpty(row.getAreaName()) + "\n" +
                "Điểm đầu: " + nullToEmpty(row.getStartNodeId()) + " (" + formatCoordinate(row.getStartLatitude()) + ", " + formatCoordinate(row.getStartLongitude()) + ")\n" +
                "Điểm cuối: " + nullToEmpty(row.getEndNodeId()) + " (" + formatCoordinate(row.getEndLatitude()) + ", " + formatCoordinate(row.getEndLongitude()) + ")\n" +
                "Chiều dài: " + controller.formatNumber(row.getSegmentLength()) + "\n" +
                "Tốc độ hiện tại: " + controller.formatNumber(row.getVelocity()) + "\n" +
                "Tốc độ tối đa: " + (row.getMaxVelocity() == null ? "" : row.getMaxVelocity()) + "\n" +
                "Trạng thái: " + row.getStatus() + "\n" +
                "Thời gian tạo: " + controller.formatDateTime(row.getCreatedAt())
        );
        detailLabel.setWrapText(true);
        detailLabel.setMaxWidth(Double.MAX_VALUE);

        content.getChildren().add(detailLabel);
        content.getChildren().add(createHistoryChart(row.getSegmentId()));
        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().setMinSize(900, 650);
        dialog.getDialogPane().setPrefSize(960, 720);
        dialog.showAndWait();
    }

    private LineChart<Number, Number> createHistoryChart(String segmentId) {
        NumberAxis xAxis = new NumberAxis();
        NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel("Lần ghi");
        yAxis.setLabel("Velocity");

        LineChart<Number, Number> chart = new LineChart<>(xAxis, yAxis);
        chart.setLegendVisible(false);
        chart.setAnimated(false);
        chart.setMinHeight(260);

        XYChart.Series<Number, Number> series = new XYChart.Series<>();
        List<Traffic> history = controller.getSegmentHistory(segmentId, null, null);
        int index = 1;
        for (Traffic traffic : history) {
            series.getData().add(new XYChart.Data<>(index++, traffic.getVelocity()));
        }
        chart.getData().add(series);
        return chart;
    }

    private void resetFilters() {
        areaBox.setValue("Tất cả");
        streetBox.setValue("Tất cả");
        statusBox.setValue("Tất cả");
        minVelocityField.clear();
        maxVelocityField.clear();
        keywordField.clear();
        loadData();
    }

    private void exportCsv() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Xuất báo cáo giám sát");
        fileChooser.setInitialFileName("traffic-monitoring.csv");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV", "*.csv"));

        Scene scene = table.getScene();
        File file = fileChooser.showSaveDialog(scene == null ? null : scene.getWindow());
        if (file != null && controller.exportMonitoringCsv(file, currentRows)) {
            new Alert(Alert.AlertType.INFORMATION, "Xuất CSV thành công.").showAndWait();
        }
    }

    private Button createPrimaryButton(String text) {
        Button button = new Button(text);
        button.setStyle("-fx-background-color: " + PRIMARY + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 7; -fx-padding: 8 14;");
        return button;
    }

    private Button createSecondaryButton(String text) {
        Button button = new Button(text);
        button.setStyle("-fx-background-color: white; -fx-text-fill: " + PRIMARY + "; -fx-border-color: #CBD5E1; -fx-border-radius: 7; -fx-background-radius: 7; -fx-padding: 8 14;");
        return button;
    }

    private String createCardStyle() {
        return "-fx-background-color: " + CARD_BG + "; -fx-background-radius: 12; -fx-border-color: #DDE5EF; -fx-border-radius: 12; -fx-effect: dropshadow(gaussian, rgba(15, 23, 42, 0.12), 8, 0.2, 0, 2);";
    }

    private String selectedId(String value) {
        if (value == null || value.trim().isEmpty() || value.equals("Tất cả")) {
            return null;
        }

        int separator = value.indexOf(" - ");
        return separator >= 0 ? value.substring(0, separator).trim() : value.trim();
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

    private String formatCoordinate(Double value) {
        return value == null ? "" : String.format("%.6f", value);
    }

    private String nullToEmpty(String value) {
        return value == null ? "" : value;
    }
}
