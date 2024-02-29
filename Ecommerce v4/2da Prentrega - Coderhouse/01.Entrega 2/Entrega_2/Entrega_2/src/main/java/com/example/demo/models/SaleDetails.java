package com.example.demo.models;
import java.util.List;

public class SaleDetails {
    private Long clientId;
    private List<Long> productIds;
    private int quantity;
    private double total;

    public SaleDetails(Long clientId, List<Long> productIds, int quantity, double total) {
        this.clientId = clientId;
        this.productIds = productIds;
        this.quantity = quantity;
        this.total = total;
    }

    public Long getAllClients() {
        return clientId;
    }

    public List<Long> getAllProducts() {
        return productIds;
    }

    public int getQty() {
        return quantity;
    }

    public double getTotalAmount() {
        return total;
    }

    public Long getClientId() {
        return clientId;
    }

    public void setClientId(Long clientId) {
        this.clientId = clientId;
    }

    public List<Long> getProductIds() {
        return productIds;
    }

    public void setProductIds(List<Long> productIds) {
        this.productIds = productIds;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }


}