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
docker-compose logs -f broker_app
```

# Shut down pact broker
```
cd pact-broker
docker-compose down
```

# Ejercicio

- El consumer-service requiere un endpoint de client provider para eliminar la subscripcion de un cliente.
- El consumer-service requiere un endpoint de client provider para actulizar la informacion la  de un cliente, el nombre del cliente es el identificador del cliente y no puede cambiarse.
- Verificar que al intentar modificar un cliente no existe el client provider retorna un error.
- Modificar que el nombre sea el identificador del cliente.


