package com.example.demo.services;

import com.example.demo.models.Client;
import com.example.demo.repository.ClientRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class ClientService {

    @Autowired
    private ClientRepository clientRepository;

    public Optional<Client> getClientById(Long id) {
        return clientRepository.findById(id);
    }

    public Client createClient(Client client) {
        return clientRepository.save(client);
    }

    public Optional<Client> updateClient(Long id, Client client) {
        return clientRepository.findById(id).map(existingClient -> {
            existingClient.setName(client.getName());
            existingClient.setEmail(client.getEmail());
            return clientRepository.save(existingClient);
        });
    }

    public Optional<Client> deleteClient(Long id) {
        Optional<Client> client = clientRepository.findById(id);
        client.ifPresent(c -> clientRepository.deleteById(id));
        return client;
    }

    public List<Client> getAllClients() {
        return clientRepository.findAll();
    }

}
