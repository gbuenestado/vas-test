package com.mcp.test.service.impl;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mcp.test.dto.AverageDto;
import com.mcp.test.dto.CallDto;
import com.mcp.test.dto.CallNumberDto;
import com.mcp.test.dto.KpiDto;
import com.mcp.test.dto.MessageDto;
import com.mcp.test.dto.MetricDto;
import com.mcp.test.dto.MsgDto;
import com.mcp.test.dto.RelationCallDto;
import com.mcp.test.dto.WordDto;
import com.mcp.test.dto.enums.StatusCode;
import com.mcp.test.exception.DateRequestThrowable;
import com.mcp.test.exception.MSISDNThowable;
import com.mcp.test.service.MCPService;
import com.mcp.test.utils.Constants;

@Service
public class MCPServiceImpl implements MCPService {
	
	private static final Logger LOG = LoggerFactory.getLogger(MCPServiceImpl.class);
	
	@Value("${mcp.json.file.path}")
	private String jsonPath;
	
	@Autowired
	private ObjectMapper objectMapper;
	
	private MetricDto metricDto;
	private Map<String, KpiDto> kpisDto = new HashMap<>();

	@Override
	public List<MessageDto> messages(String date) throws DateRequestThrowable {
		long initTime = System.currentTimeMillis();
		
		List<MessageDto> result = null;
		
		KpiDto kpiDto = new KpiDto();
		if(date != null && !date.isEmpty()) {
			
			String datePattern = "\\d{4}-\\d{2}-\\d{2}";
			if(date.matches(datePattern)) {
				
				String dateFormat = date.replace("-", "");
				StringBuilder path = new StringBuilder();
				path.append(jsonPath).append("MCP_").append(dateFormat).append(".json");
				
				result = processFile(path.toString(), kpiDto);
				
			} else {
				throw new DateRequestThrowable("Format date is incorrect. It should be: yyyy-MM-dd");
			}
			
		} else {
			throw new DateRequestThrowable("Date parameter is required");
		}
		
		long endTime = System.currentTimeMillis();
		
		kpiDto.setProcessJsonDuration((endTime - initTime) + " milliseconds");
		kpiDto.setProcessJsonDurationMillis(endTime - initTime);
		
		KpiDto kpiMapDto = kpisDto.get(date);
		if(kpiMapDto == null) {
			kpisDto.put(date, kpiDto);
		} else {
			acumulateKpi(kpiMapDto, kpiDto);
		}
		
		return result;
	}
	
	/**
	 * Process the file in path
	 * @param path
	 * @param kpiDto
	 * @return
	 * @throws DateRequestThrowable
	 */
	private synchronized List<MessageDto> processFile(String path, KpiDto kpiDto) throws DateRequestThrowable {
		List<MessageDto> result = null;
		
		metricDto = new MetricDto();
		
		if(new File(path).exists()) {
			List<String> messagesList = getFileLines(path);
			result = parse(messagesList, kpiDto);
			kpiDto.setProcessedJsonFilesNumber(kpiDto.getProcessedJsonFilesNumber() + 1);
			
			
		} else {
			throw new DateRequestThrowable("File not exist");
		}
		
		return result;
	}
	
	/**
	 * Get lines of path in List<String>
	 * @param path
	 * @return
	 */
	private List<String> getFileLines(String path) {
		List<String> result = new ArrayList<>();
		
		try(Stream<String> stream = Files.lines(Paths.get(path))) {
			result = stream.collect(Collectors.toList());
			
		} catch (IOException e) {
			LOG.error("Error reading file line", e);
		}
		
		return result;
	}
	
	/**
	 * Parse message in valid List<MessageDto>
	 * @param messagesList
	 * @param kpiDto
	 * @return
	 */
	private List<MessageDto> parse(List<String> messagesList, KpiDto kpiDto) {
		List<MessageDto> result = new ArrayList<>();
		
		for(String message : messagesList) {
			
			kpiDto.setRowsNumber(kpiDto.getRowsNumber() + 1);
			
			MessageDto messageDto = parse(message, metricDto, kpiDto);
			if(messageDto != null) {
				result.add(messageDto);
			}
		}
		
		relationOkKoCalls(metricDto, kpiDto);
		averageCalls(metricDto);
		
		return result;
	}
	
