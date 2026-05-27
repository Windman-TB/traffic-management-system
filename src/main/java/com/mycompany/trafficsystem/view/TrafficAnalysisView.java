package com.mycompany.trafficsystem.view;

import com.mycompany.trafficsystem.controller.TrafficAnalyticsController;
import com.mycompany.trafficsystem.model.TrafficAnalysisRow;
import com.mycompany.trafficsystem.model.TrafficAnalysisSummary;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
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
import java.util.List;

public class TrafficAnalysisView {

    private static final String BG = "#EEF3F8";
    private static final String CARD_BG = "#FFFFFF";
    private static final String TEXT_MUTED = "#5E7EA5";
    private static final String PRIMARY = "#123B63";

    private final TrafficAnalyticsController controller;
    private final DatePicker fromDatePicker;
    private final DatePicker toDatePicker;
    private final ComboBox<String> modeBox;
    private final TableView<TrafficAnalysisRow> table;
    private final BarChart<String, Number> chart;
    private final Label totalRecordsLabel;
    private final Label totalSegmentsLabel;
    private final Label avgVelocityLabel;
    private final Label minVelocityLabel;
    private final Label maxVelocityLabel;
    private final Label congestionRateLabel;
    private List<TrafficAnalysisRow> currentRows;

    public TrafficAnalysisView() {
        this.controller = new TrafficAnalyticsController();
        this.fromDatePicker = new DatePicker();
        this.toDatePicker = new DatePicker();
        this.modeBox = new ComboBox<>();
        this.table = new TableView<>();
        this.chart = new BarChart<>(new CategoryAxis(), new NumberAxis());
        this.totalRecordsLabel = new Label("0");
        this.totalSegmentsLabel = new Label("0");
        this.avgVelocityLabel = new Label("0");
        this.minVelocityLabel = new Label("0");
        this.maxVelocityLabel = new Label("0");
        this.congestionRateLabel = new Label("0%");
        this.currentRows = new ArrayList<>();
    }

    public VBox getView() {
        VBox root = new VBox(16);
        root.setPadding(new Insets(22, 28, 28, 28));
        root.setStyle("-fx-background-color: " + BG + ";");

        GridPane dashboard = createDashboard();
        HBox filters = createFilters();
        HBox body = new HBox(16);
        VBox chartCard = createChartCard();
        VBox tableCard = createTableCard();

        body.getChildren().addAll(chartCard, tableCard);
        HBox.setHgrow(chartCard, Priority.ALWAYS);
        HBox.setHgrow(tableCard, Priority.ALWAYS);
        root.getChildren().addAll(dashboard, filters, body);
        VBox.setVgrow(body, Priority.ALWAYS);

        loadData();
        return root;
    }

    private GridPane createDashboard() {
        GridPane cards = new GridPane();
        cards.setHgap(12);

        for (int i = 0; i < 6; i++) {
            ColumnConstraints column = new ColumnConstraints();
            column.setPercentWidth(100.0 / 6);
            cards.getColumnConstraints().add(column);
        }

        cards.add(createStatCard("Tổng bản ghi", totalRecordsLabel, "toàn bộ lịch sử"), 0, 0);
        cards.add(createStatCard("Tổng đoạn", totalSegmentsLabel, "có dữ liệu"), 1, 0);
        cards.add(createStatCard("Tốc độ TB", avgVelocityLabel, "km/h"), 2, 0);
        cards.add(createStatCard("Thấp nhất", minVelocityLabel, "km/h"), 3, 0);
        cards.add(createStatCard("Cao nhất", maxVelocityLabel, "km/h"), 4, 0);
        cards.add(createStatCard("Tỷ lệ ùn tắc", congestionRateLabel, "lịch sử"), 5, 0);
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

        fromDatePicker.setPromptText("Từ ngày");
        fromDatePicker.setPrefWidth(130);

        toDatePicker.setPromptText("Đến ngày");
        toDatePicker.setPrefWidth(130);

        modeBox.setItems(FXCollections.observableArrayList(
                "Theo giờ", "Theo ngày", "Theo khu vực", "Theo tuyến đường", "Theo loại đường", "Theo cấp đường"
        ));
        modeBox.setValue("Theo khu vực");
        modeBox.setPrefWidth(170);

        Button analyzeButton = createPrimaryButton("Phân tích");
        analyzeButton.setOnAction(event -> loadData());

        Button resetButton = createSecondaryButton("Làm mới");
        resetButton.setOnAction(event -> {
            fromDatePicker.setValue(null);
            toDatePicker.setValue(null);
            modeBox.setValue("Theo khu vực");
            loadData();
        });

        Button exportButton = createPrimaryButton("Xuất CSV");
        exportButton.setOnAction(event -> exportCsv());

        filters.getChildren().addAll(fromDatePicker, toDatePicker, modeBox, analyzeButton, resetButton, exportButton);
        return filters;
    }

