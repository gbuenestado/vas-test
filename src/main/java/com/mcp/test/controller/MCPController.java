package com.mcp.test.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.mcp.test.dto.KpiDto;
import com.mcp.test.dto.MessageDto;
import com.mcp.test.dto.MetricDto;
import com.mcp.test.exception.DateRequestThrowable;
import com.mcp.test.service.MCPService;

@RestController
public class MCPController {
	
	private static final Logger LOG = LoggerFactory.getLogger(MCPController.class);
	
	@Autowired
	private MCPService mcpService;

	//@RequestMapping(value = "/{date:^(?!MCP_).+}", method = RequestMethod.GET)
	//public ResponseEntity<String> mcp(@PathVariable String date) {
	@SuppressWarnings("rawtypes")
	@RequestMapping(value = "/mcp",  method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity mcp(@RequestParam String date) {
		ResponseEntity result = null;
		
		try {
			List<MessageDto> messagesList = mcpService.messages(date);
			result = new ResponseEntity<>(messagesList, HttpStatus.OK);
			
		} catch (DateRequestThrowable e) {
			LOG.error("Error en el parametro date: ", e);
			result = new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
			
		} catch (Exception e) {
			LOG.error("Error obteniendo mcp: ", e);
			result = new ResponseEntity<>("error", HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
		return result;
	}
	
	@SuppressWarnings("rawtypes")
	@RequestMapping(value = "/metrics",  method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity metrics() {
		ResponseEntity result = null;
		
		try {
			MetricDto metricDto = mcpService.metrics();
			result = new ResponseEntity<>(metricDto, HttpStatus.OK);
			
		} catch (Exception e) {
			LOG.error("Error obteniendo metrics: ", e);
			result = new ResponseEntity<>("error", HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
		return result;
	}
	
	@SuppressWarnings("rawtypes")
	@RequestMapping(value = "/kpis",  method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity kpis() {
		ResponseEntity result = null;
		
		try {
			KpiDto kpiDto = mcpService.kpis();
			result = new ResponseEntity<>(kpiDto, HttpStatus.OK);
			
		} catch (Exception e) {
			LOG.error("Error obteniendo kpis: ", e);
			result = new ResponseEntity<>("error", HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
		return result;
	}
	
}
