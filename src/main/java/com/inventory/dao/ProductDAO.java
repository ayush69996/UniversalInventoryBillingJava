package com.inventory.dao;

import com.inventory.database.DatabaseConnection;
import com.inventory.model.Product;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ProductDAO {

    public boolean addProduct(Product product) {

        String sql = """
                INSERT INTO products
                (name, sku, category, cost_price, selling_price, quantity, low_stock_level)
                VALUES (?, ?, ?, ?, ?, ?, ?)
                """;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, product.getName());
            statement.setString(2, product.getSku());
            statement.setString(3, product.getCategory());
            statement.setDouble(4, product.getCostPrice());
            statement.setDouble(5, product.getSellingPrice());
            statement.setInt(6, product.getQuantity());
            statement.setInt(7, product.getLowStockLevel());

            return statement.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Error adding product: " + e.getMessage());
            return false;
        }
    }

    public List<Product> getAllProducts() {

        List<Product> products = new ArrayList<>();

        String sql = "SELECT * FROM products ORDER BY id DESC";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet result = statement.executeQuery()) {

            while (result.next()) {
                products.add(mapProduct(result));
            }

        } catch (SQLException e) {
            System.out.println("Error retrieving products: " + e.getMessage());
        }

        return products;
    }

    public List<Product> searchProducts(String keyword) {

        List<Product> products = new ArrayList<>();

        String sql = """
                SELECT * FROM products
                WHERE name LIKE ? OR sku LIKE ? OR category LIKE ?
                ORDER BY id DESC
                """;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            String search = "%" + keyword + "%";

            statement.setString(1, search);
            statement.setString(2, search);
            statement.setString(3, search);

            try (ResultSet result = statement.executeQuery()) {
                while (result.next()) {
                    products.add(mapProduct(result));
                }
            }

        } catch (SQLException e) {
            System.out.println("Error searching products: " + e.getMessage());
        }

        return products;
    }

    public boolean updateProduct(Product product) {

        String sql = """
                UPDATE products
                SET name = ?, sku = ?, category = ?, cost_price = ?,
                    selling_price = ?, quantity = ?, low_stock_level = ?
                WHERE id = ?
                """;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, product.getName());
            statement.setString(2, product.getSku());
            statement.setString(3, product.getCategory());
            statement.setDouble(4, product.getCostPrice());
            statement.setDouble(5, product.getSellingPrice());
            statement.setInt(6, product.getQuantity());
            statement.setInt(7, product.getLowStockLevel());
            statement.setInt(8, product.getId());

            return statement.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Error updating product: " + e.getMessage());
            return false;
        }
    }

    public boolean deleteProduct(int id) {

        String sql = "DELETE FROM products WHERE id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, id);

            return statement.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Error deleting product: " + e.getMessage());
            return false;
        }
    }

    private Product mapProduct(ResultSet result) throws SQLException {

        return new Product(
                result.getInt("id"),
                result.getString("name"),
                result.getString("sku"),
                result.getString("category"),
                result.getDouble("cost_price"),
                result.getDouble("selling_price"),
                result.getInt("quantity"),
                result.getInt("low_stock_level")
        );
    }
}