package com.inventory.service;

import com.inventory.model.BillItem;
import com.inventory.model.Product;

import java.util.ArrayList;
import java.util.List;

public class BillingService {

    private final ProductService productService;
    private final List<BillItem> cart;

    public BillingService() {
        productService = new ProductService();
        cart = new ArrayList<>();
    }

    public boolean addToCart(int productId, int quantity) {

        if (quantity <= 0) {
            return false;
        }

        Product product = productService.getAllProducts()
                .stream()
                .filter(p -> p.getId() == productId)
                .findFirst()
                .orElse(null);

        if (product == null) {
            return false;
        }

        int existingQuantity = cart.stream()
                .filter(item -> item.getProduct().getId() == productId)
                .mapToInt(BillItem::getQuantity)
                .sum();

        if (existingQuantity + quantity > product.getQuantity()) {
            return false;
        }

        for (BillItem item : cart) {
            if (item.getProduct().getId() == productId) {
                item.setQuantity(item.getQuantity() + quantity);
                return true;
            }
        }

        cart.add(new BillItem(product, quantity));
        return true;
    }

    public boolean removeFromCart(int productId) {

        return cart.removeIf(
                item -> item.getProduct().getId() == productId
        );
    }

    public void clearCart() {
        cart.clear();
    }

    public List<BillItem> getCartItems() {
        return new ArrayList<>(cart);
    }

    public double calculateSubtotal() {

        return cart.stream()
                .mapToDouble(BillItem::getSubtotal)
                .sum();
    }

    public double calculateTax(double taxRate) {

        if (taxRate < 0) {
            return 0;
        }

        return calculateSubtotal() * taxRate / 100;
    }

    public double calculateTotal(double taxRate, double discount) {

        if (discount < 0) {
            return 0;
        }

        return Math.max(
                0,
                calculateSubtotal() + calculateTax(taxRate) - discount
        );
    }

    public boolean isCartEmpty() {
        return cart.isEmpty();
    }
}