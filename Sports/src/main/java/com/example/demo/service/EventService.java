package com.example.demo.service;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Service;

import com.example.demo.dao.EventRepo;
import com.example.demo.model.Event;
import com.example.demo.model.EventResponse;
import com.example.demo.model.Status;

@Service
public class EventService {
	
    private static final Logger logger = LoggerFactory.getLogger(EventService.class);

    @Autowired
    EventRepo eventrepo;
	
    @Autowired 
    private ThreadPoolTaskScheduler taskScheduler;
	
    @Autowired
    private KafkaProducerService kafkaProducerService;
	
    @Autowired 
    private APIClientService apiClientService;
	
    private final Map<String, ScheduledFuture<?>> activeTimers = new ConcurrentHashMap<>();

    public Event saveStatus(Event event) {
        String id = event.getEventID();
        Status status = event.getStatus();
        logger.info("Event id:{}, status:{}", id, status);
        eventrepo.save(event);
        
        if (Status.LIVE.equals(status)) {
            startTimer(id);
        } else {
            stopTimer(id);
        }
        return event;
    }
	
    private void startTimer(String id) {
        if (activeTimers.containsKey(id)) return;

        ScheduledFuture<?> task = taskScheduler.scheduleAtFixedRate(() -> {
            try {
                System.out.println(">>> [Sporty API Call] Fetching data for Live Event: " + id);
                EventResponse eventResponse = apiClientService.fetchEventData(id);
                
                if (eventResponse != null) {
                    kafkaProducerService.publishEventUpdate(id, eventResponse);
                    logger.info("Successfully published to kafka for Event id {}", id);
                } else {
                    logger.warn("Received empty response from API for Event id {}", id);
                }
            } catch (Exception e) {
                logger.error("CRITICAL: Background task failed for Event {}. Reason: {}", id, e.getMessage());
                // The timer continues to run and will try again in 10 seconds.
            }
        }, Duration.ofSeconds(10));

        activeTimers.put(id, task);
    }
	
    private void stopTimer(String id) {
        ScheduledFuture<?> task = activeTimers.remove(id);
        if (task != null) {
            task.cancel(false); 
            logger.info("### Stopped timer for Event: {} as event is NON_LIVE", id);
        }
    }
}