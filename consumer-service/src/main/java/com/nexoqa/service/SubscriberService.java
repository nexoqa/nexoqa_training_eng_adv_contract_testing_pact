package com.nexoqa.service;

import com.nexoqa.model.Client;
import com.nexoqa.model.User;
import lombok.Getter;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;

@Service
@Getter
public class SubscriberService {

    private String backendUrl = "http://localhost:8081";

    public SubscriberService setBackendURL(String urlBase) {
        this.backendUrl = urlBase;
        return this;
    }

    public ResponseEntity<Client> subscribeUser(User user) {
        HttpEntity<User> request = new HttpEntity<>(user);
        try {
            return new RestTemplate().exchange(getBackendUrl() + "/client-provider/client", HttpMethod.POST, request, Client.class);
        } catch (HttpClientErrorException httpException) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }


    public ResponseEntity<Void> unSubscribeUser(User user) {
        HttpEntity<User> request = new HttpEntity<>(user);
        try {
            return new RestTemplate().exchange(getBackendUrl() + "/client-provider/client", HttpMethod.DELETE, request, Void.class);
        } catch (HttpClientErrorException httpException) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }
}
