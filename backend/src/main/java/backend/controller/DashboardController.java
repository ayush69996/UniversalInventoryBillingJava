package backend.controller;

import backend.model.Product;
import backend.model.Sale;
import backend.repository.ProductRepository;
import backend.repository.SaleRepository;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/dashboard")
@CrossOrigin(origins = "*")
public class DashboardController {

    private final ProductRepository productRepository;
    private final SaleRepository saleRepository;

    public DashboardController(ProductRepository productRepository,
                               SaleRepository saleRepository) {
        this.productRepository = productRepository;
        this.saleRepository = saleRepository;
    }

    @GetMapping
    public Map<String, Object> getDashboard() {

        List<Product> products = productRepository.findAll();
        List<Sale> sales = saleRepository.findAll();

        double revenue = sales.stream()
                .mapToDouble(Sale::getTotal).sum();

        double profit = sales.stream()
                .flatMap(s -> s.getItems().stream())
                .mapToDouble(i -> (i.getSellingPrice() - i.getCostPrice())
                        * i.getQuantity()).sum();

        long lowStock = products.stream()
                .filter(p -> p.getQuantity() <= p.getLowStockLevel()).count();

        Map<String, Object> dashboard = new LinkedHashMap<>();

        dashboard.put("totalProducts", products.size());
        dashboard.put("totalSales", sales.size());
        dashboard.put("totalRevenue", revenue);
        dashboard.put("grossProfitBeforeTaxAndDiscount", profit);
        dashboard.put("lowStockProducts", lowStock);
        dashboard.put("totalStockUnits",
                products.stream().mapToInt(Product::getQuantity).sum());

        return dashboard;
    }
}
