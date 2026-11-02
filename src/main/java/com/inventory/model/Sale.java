package com.inventory.model;

import java.time.LocalDateTime;

public class Sale {

    private int id;
    private LocalDateTime saleDate;
    private double subtotal;
    private double taxRate;
    private double taxAmount;
    private double discount;
    private double total;

    public Sale(int id, LocalDateTime saleDate, double subtotal,
                double taxRate, double taxAmount,
                double discount, double total) {

        this.id = id;
        this.saleDate = saleDate;
        this.subtotal = subtotal;
        this.taxRate = taxRate;
        this.taxAmount = taxAmount;
        this.discount = discount;
        this.total = total;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public LocalDateTime getSaleDate() {
        return saleDate;
    }

    public void setSaleDate(LocalDateTime saleDate) {
        this.saleDate = saleDate;
    }

    public double getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(double subtotal) {
        this.subtotal = subtotal;
    }

    public double getTaxRate() {
        return taxRate;
    }

    public void setTaxRate(double taxRate) {
        this.taxRate = taxRate;
    }

    public double getTaxAmount() {
        return taxAmount;
    }

    public void setTaxAmount(double taxAmount) {
        this.taxAmount = taxAmount;
    }

    public double getDiscount() {
        return discount;
    }

    public void setDiscount(double discount) {
        this.discount = discount;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }
}