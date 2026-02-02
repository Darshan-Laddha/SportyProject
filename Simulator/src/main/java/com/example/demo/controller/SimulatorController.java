package com.example.demo.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.model.EventResponse;
import com.example.demo.model.EventType;

@RestController
@RequestMapping("/simulator")
public class SimulatorController {
	
	@GetMapping("/event/{eventId}")
	public ResponseEntity<EventResponse> getEvent(@PathVariable("eventId") String eventId){
		EventType eventType = EventType.MATCH_UPDATE;
		if(eventId.equals("1")) {
			eventType = EventType.SCORE_UPDATE;
		}
		if(eventId.equals("2")) {
			eventType = EventType.OVERS_UPDATE;
		}
		EventResponse eventResponse = new EventResponse(eventId, eventType);
		return ResponseEntity.ok().body(eventResponse) ;
	}

}
