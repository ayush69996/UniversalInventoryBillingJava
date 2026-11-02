
package com.inventory.dao;

import com.inventory.database.DatabaseConnection;
import com.inventory.model.BillItem;
import com.inventory.model.Sale;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class SaleDAO {

    public boolean addSale(Sale sale) {
        return addSale(sale, new ArrayList<>());
    }

    public boolean addSale(Sale sale, List<BillItem> items) {

        String saleSql = """
                INSERT INTO sales
                (sale_date, subtotal, tax_rate, tax_amount, discount, total)
                VALUES (?, ?, ?, ?, ?, ?)
                """;

        String itemSql = """
                INSERT INTO sale_items
                (sale_id, product_id, product_name, quantity, cost_price, selling_price)
                VALUES (?, ?, ?, ?, ?, ?)
                """;

        try (Connection connection = DatabaseConnection.getConnection()) {

            connection.setAutoCommit(false);

            try {
                int saleId;

                try (PreparedStatement statement = connection.prepareStatement(
                        saleSql,
                        PreparedStatement.RETURN_GENERATED_KEYS
                )) {

                    statement.setString(1, sale.getSaleDate().toString());
                    statement.setDouble(2, sale.getSubtotal());
                    statement.setDouble(3, sale.getTaxRate());
                    statement.setDouble(4, sale.getTaxAmount());
                    statement.setDouble(5, sale.getDiscount());
                    statement.setDouble(6, sale.getTotal());

                    statement.executeUpdate();

                    try (ResultSet keys = statement.getGeneratedKeys()) {
                        if (!keys.next()) {
                            connection.rollback();
                            return false;
                        }

                        saleId = keys.getInt(1);
                    }
                }

                try (PreparedStatement statement = connection.prepareStatement(itemSql)) {

                    for (BillItem item : items) {

                        statement.setInt(1, saleId);
                        statement.setInt(2, item.getProduct().getId());
                        statement.setString(3, item.getProduct().getName());
                        statement.setInt(4, item.getQuantity());
                        statement.setDouble(5, item.getProduct().getCostPrice());
                        statement.setDouble(6, item.getProduct().getSellingPrice());

                        statement.addBatch();
                    }

                    statement.executeBatch();
                }

                connection.commit();
                sale.setId(saleId);
                return true;

            } catch (SQLException e) {
                connection.rollback();
                throw e;
            }

        } catch (SQLException e) {
            System.out.println("Error adding sale: " + e.getMessage());
            return false;
        }
    }

    public List<Sale> getAllSales() {

        List<Sale> sales = new ArrayList<>();

        String sql = "SELECT * FROM sales ORDER BY id DESC";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet result = statement.executeQuery()) {

            while (result.next()) {

                Sale sale = new Sale(
                        result.getInt("id"),
                        LocalDateTime.parse(result.getString("sale_date")),
                        result.getDouble("subtotal"),
                        result.getDouble("tax_rate"),
                        result.getDouble("tax_amount"),
                        result.getDouble("discount"),
                        result.getDouble("total")
                );

                sales.add(sale);
            }

        } catch (SQLException e) {
            System.out.println("Error retrieving sales: " + e.getMessage());
        }

        return sales;
    }

    public double getTotalCostOfGoodsSold() {

        String sql = """
                SELECT COALESCE(SUM(quantity * cost_price), 0)
                FROM sale_items
                """;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet result = statement.executeQuery()) {

            if (result.next()) {
                return result.getDouble(1);
            }

        } catch (SQLException e) {
            System.out.println("Error calculating total cost: " + e.getMessage());
        }

        return 0;
    }

    public double getTotalProfit() {

        String sql = """
                SELECT
                    COALESCE((
                        SELECT SUM(subtotal - discount)
                        FROM sales
                        WHERE id IN (
                            SELECT DISTINCT sale_id FROM sale_items
                        )
                    ), 0)
                    -
                    COALESCE((
                        SELECT SUM(quantity * cost_price)
                        FROM sale_items
                    ), 0)
                """;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet result = statement.executeQuery()) {

            if (result.next()) {
                return result.getDouble(1);
            }

        } catch (SQLException e) {
            System.out.println("Error calculating total profit: " + e.getMessage());
        }

        return 0;
    }

    public boolean deleteSale(int id) {

        String deleteItemsSql = "DELETE FROM sale_items WHERE sale_id = ?";
        String deleteSaleSql = "DELETE FROM sales WHERE id = ?";

        try (Connection connection = DatabaseConnection.getConnection()) {

            connection.setAutoCommit(false);

            try {
                try (PreparedStatement statement = connection.prepareStatement(deleteItemsSql)) {
                    statement.setInt(1, id);
                    statement.executeUpdate();
                }

                int rowsDeleted;

                try (PreparedStatement statement = connection.prepareStatement(deleteSaleSql)) {
                    statement.setInt(1, id);
                    rowsDeleted = statement.executeUpdate();
                }

                connection.commit();
                return rowsDeleted > 0;

            } catch (SQLException e) {
                connection.rollback();
                throw e;
            }

        } catch (SQLException e) {
            System.out.println("Error deleting sale: " + e.getMessage());
            return false;
        }
    }
}
