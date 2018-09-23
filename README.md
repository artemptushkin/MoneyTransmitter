# MoneyTransmitter
Example RESTFul API service without Spring for money transfer between accounts

Frameworks:
WildFly Swarm + Arquillian + Apache HttpCommons for tests

Build:
```console
mvn clean package
```

Run:

  from root folder
```console
java -jar target/money-transmitter-1.0-SNAPSHOT-swarm.jar
```
  or with maven
```console
mvn wildfly-swarm:run
```

Example:
```console
curl -i -H "Accept: application/json" -H "Content-Type: application/json" -X GET "http://localhost:8080/api/transfer?from=1&to=2&amount=50" - 200

curl -i -H "Accept: application/json" -H "Content-Type: application/json" -X GET "http://localhost:8080/api/transfer?from=1&to=1&amount=50" - 403
```
