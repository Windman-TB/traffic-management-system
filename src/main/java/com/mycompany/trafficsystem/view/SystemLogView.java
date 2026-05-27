package com.mycompany.trafficsystem.view;

import com.mycompany.trafficsystem.controller.SystemLogController;
import com.mycompany.trafficsystem.model.SystemLog;
import com.mycompany.trafficsystem.model.SystemLogSummary;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.FileChooser;

import java.io.File;
import java.util.List;
import java.util.Map;

public class SystemLogView {

    private static final String BG = "#EEF3F8";
    private static final String CARD_BG = "#FFFFFF";
    private static final String TEXT_MUTED = "#5E7EA5";
    private static final String PRIMARY = "#123B63";
    private static final String BLUE = "#2563EB";
    private static final String GREEN = "#10B981";
    private static final String RED = "#EF4444";
    private static final String ORANGE = "#F59E0B";

    private final SystemLogController systemLogController;
    private final TableView<SystemLog> table;
    private final TextField keywordField;
    private final TextField accountIdField;
    private final TextField behaviourField;
    private final ComboBox<String> targetTableBox;
    private final ComboBox<String> statusBox;
    private final DatePicker fromDatePicker;
    private final DatePicker toDatePicker;
    private final Label totalValueLabel;
    private final Label successValueLabel;
    private final Label failedValueLabel;
    private final Label todayValueLabel;
    private final BarChart<String, Number> tableBarChart;
    private final PieChart statusPieChart;

    private List<SystemLog> currentLogs;

    public SystemLogView() {
        this.systemLogController = new SystemLogController();
        this.table = new TableView<>();
        this.keywordField = new TextField();
        this.accountIdField = new TextField();
        this.behaviourField = new TextField();
        this.targetTableBox = new ComboBox<>();
        this.statusBox = new ComboBox<>();
        this.fromDatePicker = new DatePicker();
        this.toDatePicker = new DatePicker();
        this.totalValueLabel = new Label("0");
        this.successValueLabel = new Label("0");
        this.failedValueLabel = new Label("0");
        this.todayValueLabel = new Label("0");

        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();
        this.tableBarChart = new BarChart<>(xAxis, yAxis);
        this.statusPieChart = new PieChart();
    }

    public VBox getView() {
        VBox root = new VBox(18);
        root.setPadding(new Insets(22, 28, 28, 28));
        root.setStyle("-fx-background-color: " + BG + ";");

        root.getChildren().addAll(
                createSummaryCards(),
                createChartRow(),
                createMainCard()
        );

        VBox.setVgrow(root.getChildren().get(2), Priority.ALWAYS);

        loadSummary();
        loadData();

        return root;
    }

    private GridPane createSummaryCards() {
        GridPane cards = new GridPane();
        cards.setHgap(16);
        cards.setVgap(16);

        for (int i = 0; i < 4; i++) {
            ColumnConstraints c = new ColumnConstraints();
            c.setPercentWidth(25);
            cards.getColumnConstraints().add(c);
        }

        cards.add(createStatCard("Tổng nhật ký", totalValueLabel, "Toàn bộ thao tác đã ghi", "📋", BLUE), 0, 0);
        cards.add(createStatCard("Thành công", successValueLabel, "LOG_STATUS = SUCCESS", "✅", GREEN), 1, 0);
        cards.add(createStatCard("Thất bại", failedValueLabel, "LOG_STATUS = FAILED", "⚠", RED), 2, 0);
        cards.add(createStatCard("Hôm nay", todayValueLabel, "Phát sinh trong ngày", "📅", ORANGE), 3, 0);

        return cards;
    }

    private VBox createStatCard(String label, Label valueLabel, String note, String icon, String color) {
        VBox card = new VBox(10);
        card.setPadding(new Insets(18));
        card.setPrefHeight(115);
        card.setStyle(createCardStyle());

        HBox top = new HBox();
        top.setAlignment(Pos.CENTER_LEFT);

        Label title = new Label(label);
        title.setTextFill(Color.web(TEXT_MUTED));
        title.setFont(Font.font("Arial", 12));

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label iconLabel = new Label(icon);
        iconLabel.setAlignment(Pos.CENTER);
        iconLabel.setPrefSize(34, 34);
        iconLabel.setStyle(
                "-fx-background-color: derive(" + color + ", 85%);" +
                "-fx-background-radius: 9;" +
                "-fx-text-fill: " + color + ";" +
                "-fx-font-size: 14;"
        );

        top.getChildren().addAll(title, spacer, iconLabel);

        valueLabel.setTextFill(Color.web("#030712"));
        valueLabel.setFont(Font.font("Arial", FontWeight.BOLD, 25));

        Label noteLabel = new Label(note);
        noteLabel.setTextFill(Color.web(TEXT_MUTED));
        noteLabel.setFont(Font.font("Arial", 12));

        card.getChildren().addAll(top, valueLabel, noteLabel);
        return card;
    }

