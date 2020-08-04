package com.mcp.test.dto;

import java.io.Serializable;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.mcp.test.dto.enums.MessageStatus;

@JsonPropertyOrder({"message_type", "timestamp", "origin", "destination", "message_content", "message_status"})
public class MsgDto extends MessageDto implements Serializable {

	private static final long serialVersionUID = -1048651471890174677L;
	
	@JsonProperty("message_content")
	@NotNull(message = "messageContent cannot be null")
	@NotBlank(message = "messageContent cannot be blank")
	private String messageContent;
	
	@JsonProperty("message_status")
	@NotNull(message = "messageStatus cannot be null")
	private MessageStatus messageStatus;

	public String getMessageContent() {
		return messageContent;
	}

	public void setMessageContent(String messageContent) {
		this.messageContent = messageContent;
	}

	public MessageStatus getMessageStatus() {
		return messageStatus;
	}

	public void setMessageStatus(MessageStatus messageStatus) {
		this.messageStatus = messageStatus;
	}
	
}
