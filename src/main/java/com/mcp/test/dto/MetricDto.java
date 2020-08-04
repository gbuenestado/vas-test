package com.mcp.test.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class MetricDto implements Serializable {

	private static final long serialVersionUID = 6628126995873784592L;
	
	private int rowsMissingFields;
	private int messagesBlankContent;
	private int rowsFieldErrors;
	private List<CallNumberDto> callsNumberByCounty = new ArrayList<>();
	private RelationCallDto relationCallsStatus = new RelationCallDto();
	private List<AverageDto> averageByCounty = new ArrayList<>();
	private List<String> wordRanking = new LinkedList<>();
	
	@JsonIgnore
	private Map<String, CallNumberDto> callsNumber = new HashMap<>();
	@JsonIgnore
	private Map<String, AverageDto> average = new HashMap<>();
	@JsonIgnore
	private Map<String, WordDto> wordOccurence = new HashMap<>();
	
	public int getRowsMissingFields() {
		return rowsMissingFields;
	}
	
	public void setRowsMissingFields(int rowsMissingFields) {
		this.rowsMissingFields = rowsMissingFields;
	}
	
	public int getMessagesBlankContent() {
		return messagesBlankContent;
	}
	
	public void setMessagesBlankContent(int messagesBlankContent) {
		this.messagesBlankContent = messagesBlankContent;
	}

	public int getRowsFieldErrors() {
		return rowsFieldErrors;
	}

	public void setRowsFieldErrors(int rowsFieldErrors) {
		this.rowsFieldErrors = rowsFieldErrors;
	}

	public Map<String, CallNumberDto> getCallsNumber() {
		return callsNumber;
	}

	public void setCallsNumber(Map<String, CallNumberDto> callsNumber) {
		this.callsNumber = callsNumber;
	}

	public List<CallNumberDto> getCallsNumberByCounty() {
		return callsNumberByCounty;
	}

	public void setCallsNumberByCounty(List<CallNumberDto> callsNumberByCounty) {
		this.callsNumberByCounty = callsNumberByCounty;
	}

	public RelationCallDto getRelationCallsStatus() {
		return relationCallsStatus;
	}

	public void setRelationCallsStatus(RelationCallDto relationCallsStatus) {
		this.relationCallsStatus = relationCallsStatus;
	}

	public List<AverageDto> getAverageByCounty() {
		return averageByCounty;
	}

	public void setAverageByCounty(List<AverageDto> averageByCounty) {
		this.averageByCounty = averageByCounty;
	}

	public List<String> getWordRanking() {
		return wordRanking;
	}

	public void setWordRanking(List<String> wordRanking) {
		this.wordRanking = wordRanking;
	}

	public Map<String, AverageDto> getAverage() {
		return average;
	}

	public void setAverage(Map<String, AverageDto> average) {
		this.average = average;
	}

	public Map<String, WordDto> getWordOccurence() {
		return wordOccurence;
	}

	public void setWordOccurence(Map<String, WordDto> wordOccurence) {
		this.wordOccurence = wordOccurence;
	}

}
