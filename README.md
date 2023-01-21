# contract_testing_pact
Repository to learn contract testing using Java Pact Library

# Start up pact broker
```
cd pact-broker
docker-compose up -d
```
# See pact broker logs
```
cd pact-broker
docker-compose down
```

# Shut down pact broker
```
cd pact-broker
docker-compose down
```

# Exercise

- The consumer-service requires a client provider endpoint to unsubscribe a client.
- The consumer-service requires a client provider endpoint to update a client's information, the client's name is the client's identifier and cannot be changed.
- Verify that when trying to modify a client does not exist, the client provider returns an error.
- Modify that the name is the client identifier.
