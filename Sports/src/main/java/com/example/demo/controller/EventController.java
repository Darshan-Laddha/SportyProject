package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.model.Event;
import com.example.demo.model.EventRequest;
import com.example.demo.service.EventService;

@RestController
@RequestMapping("/events")
public class EventController {

    @Autowired
    private EventService service;

    @PostMapping("/status")
    public ResponseEntity<?> updateStatus(@RequestBody EventRequest eventrequest) {
    	if(eventrequest.getEventId() ==null || eventrequest.getStatus() == null) {
    		return ResponseEntity.badRequest().body("Event ID and status as LIVE OR NON_LIVE must be passed");
    	}
    	Event event = new Event(eventrequest.getEventId(), eventrequest.getStatus());
    	service.saveStatus(event);
        return ResponseEntity.ok(event);
    }
}