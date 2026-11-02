package com.inventory.model;

public class Product {

    private int id;
    private String name;
    private String sku;
    private String category;
    private double costPrice;
    private double sellingPrice;
    private int quantity;
    private int lowStockLevel;

    public Product(int id, String name, String sku, String category,
                   double costPrice, double sellingPrice,
                   int quantity, int lowStockLevel) {

        this.id = id;
        this.name = name;
        this.sku = sku;
        this.category = category;
        this.costPrice = costPrice;
        this.sellingPrice = sellingPrice;
        this.quantity = quantity;
        this.lowStockLevel = lowStockLevel;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public double getCostPrice() {
        return costPrice;
    }

    public void setCostPrice(double costPrice) {
        this.costPrice = costPrice;
    }

    public double getSellingPrice() {
        return sellingPrice;
    }

    public void setSellingPrice(double sellingPrice) {
        this.sellingPrice = sellingPrice;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getLowStockLevel() {
        return lowStockLevel;
    }

    public void setLowStockLevel(int lowStockLevel) {
        this.lowStockLevel = lowStockLevel;
    }
}