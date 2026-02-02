package com.example.demo.service;

import com.example.demo.model.EventResponse;

import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.retry.annotation.Backoff;

@Service
public class KafkaProducerService {

    private static final Logger logger = LoggerFactory.getLogger(KafkaProducerService.class);

    @Autowired
    private KafkaTemplate<String, EventResponse> kafkaTemplate;

    private static final String TOPIC = "sporty-event-updates";

    @Retryable(
        retryFor = Exception.class, 
        maxAttempts = 3, 
        backoff = @Backoff(delay = 2000)
    )
    public void publishEventUpdate(String eventId, EventResponse payload) {
        try {
            logger.info(">>> [Attempt] Background publish for Event: {}", eventId);

            kafkaTemplate.send(TOPIC, eventId, payload).get(5, TimeUnit.SECONDS);

            logger.info(">>> [SUCCESS] Event {} reached Kafka", eventId);
        } catch (Exception e) {
            logger.error(">>> [FAILURE] Attempt failed for Event {}. Retrying...", eventId);
            throw new RuntimeException(e); // Throwing triggers @Retryable
        }
    }

    @Recover
    public void handlePublishFailure(RuntimeException e, String eventId, EventResponse payload) {
        logger.error(">>> [CRITICAL] All retries exhausted for Event {}. Moving to fallback.", eventId);
        // Save to DB or file here
    }
}