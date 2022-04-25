package com.nexoqa.controller;

import com.nexoqa.model.Client;
import com.nexoqa.model.Clients;
import com.nexoqa.model.DeleteResponse;
import com.nexoqa.model.User;
import com.nexoqa.service.ClientService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;
import static org.springframework.web.bind.annotation.RequestMethod.DELETE;
import static org.springframework.web.bind.annotation.RequestMethod.PUT;

@RestController
public class ClientController {

    private static Logger logger = LogManager.getLogger(ClientController.class);

    @Autowired
    private ClientService clientService;

    @RequestMapping(value = "/client", method = GET, produces = "application/json")
    private @ResponseBody
    ResponseEntity<Client> getClient(@RequestParam(value = "name") String name) {
        logger.info("getting client -> " + name);
        Client requestedClient = clientService.getClient(name);
        return Optional
                .ofNullable(requestedClient)
                .map(client -> ResponseEntity.ok().body(requestedClient))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @RequestMapping(value = "/clients", method = GET, produces = "application/json")
    private @ResponseBody
    ResponseEntity<Clients> getClients() {
        Clients requestedClients = clientService.getClients();
        logger.info("getting clients -> " + requestedClients.toString());
        return Optional
                .ofNullable(requestedClients)
                .map(client -> ResponseEntity.ok().body(requestedClients))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }


    @RequestMapping(value = "/client", method = POST, produces = "application/json")
    private @ResponseBody
            ResponseEntity<Client> createClient(@RequestBody User user) {
        logger.info("creating client -> " + user.toString());

        if (clientService.isRegistered(user.getName())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        } else {
            Client client = clientService.createClient(user);
            return ResponseEntity
                    .ok()
                    .header("Content-type", "application/json")
                    .body(client);
        }

    }

    @RequestMapping(value = "/client", method = PUT, produces = "application/json")
    private @ResponseBody
            ResponseEntity<Client> updateClient(@RequestBody Client client) {
        logger.info("update client -> " + client.toString());

        if (clientService.isRegistered(client.getUser().getName())) {
            Client updatedClient = clientService.updateClient(client);
            return ResponseEntity
                    .ok()
                    .header("Content-type", "application/json")
                    .body(updatedClient);
        } else {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
            
        }

    }
    
    @RequestMapping(value = "/client", method = DELETE, produces = "application/json")
    private @ResponseBody
    ResponseEntity<DeleteResponse> deleteClient(@RequestBody Client client) {
        logger.info("delete client -> " + client.toString());

        if (clientService.isRegistered(client.getUser().getName())) {
            boolean response = clientService.deleteClient(client);
            return ResponseEntity
                    .ok()
                    .header("Content-type", "application/json")
                    .body(new DeleteResponse(response));
        } else {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }

    }

}
