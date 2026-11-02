
package com.inventory.view;

import com.inventory.model.Sale;
import com.inventory.service.SaleService;

import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class SalesHistoryView {

    private final SaleService saleService;
    private final TableView<Sale> table;

    public SalesHistoryView() {
        saleService = new SaleService();
        table = new TableView<>();
    }

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

        Label heading = new Label("Sales History");
        heading.setFont(Font.font("Arial", FontWeight.BOLD, 28));
        heading.setTextFill(Color.rgb(30, 55, 90));

        Label subtitle = new Label("View and manage all recorded sales");
        subtitle.setFont(Font.font("Arial", 14));
        subtitle.setTextFill(Color.GRAY);

        createTable();

        Button refreshButton = new Button("Refresh");
        refreshButton.setOnAction(e -> loadSales());

        Button deleteButton = new Button("Delete Selected");
        deleteButton.setOnAction(e -> deleteSale());

        HBox buttons = new HBox(10, refreshButton, deleteButton);
        buttons.setAlignment(Pos.CENTER_LEFT);

        root.getChildren().addAll(
                heading,
                subtitle,
                buttons,
                table
        );

        loadSales();

        return root;
    }

    private void createTable() {

        TableColumn<Sale, Integer> idColumn =
                new TableColumn<>("Sale ID");

        idColumn.setCellValueFactory(
                new PropertyValueFactory<>("id")
        );

        TableColumn<Sale, String> dateColumn =
                new TableColumn<>("Date and Time");

        dateColumn.setCellValueFactory(
                new PropertyValueFactory<>("saleDate")
        );

        TableColumn<Sale, Double> subtotalColumn =
                new TableColumn<>("Subtotal");

        subtotalColumn.setCellValueFactory(
                new PropertyValueFactory<>("subtotal")
        );
        formatMoneyColumn(subtotalColumn);

        TableColumn<Sale, Double> taxRateColumn =
                new TableColumn<>("Tax Rate (%)");

        taxRateColumn.setCellValueFactory(
                new PropertyValueFactory<>("taxRate")
        );

        TableColumn<Sale, Double> taxColumn =
                new TableColumn<>("Tax Amount");

        taxColumn.setCellValueFactory(
                new PropertyValueFactory<>("taxAmount")
        );
        formatMoneyColumn(taxColumn);

        TableColumn<Sale, Double> discountColumn =
                new TableColumn<>("Discount");

        discountColumn.setCellValueFactory(
                new PropertyValueFactory<>("discount")
        );
        formatMoneyColumn(discountColumn);

        TableColumn<Sale, Double> totalColumn =
                new TableColumn<>("Total");

        totalColumn.setCellValueFactory(
                new PropertyValueFactory<>("total")
        );
        formatMoneyColumn(totalColumn);

        table.getColumns().add(idColumn);
        table.getColumns().add(dateColumn);
        table.getColumns().add(subtotalColumn);
        table.getColumns().add(taxRateColumn);
        table.getColumns().add(taxColumn);
        table.getColumns().add(discountColumn);
        table.getColumns().add(totalColumn);

        table.setColumnResizePolicy(
                TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN
        );

        table.setPrefHeight(450);
    }

    private void formatMoneyColumn(TableColumn<Sale, Double> column) {

        column.setCellFactory(col -> new TableCell<Sale, Double>() {

            @Override
            protected void updateItem(Double value, boolean empty) {
                super.updateItem(value, empty);

                if (empty || value == null) {
                    setText(null);
                } else {
                    setText("₹" + formatIndian(value));
                }
            }
        });
    }

    private String formatIndian(double value) {

        String[] parts = String.format(
                java.util.Locale.US, "%.2f", value
        ).split("\\.");

        String number = parts[0];

        if (number.length() <= 3) {
            return number + "." + parts[1];
        }

        String lastThree = number.substring(number.length() - 3);
        String remaining = number.substring(0, number.length() - 3);

        StringBuilder formatted = new StringBuilder();

        while (remaining.length() > 2) {
            formatted.insert(
                    0,
                    "," + remaining.substring(remaining.length() - 2)
            );
            remaining = remaining.substring(0, remaining.length() - 2);
        }

        formatted.insert(0, remaining);

        return formatted + "," + lastThree + "." + parts[1];
    }

    private void loadSales() {

        table.setItems(
                FXCollections.observableArrayList(
                        saleService.getAllSales()
                )
        );
    }

    private void deleteSale() {

        Sale selected = table.getSelectionModel().getSelectedItem();

        if (selected == null) {
            showMessage(
                    "Selection Required",
                    "Select a sale to delete."
            );
            return;
        }

        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Confirm Deletion");
        confirmation.setHeaderText(null);
        confirmation.setContentText(
                "Are you sure you want to delete Sale ID "
                        + selected.getId() + "?"
        );

        if (confirmation.showAndWait().orElse(null)
                == javafx.scene.control.ButtonType.OK) {

            if (saleService.deleteSale(selected.getId())) {
                showMessage("Success", "Sale deleted successfully.");
                loadSales();
            } else {
                showMessage("Error", "Unable to delete the sale.");
            }
        }
    }

    private void showMessage(String title, String message) {

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
