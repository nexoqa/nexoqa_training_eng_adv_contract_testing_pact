package com.nexoqa.service;

import com.nexoqa.model.Client;
import com.nexoqa.model.Clients;
import lombok.Getter;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
@Getter
public class EmailService {

    private String backendUrl = "http://localhost:8081";

    public void setBackendURL(String urlBase) {
        this.backendUrl = urlBase;
    }

    public ResponseEntity<List<Client>> notifyActiveUsers() {
        try {
            ResponseEntity<Clients> clients = new RestTemplate().exchange(getBackendUrl() + "/client-provider/clients", HttpMethod.GET, null, Clients.class);

            List<Client> activeClients = new ArrayList<>();

            clients.getBody().getClients().forEach(client -> {
                if (client.isActivated()) {
                    activeClients.add(client);
                }
            });

            activeClients.forEach(activeClient -> {
                System.out.println("Subscriber notified -> " + activeClient.getUser().getName() + " " + activeClient.getUser().getLastName());
            });
            return ResponseEntity.ok().body(activeClients);

        } catch (HttpClientErrorException e) {
            throw e;
//            return ResponseEntity.notFound().build();
        }


    }

}