	/**
	 * Parse message in valid MessageDto
	 * @param message
	 * @param metricDto
	 * @param kpiDto
	 * @return
	 */
	private MessageDto parse(String message, MetricDto metricDto, KpiDto kpiDto)  {
		MessageDto messageDto = null;
		
		try {
			JsonNode node = objectMapper.readTree(message);
			if(!node.isEmpty()) {
				if(node.has(Constants.JSON_PROPERTY_MESSAGE_TYPE)) {
					
					JsonNode messageTypeNode = node.get(Constants.JSON_PROPERTY_MESSAGE_TYPE);
					if(messageTypeNode != null) {
						
						messageDto = deserialize(message, messageTypeNode, metricDto, kpiDto);
						
					} else {
						metricDto.setRowsFieldErrors(metricDto.getRowsFieldErrors() + 1);
					}
				}
				
			} else {
				metricDto.setRowsMissingFields(metricDto.getRowsMissingFields() + 1);
			}
			
		} catch (IOException e) {
			LOG.error("Error reading file line", e);
			metricDto.setRowsFieldErrors(metricDto.getRowsFieldErrors() + 1);
		}
		
		return messageDto;
	}
	
	/**
	 * Deserialize message into MessageDto and validate dto
	 * @param message
	 * @param messageTypeNode
	 * @param metricDto
	 * @param kpiDto
	 * @return
	 */
	private MessageDto deserialize(String message, JsonNode messageTypeNode, MetricDto metricDto, KpiDto kpiDto) {
		MessageDto messageDto = null;

		try {
			String messageTypeValue = messageTypeNode.asText();
			if(messageTypeValue.equals(Constants.MESSAGE_TYPE_VALUE_CALL)) {
				messageDto = objectMapper.readValue(message, CallDto.class);

				if(!validateDto(messageDto)) {
					metricCall(metricDto, kpiDto, (CallDto) messageDto);
				} else {
					metricDto.setRowsMissingFields(metricDto.getRowsMissingFields() + 1);
					messageDto = null;
				}

			} else if(messageTypeValue.equals(Constants.MESSAGE_TYPE_VALUE_MSG)) {
				messageDto = objectMapper.readValue(message, MsgDto.class);

				if(!validateDto(messageDto)) {
					metricMsg(metricDto, kpiDto, (MsgDto) messageDto);
				} else {
					metricDto.setRowsMissingFields(metricDto.getRowsMissingFields() + 1);
					messageDto = null;
				}
				
			} else {
				metricDto.setRowsFieldErrors(metricDto.getRowsFieldErrors() + 1);
			}

		} catch (IOException | MSISDNThowable e) {
			metricDto.setRowsFieldErrors(metricDto.getRowsFieldErrors() + 1);
			messageDto = null;
		}
		
		return messageDto;
	}
	
	/**
	 * Validate messageDto
	 * @param messageDto
	 * @return
	 */
	private boolean validateDto(MessageDto messageDto)  {
		ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
		Validator validator = factory.getValidator();
		
		Set<ConstraintViolation<MessageDto>> violations = validator.validate(messageDto);
		
	    return !violations.isEmpty();
	}
	
	/**
	 * Generate metric for calls
	 * @param metricDto
	 * @param kpiDto
	 * @param callDto
	 * @throws MSISDNThowable
	 */
	private void metricCall(MetricDto metricDto, KpiDto kpiDto, CallDto callDto) throws MSISDNThowable {
		String origin = null;
		String originMSISDN = null;
		if(callDto.getOrigin() != null) {
			
			origin = callDto.getOrigin().toString();
			if(!validateMSISDN(origin)) {
				throw new MSISDNThowable("Origin" + origin + " invalid");
				
			} else {
				originMSISDN = origin.substring(0, 3);
				
				Integer differentOriginCountryCodeNumber = kpiDto.getDifferentOriginCountryCodeNumber().get(originMSISDN);
				if(differentOriginCountryCodeNumber == null) {
					differentOriginCountryCodeNumber = 0;
				}
				differentOriginCountryCodeNumber++;
				kpiDto.getDifferentOriginCountryCodeNumber().put(originMSISDN, differentOriginCountryCodeNumber);
			}
		}
		
		String destination = null;
		String destinationMSISDN = null;
		if(callDto.getDestination() != null) {
			
			destination = callDto.getDestination().toString();
			if(!validateMSISDN(destination)) {
				if(originMSISDN != null) {
					throw new MSISDNThowable("Destination" + destination + " invalid");
				}
				
			} else {
				destinationMSISDN = destination.substring(0, 3);
				
				Integer differentDestinationCountryCodeNumber = kpiDto.getDifferentDestinationCountryCodeNumber().get(destinationMSISDN);
				if(differentDestinationCountryCodeNumber == null) {
					differentDestinationCountryCodeNumber = 0;
				}
				differentDestinationCountryCodeNumber++;
				kpiDto.getDifferentDestinationCountryCodeNumber().put(destinationMSISDN, differentDestinationCountryCodeNumber);
			}
		}
		
		RelationCallDto relationCallsStatus = metricDto.getRelationCallsStatus();
		if(callDto.getStatusCode() != null) {
			if(callDto.getStatusCode().equals(StatusCode.OK)) {
				relationCallsStatus.setOk(relationCallsStatus.getOk() + 1);
				
			} else if(callDto.getStatusCode().equals(StatusCode.KO)) {
				relationCallsStatus.setKo(relationCallsStatus.getKo() + 1);
			}
		}
		
		metricCallByCountry(metricDto, callDto, originMSISDN, destinationMSISDN);
		
		kpiDto.setCallsNumber(kpiDto.getCallsNumber() + 1);
	}
	
