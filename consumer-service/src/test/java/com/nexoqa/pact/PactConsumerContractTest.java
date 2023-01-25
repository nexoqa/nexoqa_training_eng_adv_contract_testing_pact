package com.nexoqa.pact;

import au.com.dius.pact.consumer.MockServer;
import au.com.dius.pact.consumer.dsl.PactDslJsonBody;
import au.com.dius.pact.consumer.dsl.PactDslWithProvider;
import au.com.dius.pact.consumer.junit.PactProviderRule;
import au.com.dius.pact.consumer.junit.PactVerification;
import au.com.dius.pact.core.model.RequestResponsePact;
import au.com.dius.pact.core.model.annotations.Pact;
import com.google.gson.Gson;
import com.nexoqa.builder.ClientBuilder;
import com.nexoqa.builder.SubscribersBuilder;
import com.nexoqa.model.User;
import com.nexoqa.model.Client;
import com.nexoqa.model.Clients;
import com.nexoqa.service.EmailService;
import com.nexoqa.service.SubscriberService;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertTrue;

@RunWith(SpringRunner.class)
@SpringBootTest
public class PactConsumerContractTest {

    @Autowired
    private ClientBuilder clientBuilder;

    @Autowired
    private SubscribersBuilder subscribersBuilder;

    @Autowired
    private SubscriberService subscriberService;

    @Autowired
    private EmailService emailService;

    private Clients testClientsData;

    private static final String END_POINT_CLIENT = "/client-provider/client";
    private static final String END_POINT_CLIENTS = "/client-provider/clients";

    @Rule
    public PactProviderRule rule = new PactProviderRule("client-provider", this);


    @Pact(provider = "client-provider", consumer = "consumer-service")
    public RequestResponsePact createPact(PactDslWithProvider builder) {
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");

        testClientsData = subscribersBuilder.build();

        PactDslJsonBody clients = new PactDslJsonBody()
                .minArrayLike("clients", 1)
                .booleanType("activated")
                .object("user")
                .stringType("name")
                .stringType("lastName")
                .stringType("address")
                .integerType("age")
                .integerType("phoneNumber");

        PactDslJsonBody client = new PactDslJsonBody()
                .booleanType("activated")
                .object("user")
                .stringType("name")
                .stringType("lastName")
                .stringType("address")
                .integerType("age")
                .integerType("phoneNumber");

        PactDslJsonBody user = new PactDslJsonBody()
                .stringType("name")
                .stringType("lastName")
                .stringType("address")
                .integerType("age")
                .integerType("phoneNumber");

        return builder
                .given("test consumer service")
                    .uponReceiving("a request to create a new client")
                        .path(END_POINT_CLIENT)
                        .method("POST")
                        .headers(headers)
                        .body(new Gson().toJson(testClientsData.getClients().get(0).getUser(), User.class))
                    .willRespondWith()
                        .headers(headers)
                        .status(200)
                        .body(client)
                        .body(new Gson().toJson(testClientsData.getClients().get(0), Client.class))
                    .uponReceiving("a request to notify all active clients")
                        .path(END_POINT_CLIENTS)
                        .method("GET")
                    .willRespondWith()
                        .status(200)
                        .headers(headers)
                        .body(clients)
                .toPact();
    }

    @Pact(provider = "client-provider", consumer = "consumer-service")
    public RequestResponsePact unSubscribePact(PactDslWithProvider builder) {
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");

        testClientsData = subscribersBuilder.build();

        PactDslJsonBody client = new PactDslJsonBody()
                .booleanType("activated")
                .object("user")
                .stringType("name")
                .stringType("lastName")
                .stringType("address")
                .integerType("age")
                .integerType("phoneNumber");

        PactDslJsonBody user = new PactDslJsonBody()
                .stringType("name")
                .stringType("lastName")
                .stringType("address")
                .integerType("age")
                .integerType("phoneNumber");

        return builder
                .given("unsubscribe consumer service")
                    .uponReceiving("a request to create a new client")
                        .path(END_POINT_CLIENT)
                        .method("POST")
                        .headers(headers)
                        .body(new Gson().toJson(testClientsData.getClients().get(0).getUser(), User.class))
                    .willRespondWith()
                        .headers(headers)
                        .status(200)
                        .body(client)
                        .body(new Gson().toJson(testClientsData.getClients().get(0), Client.class))
                    .uponReceiving("unsubscribe user")
                        .path(END_POINT_CLIENT)
                        .headers(headers)
                        .body(new Gson().toJson(testClientsData.getClients().get(0).getUser(), User.class))
                        .method("DELETE")
                    .willRespondWith()
                        .status(200)
                    .toPact();
    }

    @Pact(provider = "client-provider", consumer = "consumer-service")
    public RequestResponsePact unSubscribeFailPact(PactDslWithProvider builder) {
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");

        testClientsData = subscribersBuilder.build();


        return builder
                .given("unsubscribe consumer service fail")
                .uponReceiving("unsubscribe user fail")
                .path(END_POINT_CLIENT)
                .headers(headers)
                .body(new Gson().toJson(testClientsData.getClients().get(0).getUser(), User.class))
                .method("DELETE")
                .willRespondWith()
                .status(409)
                .toPact();
    }


    @Test
    @PactVerification(value = "client-provider", fragment = "createPact")
    public void runTest() {
        MockServer server = rule.getMockServer();
        subscriberService.setBackendURL("http://localhost:" + server.getPort());
        ResponseEntity<Client> subscribedClient = subscriberService.subscribeUser(testClientsData.getClients().get(0).getUser());
        assertTrue(subscribedClient.getStatusCode().is2xxSuccessful());

        emailService.setBackendURL("http://localhost:" + server.getPort());
        ResponseEntity<List<Client>> notifiedClients = emailService.notifyActiveUsers();
        assertTrue(notifiedClients.getStatusCode().is2xxSuccessful());
    }

    @Test
    @PactVerification(value = "client-provider", fragment = "unSubscribePact")
    public void runUnSubscribeTest() {
        MockServer server = rule.getMockServer();
        subscriberService.setBackendURL("http://localhost:" + server.getPort());
        ResponseEntity<Client> subscribedClient = subscriberService.subscribeUser(testClientsData.getClients().get(0).getUser());
        assertTrue(subscribedClient.getStatusCode().is2xxSuccessful());

        emailService.setBackendURL("http://localhost:" + server.getPort());
        ResponseEntity<Void> unSubscribeClient = subscriberService.unSubscribeUser(testClientsData.getClients().get(0).getUser());
        assertTrue(unSubscribeClient.getStatusCode().is2xxSuccessful());
    }

    @Test
    @PactVerification(value = "client-provider", fragment = "unSubscribeFailPact")
    public void runUnSubscribeTestFail() {
        MockServer server = rule.getMockServer();
        subscriberService.setBackendURL("http://localhost:" + server.getPort());

        ResponseEntity<Void> unSubscribeClient = subscriberService.unSubscribeUser(testClientsData.getClients().get(0).getUser());
        assertTrue(unSubscribeClient.getStatusCode().is4xxClientError());
    }

}
