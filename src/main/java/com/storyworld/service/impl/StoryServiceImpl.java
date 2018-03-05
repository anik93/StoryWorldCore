package com.storyworld.service.impl;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.storyworld.domain.json.Message;
import com.storyworld.domain.json.Request;
import com.storyworld.domain.json.Response;
import com.storyworld.domain.json.enums.MessageText;
import com.storyworld.domain.json.enums.StatusMessage;
import com.storyworld.domain.sql.Story;
import com.storyworld.domain.sql.User;
import com.storyworld.functional.JSONPrepare;
import com.storyworld.repository.sql.StoryRepository;
import com.storyworld.repository.sql.UserRepository;
import com.storyworld.service.StoryService;
import com.storyworld.service.helper.StoryServiceHelper;

@Service
public class StoryServiceImpl implements StoryService {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private StoryRepository storyRepository;

	@Autowired
	private StoryServiceHelper storyServiceHelper;

	private JSONPrepare<Story> jsonPrepare = (statusMessage, message, story, list, success,
			counter) -> new Response<Story>(new Message(statusMessage, message), story, list, success, counter);

	@Override
	public Response<Story> addStory(Request request) {
		return userRepository.findByToken(request.getToken())
				.map(user -> storyServiceHelper.tryToSaveStroy(request.getStory(), user)).orElseGet(() -> jsonPrepare
						.prepareResponse(StatusMessage.ERROR, MessageText.INCORRECT_DATA, null, null, false, null));
	}

	@Override
	public Response<Story> getStory(Long id) {
		return Optional.ofNullable(storyRepository.findById(id))
				.map(story -> storyServiceHelper.getStoryContent(story.get())).orElseGet(() -> jsonPrepare
						.prepareResponse(StatusMessage.ERROR, MessageText.INCORRECT_DATA, null, null, false, null));
	}

	@Override
	public Response<Story> getStories(int page, int size, String text) {
		return Optional.ofNullable(text).map(textSearch -> Optional
				.ofNullable(storyRepository.findByName(text, PageRequest.of(page, size, Sort.Direction.ASC, "name")))
				.map(stories -> jsonPrepare.prepareResponse(null, null, null, stories.getContent(), true, null))
				.orElseGet(() -> jsonPrepare.prepareResponse(StatusMessage.ERROR, MessageText.INCORRECT_DATA, null,
						null, false, null)))
				.orElse(Optional
						.ofNullable(storyRepository.findAll(PageRequest.of(page, size, Sort.Direction.DESC, "avgRate")))
						.map(stories -> jsonPrepare.prepareResponse(null, null, null, stories.getContent(), true,
								(int) storyRepository.count()))
						.orElseGet(() -> jsonPrepare.prepareResponse(StatusMessage.ERROR, MessageText.INCORRECT_DATA,
								null, null, false, null)));
	}

	@Override
	public Response<Story> getStoriesByUser(String token) {
		Optional<User> userGet;
		try {
			Long id = Long.valueOf(token);
			userGet = userRepository.findById(id);
		} catch (Exception e) {
			userGet = userRepository.findByToken(token);
		}
		return userGet
				.map(user -> jsonPrepare.prepareResponse(null, null, null,
						storyRepository.findByAuthor(user, PageRequest.of(0, 10)).getContent(), true, null))
				.orElseGet(() -> jsonPrepare.prepareResponse(StatusMessage.ERROR, MessageText.INCORRECT_DATA, null,
						null, false, null));
	}

}
