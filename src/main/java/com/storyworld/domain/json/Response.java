package com.storyworld.domain.json;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Data;
import lombok.ToString;
import lombok.NoArgsConstructor;

@Data
@ToString
@NoArgsConstructor
@JsonInclude(Include.NON_NULL)
public class Response<T> {

	private Message message;

	private T t;

	private List<T> list;

	private boolean success;

	private Integer counter;

	public Response(Message message, T t, List<T> listT, boolean success, Integer counter) {
		if (message.getStatus() == null)
			this.message = null;
		else
			this.message = message;
		this.t = t;
		this.list = listT;
		this.success = success;
		this.counter = counter;
	}

}