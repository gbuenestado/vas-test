package com.mcp.test.service;

import java.util.List;

import com.mcp.test.dto.KpiDto;
import com.mcp.test.dto.MessageDto;
import com.mcp.test.dto.MetricDto;
import com.mcp.test.exception.DateRequestThrowable;

public interface MCPService {
	
	List<MessageDto> messages(String date) throws DateRequestThrowable;

	MetricDto metrics();

	KpiDto kpis();
	
}
