package com.storyworld.domain.json;

import com.storyworld.domain.json.enums.MessageText;
import com.storyworld.domain.json.enums.StatusMessage;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
public class Message {

	private StatusMessage status;

	private MessageText text;

	public Message(StatusMessage status, MessageText text) {
		super();
		this.status = status;
		this.text = text;
	}

}
