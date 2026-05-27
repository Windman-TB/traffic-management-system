/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.trafficsystem.controller;

/**
 *
 * @author engineer
 */


import com.mycompany.trafficsystem.database.AreaDatabase;
import com.mycompany.trafficsystem.database.StreetDatabase;
import com.mycompany.trafficsystem.database.SegmentDatabase;
import com.mycompany.trafficsystem.database.NodeDatabase;
import com.mycompany.trafficsystem.util.Session;
import com.mycompany.trafficsystem.view.LoginView;
import com.mycompany.trafficsystem.database.EmployeeDatabase;
import com.mycompany.trafficsystem.database.AccountDatabase;
import com.mycompany.trafficsystem.database.TrafficDatabase;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;

import java.util.Optional;

public class BaseController {
   
    private final AreaDatabase areaDatabase;
    private final StreetDatabase streetDatabase;
    private final SegmentDatabase roadSegmentDatabase;
    private final NodeDatabase nodeDatabase;
    private final EmployeeDatabase employeeDatabase;
    private final AccountDatabase accountDatabase;
    private final TrafficDatabase trafficDatabase;
    
    public BaseController() {
        this.areaDatabase = new AreaDatabase();
        this.streetDatabase = new StreetDatabase();
        this.roadSegmentDatabase = new SegmentDatabase();
        this.nodeDatabase = new NodeDatabase();
        this.employeeDatabase = new EmployeeDatabase();
        this.accountDatabase = new AccountDatabase();
        this.trafficDatabase = new TrafficDatabase();
    }
    
    public int getTotalAreas() {
        return areaDatabase.countAreas();
    }

    public int getTotalStreets() {
        return streetDatabase.countStreets();
    }

    public int getTotalRoadSegments() {
        return roadSegmentDatabase.countSegments();
    }

    public int getTotalNodes() {
        return nodeDatabase.countNodes();
    }
    
    public int getTotalEmployees() {
        return employeeDatabase.countEmployees();
    }

    public int getTotalAccounts() {
        return accountDatabase.countAccounts();
    }

    public int getTotalTrafficVolume() {
        return trafficDatabase.countTraffic();
    }

   
    public void handleLogout(Stage currentStage) {
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Xác nhận đăng xuất");
        confirmAlert.setHeaderText(null);
        confirmAlert.setContentText("Bạn có chắc chắn muốn đăng xuất không?");

        Optional<ButtonType> result = confirmAlert.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            Session.clear();

            LoginView loginView = new LoginView();
            loginView.show(currentStage);
        }
    }    
    
}
