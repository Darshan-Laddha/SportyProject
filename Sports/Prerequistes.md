# Event Tracking & Resilience System

A Java 17 / Spring Boot 3 integration service that polls external sports APIs and publishes live updates to Kafka. Built with a focus on high availability and fault tolerance.

---

## 🛠️ Project Setup

### 1. Prerequisites
* **Java:** JDK 17
* **Build Tool:** Maven 3.8+
* **Messaging:** Kafka Broker (Running on `localhost:9092`)

### 2. IDE Configuration
1. **Import:** Import as a Maven Project.
2. **SDK:** Ensure Project SDK and Language Level are set to **17**.
3. **Compiler:** Ensure `Annotation Processing` is enabled in your IDE settings.

### 3. Kafka Setup
Before triggering the below commands make sure to go to the path where you have installed kafka
Commands to start
1. **Zookeeper:**
bin\windows\zookeeper-server-start.bat config\zookeeper.properties

2. **Kafka Broker:**
bin\windows\kafka-server-start.bat config\server.properties

3. **Consumer:**
bin\windows\kafka-console-consumer.bat --topic sporty-event-updates --from-beginning --bootstrap-server localhost:9092


 



