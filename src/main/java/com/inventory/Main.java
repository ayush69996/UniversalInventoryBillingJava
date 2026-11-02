package com.inventory;

import com.inventory.controller.MainController;
import com.inventory.database.DatabaseInitializer;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage stage) {

        DatabaseInitializer.initialize();

        MainController controller = new MainController();

        Scene scene = new Scene(
                controller.createView(),
                1250,
                800
        );

        stage.setTitle("Universal Inventory & Billing System");
        stage.setMinWidth(1000);
        stage.setMinHeight(650);
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}