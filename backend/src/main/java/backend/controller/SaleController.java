package backend.controller;

import backend.model.Sale;
import backend.service.SaleService;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/sales")
@CrossOrigin(origins = "*")
public class SaleController {
    private final SaleService saleService;

    public SaleController(SaleService saleService) {
        this.saleService = saleService;
    }

    @GetMapping
    public List<Sale> getAllSales() {
        return saleService.getAllSales();
    }

    @GetMapping("/{id}")
    public Sale getSaleById(@PathVariable Long id) {
        return saleService.getSaleById(id);
    }

    @PostMapping
    public Sale addSale(@RequestBody Sale sale) {
        return saleService.addSale(sale);
    }

    @DeleteMapping("/{id}")
    public String deleteSale(@PathVariable Long id) {
        saleService.deleteSale(id);
        return "Sale deleted successfully";
    }
}
