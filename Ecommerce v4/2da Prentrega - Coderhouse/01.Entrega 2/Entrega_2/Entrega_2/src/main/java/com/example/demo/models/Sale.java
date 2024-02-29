package com.example.demo.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class Sale {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long ventaId;

    @ManyToOne
    @JoinColumn(name = "clienteId", nullable = false)
    private Client clientId;

    @ManyToMany
    @JoinTable(
            name = "SaleProduct",
            joinColumns = @JoinColumn(name = "saleId"),
            inverseJoinColumns = @JoinColumn(name = "productoId")
    )
    private List<Product> products;
    private LocalDateTime invoiceDate;
    private int amount;
    private double total;
    private String msg;

    public Long getVentaId() {
        return ventaId;
    }

    public void setVentaId(Long ventaId) {
        this.ventaId = ventaId;
    }

    public Client getClient() {
        return clientId;
    }

    public void setClient(Client id_client) {
        this.clientId = id_client;
    }

    public List<Product> getProducts() {
        return products;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
    }

    public LocalDateTime getInvoiceDate() {
        return invoiceDate;
    }

    public void setInvoiceDate(LocalDateTime invoiceDate) {
        this.invoiceDate = invoiceDate;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public String getMessage() {
        return msg;
    }

    public void setMessage(String msg) {
        this.msg = this.msg;
    }


}
