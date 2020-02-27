# Template for Java Microprofile 3.x event driven microservice

## Code explanations

The code is started from the miroprofile [code generator](https://start.microprofile.io/) by selecting microprofile 3.3, Java 8 and the following specifications: Config, Fault tolerance, Metrics, health checks, OpenAPI.

To represent a simple business entity, we use the order management domain. The code is structured using a domain driven approach, with domain, infrastructure and app layers.

The webapp is `ibm.gse.eda.app.MainApplication.java` and uses the webcontext name as application path.

The application starts a Kafka consumer to receive events from `orders` topic. The main design approach is to use one kafka topic to publish events on the business entity life cycle. In the domain layer we have the business entity `OrderEntity` and the service responsible to implement the business logic to manage this entity: `OrderService`.

As this application exposes API, the data definition for the API payload is done in the `dto` package. The class `OrderParameters` represents a data transfer object view of the order entity.

The ReadinessCheck is ready only if the order event agent, listening to events is running. The health

The application properties are defined in `src/main/resources/META-INF/microprofile-config.properties` and declare Kafka related properties for consumer and producer.

The event consumers is started via a ServletContextListener implementation, the `AgentsInitializer` and the agent class is `OrderEventsAgent`.

## Build & Run

### Pre-requisite

You need to have:

* maven
* mkdocs
* Have a kafka cluster accessible on the cloud or on private servers. Get the brokers URLs and the API key, You need to set the following environment variables: **KAFKA_BROKERS**, and **KAFKA_APIKEY**: use the `setenv.sh.templ` file under the `scripts` folder, and rename this file as `setenv.sh`, then use the command: `source scripts/setenv.sh` to set the environment variables inside the terminal session where you will start open liberty server. 
The environment variable **TRUSTSTORE_ENABLED** is set to true, only if the Kafka broker uses TLS certificate.

### Build

Build and execute unit and integration tests:

```shell
mvn clean package
```

Build with docker and create a docker image: `docker build -t ibmgse/KafkaPlay .`

### Test

`mvn test`

Bypass integration tests:

`mvn install -DskipITs`

### Run

Start the server: `mvn liberty:run` or in debug mode: 

Then stop the server: `mvn liberty:stop`

Or using docker:

```shell
docker rm -f KafkaPlay || true && docker run -d -p 8080:8080 -p 4848:4848 --name KafkaPlay ibmgse/KafkaPlay
```

### Remote Debug

Start liberty in debug mode: `mvn liberty:debug-server` then, in Eclipse use the `Debug Configuration menu > Remote Java application >` and then replace the port number 8000 to the one liberty is listening to (7777).

## Open API

Accessing to the [http://localhost:9080/openapi/ui/](http://localhost:9080/openapi/ui/) to see the proposed API.

## Service documentation

We encourage to use mkdocs to present a nice explanation of what the application does. 


### Building this booklet locally

The content of this repository is written with markdown files, packaged with [MkDocs](https://www.mkdocs.org/) and can be built into a book-readable format by MkDocs build processes.

1. Install MkDocs locally following the [official documentation instructions](https://www.mkdocs.org/#installation).
1. Install Material plugin for mkdocs:  `pip install mkdocs-material` 
1. Start the documentation server with: `mkdocs serve`
1. Go to `http://127.0.0.1:8000/` in your browser.

### Pushing the book to GitHub Pages

1. Ensure that all your local changes to the `master` branch have been committed and pushed to the remote repository.
   1. `git push origin master`
2. Ensure that you have the latest commits to the `gh-pages` branch, so you can get others' updates.

	```bash
	git checkout gh-pages
	git pull origin gh-pages
	
	git checkout master
	```

3. Run `mkdocs gh-deploy` from the root openshift-studies directory.

## What can be done from there

This getting started code needs to be changed to support new application. Basically we think, you need to do the following:

* Define your new business entity, and get inspiration from `OrderEntity` class
* Define the life cycle for this entity: which should match to a new set of constants and operation in the service to change the states of the business entity.
* Define the AVRO schema for the events. 

