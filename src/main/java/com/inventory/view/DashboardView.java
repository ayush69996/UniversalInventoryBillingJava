
package com.inventory.view;

import com.inventory.model.Product;
import com.inventory.model.Sale;
import com.inventory.service.ProductService;
import com.inventory.service.SaleService;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.util.List;

public class DashboardView {

    private final ProductService productService;
    private final SaleService saleService;

    private final Color backgroundColor = Color.rgb(246, 248, 252);
    private final Color navy = Color.rgb(30, 45, 75);
    private final Color muted = Color.rgb(125, 137, 157);

    public DashboardView() {
        productService = new ProductService();
        saleService = new SaleService();
    }

    public VBox createView() {

        VBox root = new VBox(25);
        root.setPadding(new Insets(35));
        root.setAlignment(Pos.TOP_LEFT);
        root.setBackground(new Background(
                new BackgroundFill(backgroundColor, CornerRadii.EMPTY, Insets.EMPTY)
        ));

        List<Product> products = productService.getAllProducts();
        List<Sale> sales = saleService.getAllSales();

        int totalProducts = products.size();

        int totalStock = products.stream()
                .mapToInt(Product::getQuantity)
                .sum();

        int lowStock = productService.getLowStockProducts().size();

        double totalRevenue = sales.stream()
                .mapToDouble(Sale::getTotal)
                .sum();

        double totalCost = saleService.getTotalCostOfGoodsSold();
        double totalProfit = saleService.getTotalProfit();

        Label heading = new Label("Dashboard");
        heading.setFont(Font.font("Arial", FontWeight.BOLD, 30));
        heading.setTextFill(navy);

        Label subtitle = new Label(
                "Welcome back! Here's what's happening with your business."
        );
        subtitle.setFont(Font.font("Arial", 14));
        subtitle.setTextFill(muted);

        VBox headingBox = new VBox(7, heading, subtitle);

        GridPane cards = new GridPane();
        cards.setHgap(18);
        cards.setVgap(18);

        cards.add(createCard(
                "TOTAL PRODUCTS",
                String.valueOf(totalProducts),
                "▦",
                "Products in inventory",
                Color.rgb(48, 112, 220),
                Color.rgb(235, 242, 255)
        ), 0, 0);

        cards.add(createCard(
                "TOTAL STOCK",
                String.valueOf(totalStock),
                "□",
                "Units available",
                Color.rgb(14, 165, 130),
                Color.rgb(230, 249, 244)
        ), 1, 0);

        cards.add(createCard(
                "LOW STOCK ITEMS",
                String.valueOf(lowStock),
                "!",
                "Need restocking",
                Color.rgb(230, 150, 45),
                Color.rgb(255, 246, 230)
        ), 2, 0);

        cards.add(createCard(
                "TOTAL REVENUE",
                "₹" + formatIndian(totalRevenue),
                "₹",
                "Revenue from all saved sales",
                Color.rgb(48, 112, 220),
                Color.rgb(235, 242, 255)
        ), 0, 1);

        cards.add(createCard(
                "COST OF GOODS SOLD",
                "₹" + formatIndian(totalCost),
                "↓",
                "Cost of products sold with tracking",
                Color.rgb(230, 150, 45),
                Color.rgb(255, 246, 230)
        ), 1, 1);

        cards.add(createCard(
                "TOTAL PROFIT",
                "₹" + formatIndian(totalProfit),
                "↗",
                "Profit from tracked sales",
                Color.rgb(14, 165, 130),
                Color.rgb(230, 249, 244)
        ), 2, 1);

        for (int i = 0; i < 3; i++) {
            ColumnConstraintsHelper.setEqualColumn(cards, i);
        }

        VBox inventoryPanel = createInventoryPanel(
                totalProducts,
                totalStock,
                lowStock
        );

        VBox salesPanel = createSalesPanel(
                sales.size(),
                totalRevenue,
                totalProfit
        );

        root.getChildren().addAll(
                headingBox,
                cards,
                inventoryPanel,
                salesPanel
        );

        return root;
    }

