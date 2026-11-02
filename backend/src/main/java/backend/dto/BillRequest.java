package backend.dto;

import java.util.List;

public class BillRequest {

    private List<BillLineRequest> items;
    private double taxRate;
    private double discount;

    public List<BillLineRequest> getItems() { return items; }
    public void setItems(List<BillLineRequest> items) { this.items = items; }

    public double getTaxRate() { return taxRate; }
    public void setTaxRate(double taxRate) { this.taxRate = taxRate; }

    public double getDiscount() { return discount; }
    public void setDiscount(double discount) { this.discount = discount; }
}
