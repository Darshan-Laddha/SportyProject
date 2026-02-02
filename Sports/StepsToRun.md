# Steps to Run and Test

This guide covers the execution of the dual-app setup: the **Sports Tracker (Main App)** and the **External Service Simulator**.

## 🛠️ Execution Steps

### 1. Start the Simulator App
The Simulator acts as the external "Sporty API" and must be running for the happy path.
* **Port:** `8081`
* **Command:** Navigate to the simulator folder and run:
  ```bash
  mvn spring-boot:run
  
### 2. Start the SportsApp (make sure Kafka broker has started succesfully before starting this app)
The Simulator acts as the external "Sporty API" and must be running for the happy path.
* **Port:** `8080` (you can give any port)
* **Command:** Navigate to the simulator folder and run:
  ```bash
  mvn spring-boot:run
  
### 3. Happy path testing
curl --location 'http://localhost:8080/events/status' \
--header 'Content-Type: application/json' \
--data '{
    "eventID":"1",
    "status":"LIVE"
}'

and check the consumer terminal (every 10 seconds you should be able to see
{"id":"1","eventType":"SCORE_UPDATE"}

and it should roll periodically

Similarly for eventID: "2" you should be able to see OVERS_UPDATE

### 4. Various other scenarios

** Publishing a non live event:**
For any published event which is marked live or any new event which is non live
Hit the curl
```bash
curl --location 'http://localhost:8080/events/status' \
--header 'Content-Type: application/json' \
--data '{
    "eventID":"1",
    "status":"NON_LIVE"
}
The eventID which is marked NON_LIVE should NOT roll periodically on the consumer

** Testing unhappy flow **
* ** Stop the Simulator App **
Now check the consumer
We should see EMERGENCY_ALARM event indicating that the external application isnt functionaing as expected

* ** Stop the kafka broker **
Check the Sports application logs
It should say ">>> [FAILURE] Attempt failed for Event:eventId,Retrying..."








  
