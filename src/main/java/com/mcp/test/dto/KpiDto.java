package com.mcp.test.dto;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class KpiDto implements Serializable {

	private static final long serialVersionUID = -2371751743549232063L;
	
	private int processedJsonFilesNumber;
	private int rowsNumber;
	private int callsNumber;
	private int messagesNumber;
	private Map<String, Integer> differentOriginCountryCodeNumber = new HashMap<>();
	private Map<String, Integer> differentDestinationCountryCodeNumber = new HashMap<>();
	private String processJsonDuration;
	
	@JsonIgnore
	private long processJsonDurationMillis;
	
	public int getProcessedJsonFilesNumber() {
		return processedJsonFilesNumber;
	}
	
	public void setProcessedJsonFilesNumber(int processedJsonFilesNumber) {
		this.processedJsonFilesNumber = processedJsonFilesNumber;
	}

	public int getRowsNumber() {
		return rowsNumber;
	}

	public void setRowsNumber(int rowsNumber) {
		this.rowsNumber = rowsNumber;
	}

	public int getCallsNumber() {
		return callsNumber;
	}

	public void setCallsNumber(int callsNumber) {
		this.callsNumber = callsNumber;
	}

	public int getMessagesNumber() {
		return messagesNumber;
	}

	public void setMessagesNumber(int messagesNumber) {
		this.messagesNumber = messagesNumber;
	}

	public Map<String, Integer> getDifferentOriginCountryCodeNumber() {
		return differentOriginCountryCodeNumber;
	}

	public void setDifferentOriginCountryCodeNumber(Map<String, Integer> differentOriginCountryCodeNumber) {
		this.differentOriginCountryCodeNumber = differentOriginCountryCodeNumber;
	}

	public Map<String, Integer> getDifferentDestinationCountryCodeNumber() {
		return differentDestinationCountryCodeNumber;
	}

	public void setDifferentDestinationCountryCodeNumber(Map<String, Integer> differentDestinationCountryCodeNumber) {
		this.differentDestinationCountryCodeNumber = differentDestinationCountryCodeNumber;
	}

	public String getProcessJsonDuration() {
		return processJsonDuration;
	}

	public void setProcessJsonDuration(String processJsonDuration) {
		this.processJsonDuration = processJsonDuration;
	}

	public long getProcessJsonDurationMillis() {
		return processJsonDurationMillis;
	}

	public void setProcessJsonDurationMillis(long processJsonDurationMillis) {
		this.processJsonDurationMillis = processJsonDurationMillis;
	}
	
}
