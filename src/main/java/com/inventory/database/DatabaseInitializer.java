
package com.inventory.database;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseInitializer {

    public static void initialize() {

        String productsSql = """
                CREATE TABLE IF NOT EXISTS products (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    name TEXT NOT NULL,
                    sku TEXT UNIQUE NOT NULL,
                    category TEXT NOT NULL,
                    cost_price REAL NOT NULL,
                    selling_price REAL NOT NULL,
                    quantity INTEGER NOT NULL,
                    low_stock_level INTEGER DEFAULT 5
                );
                """;

        String salesSql = """
                CREATE TABLE IF NOT EXISTS sales (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    sale_date TEXT NOT NULL,
                    subtotal REAL NOT NULL,
                    tax_rate REAL NOT NULL,
                    tax_amount REAL NOT NULL,
                    discount REAL DEFAULT 0,
                    total REAL NOT NULL
                );
                """;

        String saleItemsSql = """
                CREATE TABLE IF NOT EXISTS sale_items (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    sale_id INTEGER NOT NULL,
                    product_id INTEGER,
                    product_name TEXT NOT NULL,
                    quantity INTEGER NOT NULL,
                    cost_price REAL NOT NULL,
                    selling_price REAL NOT NULL,
                    FOREIGN KEY (sale_id) REFERENCES sales(id)
                );
                """;

        try (Connection connection = DatabaseConnection.getConnection();
             Statement statement = connection.createStatement()) {

            statement.execute(productsSql);
            statement.execute(salesSql);
            statement.execute(saleItemsSql);

            System.out.println("Database initialized successfully.");

        } catch (SQLException e) {
            System.out.println("Database initialization failed: " + e.getMessage());
        }
    }
}