	/**
	 * Check MSISDN of number
	 * @param number
	 * @return
	 */
	private boolean validateMSISDN(String number) {
		boolean result = false;
		
		if(!number.isEmpty() && number.length() >= 3 && number.length() <= 15) {
			result = true;
		}
		
		return result;
	}
	
	/**
	 * Generate metric for call grouped by country
	 * @param metricDto
	 * @param callDto
	 * @param ccOrigin
	 * @param ccDestination
	 */
	private void metricCallByCountry(MetricDto metricDto, CallDto callDto, String ccOrigin, String ccDestination) {
		if(ccOrigin != null) {
			// Average
			AverageDto averageDto = metricDto.getAverage().get(ccOrigin);
			if(averageDto == null) {
				averageDto = new AverageDto();
				averageDto.setCountry(ccOrigin);
				metricDto.getAverage().put(ccOrigin, averageDto);
			}
			averageDto.setDuration(averageDto.getDuration() + callDto.getDuration());
			
			// Number call origin
			CallNumberDto originCall = metricDto.getCallsNumber().get(ccOrigin);
			if(originCall == null) {
				originCall = new CallNumberDto();
				originCall.setCountry(ccOrigin);
				metricDto.getCallsNumber().put(ccOrigin, originCall);
			}
			originCall.setOrigin(originCall.getOrigin() + 1);
		}
		
		if(ccDestination != null) {
			// Number call destination
			CallNumberDto destinationCall = metricDto.getCallsNumber().get(ccDestination);
			if(destinationCall == null) {
				destinationCall = new CallNumberDto();
				destinationCall.setCountry(ccOrigin);
				metricDto.getCallsNumber().put(ccDestination, destinationCall);
			}
			destinationCall.setDestination(destinationCall.getDestination() + 1);
		}
	}
	
	/**
	 * Generate metric for msg
	 * @param metricDto
	 * @param kpiDto
	 * @param msgDto
	 * @throws MSISDNThowable
	 */
	private void metricMsg(MetricDto metricDto, KpiDto kpiDto, MsgDto msgDto) throws MSISDNThowable {
		String origin = null;
		String originMSISDN = null;
		if(msgDto.getOrigin() != null) {
			
			origin = msgDto.getOrigin().toString();
			if(validateMSISDN(origin)) {
				originMSISDN = origin.substring(0, 3);
				
				Integer differentOriginCountryCodeNumber = kpiDto.getDifferentOriginCountryCodeNumber().get(originMSISDN);
				if(differentOriginCountryCodeNumber == null) {
					differentOriginCountryCodeNumber = 0;
				}
				differentOriginCountryCodeNumber++;
				kpiDto.getDifferentOriginCountryCodeNumber().put(originMSISDN, differentOriginCountryCodeNumber);
				
			} else {
				throw new MSISDNThowable("Origin" + origin + " invalid");
			}
		}
		
		String destination = null;
		String destinationMSISDN = null;
		if(msgDto.getDestination() != null) {
			
			destination = msgDto.getDestination().toString();
			if(validateMSISDN(destination)) {
				destinationMSISDN = destination.substring(0, 3);
				
				Integer differentDestinationCountryCodeNumber = kpiDto.getDifferentDestinationCountryCodeNumber().get(destinationMSISDN);
				if(differentDestinationCountryCodeNumber == null) {
					differentDestinationCountryCodeNumber = 0;
				}
				differentDestinationCountryCodeNumber++;
				kpiDto.getDifferentDestinationCountryCodeNumber().put(destinationMSISDN, differentDestinationCountryCodeNumber);
				
			} else {
				throw new MSISDNThowable("Destination" + destination + " invalid");
			}
		}
		
		if(msgDto.getMessageContent() != null && msgDto.getMessageContent().isEmpty()) {
			metricDto.setMessagesBlankContent(metricDto.getMessagesBlankContent() + 1);
			
		} else {
			String[] words = msgDto.getMessageContent().split(" ");
			for(String word : words) {
				
				WordDto wordDto = metricDto.getWordOccurence().get(word);
				if(wordDto == null) {
					wordDto = new WordDto();
					wordDto.setWord(word);
					wordDto.setOccurrences(0);
					metricDto.getWordOccurence().put(word, wordDto);
				}
				wordDto.setOccurrences(wordDto.getOccurrences() + 1);
			}
		}
		
		kpiDto.setMessagesNumber(kpiDto.getMessagesNumber() + 1);
	}
	
