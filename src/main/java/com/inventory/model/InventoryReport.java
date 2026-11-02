
package com.inventory.model;

import com.inventory.service.ProductService;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.List;
import java.util.Locale;

public class InventoryReport extends Report {

    private final ProductService productService;

    public InventoryReport() {
        super("Inventory Report");
        productService = new ProductService();
    }

    @Override
    public String generate() {

        List<Product> products = productService.getAllProducts();

        DecimalFormat format = new DecimalFormat(
                "#,##,##0.00",
                DecimalFormatSymbols.getInstance(Locale.forLanguageTag("en-IN"))
        );

        int totalProducts = products.size();

        int totalStock = products.stream()
                .mapToInt(Product::getQuantity)
                .sum();

        int lowStock = productService.getLowStockProducts().size();

        double totalCostValue = products.stream()
                .mapToDouble(product ->
                        product.getCostPrice() * product.getQuantity())
                .sum();

        double totalSellingValue = products.stream()
                .mapToDouble(product ->
                        product.getSellingPrice() * product.getQuantity())
                .sum();

        return String.format(
                """
                %s

                Generated At: %s

                Total Products: %s
                Total Stock Quantity: %s
                Low Stock Products: %s
                Total Cost Value: ₹%s
                Total Selling Value: ₹%s
                """,
                getTitle(),
                getGeneratedAt(),
                format.format(totalProducts),
                format.format(totalStock),
                format.format(lowStock),
                format.format(totalCostValue),
                format.format(totalSellingValue)
        );
    }
}
