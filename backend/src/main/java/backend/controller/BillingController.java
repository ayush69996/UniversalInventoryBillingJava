package backend.controller;

import backend.dto.BillRequest;
import backend.model.Sale;
import backend.service.BillingService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/billing")
@CrossOrigin(origins = "*")
public class BillingController {

    private final BillingService billingService;

    public BillingController(BillingService billingService) {
        this.billingService = billingService;
    }

    @PostMapping
    public Sale createBill(@RequestBody BillRequest request) {
        return billingService.createBill(request);
    }
}
