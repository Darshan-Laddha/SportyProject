package com.example.demo.service;

import java.rmi.RemoteException;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.example.demo.model.EventResponse;
import com.example.demo.model.EventType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.annotation.Backoff;  
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;

@Service
public class APIClientService {

	
		private static final Logger logger = LoggerFactory.getLogger(APIClientService.class);
	    @Autowired
	    private RestTemplate restTemplate;

	    // The URL where your SimulatorController is running
	    private final String SIMULATOR_URL = "http://localhost:8081/simulator/event/";

	    @Retryable(
	        retryFor = { Exception.class }, 
	        maxAttempts = 3, 
	        backoff = @Backoff(delay = 5000)
	    )
	    public EventResponse fetchEventData(String id) {
	        logger.info(">>> [API Client] Fetching from Simulator for ID: {}", id);
	        
	        // This actually hits your new SimulatorController
	        return restTemplate.getForObject(SIMULATOR_URL + id, EventResponse.class);
	    }

	    @Recover
	    public EventResponse recover(Exception e, String id) {
	        logger.error(">>> [Recover] Simulator is DOWN. Returning fallback for ID: {}" , id);
	        return new EventResponse(id, EventType.EMERGENCY_ALARM);
	    }

}
