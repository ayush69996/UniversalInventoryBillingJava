package backend.service;

import backend.model.Product;
import backend.repository.ProductRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ProductService {
    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public Product getProductById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));
    }

    public Product addProduct(Product product) {
        if (productRepository.existsBySku(product.getSku())) {
            throw new IllegalArgumentException("SKU already exists");
        }
        return productRepository.save(product);
    }

    public Product updateProduct(Long id, Product updatedProduct) {
        Product existing = getProductById(id);

        productRepository.findBySku(updatedProduct.getSku()).ifPresent(product -> {
            if (!product.getId().equals(id)) {
                throw new IllegalArgumentException("SKU already exists");
            }
        });

        existing.setName(updatedProduct.getName());
        existing.setSku(updatedProduct.getSku());
        existing.setCategory(updatedProduct.getCategory());
        existing.setCostPrice(updatedProduct.getCostPrice());
        existing.setSellingPrice(updatedProduct.getSellingPrice());
        existing.setQuantity(updatedProduct.getQuantity());
        existing.setLowStockLevel(updatedProduct.getLowStockLevel());

        return productRepository.save(existing);
    }

    public void deleteProduct(Long id) {
        productRepository.delete(getProductById(id));
    }
}