    private VBox createCard(
            String title,
            String value,
            String symbol,
            String description,
            Color accent,
            Color iconBackground
    ) {

        VBox card = new VBox(15);
        card.setPadding(new Insets(22));
        card.setMinHeight(155);
        card.setMaxWidth(Double.MAX_VALUE);
        card.setBackground(new Background(
                new BackgroundFill(Color.WHITE, new CornerRadii(16), Insets.EMPTY)
        ));
        card.setBorder(new Border(new BorderStroke(
                Color.rgb(232, 236, 244),
                BorderStrokeStyle.SOLID,
                new CornerRadii(16),
                new BorderWidths(1)
        )));

        HBox top = new HBox();
        top.setAlignment(Pos.CENTER_LEFT);

        Label titleLabel = new Label(title);
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 11));
        titleLabel.setTextFill(muted);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label icon = new Label(symbol);
        icon.setFont(Font.font("Arial", FontWeight.BOLD, 19));
        icon.setTextFill(accent);
        icon.setAlignment(Pos.CENTER);
        icon.setMinSize(40, 40);
        icon.setBackground(new Background(
                new BackgroundFill(iconBackground, new CornerRadii(12), Insets.EMPTY)
        ));

        top.getChildren().addAll(titleLabel, spacer, icon);

        Label valueLabel = new Label(value);
        valueLabel.setFont(Font.font("Arial", FontWeight.BOLD, 22));
        valueLabel.setTextFill(navy);

        Label descriptionLabel = new Label(description);
        descriptionLabel.setFont(Font.font("Arial", 11));
        descriptionLabel.setTextFill(muted);
        descriptionLabel.setWrapText(true);

        card.getChildren().addAll(top, valueLabel, descriptionLabel);

        return card;
    }

    private VBox createInventoryPanel(
            int totalProducts,
            int totalStock,
            int lowStock
    ) {

        VBox panel = createPanel();

        Label title = new Label("Inventory Overview");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        title.setTextFill(navy);

        Label subtitle = new Label(
                "A quick look at your current inventory health"
        );
        subtitle.setFont(Font.font("Arial", 12));
        subtitle.setTextFill(muted);

        Label stockLabel = new Label("Stock Health");
        stockLabel.setFont(Font.font("Arial", FontWeight.BOLD, 13));
        stockLabel.setTextFill(navy);

        double healthyRatio = totalProducts == 0
                ? 0
                : Math.max(0, (double) (totalProducts - lowStock) / totalProducts);

        ProgressBar progressBar = new ProgressBar(healthyRatio);
        progressBar.setMaxWidth(Double.MAX_VALUE);
        progressBar.setPrefHeight(13);
        progressBar.setStyle(
                "-fx-accent: #20B486; " +
                "-fx-control-inner-background: #E9EEF5;"
        );

        Label healthText = new Label(
                totalProducts == 0
                        ? "No products added yet"
                        : (totalProducts - lowStock) + " healthy products out of " + totalProducts
        );
        healthText.setFont(Font.font("Arial", 12));
        healthText.setTextFill(muted);

        HBox stats = new HBox(15);
        stats.setAlignment(Pos.CENTER_LEFT);

        stats.getChildren().addAll(
                createSmallStat("Total Products", String.valueOf(totalProducts)),
                createSmallStat("Available Units", String.valueOf(totalStock)),
                createSmallStat("Low Stock", String.valueOf(lowStock))
        );

        panel.getChildren().addAll(
                title,
                subtitle,
                stats,
                stockLabel,
                progressBar,
                healthText
        );

        return panel;
    }

    private VBox createSalesPanel(
            int totalOrders,
            double totalRevenue,
            double totalProfit
    ) {

        VBox panel = createPanel();

        Label title = new Label("Financial Summary");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        title.setTextFill(navy);

        Label subtitle = new Label(
                "Summary of recorded sales and tracked profit"
        );
        subtitle.setFont(Font.font("Arial", 12));
        subtitle.setTextFill(muted);

        HBox summary = new HBox(15);
        summary.setAlignment(Pos.CENTER_LEFT);

        summary.getChildren().addAll(
                createSmallStat("Total Orders", String.valueOf(totalOrders)),
                createSmallStat("Total Revenue", "₹" + formatIndian(totalRevenue)),
                createSmallStat("Tracked Profit", "₹" + formatIndian(totalProfit))
        );

        panel.getChildren().addAll(title, subtitle, summary);

        return panel;
    }

    private VBox createPanel() {

        VBox panel = new VBox(18);
        panel.setPadding(new Insets(25));
        panel.setBackground(new Background(
                new BackgroundFill(Color.WHITE, new CornerRadii(16), Insets.EMPTY)
        ));
        panel.setBorder(new Border(new BorderStroke(
                Color.rgb(232, 236, 244),
                BorderStrokeStyle.SOLID,
                new CornerRadii(16),
                new BorderWidths(1)
        )));

        return panel;
    }

    private VBox createSmallStat(String title, String value) {

        VBox stat = new VBox(7);
        stat.setPadding(new Insets(15));
        stat.setMinWidth(150);
        stat.setBackground(new Background(
                new BackgroundFill(
                        Color.rgb(247, 249, 253),
                        new CornerRadii(12),
                        Insets.EMPTY
                )
        ));

        Label titleLabel = new Label(title);
        titleLabel.setFont(Font.font("Arial", 12));
        titleLabel.setTextFill(muted);

        Label valueLabel = new Label(value);
        valueLabel.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        valueLabel.setTextFill(navy);

        stat.getChildren().addAll(titleLabel, valueLabel);

        return stat;
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

    private static class ColumnConstraintsHelper {

        private static void setEqualColumn(GridPane grid, int index) {
            javafx.scene.layout.ColumnConstraints constraints =
                    new javafx.scene.layout.ColumnConstraints();

            constraints.setPercentWidth(33.333);
            constraints.setHgrow(Priority.ALWAYS);

            grid.getColumnConstraints().add(constraints);
        }
    }
}
