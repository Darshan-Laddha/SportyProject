# 1. Application Overview

The **Sporty Live Event Tracker** is a high-availability integration service designed to bridge external sports data providers with internal downstream consumers. The application acts as a state-managed gateway that monitors event transitions and automates data synchronization.

## 🔄 Event Lifecycle Management
The system processes two distinct types of events:
* **Live Events:** Active matches or events (e.g., a Cricket match in progress) that require real-time monitoring.
* **Non-Live Events:** Scheduled or completed events that do not require active polling.

The application dynamically transitions between these states. Any live event can be downgraded to non-live, and vice-versa, which instantly attaches or detaches the background monitoring logic.



---

## ⏲️ The 10-Second Polling Job
When an event is identified as **LIVE**, the application orchestrates a background worker that executes the following sequence every 10 seconds:

### Step 1: External Data Retrieval
The worker calls a specialized external service using the unique **Event ID**. 
* *Example:* For Event ID `1`, it fetches the latest live score, player statistics, or match status.

### Step 2: Fan-out Data Distribution
To ensure high scalability, the fetched data is published to a dedicated Kafka topic: `sporty-event-updates`.
* **Architecture Advantage:** By using Kafka, we achieve a "Fan-out" pattern where any number of downstream consumers (UI dashboards, betting engines, notification services) can listen to the stream simultaneously without impacting the performance of the main application.



---

## 🛡️ Resilience & Fallback Logic
The application is built with a "Failure-First" mindset to ensure the 10-second pulse is never broken:

* **API Outages:** If the external service is down or returns a `5xx` error, the application enters a "Safe Mode."
* **Default Event Publishing:** Instead of failing silently or crashing the thread, the system generates and publishes a **Default Event Response** to the Kafka topic. This ensures downstream consumers know the system is still alive, even if the source data is temporarily unavailable.
* **Thread Survival:** All exceptions are localized to the specific Event Job, meaning a failure in "Match A" will never stop the monitoring of "Match B."



---
[⬅️ Back to Main README](README.md)
