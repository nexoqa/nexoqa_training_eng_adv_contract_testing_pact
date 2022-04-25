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
import com.nexoqa.model.Client;
import com.nexoqa.model.Clients;
import com.nexoqa.model.DeleteResponse;
import com.nexoqa.model.User;
import com.nexoqa.service.EmailService;
import com.nexoqa.service.SubscriberService;
import com.nexoqa.service.UnSubscriberService;
import com.nexoqa.service.UpdateSubscriptionService;

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
import static org.junit.jupiter.api.Assertions.assertEquals;

@RunWith(SpringRunner.class)
@SpringBootTest
public class PactConsumerContractTest {

    @Autowired
    private ClientBuilder clientBuilder;

    @Autowired
    private SubscribersBuilder subscribersBuilder;

    @Autowired
    private SubscribersBuilder unSubscribersBuilder;

    @Autowired
    private SubscribersBuilder updateSubscriptionBuilder;

    @Autowired
    private SubscriberService subscriberService;

    @Autowired
    private UnSubscriberService unSubscriberService;

    @Autowired
    private EmailService emailService;

    @Autowired
    private UpdateSubscriptionService updateSubscriptionService;

    private Clients testClientsData;

    private Clients testUnSubscribeClientsData;

    private Clients updateSubscriptionData;

    private static final String END_POINT_CLIENT = "/client-provider/client";
    private static final String END_POINT_CLIENTS = "/client-provider/clients";

    @Rule
    public PactProviderRule rule = new PactProviderRule("client-provider", this);


    @Pact(provider = "client-provider", consumer = "consumer-service")
    public RequestResponsePact subscribePact(PactDslWithProvider builder) {
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");

        testClientsData = subscribersBuilder.build();

        Client clientData = testClientsData.getClients().get(0);

        User userData = clientData.getUser();


        PactDslJsonBody clients = new PactDslJsonBody()
                .minArrayLike("clients", 1)
                .booleanType("activated")
                .object("user")
                .stringType("name")
                .stringType("lastName")
                .stringType("address")
                .integerType("age")
                .integerType("phoneNumber");


        PactDslJsonBody user = new PactDslJsonBody()
                .stringType("name", userData.getName())
                .stringType("lastName", userData.getLastName())
                .stringType("address", userData.getAddress())
                .integerType("age", userData.getAge())
                .integerType("phoneNumber", userData.getPhoneNumber());

        return builder
                .given("test consumer service -  subscribe")
                .uponReceiving("a request to create a new client")
                .path(END_POINT_CLIENT)
                .method("POST")
                .headers(headers)
                .body(user)
                .willRespondWith()
                .headers(headers)
                .status(200)
                .body(new Gson().toJson(clientData, Client.class))
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

            testUnSubscribeClientsData = unSubscribersBuilder.build();

            Client clientData = testUnSubscribeClientsData.getClients().get(0);

            User userData = clientData.getUser();

            PactDslJsonBody user = new PactDslJsonBody()
                            .stringType("name", userData.getName())
                            .stringType("lastName", userData.getLastName())
                            .stringType("address", userData.getAddress())
                            .integerType("age", userData.getAge())
                            .integerType("phoneNumber", userData.getPhoneNumber());

            PactDslJsonBody client = new PactDslJsonBody()
                            .object("user", user)
                            .booleanType("activated", true);

            PactDslJsonBody deleteResponse = new PactDslJsonBody()
                            .booleanType("ok", true);

            return builder
                            .given("test consumer service  -  unsubscribe")
                            .uponReceiving("a request to create a new client to unsubscribe")
                            .path(END_POINT_CLIENT)
                            .method("POST")
                            .headers(headers)
                            .body(user)
                            .willRespondWith()
                            .headers(headers)
                            .status(200)
                            .body(new Gson().toJson(clientData, Client.class))
                            .uponReceiving("unsubscribe a client")
                            .path(END_POINT_CLIENT)
                            .method("DELETE")
                            .headers(headers)
                            .body(client)
                            .willRespondWith()
                            .status(200)
                            .headers(headers)
                            .body(deleteResponse)
                            .toPact();
    }
    