    private HBox createChartRow() {
        HBox row = new HBox(16);
        row.getChildren().addAll(createBarChartCard(), createPieChartCard());
        HBox.setHgrow(row.getChildren().get(0), Priority.ALWAYS);
        return row;
    }

    private VBox createBarChartCard() {
        VBox card = new VBox(8);
        card.setPrefHeight(260);
        card.setStyle(createCardStyle());

        Label title = new Label("Thống kê nhật ký theo bảng dữ liệu");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        title.setPadding(new Insets(14, 16, 6, 16));

        tableBarChart.setLegendVisible(false);
        tableBarChart.setAnimated(false);
        tableBarChart.setMinHeight(210);
        tableBarChart.setCategoryGap(12);
        tableBarChart.setBarGap(4);

        card.getChildren().addAll(title, new Separator(), tableBarChart);
        VBox.setVgrow(tableBarChart, Priority.ALWAYS);
        return card;
    }

    private VBox createPieChartCard() {
        VBox card = new VBox(8);
        card.setPrefWidth(360);
        card.setPrefHeight(260);
        card.setStyle(createCardStyle());

        Label title = new Label("Tỷ lệ trạng thái ghi log");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        title.setPadding(new Insets(14, 16, 6, 16));

        statusPieChart.setLegendVisible(true);
        statusPieChart.setLabelsVisible(true);
        statusPieChart.setMinHeight(210);

        card.getChildren().addAll(title, new Separator(), statusPieChart);
        VBox.setVgrow(statusPieChart, Priority.ALWAYS);
        return card;
    }

    private VBox createMainCard() {
        VBox card = new VBox();
        card.setStyle(createCardStyle());

        HBox toolbar = createToolbar();
        TableView<SystemLog> logTable = createTable();

        card.getChildren().addAll(toolbar, logTable);
        VBox.setVgrow(logTable, Priority.ALWAYS);
        return card;
    }

    private HBox createToolbar() {
        HBox toolbar = new HBox(10);
        toolbar.setAlignment(Pos.CENTER_LEFT);
        toolbar.setPadding(new Insets(14, 16, 14, 16));

        keywordField.setPromptText("Tìm mã log, hành vi, bảng...");
        keywordField.setPrefWidth(210);

        accountIdField.setPromptText("Mã tài khoản");
        accountIdField.setPrefWidth(115);

        behaviourField.setPromptText("Hành vi");
        behaviourField.setPrefWidth(140);

        targetTableBox.setItems(FXCollections.observableArrayList(
                "Tất cả", "ACCOUNT", "EMPLOYEE", "AREA", "STREET", "SEGMENT", "NODE", "TRAFFIC", "PASSWORD_RESET"
        ));
        targetTableBox.setValue("Tất cả");
        targetTableBox.setPrefWidth(125);

        statusBox.setItems(FXCollections.observableArrayList("Tất cả", "SUCCESS", "FAILED"));
        statusBox.setValue("Tất cả");
        statusBox.setPrefWidth(105);

        fromDatePicker.setPromptText("Từ ngày");
        fromDatePicker.setPrefWidth(120);

        toDatePicker.setPromptText("Đến ngày");
        toDatePicker.setPrefWidth(120);

        Button searchBtn = createPrimaryButton("Tra cứu");
        searchBtn.setOnAction(e -> searchData());

        Button resetBtn = createSecondaryButton("Làm mới");
        resetBtn.setOnAction(e -> resetFilters());

        Button detailBtn = createSecondaryButton("Chi tiết");
        detailBtn.setOnAction(e -> showSelectedLogDetail());

        Button exportBtn = createPrimaryButton("Xuất CSV");
        exportBtn.setOnAction(e -> exportCsv());

        toolbar.getChildren().addAll(
                keywordField,
                accountIdField,
                behaviourField,
                targetTableBox,
                statusBox,
                fromDatePicker,
                toDatePicker,
                searchBtn,
                resetBtn,
                detailBtn,
                exportBtn
        );

        return toolbar;
    }

    private TableView<SystemLog> createTable() {
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);
        table.setPrefHeight(360);

        TableColumn<SystemLog, String> logIdCol = new TableColumn<>("Mã log");
        logIdCol.setCellValueFactory(data -> new SimpleStringProperty(nullToEmpty(data.getValue().getLogId())));
        logIdCol.setPrefWidth(90);

        TableColumn<SystemLog, String> accountIdCol = new TableColumn<>("Tài khoản");
        accountIdCol.setCellValueFactory(data -> new SimpleStringProperty(nullToEmpty(data.getValue().getAccountId())));
        accountIdCol.setPrefWidth(95);

