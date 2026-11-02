package backend.service;

import backend.dto.BillLineRequest;
import backend.dto.BillRequest;
import backend.model.BillItem;
import backend.model.Product;
import backend.model.Sale;
import backend.repository.ProductRepository;
import backend.repository.SaleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class BillingService {

    private final ProductRepository productRepository;
    private final SaleRepository saleRepository;

    public BillingService(ProductRepository productRepository,
                          SaleRepository saleRepository) {
        this.productRepository = productRepository;
        this.saleRepository = saleRepository;
    }

    @Transactional
    public Sale createBill(BillRequest request) {

        if (request.getItems() == null || request.getItems().isEmpty()) {
            throw new IllegalArgumentException("Bill must contain at least one item");
        }

        if (request.getTaxRate() < 0 || request.getDiscount() < 0) {
            throw new IllegalArgumentException("Tax and discount cannot be negative");
        }

        Sale sale = new Sale();
        sale.setDate(LocalDateTime.now());
        sale.setTaxRate(request.getTaxRate());
        sale.setDiscount(request.getDiscount());

        double subtotal = 0;

        for (BillLineRequest line : request.getItems()) {

            if (line.getQuantity() <= 0) {
                throw new IllegalArgumentException("Quantity must be positive");
            }

            Product product = productRepository.findById(line.getProductId())
                    .orElseThrow(() -> new RuntimeException("Product not found"));

            if (product.getQuantity() < line.getQuantity()) {
                throw new IllegalArgumentException(
                        "Insufficient stock for " + product.getName());
            }

            double lineTotal = product.getSellingPrice() * line.getQuantity();
            subtotal += lineTotal;

            BillItem item = new BillItem();
            item.setSale(sale);
            item.setProduct(product);
            item.setProductName(product.getName());
            item.setSku(product.getSku());
            item.setQuantity(line.getQuantity());
            item.setCostPrice(product.getCostPrice());
            item.setSellingPrice(product.getSellingPrice());
            item.setLineTotal(lineTotal);

            sale.getItems().add(item);

            product.setQuantity(product.getQuantity() - line.getQuantity());
            productRepository.save(product);
        }

        if (request.getDiscount() > subtotal) {
            throw new IllegalArgumentException("Discount cannot exceed subtotal");
        }

        double taxableAmount = subtotal - request.getDiscount();
        double taxAmount = taxableAmount * request.getTaxRate() / 100.0;

        sale.setSubtotal(subtotal);
        sale.setTaxAmount(taxAmount);
        sale.setTotal(taxableAmount + taxAmount);

        return saleRepository.save(sale);
    }

    public List<Sale> getAllSales() {
        return saleRepository.findAll();
    }

    public Sale getSale(Long id) {
        return saleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Sale not found"));
    }
}
