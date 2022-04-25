package com.nexoqa.service;

import com.nexoqa.model.Client;
import com.nexoqa.model.DeleteResponse;
import com.nexoqa.model.User;
import lombok.Getter;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;

@Service
@Getter
public class UpdateSubscriptionService {

    private String backendUrl = "http://localhost:8081";

    public UpdateSubscriptionService setBackendURL(String urlBase) {
        this.backendUrl = urlBase;
        return this;
    }

    public ResponseEntity<Client> updateClient(Client client) {
        HttpEntity<Client> request = new HttpEntity<>(client);
        try {
            return new RestTemplate().exchange(getBackendUrl() + "/client-provider/client", HttpMethod.PUT, request, Client.class);
        } catch (HttpClientErrorException httpException) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }

}