        TableColumn<SystemLog, String> behaviourCol = new TableColumn<>("Hành vi");
        behaviourCol.setCellValueFactory(data -> new SimpleStringProperty(nullToEmpty(data.getValue().getBehaviour())));
        behaviourCol.setPrefWidth(190);

        TableColumn<SystemLog, String> tableCol = new TableColumn<>("Bảng tác động");
        tableCol.setCellValueFactory(data -> new SimpleStringProperty(nullToEmpty(data.getValue().getTargetTable())));
        tableCol.setPrefWidth(120);

        TableColumn<SystemLog, String> targetIdCol = new TableColumn<>("Mã bản ghi");
        targetIdCol.setCellValueFactory(data -> new SimpleStringProperty(nullToEmpty(data.getValue().getTargetId())));
        targetIdCol.setPrefWidth(110);

        TableColumn<SystemLog, String> statusCol = new TableColumn<>("Trạng thái");
        statusCol.setCellValueFactory(data -> new SimpleStringProperty(nullToEmpty(data.getValue().getLogStatus())));
        statusCol.setPrefWidth(100);

        TableColumn<SystemLog, String> createdAtCol = new TableColumn<>("Thời điểm");
        createdAtCol.setCellValueFactory(data -> new SimpleStringProperty(systemLogController.formatDateTime(data.getValue())));
        createdAtCol.setPrefWidth(150);

        table.getColumns().setAll(java.util.List.of(
                logIdCol,
                accountIdCol,
                behaviourCol,
                tableCol,
                targetIdCol,
                statusCol,
                createdAtCol
        ));

