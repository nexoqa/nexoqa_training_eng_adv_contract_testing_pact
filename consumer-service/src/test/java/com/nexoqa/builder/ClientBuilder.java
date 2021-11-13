package com.nexoqa.builder;

import com.nexoqa.model.Client;
import com.nexoqa.model.User;
import org.springframework.stereotype.Service;

import static com.nexoqa.utils.BuilderUtils.generateRandomInteger;
import static com.nexoqa.utils.BuilderUtils.generateRandomString;

@Service
public class ClientBuilder {

    public Client build() {

        return new Client(
                new User(
                        generateRandomString(5),
                        generateRandomString(5),
                        "Street " + generateRandomString(6),
                        generateRandomInteger(18, 99),
                        generateRandomInteger(911111111, 999999999)
                ),
                true
        );
    }

}