    @Pact(provider = "client-provider", consumer = "consumer-service")
    public RequestResponsePact updatePact(PactDslWithProvider builder) {
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");

        updateSubscriptionData = updateSubscriptionBuilder.build();

        Client clientData = updateSubscriptionData.getClients().get(0);

        User userData = clientData.getUser();


        PactDslJsonBody user = new PactDslJsonBody()
                .stringType("name", userData.getName())
                .stringType("lastName", userData.getLastName())
                .stringType("address", userData.getAddress())
                .integerType("age", userData.getAge())
                        .integerType("phoneNumber", userData.getPhoneNumber());
                
        PactDslJsonBody userUpdated = new PactDslJsonBody()
                .stringType("name", userData.getName())
                .stringType("lastName", "Last Name updated")
                .stringType("address", "Address updated")
                .integerType("age", userData.getAge()+1)
                .integerType("phoneNumber", userData.getPhoneNumber()-100);

        PactDslJsonBody client = new PactDslJsonBody()
            .object("user", userUpdated)
            .booleanType("activated", true);

        return builder
                .given("test consumer service  -  update subscription")
                .uponReceiving("a request to create a new client to update")
                .path(END_POINT_CLIENT)
                .method("POST")
                .headers(headers)
                .body(user)
                .willRespondWith()
                .headers(headers)
                .status(200)
                .body(new Gson().toJson(clientData, Client.class))
                .uponReceiving("update a client")
                .path(END_POINT_CLIENT)
                .method("PUT")
                .headers(headers)
                .body(client)
                .willRespondWith()
                .status(200)
                .headers(headers)
                .body(client)
                .toPact();
    }

    @Pact(provider = "client-provider", consumer = "consumer-service")
    public RequestResponsePact unSubscribeFailPact(PactDslWithProvider builder) {
            Map<String, String> headers = new HashMap<>();
            headers.put("Content-Type", "application/json");

            User userData = new User();
            userData.setName("non-exist");
            userData.setLastName("Last Name updated");
            userData.setAddress("Address updated");
            userData.setAge(40);
            userData.setPhoneNumber(12345678);

            PactDslJsonBody user = new PactDslJsonBody()
                            .stringType("name", userData.getName())
                            .stringType("lastName", userData.getLastName())
                            .stringType("address", userData.getAddress())
                            .integerType("age", userData.getAge())
                            .integerType("phoneNumber", userData.getPhoneNumber());

            PactDslJsonBody client = new PactDslJsonBody()
                            .object("user", user)
                            .booleanType("activated", true);

            return builder
                            .given("test consumer service  -  unsubscription fail")
                            .uponReceiving("unsubscription fail")
                            .path(END_POINT_CLIENT)
                            .method("DELETE")
                            .headers(headers)
                            .body(client)
                            .willRespondWith()
                            .status(409)
                            .toPact();
    }
    
    @Pact(provider = "client-provider", consumer = "consumer-service")
    public RequestResponsePact updateSubscriptionFailPact(PactDslWithProvider builder) {
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");

        User userData = new User();
        userData.setName("non-exist");
        userData.setLastName("Last Name updated");
        userData.setAddress("Address updated");
        userData.setAge(40);
        userData.setPhoneNumber(12345678);


        PactDslJsonBody user = new PactDslJsonBody()
                .stringType("name", userData.getName())
                .stringType("lastName", userData.getLastName())
                .stringType("address", userData.getAddress())
                .integerType("age", userData.getAge())
                .integerType("phoneNumber", userData.getPhoneNumber());
                

        PactDslJsonBody client = new PactDslJsonBody()
            .object("user", user)
            .booleanType("activated", true);

        return builder
                .given("test consumer service  -  update fail")
                .uponReceiving("update fail")
                .path(END_POINT_CLIENT)
                .method("PUT")
                .headers(headers)
                .body(client)
                .willRespondWith()
                .status(409)
                .toPact();
    }

