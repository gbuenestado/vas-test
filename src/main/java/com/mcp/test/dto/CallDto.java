package com.mcp.test.dto;

import java.io.Serializable;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.mcp.test.dto.enums.StatusCode;

@JsonPropertyOrder({"message_type", "timestamp", "origin", "destination", "duration", "status_code", "status_description"})
public class CallDto extends MessageDto implements Serializable {

	private static final long serialVersionUID = 3837944042737927104L;
	
	@NotNull(message = "duration cannot be null")
	private Double duration;
	
	@JsonProperty("status_code")
	@NotNull(message = "statusCode cannot be null")
	private StatusCode statusCode;
	
	@JsonProperty("status_description")
	@NotNull(message = "statusDescription cannot be null")
	@NotBlank(message = "statusDescription cannot be blank")
	private String statusDescription;
	
	public Double getDuration() {
		return duration;
	}

	public void setDuration(Double duration) {
		this.duration = duration;
	}

	public StatusCode getStatusCode() {
		return statusCode;
	}

	public void setStatusCode(StatusCode statusCode) {
		this.statusCode = statusCode;
	}

	public String getStatusDescription() {
		return statusDescription;
	}

	public void setStatusDescription(String statusDescription) {
		this.statusDescription = statusDescription;
	}
	
}
