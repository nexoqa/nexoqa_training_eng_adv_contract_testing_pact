package com.nexoqa.service;

import com.nexoqa.model.Client;
import com.nexoqa.model.Clients;
import com.nexoqa.model.User;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ClientService {

    private Map<String, Client> clients = new HashMap<>();

    public Client getClient(String name) {
        return searchClient(name.toLowerCase());
    }

    public Clients getClients() {
        List<Client> listedClients = new ArrayList<>();

        clients.forEach((k, v) -> {
            listedClients.add(v);
        });

        return new Clients(listedClients);
    }

    public Client createClient(User user) {
        Client newClient = new Client(user, true);
        insertClient(newClient);
        return getClient(newClient.getUser().getName());
    }

    public boolean isRegistered(String name) {
        boolean isRegistered = false;

        if (getClient(name) != null) {
            isRegistered = true;
        }
        return isRegistered;
    }

    private void insertClient(Client client) {
        clients.put(client.getUser().getName().toLowerCase(), client);
    }

    private Client searchClient(String name) {
        return clients.get(name);
    }

}
