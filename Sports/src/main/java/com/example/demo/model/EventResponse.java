package com.example.demo.model;

public class EventResponse {

	
	private String id;
	private EventType eventType;
	public EventResponse(String id, EventType eventType) {
		super();
		this.id = id;
		this.eventType = eventType;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public EventType getEventType() {
		return eventType;
	}
	public void setEventType(EventType eventType) {
		this.eventType = eventType;
	}
	
	
	
}
