
package com.inventory.view;

import com.inventory.model.Product;
import com.inventory.service.ProductService;

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

public class ProductView {

    private final ProductService productService;
    private final TableView<Product> table;

    private TextField nameField;
    private TextField skuField;
    private TextField categoryField;
    private TextField costField;
    private TextField sellingField;
    private TextField quantityField;
    private TextField lowStockField;
    private TextField searchField;

    public ProductView() {
        productService = new ProductService();
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

        Label heading = new Label("Product Management");
        heading.setFont(Font.font("Arial", FontWeight.BOLD, 28));
        heading.setTextFill(Color.rgb(30, 55, 90));

        createTable();
        createForm();

        searchField = new TextField();
        searchField.setPromptText("Search by name, SKU or category");
        searchField.setPrefWidth(300);

        Button searchButton = new Button("Search");
        searchButton.setOnAction(e -> searchProducts());

        Button refreshButton = new Button("Refresh");
        refreshButton.setOnAction(e -> loadProducts());

        HBox searchBar = new HBox(10, searchField, searchButton, refreshButton);
        searchBar.setAlignment(Pos.CENTER_LEFT);

        root.getChildren().addAll(
                heading,
                createFormLayout(),
                searchBar,
                table
        );

        loadProducts();

        return root;
    }

    private void createTable() {

        TableColumn<Product, Integer> idColumn = new TableColumn<>("ID");
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));

        TableColumn<Product, String> nameColumn = new TableColumn<>("Name");
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn<Product, String> skuColumn = new TableColumn<>("SKU");
        skuColumn.setCellValueFactory(new PropertyValueFactory<>("sku"));

        TableColumn<Product, String> categoryColumn = new TableColumn<>("Category");
        categoryColumn.setCellValueFactory(new PropertyValueFactory<>("category"));

        TableColumn<Product, Double> costColumn = new TableColumn<>("Cost Price");
        costColumn.setCellValueFactory(new PropertyValueFactory<>("costPrice"));

        TableColumn<Product, Double> sellingColumn = new TableColumn<>("Selling Price");
        sellingColumn.setCellValueFactory(new PropertyValueFactory<>("sellingPrice"));

        TableColumn<Product, Integer> quantityColumn = new TableColumn<>("Quantity");
        quantityColumn.setCellValueFactory(new PropertyValueFactory<>("quantity"));

        TableColumn<Product, Integer> stockColumn = new TableColumn<>("Low Stock Level");
        stockColumn.setCellValueFactory(new PropertyValueFactory<>("lowStockLevel"));

        costColumn.setCellFactory(column -> new TableCell<Product, Double>() {
            @Override
            protected void updateItem(Double value, boolean empty) {
                super.updateItem(value, empty);
                setText(empty || value == null ? null : formatIndian(value));
            }
        });

        sellingColumn.setCellFactory(column -> new TableCell<Product, Double>() {
            @Override
            protected void updateItem(Double value, boolean empty) {
                super.updateItem(value, empty);
                setText(empty || value == null ? null : formatIndian(value));
            }
        });

        table.getColumns().add(idColumn);
        table.getColumns().add(nameColumn);
        table.getColumns().add(skuColumn);
        table.getColumns().add(categoryColumn);
        table.getColumns().add(costColumn);
        table.getColumns().add(sellingColumn);
        table.getColumns().add(quantityColumn);
        table.getColumns().add(stockColumn);

        table.setColumnResizePolicy(
                TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN
        );

        table.setPrefHeight(350);
    }

    private void createForm() {

        nameField = new TextField();
        nameField.setPromptText("Product Name");

        skuField = new TextField();
        skuField.setPromptText("SKU");

        categoryField = new TextField();
        categoryField.setPromptText("Category");

        costField = new TextField();
        costField.setPromptText("Cost Price");

        sellingField = new TextField();
        sellingField.setPromptText("Selling Price");

        quantityField = new TextField();
        quantityField.setPromptText("Quantity");

        lowStockField = new TextField();
        lowStockField.setPromptText("Low Stock Level");
        lowStockField.setText("5");
    }

    private VBox createFormLayout() {

        HBox row1 = new HBox(10,
                nameField,
                skuField,
                categoryField
        );

        HBox row2 = new HBox(10,
                costField,
                sellingField,
                quantityField,
                lowStockField
        );

        Button addButton = new Button("Add Product");
        addButton.setOnAction(e -> addProduct());

        Button updateButton = new Button("Update Selected");
        updateButton.setOnAction(e -> updateProduct());

        Button deleteButton = new Button("Delete Selected");
        deleteButton.setOnAction(e -> deleteProduct());

        Button clearButton = new Button("Clear Fields");
        clearButton.setOnAction(e -> clearFields());

        HBox buttons = new HBox(10,
                addButton,
                updateButton,
                deleteButton,
                clearButton
        );

        buttons.setAlignment(Pos.CENTER_LEFT);

        return new VBox(12, row1, row2, buttons);
    }

    private void addProduct() {

        try {

            Product product = new Product(
                    0,
                    nameField.getText().trim(),
                    skuField.getText().trim(),
                    categoryField.getText().trim(),
                    Double.parseDouble(costField.getText().replace(",", "").trim()),
                    Double.parseDouble(sellingField.getText().replace(",", "").trim()),
                    Integer.parseInt(quantityField.getText().replace(",", "").trim()),
                    Integer.parseInt(lowStockField.getText().replace(",", "").trim())
            );

            if (productService.addProduct(product)) {
                showMessage("Success", "Product added successfully.");
                loadProducts();
                clearFields();
            } else {
                showMessage("Error", "Unable to add product. Check the entered details.");
            }

        } catch (NumberFormatException e) {
            showMessage("Invalid Input", "Enter valid numbers for prices and quantities.");
        }
    }

    private void updateProduct() {

        Product selected = table.getSelectionModel().getSelectedItem();

        if (selected == null) {
            showMessage("Selection Required", "Select a product to update.");
            return;
        }

        try {

            Product product = new Product(
                    selected.getId(),
                    nameField.getText().trim(),
                    skuField.getText().trim(),
                    categoryField.getText().trim(),
                    Double.parseDouble(costField.getText().replace(",", "").trim()),
                    Double.parseDouble(sellingField.getText().replace(",", "").trim()),
                    Integer.parseInt(quantityField.getText().replace(",", "").trim()),
                    Integer.parseInt(lowStockField.getText().replace(",", "").trim())
            );

            if (productService.updateProduct(product)) {
                showMessage("Success", "Product updated successfully.");
                loadProducts();
                clearFields();
            } else {
                showMessage("Error", "Unable to update product.");
            }

        } catch (NumberFormatException e) {
            showMessage("Invalid Input", "Enter valid numbers for prices and quantities.");
        }
    }

    private void deleteProduct() {

        Product selected = table.getSelectionModel().getSelectedItem();

        if (selected == null) {
            showMessage("Selection Required", "Select a product to delete.");
            return;
        }

        if (productService.deleteProduct(selected.getId())) {
            showMessage("Success", "Product deleted successfully.");
            loadProducts();
            clearFields();
        } else {
            showMessage("Error", "Unable to delete product.");
        }
    }

    private void searchProducts() {

        String keyword = searchField.getText().trim();

        if (keyword.isEmpty()) {
            loadProducts();
        } else {
            table.setItems(
                    FXCollections.observableArrayList(
                            productService.searchProducts(keyword)
                    )
            );
        }
    }

    private void loadProducts() {

        table.setItems(
                FXCollections.observableArrayList(
                        productService.getAllProducts()
                )
        );
    }

    private void clearFields() {

        nameField.clear();
        skuField.clear();
        categoryField.clear();
        costField.clear();
        sellingField.clear();
        quantityField.clear();
        lowStockField.setText("5");

        table.getSelectionModel().clearSelection();
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
