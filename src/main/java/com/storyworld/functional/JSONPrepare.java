package com.storyworld.functional;

import java.util.List;

import com.storyworld.domain.json.Response;
import com.storyworld.domain.json.enums.MessageText;
import com.storyworld.domain.json.enums.StatusMessage;

@FunctionalInterface
public interface JSONPrepare<T> {

	public Response<T> prepareResponse(StatusMessage messageStatus, MessageText messageString, T t, List<T> list,
			boolean success, Integer counter);

}
