package com.mcp.test.dto;

import java.io.Serializable;

public class CallNumberDto implements Serializable {

	private static final long serialVersionUID = -705359452150318175L;
	
	private String country;
	private int origin;
	private int destination;
	
	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public int getOrigin() {
		return origin;
	}
	
	public void setOrigin(int origin) {
		this.origin = origin;
	}
	
	public int getDestination() {
		return destination;
	}
	
	public void setDestination(int destination) {
		this.destination = destination;
	}
	
}
