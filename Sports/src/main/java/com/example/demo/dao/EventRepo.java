package com.example.demo.dao;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import com.example.demo.model.Event;


public class EventRepo {
    // This is your "In-Memory Database"
    private final Map<String, Event> storage = new ConcurrentHashMap<>();

    public Event save(Event event) {
        storage.put(event.getEventID(), event);
        return event;
    }

    public Event findById(String eventId) {
        return storage.get(eventId);
    }
}
