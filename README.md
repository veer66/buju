# buju
A simple geosparql server


## Build
````
mvn clean compile
````

## Example

### Run server
````
mvn exec:java -Dexec.mainClass="rocks.veer66.AppKt"
````

### Query
````
curl -v -XPOST localhost:3012/query --data '@example/ex1.sparql'
````