/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.trafficsystem.view;


/**
 *
 * @author engineer
 */
import com.mycompany.trafficsystem.model.Account;
import com.mycompany.trafficsystem.util.Session;
import com.mycompany.trafficsystem.controller.BaseController;
import com.mycompany.trafficsystem.controller.AccountInfoController;

import javafx.scene.Node;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

public abstract class BaseView {

    protected static final String PRIMARY = "#123B63";
    protected static final String PRIMARY_LIGHT = "#2D557F";
    protected static final String BG = "#EEF3F8";
    protected static final String CARD_BG = "#FFFFFF";
    protected static final String TEXT_MUTED = "#5E7EA5";
    protected static final String ORANGE = "#F59E0B";
    protected static final String BLUE = "#2563EB";
    protected static final String GREEN = "#10B981";
    protected static final String RED = "#EF4444";
    
    private BorderPane root;
    
    private VBox menuBox;

    private String activeMenu = "Tổng quan"; 
    
    protected final BaseController baseController = new BaseController();   

    protected Account getCurrentAccount() {
        return Session.getCurrentAccount();
    }

    protected String getUserName() {
        Account account = getCurrentAccount();

        if (account == null || account.getUsername() == null || account.getUsername().trim().isEmpty()) {
            return "Chưa đăng nhập";
        }

        return account.getUsername();
    }

    protected String getRoleName() {
        String role = Session.getCurrentRole();

        if (role == null || role.trim().isEmpty()) {
            return "Không xác định";
        }

        return role;
    }

    protected String getAccountId() {
        Account account = getCurrentAccount();

        if (account == null || account.getAccountId() == null) {
            return "N/A";
        }

        return account.getAccountId();
    }

    protected String getEmployeeId() {
        Account account = getCurrentAccount();

        if (account == null || account.getEmployeeId() == null) {
            return "N/A";
        }

        return account.getEmployeeId();
    }

    protected String getAccountStatus() {
        Account account = getCurrentAccount();

        if (account == null || account.getStatus() == null) {
            return "N/A";
        }

        return account.getStatus();
    }

    protected String getAvatarText() {
        String username = getUserName();

        if (username == null || username.trim().isEmpty() || username.equals("Chưa đăng nhập")) {
            return "?";
        }

        return username.substring(0, 1).toUpperCase();
    }

