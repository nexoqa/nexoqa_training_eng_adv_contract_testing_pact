package com.nexoqa.builder;

import com.nexoqa.model.Client;
import com.nexoqa.model.Clients;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class SubscribersBuilder {

    @Autowired
    private ClientBuilder clientBuilder;

    public Clients build() {

        List<Client> clients = new ArrayList<Client>() {{
            add(clientBuilder.build());
        }};

        return new Clients(clients);
    }

}
