package backend.controller;

import backend.model.Product;
import backend.model.Sale;
import backend.repository.ProductRepository;
import backend.repository.SaleRepository;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/reports")
@CrossOrigin(origins = "*")
public class ReportsController {

    private final ProductRepository productRepository;
    private final SaleRepository saleRepository;

    public ReportsController(ProductRepository productRepository,
                             SaleRepository saleRepository) {
        this.productRepository = productRepository;
        this.saleRepository = saleRepository;
    }

    @GetMapping("/inventory")
    public Map<String, Object> inventoryReport() {
        List<Product> products = productRepository.findAll();

        double stockCostValue = products.stream()
                .mapToDouble(p -> p.getCostPrice() * p.getQuantity()).sum();

        double stockSellingValue = products.stream()
                .mapToDouble(p -> p.getSellingPrice() * p.getQuantity()).sum();

        long lowStockCount = products.stream()
                .filter(p -> p.getQuantity() <= p.getLowStockLevel()).count();

        Map<String, Object> report = new LinkedHashMap<>();
        report.put("totalProducts", products.size());
        report.put("totalUnits", products.stream().mapToInt(Product::getQuantity).sum());
        report.put("lowStockCount", lowStockCount);
        report.put("stockCostValue", stockCostValue);
        report.put("stockSellingValue", stockSellingValue);
        report.put("products", products);

        return report;
    }

    @GetMapping("/sales")
    public Map<String, Object> salesReport() {
        List<Sale> sales = saleRepository.findAll();

        double revenue = sales.stream().mapToDouble(Sale::getTotal).sum();
        double tax = sales.stream().mapToDouble(Sale::getTaxAmount).sum();
        double discounts = sales.stream().mapToDouble(Sale::getDiscount).sum();

        double profit = sales.stream()
                .flatMap(s -> s.getItems().stream())
                .mapToDouble(i -> (i.getSellingPrice() - i.getCostPrice())
                        * i.getQuantity()).sum();

        Map<String, Object> report = new LinkedHashMap<>();
        report.put("totalSales", sales.size());
        report.put("revenue", revenue);
        report.put("taxCollected", tax);
        report.put("discounts", discounts);
        report.put("grossProfitBeforeTaxAndDiscount", profit);
        report.put("sales", sales);

        return report;
    }
}
