
package com.inventory.view;

import com.inventory.model.BillItem;
import com.inventory.model.Product;
import com.inventory.model.Sale;
import com.inventory.service.BillingService;
import com.inventory.service.ProductService;
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
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.util.ArrayList;
import java.util.List;

public class BillingView {

    private final BillingService billingService;
    private final ProductService productService;
    private final SaleService saleService;

    private TableView<Product> productTable;
    private TableView<BillItem> cartTable;

    private TextField quantityField;
    private TextField taxField;
    private TextField discountField;

    private Label subtotalLabel;
    private Label taxLabel;
    private Label totalLabel;

    public BillingView() {
        billingService = new BillingService();
        productService = new ProductService();
        saleService = new SaleService();
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

        Label heading = new Label("Billing Management");
        heading.setFont(Font.font("Arial", FontWeight.BOLD, 28));
        heading.setTextFill(Color.rgb(30, 55, 90));

        createProductTable();
        createCartTable();

        quantityField = new TextField("1");
        quantityField.setPromptText("Quantity");
        quantityField.setPrefWidth(100);

        Button addButton = new Button("Add to Cart");
        addButton.setOnAction(e -> addToCart());

        Button removeButton = new Button("Remove Selected");
        removeButton.setOnAction(e -> removeFromCart());

        HBox productActions = new HBox(
                10, quantityField, addButton, removeButton
        );

        productActions.setAlignment(Pos.CENTER_LEFT);

        taxField = new TextField("18");
        taxField.setPromptText("Tax %");
        taxField.setPrefWidth(100);

        discountField = new TextField("0");
        discountField.setPromptText("Discount");
        discountField.setPrefWidth(100);

        subtotalLabel = new Label("Subtotal: ₹0.00");
        taxLabel = new Label("Tax: ₹0.00");
        totalLabel = new Label("Total: ₹0.00");

        totalLabel.setFont(Font.font("Arial", FontWeight.BOLD, 22));
        totalLabel.setTextFill(Color.rgb(35, 105, 190));

        Button completeButton = new Button("Complete Sale");
        completeButton.setOnAction(e -> completeSale());

        Button clearButton = new Button("Clear Cart");
        clearButton.setOnAction(e -> clearCart());

        HBox billingOptions = new HBox(
                10,
                new Label("Tax (%):"),
                taxField,
                new Label("Discount (₹):"),
                discountField
        );

        HBox billingButtons = new HBox(
                15, completeButton, clearButton
        );

        VBox summary = new VBox(
                12,
                billingOptions,
                subtotalLabel,
                taxLabel,
                totalLabel,
                billingButtons
        );

        root.getChildren().addAll(
                heading,
                new Label("Available Products"),
                productTable,
                productActions,
                new Label("Shopping Cart"),
                cartTable,
                summary
        );

        loadProducts();
        updateSummary();

        return root;
    }

