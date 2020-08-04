package com.mcp.test.dto;

import java.io.Serializable;

public class AverageDto implements Serializable {

	private static final long serialVersionUID = -498903726135609881L;
	
	private String country;
	private double duration;
	
	public String getCountry() {
		return country;
	}
	
	public void setCountry(String country) {
		this.country = country;
	}
	
	public double getDuration() {
		return duration;
	}
	
	public void setDuration(double duration) {
		this.duration = duration;
	}
	
}
