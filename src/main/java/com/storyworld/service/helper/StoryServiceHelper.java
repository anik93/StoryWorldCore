package com.storyworld.service.helper;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.storyworld.domain.elastic.StoryContent;
import com.storyworld.domain.json.Message;
import com.storyworld.domain.json.Response;
import com.storyworld.domain.json.enums.MessageText;
import com.storyworld.domain.json.enums.StatusMessage;
import com.storyworld.domain.sql.Story;
import com.storyworld.domain.sql.User;
import com.storyworld.functional.JSONPrepare;
import com.storyworld.repository.elastic.StoryContentRepository;
import com.storyworld.repository.sql.StoryRepository;
import com.storyworld.repository.sql.UserRepository;

@Component
public class StoryServiceHelper {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private StoryRepository storyRepository;

	@Autowired
	private StoryContentRepository storyContentRepository;

	private JSONPrepare<Story> jsonPrepare = (statusMessage, message, story, list, success,
			counter) -> new Response<Story>(new Message(statusMessage, message), story, list, success, counter);

	private static final Logger LOG = LoggerFactory.getLogger(StoryServiceHelper.class);

	public Response<Story> tryToSaveStroy(Story storyRequest, User user) {
		Story story = storyRequest;
		story.setAuthor(user);
		StoryContent storyContent = new StoryContent();
		List<String> pages = new ArrayList<>();
		pages.add(story.getRawText());
		storyContent.setPages(pages);
		storyContent.setTitle(story.getName());
		storyContent = storyContentRepository.save(storyContent);
		story.setContentId(storyContent.getId());
		try {
			storyRepository.save(story);
			user.setLastActionTime(LocalDateTime.now());
			userRepository.save(user);
			return jsonPrepare.prepareResponse(StatusMessage.SUCCESS, MessageText.STORY_CRT, null, null, true, null);
		} catch (Exception e) {
			LOG.error(e.toString());
			return jsonPrepare.prepareResponse(StatusMessage.ERROR, MessageText.INCORRECT_DATA, null, null, false,
					null);
		}
	}

	public Response<Story> getStoryContent(Story story) {
		return Optional.ofNullable(storyContentRepository.findById(story.getContentId())).map(storyContent -> {
			story.setPages(storyContent.get().getPages());
			return jsonPrepare.prepareResponse(null, null, story, null, true, null);
		}).orElseGet(() -> jsonPrepare.prepareResponse(StatusMessage.ERROR, MessageText.INCORRECT_DATA, null, null,
				false, null));
	}

}
