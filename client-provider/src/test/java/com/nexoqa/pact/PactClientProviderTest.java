package com.nexoqa.pact;

import au.com.dius.pact.provider.junit.PactRunner;
import au.com.dius.pact.provider.junit.Provider;
import au.com.dius.pact.provider.junit.State;
import au.com.dius.pact.provider.junit.loader.PactBroker;
import au.com.dius.pact.provider.junit.loader.PactUrl;
import au.com.dius.pact.provider.junit.target.HttpTarget;
import au.com.dius.pact.provider.junit.target.Target;
import au.com.dius.pact.provider.junit.target.TestTarget;
import com.nexoqa.Application;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.springframework.boot.SpringApplication;
import org.springframework.web.context.ConfigurableWebApplicationContext;


@RunWith(PactRunner.class)
@Provider("client-provider")
@PactBroker(
        host = "localhost", port = "80", consumers = "consumer-service"
)
public class PactClientProviderTest {

    @TestTarget
    public final Target target = new HttpTarget("http", "localhost", 8080, "/");

    private static ConfigurableWebApplicationContext application;

    @BeforeClass
    public static void start() {
        application = (ConfigurableWebApplicationContext) SpringApplication.run(Application.class);
    }

    @State("test consumer service")
    public void toGetState() {
    }

    @State("unsubscribe consumer service")
    public void toUnsubscribeGetState() {
    }

    @State("unsubscribe consumer service fail")
    public void toUnsubscribeFailGetState() {
    }

}
