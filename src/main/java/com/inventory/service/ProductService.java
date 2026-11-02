package com.inventory.service;

import com.inventory.dao.ProductDAO;
import com.inventory.model.Product;

import java.util.List;

public class ProductService {

    private final ProductDAO productDAO;

    public ProductService() {
        this.productDAO = new ProductDAO();
    }

    public boolean addProduct(Product product) {

        if (product.getName() == null || product.getName().isBlank()
                || product.getSku() == null || product.getSku().isBlank()
                || product.getCategory() == null || product.getCategory().isBlank()) {
            return false;
        }

        if (product.getCostPrice() < 0
                || product.getSellingPrice() < 0
                || product.getQuantity() < 0
                || product.getLowStockLevel() < 0) {
            return false;
        }

        return productDAO.addProduct(product);
    }

    public List<Product> getAllProducts() {
        return productDAO.getAllProducts();
    }

    public List<Product> searchProducts(String keyword) {
        return productDAO.searchProducts(keyword);
    }

    public boolean updateProduct(Product product) {

        if (product.getId() <= 0
                || product.getName() == null || product.getName().isBlank()
                || product.getSku() == null || product.getSku().isBlank()
                || product.getCategory() == null || product.getCategory().isBlank()
                || product.getCostPrice() < 0
                || product.getSellingPrice() < 0
                || product.getQuantity() < 0
                || product.getLowStockLevel() < 0) {
            return false;
        }

        return productDAO.updateProduct(product);
    }

    public boolean deleteProduct(int id) {

        if (id <= 0) {
            return false;
        }

        return productDAO.deleteProduct(id);
    }

    public boolean restockProduct(int id, int additionalQuantity) {

        if (id <= 0 || additionalQuantity <= 0) {
            return false;
        }

        for (Product product : productDAO.getAllProducts()) {

            if (product.getId() == id) {

                product.setQuantity(
                        product.getQuantity() + additionalQuantity
                );

                return productDAO.updateProduct(product);
            }
        }

        return false;
    }

    public List<Product> getLowStockProducts() {

        return productDAO.getAllProducts()
                .stream()
                .filter(product ->
                        product.getQuantity() <= product.getLowStockLevel())
                .toList();
    }
}