    protected String getCurrentDateTimeText() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        return LocalDateTime.now().format(formatter);
    }

    protected abstract String[] getMenuItems();
    
    protected List<DashboardCardInfo> getOverviewCards() {
        return List.of(
                new DashboardCardInfo(
                        "Tổng khu vực",
                        String.valueOf(baseController.getTotalAreas()),
                        "Dữ liệu từ bảng AREA",
                        "📍",
                        BLUE
                ),
                new DashboardCardInfo(
                        "Tổng tuyến đường",
                        String.valueOf(baseController.getTotalStreets()),
                        "Dữ liệu từ bảng STREET",
                        "🛣",
                        GREEN
                ),
                new DashboardCardInfo(
                        "Tổng đoạn đường",
                        String.valueOf(baseController.getTotalRoadSegments()),
                        "Dữ liệu từ bảng SEGMENT",
                        "🔗",
                        ORANGE
                ),
                new DashboardCardInfo(
                        "Tổng nút giao",
                        String.valueOf(baseController.getTotalNodes()),
                        "Dữ liệu từ bảng NODE",
                        "🚦",
                        "#6366F1"
                )
        );
    }

    protected String getOverviewIntro() {
        return "Theo dõi nhanh các nhóm dữ liệu nền tảng của hệ thống giao thông.";
    }

    protected List<String[]> getOverviewFocusItems() {
        return List.of(
                new String[]{"📍", "Khu vực", "Quản lý phạm vi địa lý và khu vực vận hành."},
                new String[]{"🛣", "Tuyến và đoạn đường", "Theo dõi cấu trúc đường, đoạn đường và điểm kết nối."},
                new String[]{"🚦", "Nút giao", "Kiểm soát dữ liệu các nút giao trong mạng lưới."}
        );
    }

    protected List<String[]> getOverviewActionItems() {
        return List.of(
                new String[]{"Kiểm tra dữ liệu tổng", "Đối chiếu số lượng khu vực, tuyến, đoạn và nút giao."},
                new String[]{"Cập nhật hồ sơ", "Vào từng mục quản lý để thêm mới hoặc chỉnh sửa dữ liệu."},
                new String[]{"Theo dõi vận hành", "Quan sát trạng thái chung trước khi xử lý chi tiết."}
        );
    }

    protected List<String> getOverviewWorkflowSteps() {
        return List.of(
                "Chọn nhóm dữ liệu cần làm việc từ thanh menu.",
                "Tìm kiếm hoặc lọc bản ghi cần kiểm tra.",
                "Cập nhật thông tin và kiểm tra lại trên màn tổng quan."
        );
    }

    public void show(Stage stage) {
        root = new BorderPane();
        root.setStyle("-fx-background-color: " + BG + ";");

        root.setLeft(createSidebar(stage));
        root.setTop(createHeader());
        root.setCenter(createContent());

        Scene scene = new Scene(root, 1280, 720);
        stage.setTitle("TP.HCM - " + getRoleName());
        stage.setScene(scene);
        stage.show();
        stage.centerOnScreen();
    }

    private VBox createSidebar(Stage stage) {
        VBox sidebar = new VBox();
        sidebar.setPrefWidth(230);
        sidebar.setStyle("-fx-background-color: " + PRIMARY + ";");
        sidebar.setPadding(new Insets(18, 14, 18, 14));
        sidebar.setSpacing(16);

        HBox logoBox = new HBox(10);
        logoBox.setAlignment(Pos.CENTER_LEFT);

        Label logoIcon = new Label("🚕");
        logoIcon.setAlignment(Pos.CENTER);
        logoIcon.setPrefSize(34, 34);
        logoIcon.setStyle(
                "-fx-background-color: #FBBF24;" +
                "-fx-background-radius: 9;" +
                "-fx-font-size: 16;"
        );

        VBox logoText = new VBox(2);
        Label appName = new Label("TP.HCM");
        appName.setTextFill(Color.WHITE);
        appName.setFont(Font.font("Arial", FontWeight.BOLD, 14));

        Label appSub = new Label("Hệ thống quản lý");
        appSub.setTextFill(Color.web("#9DB7D3"));
        appSub.setFont(Font.font("Arial", 11));

        logoText.getChildren().addAll(appName, appSub);
        logoBox.getChildren().addAll(logoIcon, logoText);

        Separator sep1 = new Separator();
        sep1.setStyle("-fx-background-color: #31577C;");

        HBox userBox = createUserBox();

        menuBox = new VBox(8);
        String[] menuItems = getMenuItems();

        for (String item : menuItems) {
            Button menuButton = createMenuButton(item, item.equals(activeMenu));

            menuButton.setOnAction(e -> {
                activeMenu = item;
                refreshSidebar(stage);

                if (item.equals("Tổng quan")) {
                    showOverviewView();
                } else if (item.contains("Quản lý nhân viên")) {
                    showEmployeeView();
                } else if (item.contains("Quản lý tài khoản")) {
                    showAccountManagementView();
                } else if (item.contains("Quản lý khu vực")) {
                    showAreaView();
                } else if (item.contains("Quản lý tuyến đường")) {
                    showStreetView();
                } else if (item.contains("Quản lý đoạn đường")) {
                    showSegmentView();
                } else if (item.contains("Quản lý nút giao")) {
                    showNodeView();
                } else if (item.contains("Quản lý lưu lượng")) {
                    showTrafficView();
                } else if (item.contains("Giám sát giao thông")) {
                    showTrafficMonitoringView();
                } else if (item.contains("Phân tích dữ liệu")) {
                    showTrafficAnalysisView();
                } else if (item.contains("Nhật ký hệ thống")) {
                    showSystemLogView();
                }
                // Các mục khác sau này sẽ xử lý tiếp:
                // Quản lý nhân viên
                // Quản lý tài khoản
                // Quản lý khu vực
                // ...
            });

            menuBox.getChildren().add(menuButton);
        }
        
        

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);
        
        Button accountInfoBtn = new Button();
        configureSidebarButtonContent(accountInfoBtn, "👤Thông tin tài khoản");
        accountInfoBtn.setMaxWidth(Double.MAX_VALUE);
        accountInfoBtn.setAlignment(Pos.CENTER_LEFT);
        styleSidebarButton(accountInfoBtn, activeMenu.equals("Thông tin tài khoản"));

        accountInfoBtn.setOnAction(e -> {
            activeMenu = "Thông tin tài khoản";
            refreshSidebar(stage);
            showAccountInfoView();
        });
        
        Button logoutBtn = new Button();
        configureSidebarButtonContent(logoutBtn, "↪  Đăng xuất");
        logoutBtn.setMaxWidth(Double.MAX_VALUE);
        logoutBtn.setAlignment(Pos.CENTER_LEFT);
        logoutBtn.setStyle(
                "-fx-background-color: transparent;" +
                "-fx-text-fill: #C9D8E8;" +
                "-fx-font-size: 13;" +
                "-fx-padding: 10 8;"
        );
        setSidebarIconColor(logoutBtn, Color.web("#C9D8E8"));
        logoutBtn.setOnAction(e -> baseController.handleLogout(stage));

        sidebar.getChildren().addAll(logoBox, sep1, userBox, menuBox, spacer, accountInfoBtn, logoutBtn);
        return sidebar;
    }

    private HBox createUserBox() {
        HBox userBox = new HBox(10);
        userBox.setAlignment(Pos.CENTER_LEFT);
        userBox.setPadding(new Insets(10));
        userBox.setStyle(
                "-fx-background-color: " + PRIMARY_LIGHT + ";" +
                "-fx-background-radius: 8;"
        );

        Label avatar = new Label(getAvatarText());
        avatar.setAlignment(Pos.CENTER);
        avatar.setTextFill(Color.web("#FBBF24"));
        avatar.setFont(Font.font("Arial", FontWeight.BOLD, 13));
        avatar.setPrefSize(28, 28);
        avatar.setStyle(
                "-fx-background-color: #4B6380;" +
                "-fx-background-radius: 50;"
        );

        VBox info = new VBox(2);

        Label name = new Label(getUserName());
        name.setTextFill(Color.WHITE);
        name.setFont(Font.font("Arial", FontWeight.BOLD, 12));

        Label role = new Label(getRoleName());
        role.setTextFill(Color.web("#B8CBE0"));
        role.setFont(Font.font("Arial", 11));

        Label employeeId = new Label("NV: " + getEmployeeId());
        employeeId.setTextFill(Color.web("#9DB7D3"));
        employeeId.setFont(Font.font("Arial", 10));

        info.getChildren().addAll(name, role, employeeId);
        userBox.getChildren().addAll(avatar, info);

        return userBox;
    }

    private Button createMenuButton(String text, boolean active) {
        Button btn = new Button();
        configureSidebarButtonContent(btn, text);
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setAlignment(Pos.CENTER_LEFT);
        btn.setPrefHeight(38);

        styleSidebarButton(btn, active);

        return btn;
    }

    private void configureSidebarButtonContent(Button button, String text) {
        String icon = extractLeadingIcon(text);
        String labelText = removeLeadingIcon(text);

        Label iconLabel = new Label(icon);
        iconLabel.setMinWidth(20);
        iconLabel.setPrefWidth(20);
        iconLabel.setMaxWidth(20);
        iconLabel.setAlignment(Pos.CENTER);

        button.setText(labelText);
        button.setGraphic(iconLabel);
        button.setGraphicTextGap(8);
        button.setContentDisplay(ContentDisplay.LEFT);
    }

    private String extractLeadingIcon(String text) {
        if (text == null || text.isEmpty()) {
            return "";
        }

        int codePoint = text.codePointAt(0);

        if (Character.isLetterOrDigit(codePoint) || Character.isWhitespace(codePoint)) {
            return "";
        }

        return new String(Character.toChars(codePoint));
    }

    private String removeLeadingIcon(String text) {
        if (text == null || text.isEmpty()) {
            return "";
        }

        int codePoint = text.codePointAt(0);

        if (Character.isLetterOrDigit(codePoint) || Character.isWhitespace(codePoint)) {
            return text;
        }

        return text.substring(Character.charCount(codePoint)).trim();
    }
    
    private HBox createHeader() {
        return createHeader("Tổng quan hệ thống");
    }

    private HBox createHeader(String pageTitle) {
        HBox header = new HBox();
        header.setAlignment(Pos.CENTER_LEFT);
        header.setPadding(new Insets(16, 28, 16, 28));
        header.setStyle("-fx-background-color: white;");
        header.setPrefHeight(70);

        VBox titleBox = new VBox(2);
        
        

        Label title = new Label(pageTitle);
        title.setTextFill(Color.web("#111827"));
        title.setFont(Font.font("Arial", FontWeight.BOLD, 20));

        Label subTitle = new Label("Cập nhật: " + getCurrentDateTimeText() + " · " + getRoleName());
        subTitle.setTextFill(Color.web(TEXT_MUTED));
        subTitle.setFont(Font.font("Arial", 12));

        titleBox.getChildren().addAll(title, subTitle);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox statusBox = new HBox(7);
        statusBox.setAlignment(Pos.CENTER);
        statusBox.setPadding(new Insets(7, 13, 7, 13));
        statusBox.setStyle(
                "-fx-background-color: #ECFDF5;" +
                "-fx-border-color: #A7F3D0;" +
                "-fx-border-radius: 20;" +
                "-fx-background-radius: 20;"
        );

        Circle dot = new Circle(4, Color.web(GREEN));
        Label status = new Label("Hệ thống hoạt động bình thường");
        status.setTextFill(Color.web("#059669"));
        status.setFont(Font.font("Arial", FontWeight.BOLD, 12));

        statusBox.getChildren().addAll(dot, status);
        header.getChildren().addAll(titleBox, spacer, statusBox);

        return header;
    }

    private ScrollPane createContent() {
        VBox content = new VBox(18);
        content.setPadding(new Insets(22, 28, 28, 28));
        content.setStyle("-fx-background-color: " + BG + ";");

        GridPane cards = new GridPane();
        cards.setHgap(16);
        cards.setVgap(16);

        ColumnConstraints c1 = new ColumnConstraints();
        c1.setPercentWidth(25);
        ColumnConstraints c2 = new ColumnConstraints();
        c2.setPercentWidth(25);
        ColumnConstraints c3 = new ColumnConstraints();
        c3.setPercentWidth(25);
        ColumnConstraints c4 = new ColumnConstraints();
        c4.setPercentWidth(25);
        cards.getColumnConstraints().addAll(c1, c2, c3, c4);
        
        List<DashboardCardInfo> overviewCards = getOverviewCards();

        for (int i = 0; i < overviewCards.size(); i++) {
            DashboardCardInfo cardInfo = overviewCards.get(i);

            int column = i % 4;
            int row = i / 4;

            cards.add(
                    createStatCard(
                            cardInfo.getLabel(),
                            cardInfo.getValue(),
                            cardInfo.getNote(),
                            cardInfo.getIcon(),
                            cardInfo.getColor()
                    ),
                    column,
                    row
            );
        }

//        HBox chartRow = new HBox(16);
//        chartRow.getChildren().addAll(createTrafficChartCard(), createIncidentBarChartCard());
//        HBox.setHgrow(chartRow.getChildren().get(0), Priority.ALWAYS);
//
//        VBox tableCard = createTrafficTableCard();

//        content.getChildren().addAll(cards, chartRow, tableCard);
        content.getChildren().addAll(cards, createRoleOverviewSection());

        ScrollPane scrollPane = new ScrollPane(content);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: transparent;");
        return scrollPane;
    }

    private Node createRoleOverviewSection() {
        VBox wrapper = new VBox(14);

        HBox heading = new HBox();
        heading.setAlignment(Pos.CENTER_LEFT);

        VBox titleBox = new VBox(4);
        Label title = new Label("Tổng quan theo vai trò");
        title.setTextFill(Color.web("#111827"));
        title.setFont(Font.font("Arial", FontWeight.BOLD, 17));

        Label intro = new Label(getOverviewIntro());
        intro.setTextFill(Color.web(TEXT_MUTED));
        intro.setFont(Font.font("Arial", 12));
        intro.setWrapText(true);

        titleBox.getChildren().addAll(title, intro);
        heading.getChildren().add(titleBox);

        HBox body = new HBox(16);
        body.getChildren().addAll(
                createOverviewListCard("Phạm vi quản lý", getOverviewFocusItems()),
                createOverviewActionCard(),
                createOverviewWorkflowCard()
        );

        for (Node node : body.getChildren()) {
            HBox.setHgrow(node, Priority.ALWAYS);
            if (node instanceof Region region) {
                region.setMaxWidth(Double.MAX_VALUE);
            }
        }

        wrapper.getChildren().addAll(heading, body);
        return wrapper;
    }

    private VBox createOverviewListCard(String titleText, List<String[]> items) {
        VBox card = new VBox(14);
        card.setPadding(new Insets(18));
        card.setMinHeight(260);
        card.setStyle(createCardStyle());

        Label title = createSectionTitle(titleText);
        card.getChildren().add(title);

        for (String[] item : items) {
            card.getChildren().add(createOverviewItem(item[0], item[1], item[2]));
        }

        return card;
    }

    private VBox createOverviewActionCard() {
        VBox card = new VBox(14);
        card.setPadding(new Insets(18));
        card.setMinHeight(260);
        card.setStyle(createCardStyle());

        Label title = createSectionTitle("Việc cần chú ý");
        card.getChildren().add(title);

        for (String[] action : getOverviewActionItems()) {
            VBox item = new VBox(4);
            Label actionTitle = new Label(action[0]);
            actionTitle.setTextFill(Color.web("#111827"));
            actionTitle.setFont(Font.font("Arial", FontWeight.BOLD, 13));

            Label actionDescription = new Label(action[1]);
            actionDescription.setTextFill(Color.web(TEXT_MUTED));
            actionDescription.setFont(Font.font("Arial", 12));
            actionDescription.setWrapText(true);

            item.getChildren().addAll(actionTitle, actionDescription);
            card.getChildren().add(item);
        }

        return card;
    }

    private VBox createOverviewWorkflowCard() {
        VBox card = new VBox(14);
        card.setPadding(new Insets(18));
        card.setMinHeight(260);
        card.setStyle(createCardStyle());

        Label title = createSectionTitle("Quy trình nhanh");
        card.getChildren().add(title);

        List<String> steps = getOverviewWorkflowSteps();

        for (int i = 0; i < steps.size(); i++) {
            HBox row = new HBox(10);
            row.setAlignment(Pos.TOP_LEFT);

            Label number = new Label(String.valueOf(i + 1));
            number.setAlignment(Pos.CENTER);
            number.setTextFill(Color.WHITE);
            number.setFont(Font.font("Arial", FontWeight.BOLD, 11));
            number.setMinSize(24, 24);
            number.setPrefSize(24, 24);
            number.setStyle(
                    "-fx-background-color: " + PRIMARY + ";" +
                    "-fx-background-radius: 50;"
            );

            Label text = new Label(steps.get(i));
            text.setTextFill(Color.web("#334155"));
            text.setFont(Font.font("Arial", 12));
            text.setWrapText(true);

            row.getChildren().addAll(number, text);
            card.getChildren().add(row);
        }

        return card;
    }

    private HBox createOverviewItem(String icon, String titleText, String descriptionText) {
        HBox item = new HBox(10);
        item.setAlignment(Pos.TOP_LEFT);

        Label iconLabel = new Label(icon);
        iconLabel.setAlignment(Pos.CENTER);
        iconLabel.setMinSize(34, 34);
        iconLabel.setPrefSize(34, 34);
        iconLabel.setStyle(
                "-fx-background-color: #EEF6FF;" +
                "-fx-background-radius: 9;" +
                "-fx-font-size: 14;"
        );

        VBox textBox = new VBox(3);
        Label title = new Label(titleText);
        title.setTextFill(Color.web("#111827"));
        title.setFont(Font.font("Arial", FontWeight.BOLD, 13));

        Label description = new Label(descriptionText);
        description.setTextFill(Color.web(TEXT_MUTED));
        description.setFont(Font.font("Arial", 12));
        description.setWrapText(true);

        textBox.getChildren().addAll(title, description);
        item.getChildren().addAll(iconLabel, textBox);
        return item;
    }

    private Label createSectionTitle(String text) {
        Label title = new Label(text);
        title.setTextFill(Color.web("#111827"));
        title.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        return title;
    }

    private VBox createStatCard(String label, String value, String note, String icon, String color) {
        VBox card = new VBox(10);
        card.setPadding(new Insets(18));
        card.setPrefHeight(120);
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

        Label valueLabel = new Label(value);
        valueLabel.setTextFill(Color.web("#030712"));
        valueLabel.setFont(Font.font("Arial", FontWeight.BOLD, 25));

        Label noteLabel = new Label(note);
        noteLabel.setTextFill(Color.web(TEXT_MUTED));
        noteLabel.setFont(Font.font("Arial", 12));

        card.getChildren().addAll(top, valueLabel, noteLabel);
        return card;
    }

    protected String createCardStyle() {
        return "-fx-background-color: " + CARD_BG + ";" +
                "-fx-background-radius: 12;" +
                "-fx-border-color: #DDE5EF;" +
                "-fx-border-radius: 12;" +
                "-fx-effect: dropshadow(gaussian, rgba(15, 23, 42, 0.12), 8, 0.2, 0, 2);";
    }
    
    private void showAccountInfoView() {
        if (root == null) {
            return;
        }

        AccountInfoController accountInfoController = new AccountInfoController();
        AccountInfoView accountInfoView = new AccountInfoView(accountInfoController);

        root.setCenter(accountInfoView.getView());
    }
    
    private void styleSidebarButton(Button btn, boolean active) {
        if (active) {
            btn.setStyle(
                    "-fx-background-color: " + PRIMARY_LIGHT + ";" +
                    "-fx-text-fill: white;" +
                    "-fx-background-radius: 8;" +
                    "-fx-font-weight: bold;" +
                    "-fx-font-size: 13;" +
                    "-fx-padding: 10 8;"
            );
            setSidebarIconColor(btn, Color.WHITE);
        } else {
            btn.setStyle(
                    "-fx-background-color: transparent;" +
                    "-fx-text-fill: #C9D8E8;" +
                    "-fx-font-size: 13;" +
                    "-fx-padding: 10 8;"
            );
            setSidebarIconColor(btn, Color.web("#C9D8E8"));
        }
    }

    private void setSidebarIconColor(Button button, Color color) {
        if (button.getGraphic() instanceof Label iconLabel) {
            iconLabel.setTextFill(color);
        }
    }

    private void refreshSidebar(Stage stage) {
        if (root == null) {
            return;
        }

        root.setLeft(createSidebar(stage));
    }
    
    private void showOverviewView() {
        if (root == null) {
            return;
        }

        root.setTop(createHeader());
        root.setCenter(createContent());
    }
    
    private void showEmployeeView() {
        if (root == null) {
            return;
        }

        EmployeeView employeeView = new EmployeeView();

        root.setTop(createHeader("Quản lý nhân viên"));
        root.setCenter(employeeView.getView());
    }

    private void showAccountManagementView() {
        if (root == null) {
            return;
        }

        AccountView accountView = new AccountView();

        root.setTop(createHeader("Quản lý tài khoản"));
        root.setCenter(accountView.getView());
    }

    private void showAreaView() {
        if (root == null) {
            return;
        }

        AreaView areaView = new AreaView();

        root.setTop(createHeader("Quản lý khu vực"));
        root.setCenter(areaView.getView());
    }

    private void showStreetView() {
        if (root == null) {
            return;
        }

        StreetView streetView = new StreetView();

        root.setTop(createHeader("Quản lý tuyến đường"));
        root.setCenter(streetView.getView());
    }

    private void showSegmentView() {
        if (root == null) {
            return;
        }

        SegmentView segmentView = new SegmentView();

        root.setTop(createHeader("Quản lý đoạn đường"));
        root.setCenter(segmentView.getView());
    }

    private void showNodeView() {
        if (root == null) {
            return;
        }

        NodeView nodeView = new NodeView();

        root.setTop(createHeader("Quản lý nút giao"));
        root.setCenter(nodeView.getView());
    }

    private void showTrafficView() {
        if (root == null) {
            return;
        }

        TrafficView trafficView = new TrafficView();

        root.setTop(createHeader("Quản lý lưu lượng"));
        root.setCenter(trafficView.getView());
    }

    private void showTrafficMonitoringView() {
        if (root == null) {
            return;
        }

        TrafficMonitoringView trafficMonitoringView = new TrafficMonitoringView();

        root.setTop(createHeader("Giám sát giao thông"));
        root.setCenter(trafficMonitoringView.getView());
    }

    private void showTrafficAnalysisView() {
        if (root == null) {
            return;
        }

        TrafficAnalysisView trafficAnalysisView = new TrafficAnalysisView();

        root.setTop(createHeader("Phân tích dữ liệu"));
        root.setCenter(trafficAnalysisView.getView());
    }

    private void showSystemLogView() {
        if (root == null) {
            return;
        }

        SystemLogView systemLogView = new SystemLogView();

        root.setTop(createHeader("Nhật ký hệ thống"));
        root.setCenter(systemLogView.getView());
    }
}
