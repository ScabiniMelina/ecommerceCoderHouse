package com.example.demo.services;

import com.example.demo.models.*;
import com.example.demo.repository.SaleRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
public class SaleService {

    private static final String WORLD_CLOCK_API_URL = "http://worldclockapi.com/api/json/utc/now";
    private static final int HTTP_OK_STATUS = 200;

    @Autowired
    private SaleRepository saleRepository;
    @Autowired
    private ClientService clientService;
    @Autowired
    private ProductService productService;

    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    // Constructor de la clase VentaService
    public SaleService() {
        // Inicialización del cliente HTTP y el ObjectMapper
        this.httpClient = HttpClient.newHttpClient();
        this.objectMapper = new ObjectMapper();
    }

    public List<Sale> getAllSales() {
        return saleRepository.findAll();
    }

    public Optional<Sale> getSaleById(Long id) {
        return saleRepository.findById(id);
    }

    public Sale createSale(SaleDetails saleDetails) {

        LocalDateTime saleDateTime = getCurrentDateTime();

        // Get client associated with the sale
        Optional<Client> foundClient = clientService.getClientById(saleDetails.getAllClients());
        if (foundClient.isEmpty()) {
            throw new IllegalArgumentException("Client not found for the sale.");
        }

        // Get products associated with the sale
        List<Long> productIds = saleDetails.getAllProducts();
        List<Product> foundProducts = productService.getProductsById(productIds);
        if (foundProducts.isEmpty()) {
            throw new IllegalArgumentException("No products found for the sale.");
        }

        // Filter products with sufficient stock
        List<Product> saleProducts = foundProducts.stream()
                .filter(product -> product.getStock() >= 1 && productIds.contains(product.getProductId()))
                .collect(Collectors.toList());

        // Collect omitted products due to lack of stock
        List<Product> omittedProducts = foundProducts.stream()
                .filter(product -> !saleProducts.contains(product))
                .collect(Collectors.toList());


        // Calculate total sale amount
        double saleTotal = saleProducts.stream()
                .mapToDouble(Product::getPrice)
                .sum();

        // Create Sale object with the obtained data
        Sale sale = new Sale();
        sale.setClient(foundClient.get());
        sale.setProducts(saleProducts);
        sale.setInvoiceDate(saleDateTime);
        sale.setAmount(saleProducts.size());
        sale.setTotal(saleTotal);

        // Set message for omitted products, if any
        if (!omittedProducts.isEmpty()) {
            StringBuilder omittedProductsMessage = new StringBuilder();
            omittedProducts.forEach(product -> omittedProductsMessage.append("Product with ID ")
                    .append(product.getProductId())
                    .append(" omitted due to lack of stock.\n"));
            sale.setMessage(omittedProductsMessage.toString());
        }

        // Update stock of sold products
        saleProducts.forEach(product -> productService.updateStock(product.getProductId(), 1));

        // Save the sale in the database and return it
        return saleRepository.save(sale);
    }



    public String generateInvoice(Long id) {

        Optional<Sale> foundSale = saleRepository.findById(id);
        if (foundSale.isEmpty()) {
            throw new IllegalArgumentException("Sale not found with ID: " + id);
        }


        Sale sale = foundSale.get();
        StringBuilder invoice = new StringBuilder();

        invoice.append("Invoice for Sale ID: ").append(id).append("\n");
        invoice.append("Date: ").append(sale.getInvoiceDate()).append("\n\n");

        invoice.append("Products:\n");
        for (Product product : sale.getProducts()) {
            invoice.append("- ").append(product.getName()).append(": ").append(product.getPrice()).append("\n");
        }

        double total = sale.getTotal();
        invoice.append("\nTotal: ").append(total).append("\n");

        return invoice.toString();
    }


    public Optional<Sale> deleteSale(Long id) {
        Optional<Sale> foundSale = saleRepository.findById(id);
        foundSale.ifPresent(sale -> saleRepository.deleteById(id));
        return foundSale;
    }

    private LocalDateTime getCurrentDateTime() {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(WORLD_CLOCK_API_URL))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == HTTP_OK_STATUS) {
                WorldClockApiResponse clockResponse = objectMapper.readValue(response.body(), WorldClockApiResponse.class);
                return LocalDateTime.parse(clockResponse.getCurrentDateTime(), DateTimeFormatter.ISO_OFFSET_DATE_TIME)
                        .atZone(ZoneOffset.UTC)
                        .withZoneSameInstant(ZoneId.systemDefault())
                        .toLocalDateTime();
            } else {
                System.err.println("Error retrieving server date and time: " + response.statusCode());
                return LocalDateTime.now();
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return LocalDateTime.now();
        }
    }


}