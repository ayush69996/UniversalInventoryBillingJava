
package com.inventory.model;

import com.inventory.service.SaleService;

import java.util.List;

public class SalesReport extends Report {

    private final SaleService saleService;

    public SalesReport() {
        super("Sales Report");
        saleService = new SaleService();
    }

    @Override
    public String generate() {

        List<Sale> sales = saleService.getAllSales();

        int totalSales = sales.size();

        double totalRevenue = sales.stream()
                .mapToDouble(Sale::getTotal)
                .sum();

        double totalTax = sales.stream()
                .mapToDouble(Sale::getTaxAmount)
                .sum();

        double totalDiscount = sales.stream()
                .mapToDouble(Sale::getDiscount)
                .sum();

        double averageSale = totalSales == 0
                ? 0
                : totalRevenue / totalSales;

        return String.format(
                """
                %s

                Generated At: %s

                Total Number of Sales: %s
                Total Revenue: ₹%s
                Total Tax Collected: ₹%s
                Total Discounts: ₹%s
                Average Sale Value: ₹%s
                """,
                getTitle(),
                getGeneratedAt(),
                formatIndianInteger(totalSales),
                formatIndian(totalRevenue),
                formatIndian(totalTax),
                formatIndian(totalDiscount),
                formatIndian(averageSale)
        );
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

    private String formatIndianInteger(int value) {
        return formatIndian(value).replace(".00", "");
    }
}
