
package com.inventory.service;

import com.inventory.dao.SaleDAO;
import com.inventory.model.BillItem;
import com.inventory.model.Sale;

import java.time.LocalDateTime;
import java.util.List;

public class SaleService {

    private final SaleDAO saleDAO;

    public SaleService() {
        this.saleDAO = new SaleDAO();
    }

    public boolean addSale(Sale sale) {

        if (sale == null || sale.getSaleDate() == null) {
            return false;
        }

        if (sale.getSubtotal() < 0
                || sale.getTaxRate() < 0
                || sale.getTaxAmount() < 0
                || sale.getDiscount() < 0
                || sale.getTotal() < 0) {
            return false;
        }

        return saleDAO.addSale(sale);
    }

    public boolean addSale(Sale sale, List<BillItem> items) {

        if (sale == null || sale.getSaleDate() == null
                || items == null || items.isEmpty()) {
            return false;
        }

        if (sale.getSubtotal() < 0
                || sale.getTaxRate() < 0
                || sale.getTaxAmount() < 0
                || sale.getDiscount() < 0
                || sale.getTotal() < 0) {
            return false;
        }

        for (BillItem item : items) {
            if (item == null
                    || item.getProduct() == null
                    || item.getQuantity() <= 0
                    || item.getProduct().getCostPrice() < 0
                    || item.getProduct().getSellingPrice() < 0) {
                return false;
            }
        }

        return saleDAO.addSale(sale, items);
    }

    public List<Sale> getAllSales() {
        return saleDAO.getAllSales();
    }

    public double getTotalCostOfGoodsSold() {
        return saleDAO.getTotalCostOfGoodsSold();
    }

    public double getTotalProfit() {
        return saleDAO.getTotalProfit();
    }

    public boolean deleteSale(int id) {

        if (id <= 0) {
            return false;
        }

        return saleDAO.deleteSale(id);
    }

    public double calculateTax(double subtotal, double taxRate) {

        if (subtotal < 0 || taxRate < 0) {
            return 0;
        }

        return subtotal * taxRate / 100;
    }

    public double calculateTotal(double subtotal, double taxAmount, double discount) {

        if (subtotal < 0 || taxAmount < 0 || discount < 0) {
            return 0;
        }

        return Math.max(0, subtotal + taxAmount - discount);
    }

    public Sale createSale(double subtotal, double taxRate, double discount) {

        double taxAmount = calculateTax(subtotal, taxRate);

        double total = calculateTotal(subtotal, taxAmount, discount);

        return new Sale(
                0,
                LocalDateTime.now(),
                subtotal,
                taxRate,
                taxAmount,
                discount,
                total
        );
    }
}
