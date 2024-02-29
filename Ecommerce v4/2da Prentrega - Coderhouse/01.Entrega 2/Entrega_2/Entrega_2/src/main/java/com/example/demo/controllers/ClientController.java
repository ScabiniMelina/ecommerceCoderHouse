package com.example.demo.controllers;

import com.example.demo.models.Client;
import com.example.demo.services.ClientService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/clients")
public class ClientController {

    private final ClientService clientService;

    @Autowired
    public ClientController(ClientService clientService) {
        this.clientService = clientService;
    }

    @Operation(summary = "Get all Clients", description = "Retrieve all clients.")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Successful operation"),
            @ApiResponse(responseCode = "404", description = "Client not found")})
    @GetMapping("/get/all")
    public List<Client> getClients() {
        return clientService.getAllClients();
    }

    @Operation(summary = "Get Client by ID", description = "Retrieve a client by ID.")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Successful operation"),
            @ApiResponse(responseCode = "404", description = "Client not found")})
    @GetMapping("/get/{id}")
    public ResponseEntity<?> getClientById(@PathVariable Long id) {
        Optional<Client> foundClient = clientService.getClientById(id);
        if (foundClient.isPresent()) {
            return ResponseEntity.ok(foundClient.get());
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("The specified user ID does not exist");
        }
    }

    @Operation(summary = "Create Client", description = "Create a new client.")
    @ApiResponses(value = {@ApiResponse(responseCode = "201", description = "Successful operation"),
            @ApiResponse(responseCode = "404", description = "Client not found")})
    @PostMapping("/add")
    public ResponseEntity<Client> createClient(@RequestBody Client client) {
        Client createdClient = clientService.createClient(client);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdClient);
    }

    @Operation(summary = "Update Client by ID", description = "Update a client by ID.")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Successful operation"),
            @ApiResponse(responseCode = "404", description = "Client not found")})
    @PutMapping("/update/{id}")
    public ResponseEntity<Client> update(@PathVariable Long id, @RequestBody Client client) {
        Optional<Client> updatedClient = clientService.updateClient(id, client);
        return updatedClient.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Delete Client by ID", description = "Delete a client by ID.")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Successful operation"),
            @ApiResponse(responseCode = "404", description = "Client not found")})
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Client> delete(@PathVariable Long id) {
        Optional<Client> deletedClient = clientService.deleteClient(id);
        return deletedClient.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }
}