    private VBox createChartCard() {
        VBox card = new VBox(10);
        card.setPadding(new Insets(14));
        card.setStyle(createCardStyle());

        Label title = new Label("Biểu đồ tốc độ trung bình");
        title.setTextFill(Color.web("#111827"));
        title.setFont(Font.font("Arial", FontWeight.BOLD, 14));

        chart.setAnimated(false);
        chart.setLegendVisible(false);
        chart.setMinHeight(380);

        card.getChildren().addAll(title, chart);
        VBox.setVgrow(chart, Priority.ALWAYS);
        return card;
    }

    private VBox createTableCard() {
        VBox card = new VBox(10);
        card.setPadding(new Insets(14));
        card.setStyle(createCardStyle());

        Label title = new Label("Bảng kết quả phân tích");
        title.setTextFill(Color.web("#111827"));
        title.setFont(Font.font("Arial", FontWeight.BOLD, 14));

        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<TrafficAnalysisRow, String> groupCol = new TableColumn<>("Nhóm");
        groupCol.setCellValueFactory(data -> new SimpleStringProperty(nullToEmpty(data.getValue().getGroupName())));

        TableColumn<TrafficAnalysisRow, String> recordCol = new TableColumn<>("Bản ghi");
        recordCol.setCellValueFactory(data -> new SimpleStringProperty(String.valueOf(data.getValue().getRecordCount())));

        TableColumn<TrafficAnalysisRow, String> segmentCol = new TableColumn<>("Đoạn");
        segmentCol.setCellValueFactory(data -> new SimpleStringProperty(String.valueOf(data.getValue().getSegmentCount())));

        TableColumn<TrafficAnalysisRow, String> avgCol = new TableColumn<>("TB");
        avgCol.setCellValueFactory(data -> new SimpleStringProperty(controller.formatNumber(data.getValue().getAverageVelocity())));

        TableColumn<TrafficAnalysisRow, String> minCol = new TableColumn<>("Min");
        minCol.setCellValueFactory(data -> new SimpleStringProperty(controller.formatNumber(data.getValue().getMinVelocity())));

        TableColumn<TrafficAnalysisRow, String> maxCol = new TableColumn<>("Max");
        maxCol.setCellValueFactory(data -> new SimpleStringProperty(controller.formatNumber(data.getValue().getMaxVelocity())));

        TableColumn<TrafficAnalysisRow, String> congestionCol = new TableColumn<>("Ùn tắc");
        congestionCol.setCellValueFactory(data -> new SimpleStringProperty(String.valueOf(data.getValue().getCongestionCount())));

        TableColumn<TrafficAnalysisRow, String> rateCol = new TableColumn<>("Tỷ lệ");
        rateCol.setCellValueFactory(data -> new SimpleStringProperty(controller.formatPercent(data.getValue().getCongestionRate())));

        TableColumn<TrafficAnalysisRow, String> ratioCol = new TableColumn<>("Velocity/Max");
        ratioCol.setCellValueFactory(data -> new SimpleStringProperty(controller.formatNumber(data.getValue().getAverageVelocityRatio())));

        table.getColumns().setAll(groupCol, recordCol, segmentCol, avgCol, minCol, maxCol, congestionCol, rateCol, ratioCol);

        card.getChildren().addAll(title, table);
        VBox.setVgrow(table, Priority.ALWAYS);
        return card;
    }

    private void loadData() {
        TrafficAnalysisSummary summary = controller.getSummary(fromDatePicker.getValue(), toDatePicker.getValue());
        currentRows = controller.analyze(modeBox.getValue(), fromDatePicker.getValue(), toDatePicker.getValue());

        totalRecordsLabel.setText(String.valueOf(summary.getTotalRecords()));
        totalSegmentsLabel.setText(String.valueOf(summary.getTotalSegments()));
        avgVelocityLabel.setText(controller.formatNumber(summary.getAverageVelocity()));
        minVelocityLabel.setText(controller.formatNumber(summary.getMinVelocity()));
        maxVelocityLabel.setText(controller.formatNumber(summary.getMaxVelocity()));
        congestionRateLabel.setText(controller.formatPercent(summary.getCongestionRate()));

        table.setItems(FXCollections.observableArrayList(currentRows));
        loadChart();
    }

    private void loadChart() {
        chart.getData().clear();
        XYChart.Series<String, Number> series = new XYChart.Series<>();

        int count = 0;
        for (TrafficAnalysisRow row : currentRows) {
            if (count >= 12) {
                break;
            }
            series.getData().add(new XYChart.Data<>(shortLabel(row.getGroupName()), valueOrZero(row.getAverageVelocity())));
            count++;
        }

        chart.getData().add(series);
    }

    private void exportCsv() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Xuất báo cáo phân tích");
        fileChooser.setInitialFileName("traffic-analysis.csv");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV", "*.csv"));

        Scene scene = table.getScene();
        File file = fileChooser.showSaveDialog(scene == null ? null : scene.getWindow());
        if (file != null && controller.exportAnalysisCsv(file, currentRows)) {
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

    private Number valueOrZero(Double value) {
        return value == null ? 0 : value;
    }

    private String shortLabel(String value) {
        if (value == null) {
            return "";
        }
        return value.length() > 18 ? value.substring(0, 18) : value;
    }

    private String nullToEmpty(String value) {
        return value == null ? "" : value;
    }
}
