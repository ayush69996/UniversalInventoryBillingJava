package com.inventory.model;

public class BillItem {

    private Product product;
    private int quantity;

    public BillItem(Product product, int quantity) {
        this.product = product;
        this.quantity = quantity;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getUnitPrice() {
        return product.getSellingPrice();
    }

    public double getSubtotal() {
        return getUnitPrice() * quantity;
    }

    public double getTotal() {
        return getSubtotal();
    }
}