package com.inventory.view;

import com.inventory.model.InventoryReport;
import com.inventory.model.Report;
import com.inventory.model.SalesReport;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class ReportsView {

    private TextArea reportArea;

    public VBox createView() {

        VBox root = new VBox(20);
        root.setPadding(new Insets(30));

        root.setBackground(
                new javafx.scene.layout.Background(
                        new javafx.scene.layout.BackgroundFill(
                                Color.rgb(245, 249, 255),
                                javafx.scene.layout.CornerRadii.EMPTY,
                                Insets.EMPTY
                        )
                )
        );

        Label heading = new Label("Reports");
        heading.setFont(Font.font("Arial", FontWeight.BOLD, 28));
        heading.setTextFill(Color.rgb(30, 55, 90));

        Label subtitle = new Label(
                "Generate inventory and sales reports"
        );
        subtitle.setFont(Font.font("Arial", 14));
        subtitle.setTextFill(Color.GRAY);

        Button inventoryButton = new Button("Inventory Report");
        inventoryButton.setOnAction(e ->
                generateReport(new InventoryReport())
        );

        Button salesButton = new Button("Sales Report");
        salesButton.setOnAction(e ->
                generateReport(new SalesReport())
        );

        HBox buttons = new HBox(
                12,
                inventoryButton,
                salesButton
        );

        buttons.setAlignment(Pos.CENTER_LEFT);

        reportArea = new TextArea();
        reportArea.setPromptText(
                "Select a report type to generate your report."
        );
        reportArea.setEditable(false);
        reportArea.setWrapText(true);
        reportArea.setPrefHeight(450);
        reportArea.setFont(Font.font("Consolas", 14));

        root.getChildren().addAll(
                heading,
                subtitle,
                buttons,
                reportArea
        );

        return root;
    }

    private void generateReport(Report report) {

        reportArea.setText(report.generate());
    }
}