	/**
	 * Calculate the percentage of calls ok and ko.
	 * @param metricDto
	 * @param kpiDto
	 */
	private void relationOkKoCalls(MetricDto metricDto, KpiDto kpiDto) {
		RelationCallDto relationCallDto = metricDto.getRelationCallsStatus();
		relationCallDto.setOkCalls((relationCallDto.getOk() * 100 / (double)kpiDto.getCallsNumber()) + " %");
		relationCallDto.setKoCalls((relationCallDto.getKo() * 100 / (double)kpiDto.getCallsNumber()) + " %");
	}
	
	/**
	 * Calculate the average call duration
	 * @param metricDto
	 */
	private void averageCalls(MetricDto metricDto) {
		Map<String, AverageDto> averageByCountrDto = metricDto.getAverage();
		if(averageByCountrDto != null) {
			
			for(Entry<String, AverageDto> entry : averageByCountrDto.entrySet()) {
				
				String originCountry = entry.getKey();
				AverageDto averageDto = averageByCountrDto.get(originCountry);
				
				CallNumberDto callNumberDto = metricDto.getCallsNumber().get(originCountry);
				if(callNumberDto != null && averageDto.getDuration() != 0) {
					averageDto.setDuration(averageDto.getDuration() / callNumberDto.getOrigin());
				}
			}
		}
	}
	
	/**
	 * Add results of kpiDto in kpiMapDto
	 * @param kpiMapDto
	 * @param kpiDto
	 */
	private void acumulateKpi(KpiDto kpiMapDto, KpiDto kpiDto) {
		kpiMapDto.setCallsNumber(kpiMapDto.getCallsNumber() + kpiDto.getCallsNumber());
		kpiMapDto.setMessagesNumber(kpiMapDto.getMessagesNumber() + kpiDto.getMessagesNumber());
		kpiMapDto.setProcessedJsonFilesNumber(kpiMapDto.getProcessedJsonFilesNumber() + kpiDto.getProcessedJsonFilesNumber());
		kpiMapDto.setProcessJsonDurationMillis(kpiMapDto.getProcessJsonDurationMillis() + kpiDto.getProcessJsonDurationMillis());
		kpiMapDto.setProcessJsonDuration(kpiMapDto.getProcessJsonDurationMillis() + " milliseconds");
		kpiMapDto.setRowsNumber(kpiMapDto.getRowsNumber() + kpiDto.getRowsNumber());
		
		for(String origin : kpiDto.getDifferentOriginCountryCodeNumber().keySet()) {
			Integer originNumber = kpiDto.getDifferentOriginCountryCodeNumber().get(origin);
			originNumber = originNumber == null ? 0 : originNumber;
			
			Integer originMapNumber = kpiMapDto.getDifferentOriginCountryCodeNumber().get(origin);
			originMapNumber = originMapNumber == null ? 0 : originMapNumber;
			
			kpiMapDto.getDifferentOriginCountryCodeNumber().put(origin, originNumber + originMapNumber);
		}
		
		
		for(String destination : kpiDto.getDifferentDestinationCountryCodeNumber().keySet()) {
			Integer destinationNumber = kpiDto.getDifferentDestinationCountryCodeNumber().get(destination);
			destinationNumber = destinationNumber == null ? 0 : destinationNumber;
			
			Integer destinationMapNumber = kpiMapDto.getDifferentDestinationCountryCodeNumber().get(destination);
			destinationMapNumber = destinationMapNumber == null ? 0 : destinationMapNumber;
			
			kpiMapDto.getDifferentDestinationCountryCodeNumber().put(destination, destinationNumber + destinationMapNumber);
		}
	}

	@Override
	public MetricDto metrics() {
		metricDto.setCallsNumberByCounty(new ArrayList<CallNumberDto>(metricDto.getCallsNumber().values()));
		metricDto.setAverageByCounty(new ArrayList<AverageDto>(metricDto.getAverage().values()));
		
		Collections.sort(new ArrayList<WordDto>(metricDto.getWordOccurence().values()), WordDto.comparator);
		
		List<String> wordRanking = metricDto.getWordRanking();
		for(WordDto wordDto : metricDto.getWordOccurence().values()) {
			wordRanking.add(wordDto.getWord());
		}
		
		return metricDto;
	}

	@Override
	public KpiDto kpis() {
		KpiDto result = new KpiDto();
		
		for(KpiDto kpiDto : kpisDto.values()) {
			acumulateKpi(result, kpiDto);
		}
		
		return result;
	}
	
}
