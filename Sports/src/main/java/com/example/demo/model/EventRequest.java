package com.example.demo.model;

public class EventRequest {
    private String eventID;
    private Status status;
	public EventRequest(String eventID, Status status) {
		super();
		this.eventID = eventID;
		this.status = status;
	}
	public String getEventId() {
		return eventID;
	}
	public void setEventId(String eventName) {
		this.eventID = eventName;
	}
	public Status getStatus() {
		return status;
	}
	public void setStatus(Status status) {
		this.status = status;
	}
    
    
    
    // Getters and Setters only for these two
}