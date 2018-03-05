package com.storyworld.service;

import com.storyworld.domain.json.Request;
import com.storyworld.domain.json.Response;
import com.storyworld.domain.sql.Story;

public interface StoryService {

	public Response<Story> addStory(Request request);

	public Response<Story> getStory(Long id);

	public Response<Story> getStories(int page, int size, String text);

	public Response<Story> getStoriesByUser(String token);

}
