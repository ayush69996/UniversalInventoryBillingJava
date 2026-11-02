
package backend.service;

import backend.model.Sale;
import backend.repository.SaleRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SaleService {

    private final SaleRepository saleRepository;

    public SaleService(SaleRepository saleRepository) {
        this.saleRepository = saleRepository;
    }

    public List<Sale> getAllSales() {
        return saleRepository.findAll();
    }

    public Sale getSaleById(Long id) {
        return saleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Sale not found"));
    }

    public Sale addSale(Sale sale) {
        if (sale.getDate() == null) {
            throw new IllegalArgumentException("Sale date is required");
        }

        return saleRepository.save(sale);
    }

    public void deleteSale(Long id) {
        Sale sale = getSaleById(id);
        saleRepository.delete(sale);
    }
}
