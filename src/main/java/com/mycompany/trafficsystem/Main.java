/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.mycompany.trafficsystem;

/**
 *
 * @author engineer
 */

import com.mycompany.trafficsystem.view.LoginView;
import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage stage) {
        LoginView loginView = new LoginView();
        loginView.show(stage);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