    @Test
    @PactVerification(value = "client-provider", fragment = "subscribePact")
    public void runSubscribePactTest() {
        MockServer server = rule.getMockServer();
        subscriberService.setBackendURL("http://localhost:" + server.getPort());
        ResponseEntity<Client> subscribedClient = subscriberService
                .subscribeUser(testClientsData.getClients().get(0).getUser());
        assertTrue(subscribedClient.getStatusCode().is2xxSuccessful());

        emailService.setBackendURL("http://localhost:" + server.getPort());
        ResponseEntity<List<Client>> notifiedClients = emailService.notifyActiveUsers();
        assertTrue(notifiedClients.getStatusCode().is2xxSuccessful());
    }
    
    @Test
    @PactVerification(value = "client-provider", fragment = "unSubscribePact")
    public void runUnSubscribePactTest() {
            MockServer server = rule.getMockServer();
            subscriberService.setBackendURL("http://localhost:" + server.getPort());
            ResponseEntity<Client> subscribedClient = subscriberService
                            .subscribeUser(testUnSubscribeClientsData.getClients().get(0).getUser());
            assertTrue(subscribedClient.getStatusCode().is2xxSuccessful());

            unSubscriberService.setBackendURL("http://localhost:" + server.getPort());
            ResponseEntity<DeleteResponse> unSubscribedClient = unSubscriberService
                            .unSubscribeClient(testUnSubscribeClientsData.getClients().get(0));
            assertTrue(unSubscribedClient.getStatusCode().is2xxSuccessful());

    }

    @Test
    @PactVerification(value = "client-provider", fragment = "unSubscribeFailPact")
    public void runUnSubscribeFailPactTest() {
        MockServer server = rule.getMockServer();

        User user = new User();
        user.setName("non-exist");
        user.setLastName("Last Name updated");
        user.setAddress("Address updated");
        user.setAge(40);
        user.setPhoneNumber(12345678);
        
        Client client = new Client();
        client.setUser(user);
        client.setActivated(true);

        unSubscriberService.setBackendURL("http://localhost:" + server.getPort());
        ResponseEntity<DeleteResponse> unSubscribedClient = unSubscriberService
                        .unSubscribeClient(client);
        assertTrue(unSubscribedClient.getStatusCode().is4xxClientError());

    }
    
    @Test
    @PactVerification(value = "client-provider", fragment = "updatePact")
    public void runUpdatePactTest() {
            MockServer server = rule.getMockServer();
            subscriberService.setBackendURL("http://localhost:" + server.getPort());
            ResponseEntity<Client> subscribedClient = subscriberService
                            .subscribeUser(updateSubscriptionData.getClients().get(0).getUser());
            assertTrue(subscribedClient.getStatusCode().is2xxSuccessful());

            User user = updateSubscriptionData.getClients().get(0).getUser();
            user.setLastName("Last Name updated");
            user.setAddress("Address updated");
            user.setAge(user.getAge() + 1);
            user.setPhoneNumber(user.getPhoneNumber() - 100);

            Client client = new Client();
            client.setUser(user);
            client.setActivated(true);

            updateSubscriptionService.setBackendURL("http://localhost:" + server.getPort());
            ResponseEntity<Client> responseClient = updateSubscriptionService.updateClient(client);
            assertTrue(responseClient.getStatusCode().is2xxSuccessful());

            Client updatedClient = responseClient.getBody();
            assertEquals(client, updatedClient);
    }
    
    @Test
    @PactVerification(value = "client-provider", fragment = "updateSubscriptionFailPact")
    public void runUpdateSubscriptionFailPactTest() {
        MockServer server = rule.getMockServer();

        User user = new User();
        user.setName("non-exist");
        user.setLastName("Last Name updated");
        user.setAddress("Address updated");
        user.setAge(40);
        user.setPhoneNumber(12345678);
        
        Client client = new Client();
        client.setUser(user);
        client.setActivated(true);

        updateSubscriptionService.setBackendURL("http://localhost:" + server.getPort());
        ResponseEntity<Client> responseClient = updateSubscriptionService
                        .updateClient(client);
        assertTrue(responseClient.getStatusCode().is4xxClientError());

    }

}
