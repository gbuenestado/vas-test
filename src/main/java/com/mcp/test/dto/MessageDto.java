package com.mcp.test.dto;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.Date;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonProperty;

public class MessageDto implements Serializable {

	private static final long serialVersionUID = 3831623493401042455L;
	
	@JsonProperty("message_type")
	@NotNull(message = "messageType cannot be null")
	@NotBlank(message = "messageType cannot be blank")
	private String messageType;
	
	@NotNull(message = "timestamp cannot be null")
	private Date timestamp;
	
	@NotNull(message = "origin cannot be null")
	private BigInteger origin;
	
	@NotNull(message = "destination cannot be null")
	private BigInteger destination;
	
	public String getMessageType() {
		return messageType;
	}
	
	public void setMessageType(String messageType) {
		this.messageType = messageType;
	}

	public Date getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}

	public BigInteger getOrigin() {
		return origin;
	}

	public void setOrigin(BigInteger origin) {
		this.origin = origin;
	}

	public BigInteger getDestination() {
		return destination;
	}

	public void setDestination(BigInteger destination) {
		this.destination = destination;
	}

}
