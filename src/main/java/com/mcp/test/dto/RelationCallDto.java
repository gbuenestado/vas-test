package com.mcp.test.dto;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class RelationCallDto implements Serializable {

	private static final long serialVersionUID = 9126348270503544827L;
	
	private String okCalls;
	private String koCalls;
	
	@JsonIgnore
	private int ok;
	@JsonIgnore
	private int ko;
	
	public String getOkCalls() {
		return okCalls;
	}
	
	public void setOkCalls(String okCalls) {
		this.okCalls = okCalls;
	}
	
	public String getKoCalls() {
		return koCalls;
	}
	
	public void setKoCalls(String koCalls) {
		this.koCalls = koCalls;
	}

	public int getOk() {
		return ok;
	}

	public void setOk(int ok) {
		this.ok = ok;
	}

	public int getKo() {
		return ko;
	}

	public void setKo(int ko) {
		this.ko = ko;
	}
	
}
