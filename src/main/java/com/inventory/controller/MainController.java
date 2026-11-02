package com.inventory.controller;

import com.inventory.view.BillingView;
import com.inventory.view.DashboardView;
import com.inventory.view.ProductView;
import com.inventory.view.ReportsView;
import com.inventory.view.SalesHistoryView;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class MainController {

    private StackPane contentArea;

    public BorderPane createView() {

        BorderPane root = new BorderPane();

        root.setBackground(
                new Background(
                        new BackgroundFill(
                                Color.rgb(245, 249, 255),
                                CornerRadii.EMPTY,
                                Insets.EMPTY
                        )
                )
        );

        contentArea = new StackPane();

        root.setLeft(createSidebar());
        root.setCenter(contentArea);

        showDashboard();

        return root;
    }

    private VBox createSidebar() {

        VBox sidebar = new VBox(15);
        sidebar.setPadding(new Insets(30, 18, 30, 18));
        sidebar.setPrefWidth(230);
        sidebar.setAlignment(Pos.TOP_CENTER);

        sidebar.setBackground(
                new Background(
                        new BackgroundFill(
                                Color.rgb(25, 45, 75),
                                CornerRadii.EMPTY,
                                Insets.EMPTY
                        )
                )
        );

        Label title = new Label("UNIVERSAL");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 21));
        title.setTextFill(Color.WHITE);

        Label subtitle = new Label("Inventory & Billing");
        subtitle.setFont(Font.font("Arial", 13));
        subtitle.setTextFill(Color.LIGHTBLUE);

        VBox logo = new VBox(5, title, subtitle);
        logo.setAlignment(Pos.CENTER);
        logo.setPadding(new Insets(0, 0, 25, 0));

        Button dashboardButton = createNavButton("Dashboard");
        dashboardButton.setOnAction(e -> showDashboard());

        Button productsButton = createNavButton("Products");
        productsButton.setOnAction(e -> showProducts());

        Button billingButton = createNavButton("Billing");
        billingButton.setOnAction(e -> showBilling());

        Button salesButton = createNavButton("Sales History");
        salesButton.setOnAction(e -> showSalesHistory());

        Button reportsButton = createNavButton("Reports");
        reportsButton.setOnAction(e -> showReports());

        sidebar.getChildren().addAll(
                logo,
                dashboardButton,
                productsButton,
                billingButton,
                salesButton,
                reportsButton
        );

        return sidebar;
    }

    private Button createNavButton(String text) {

        Button button = new Button(text);

        button.setMaxWidth(Double.MAX_VALUE);
        button.setPrefHeight(45);
        button.setAlignment(Pos.CENTER_LEFT);
        button.setPadding(new Insets(0, 15, 0, 15));

        button.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        button.setTextFill(Color.WHITE);

        button.setBackground(
                new Background(
                        new BackgroundFill(
                                Color.rgb(43, 67, 101),
                                new CornerRadii(8),
                                Insets.EMPTY
                        )
                )
        );

        return button;
    }

    private void showDashboard() {
        contentArea.getChildren().setAll(
                new DashboardView().createView()
        );
    }

    private void showProducts() {
        contentArea.getChildren().setAll(
                new ProductView().createView()
        );
    }

    private void showBilling() {
        contentArea.getChildren().setAll(
                new BillingView().createView()
        );
    }

    private void showSalesHistory() {
        contentArea.getChildren().setAll(
                new SalesHistoryView().createView()
        );
    }

    private void showReports() {
        contentArea.getChildren().setAll(
                new ReportsView().createView()
        );
    }
}