        table.setRowFactory(tv -> {
            TableRow<SystemLog> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && !row.isEmpty()) {
                    showLogDetail(row.getItem());
                }
            });
            return row;
        });

        return table;
    }

    private void loadData() {
        currentLogs = systemLogController.getAllLogs();
        table.setItems(FXCollections.observableArrayList(currentLogs));
    }

    private void searchData() {
        currentLogs = systemLogController.searchLogs(
                keywordField.getText(),
                accountIdField.getText(),
                targetTableBox.getValue(),
                behaviourField.getText(),
                statusBox.getValue(),
                fromDatePicker.getValue(),
                toDatePicker.getValue()
        );

        table.setItems(FXCollections.observableArrayList(currentLogs));
    }

    private void resetFilters() {
        keywordField.clear();
        accountIdField.clear();
        behaviourField.clear();
        targetTableBox.setValue("Tất cả");
        statusBox.setValue("Tất cả");
        fromDatePicker.setValue(null);
        toDatePicker.setValue(null);
        loadSummary();
        loadData();
    }

    private void loadSummary() {
        SystemLogSummary summary = systemLogController.getSummary();

        totalValueLabel.setText(String.valueOf(summary.getTotalLogs()));
        successValueLabel.setText(String.valueOf(summary.getSuccessLogs()));
        failedValueLabel.setText(String.valueOf(summary.getFailedLogs()));
        todayValueLabel.setText(String.valueOf(summary.getTodayLogs()));

        loadTableBarChart(summary.getLogsByTable());
        loadStatusPieChart(summary.getSuccessLogs(), summary.getFailedLogs());
    }

    private void loadTableBarChart(Map<String, Integer> logsByTable) {
        tableBarChart.getData().clear();

        XYChart.Series<String, Number> series = new XYChart.Series<>();

        if (logsByTable != null) {
            logsByTable.forEach((tableName, total) -> series.getData().add(new XYChart.Data<>(tableName, total)));
        }

        tableBarChart.getData().add(series);
    }

    private void loadStatusPieChart(int success, int failed) {
        statusPieChart.setData(FXCollections.observableArrayList(
                new PieChart.Data("SUCCESS", success),
                new PieChart.Data("FAILED", failed)
        ));
    }

    private void showSelectedLogDetail() {
        SystemLog selected = table.getSelectionModel().getSelectedItem();

        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "Chưa chọn nhật ký", "Vui lòng chọn một dòng nhật ký để xem chi tiết.");
            return;
        }

        showLogDetail(selected);
    }

    private void showLogDetail(SystemLog log) {
        if (log == null) {
            return;
        }

        SystemLog fullLog = systemLogController.getLogById(log.getLogId());
        if (fullLog == null) {
            fullLog = log;
        }

        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Chi tiết nhật ký hệ thống");
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);

        VBox content = new VBox(12);
        content.setPadding(new Insets(14));
        content.setPrefWidth(720);

        GridPane infoGrid = new GridPane();
        infoGrid.setHgap(12);
        infoGrid.setVgap(10);

        addInfoRow(infoGrid, 0, "Mã log", fullLog.getLogId());
        addInfoRow(infoGrid, 1, "Mã tài khoản", fullLog.getAccountId());
        addInfoRow(infoGrid, 2, "Hành vi", fullLog.getBehaviour());
        addInfoRow(infoGrid, 3, "Bảng bị tác động", fullLog.getTargetTable());
        addInfoRow(infoGrid, 4, "Mã bản ghi", fullLog.getTargetId());
        addInfoRow(infoGrid, 5, "Trạng thái", fullLog.getLogStatus());
        addInfoRow(infoGrid, 6, "Thời điểm", systemLogController.formatDateTime(fullLog));

        HBox valueBox = new HBox(12);

        VBox oldBox = new VBox(6);
        Label oldLabel = new Label("Dữ liệu trước khi chỉnh sửa");
        oldLabel.setFont(Font.font("Arial", FontWeight.BOLD, 13));
        TextArea oldArea = new TextArea(nullToEmpty(fullLog.getOldValue()));
        oldArea.setEditable(false);
        oldArea.setWrapText(true);
        oldArea.setPrefHeight(180);
        oldBox.getChildren().addAll(oldLabel, oldArea);

        VBox newBox = new VBox(6);
        Label newLabel = new Label("Dữ liệu sau khi chỉnh sửa");
        newLabel.setFont(Font.font("Arial", FontWeight.BOLD, 13));
        TextArea newArea = new TextArea(nullToEmpty(fullLog.getNewValue()));
        newArea.setEditable(false);
        newArea.setWrapText(true);
        newArea.setPrefHeight(180);
        newBox.getChildren().addAll(newLabel, newArea);

        valueBox.getChildren().addAll(oldBox, newBox);
        HBox.setHgrow(oldBox, Priority.ALWAYS);
        HBox.setHgrow(newBox, Priority.ALWAYS);

        content.getChildren().addAll(infoGrid, new Separator(), valueBox);

        ScrollPane scrollPane = new ScrollPane(content);
        scrollPane.setFitToWidth(true);
        dialog.getDialogPane().setContent(scrollPane);
        dialog.showAndWait();
    }

    private void addInfoRow(GridPane grid, int row, String label, String value) {
        Label labelNode = new Label(label + ":");
        labelNode.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        labelNode.setTextFill(Color.web("#374151"));

        Label valueNode = new Label(nullToEmpty(value));
        valueNode.setTextFill(Color.web("#111827"));
        valueNode.setWrapText(true);

        grid.add(labelNode, 0, row);
        grid.add(valueNode, 1, row);
    }

    private void exportCsv() {
        if (currentLogs == null || currentLogs.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Không có dữ liệu", "Không có nhật ký nào để xuất file.");
            return;
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Xuất nhật ký hệ thống");
        fileChooser.setInitialFileName("system_log_export.csv");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV files (*.csv)", "*.csv"));

        File file = fileChooser.showSaveDialog(table.getScene().getWindow());
        if (file == null) {
            return;
        }

        boolean success = systemLogController.exportLogsToCsv(currentLogs, file);

        if (success) {
            showAlert(Alert.AlertType.INFORMATION, "Xuất file thành công", "Đã xuất nhật ký hệ thống ra file:\n" + file.getAbsolutePath());
        } else {
            showAlert(Alert.AlertType.ERROR, "Xuất file thất bại", "Không thể xuất file nhật ký hệ thống.");
        }
    }

    private Button createPrimaryButton(String text) {
        Button button = new Button(text);
        button.setStyle(
                "-fx-background-color: " + PRIMARY + ";" +
                "-fx-text-fill: white;" +
                "-fx-background-radius: 7;" +
                "-fx-font-weight: bold;" +
                "-fx-padding: 8 13;"
        );
        return button;
    }

    private Button createSecondaryButton(String text) {
        Button button = new Button(text);
        button.setStyle(
                "-fx-background-color: #FFFFFF;" +
                "-fx-text-fill: " + PRIMARY + ";" +
                "-fx-border-color: #CBD5E1;" +
                "-fx-border-radius: 7;" +
                "-fx-background-radius: 7;" +
                "-fx-font-weight: bold;" +
                "-fx-padding: 8 13;"
        );
        return button;
    }

    private String createCardStyle() {
        return "-fx-background-color: " + CARD_BG + ";" +
                "-fx-background-radius: 12;" +
                "-fx-border-color: #DDE5EF;" +
                "-fx-border-radius: 12;" +
                "-fx-effect: dropshadow(gaussian, rgba(15, 23, 42, 0.12), 8, 0.2, 0, 2);";
    }

    private String nullToEmpty(String value) {
        return value == null ? "" : value;
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
