package com.example.demo.model;

public class Event {

	private Long id;
	private String eventID;
	private Status status;

	public Event() {
	}

	public Event(String eventID, Status status) {
		this.eventID = eventID;
		this.status = status;
	}

	public String getEventID() {
		return eventID;
	}

	public void setEventID(String eventID) {
		this.eventID = eventID;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

}