    private void createProductTable() {

        productTable = new TableView<>();

        TableColumn<Product, Integer> idColumn = new TableColumn<>("ID");
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));

        TableColumn<Product, String> nameColumn = new TableColumn<>("Product");
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn<Product, String> skuColumn = new TableColumn<>("SKU");
        skuColumn.setCellValueFactory(new PropertyValueFactory<>("sku"));

        TableColumn<Product, Double> priceColumn = new TableColumn<>("Price");
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("sellingPrice"));

        priceColumn.setCellFactory(column -> new TableCell<Product, Double>() {
            @Override
            protected void updateItem(Double value, boolean empty) {
                super.updateItem(value, empty);
                setText(empty || value == null ? null : "₹" + formatIndian(value));
            }
        });

        TableColumn<Product, Integer> stockColumn = new TableColumn<>("Stock");
        stockColumn.setCellValueFactory(new PropertyValueFactory<>("quantity"));

        productTable.getColumns().add(idColumn);
        productTable.getColumns().add(nameColumn);
        productTable.getColumns().add(skuColumn);
        productTable.getColumns().add(priceColumn);
        productTable.getColumns().add(stockColumn);

        productTable.setColumnResizePolicy(
                TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN
        );

        productTable.setPrefHeight(180);
    }

    private void createCartTable() {

        cartTable = new TableView<>();

        TableColumn<BillItem, String> nameColumn =
                new TableColumn<>("Product");

        nameColumn.setCellValueFactory(
                data -> new javafx.beans.property.SimpleStringProperty(
                        data.getValue().getProduct().getName()
                )
        );

        TableColumn<BillItem, Integer> quantityColumn =
                new TableColumn<>("Quantity");

        quantityColumn.setCellValueFactory(
                new PropertyValueFactory<>("quantity")
        );

        TableColumn<BillItem, Double> priceColumn =
                new TableColumn<>("Unit Price");

        priceColumn.setCellValueFactory(
                new PropertyValueFactory<>("unitPrice")
        );

        priceColumn.setCellFactory(column -> new TableCell<BillItem, Double>() {
            @Override
            protected void updateItem(Double value, boolean empty) {
                super.updateItem(value, empty);
                setText(empty || value == null ? null : "₹" + formatIndian(value));
            }
        });

        TableColumn<BillItem, Double> subtotalColumn =
                new TableColumn<>("Subtotal");

        subtotalColumn.setCellValueFactory(
                new PropertyValueFactory<>("subtotal")
        );

        subtotalColumn.setCellFactory(column -> new TableCell<BillItem, Double>() {
            @Override
            protected void updateItem(Double value, boolean empty) {
                super.updateItem(value, empty);
                setText(empty || value == null ? null : "₹" + formatIndian(value));
            }
        });

        cartTable.getColumns().add(nameColumn);
        cartTable.getColumns().add(quantityColumn);
        cartTable.getColumns().add(priceColumn);
        cartTable.getColumns().add(subtotalColumn);

        cartTable.setColumnResizePolicy(
                TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN
        );

        cartTable.setPrefHeight(180);
    }

    private void loadProducts() {

        productTable.setItems(
                FXCollections.observableArrayList(
                        productService.getAllProducts()
                )
        );
    }

    private void refreshCart() {

        cartTable.setItems(
                FXCollections.observableArrayList(
                        billingService.getCartItems()
                )
        );

        updateSummary();
    }

    private void addToCart() {

        Product selected = productTable.getSelectionModel()
                .getSelectedItem();

        if (selected == null) {
            showMessage("Selection Required", "Select a product first.");
            return;
        }

        try {

            int quantity = Integer.parseInt(quantityField.getText().trim());

            if (billingService.addToCart(selected.getId(), quantity)) {
                refreshCart();
                quantityField.setText("1");
            } else {
                showMessage(
                        "Unable to Add",
                        "Invalid quantity or insufficient stock."
                );
            }

        } catch (NumberFormatException e) {
            showMessage("Invalid Input", "Enter a valid quantity.");
        }
    }

    private void removeFromCart() {

        BillItem selected = cartTable.getSelectionModel()
                .getSelectedItem();

        if (selected == null) {
            showMessage("Selection Required", "Select a cart item first.");
            return;
        }

        billingService.removeFromCart(
                selected.getProduct().getId()
        );

        refreshCart();
    }

    private void updateSummary() {

        try {

            double taxRate = Double.parseDouble(taxField.getText());
            double discount = Double.parseDouble(
                    discountField.getText().replace(",", "").trim()
            );

            double subtotal = billingService.calculateSubtotal();
            double tax = billingService.calculateTax(taxRate);
            double total = billingService.calculateTotal(taxRate, discount);

            subtotalLabel.setText("Subtotal: ₹" + formatIndian(subtotal));
            taxLabel.setText("Tax: ₹" + formatIndian(tax));
            totalLabel.setText("Total: ₹" + formatIndian(total));

        } catch (NumberFormatException e) {

            subtotalLabel.setText("Subtotal: ₹0.00");
            taxLabel.setText("Tax: ₹0.00");
            totalLabel.setText("Total: ₹0.00");
        }
    }

    private void completeSale() {

        if (billingService.isCartEmpty()) {
            showMessage("Empty Cart", "Add products before completing the sale.");
            return;
        }

        try {

            double taxRate = Double.parseDouble(taxField.getText().trim());
            double discount = Double.parseDouble(
                    discountField.getText().replace(",", "").trim()
            );

            if (taxRate < 0 || discount < 0) {
                showMessage("Invalid Input", "Tax and discount cannot be negative.");
                return;
            }

            double subtotal = billingService.calculateSubtotal();
            double taxAmount = billingService.calculateTax(taxRate);

            double total = billingService.calculateTotal(
                    taxRate, discount
            );

            List<BillItem> invoiceItems = new ArrayList<>(
                    billingService.getCartItems()
            );

            for (BillItem item : invoiceItems) {

                Product product = item.getProduct();

                if (item.getQuantity() > product.getQuantity()) {
                    showMessage(
                            "Stock Error",
                            "Insufficient stock for " + product.getName()
                    );
                    loadProducts();
                    return;
                }
            }

            Sale sale = saleService.createSale(
                    subtotal, taxRate, discount
            );

            if (!saleService.addSale(sale, invoiceItems)) {
                showMessage("Error", "Unable to save the sale.");
                return;
            }

            for (BillItem item : invoiceItems) {

                Product product = item.getProduct();

                product.setQuantity(
                        product.getQuantity() - item.getQuantity()
                );

                if (!productService.updateProduct(product)) {
                    showMessage(
                            "Stock Update Error",
                            "Sale was saved, but stock could not be updated for "
                                    + product.getName()
                    );
                    loadProducts();
                    return;
                }
            }

            InvoicePrinter.printInvoice(sale, invoiceItems);

            showMessage(
                    "Sale Completed",
                    "Sale saved successfully!\n\n"
                            + "Invoice ID: " + sale.getId()
                            + "\nSubtotal: ₹" + formatIndian(subtotal)
                            + "\nTax: ₹" + formatIndian(taxAmount)
                            + "\nTotal: ₹" + formatIndian(total)
            );

            billingService.clearCart();
            refreshCart();
            loadProducts();

        } catch (NumberFormatException e) {
            showMessage("Invalid Input", "Enter valid tax and discount values.");
        }
    }

    private void clearCart() {

        billingService.clearCart();
        refreshCart();
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
            formatted.insert(0, "," + remaining.substring(remaining.length() - 2));
            remaining = remaining.substring(0, remaining.length() - 2);
        }

        formatted.insert(0, remaining);

        return formatted + "," + lastThree + "." + parts[1];
    }

    private void showMessage(String title, String message) {

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
