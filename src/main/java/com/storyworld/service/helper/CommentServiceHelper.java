package com.storyworld.service.helper;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import com.storyworld.domain.elastic.CommentContent;
import com.storyworld.domain.json.Message;
import com.storyworld.domain.json.Request;
import com.storyworld.domain.json.Response;
import com.storyworld.domain.json.enums.MessageText;
import com.storyworld.domain.json.enums.StatusMessage;
import com.storyworld.domain.sql.Comment;
import com.storyworld.domain.sql.Story;
import com.storyworld.domain.sql.User;
import com.storyworld.functional.JSONPrepare;
import com.storyworld.repository.elastic.CommentContentRepository;
import com.storyworld.repository.sql.CommentRepository;
import com.storyworld.repository.sql.StoryRepository;
import com.storyworld.repository.sql.UserRepository;

@Component
public class CommentServiceHelper {

	@Autowired
	private CommentRepository commentRepository;

	@Autowired
	private StoryRepository storyRepository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private CommentContentRepository commentContentRepository;

	private JSONPrepare<CommentContent> jsonPrepare = (statusMessage, message, commentContent, list, success,
			counter) -> new Response<CommentContent>(new Message(statusMessage, message), commentContent, list, success,
					counter);

	private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

	private static final Logger LOG = LoggerFactory.getLogger(CommentServiceHelper.class);

	public Response<CommentContent> prepareCommentContent(Story story, int page, int pageSize) {
		Page<Comment> comments = commentRepository.findByStory(story, PageRequest.of(page, pageSize));
		List<CommentContent> commentsContent = new LinkedList<>();
		comments.forEach(x -> commentsContent.add(commentContentRepository.findById(x.get_id()).get()));
		commentsContent.sort((CommentContent o1, CommentContent o2) -> o2.getDate().compareTo(o1.getDate()));
		return jsonPrepare.prepareResponse(null, null, null, commentsContent, true, null);
	}

	public synchronized Response<CommentContent> tryToSaveComment(User user, Story story,
			CommentContent commentContent) {
		Comment commentSave = new Comment(user, story);
		try {
			user.setLastActionTime(LocalDateTime.now());
			userRepository.save(user);
			commentContent.setAuthorName(user.getName());
			commentContent.setStoryId(story.getId());
			commentContent.setLikes(0);
			commentContent.setDislikes(0);
			commentContent.setDate(LocalDateTime.now().format(FORMATTER));
			commentContent = commentContentRepository.save(commentContent);
			commentSave.set_id(commentContent.getId());
			commentRepository.save(commentSave);
			return jsonPrepare.prepareResponse(StatusMessage.SUCCESS, MessageText.ADDED2, commentContent, null, true,
					null);
		} catch (Exception e) {
			LOG.error(e.toString());
			return jsonPrepare.prepareResponse(StatusMessage.ERROR, MessageText.INCORRECT_DATA3, null, null, false,
					null);
		}
	}

	public Response<CommentContent> prepareToSaveComment(Request request, User user) {
		Optional<Story> story = storyRepository.findById(request.getStory().getId());
		if (story.isPresent()) {
			Optional<Comment> comment = commentRepository.findByAuthorAndStory(user, story.get());
			Optional<CommentContent> commentContent = Optional.ofNullable(request.getCommentContent());
			return Optional.ofNullable(story).isPresent() && commentContent.isPresent() && !comment.isPresent()
					? tryToSaveComment(user, story.get(), commentContent.get())
					: prepareErrorForSaveComment(comment);
		} else
			return prepareErrorForSaveStroy(story);
	}

	private Response<CommentContent> prepareErrorForSaveStroy(Optional<Story> story) {
		return null;
	}

	private Response<CommentContent> prepareErrorForSaveComment(Optional<Comment> comment) {
		return Optional.ofNullable(comment).isPresent()
				? jsonPrepare.prepareResponse(StatusMessage.ERROR, MessageText.UNIQUE_COMMENT, null, null, false, null)
				: jsonPrepare.prepareResponse(StatusMessage.ERROR, MessageText.INCORRECT_DATA, null, null, false, null);
	}

	public synchronized Response<CommentContent> updateCommentContent(Optional<Comment> comment, User user,
			CommentContent commentContentRequest) {
		if (comment.isPresent()) {
			Optional<CommentContent> commentContentOpt = commentContentRepository.findById(comment.get().get_id());
			if (commentContentOpt.isPresent()) {
				CommentContent commentContent = commentContentOpt.get();
				commentContent.setEdited(true);
				commentContent.setContent(commentContentRequest.getContent());
				commentContent.setDate(LocalDateTime.now().format(FORMATTER));
				commentContent = commentContentRepository.save(commentContent);
				user.setLastActionTime(LocalDateTime.now());
				userRepository.save(user);
				return jsonPrepare.prepareResponse(StatusMessage.SUCCESS, MessageText.UPDATED, commentContent, null,
						true, null);
			} else
				return jsonPrepare.prepareResponse(StatusMessage.ERROR, MessageText.INCORRECT_DATA, null, null, false,
						null);
		} else
			return jsonPrepare.prepareResponse(StatusMessage.ERROR, MessageText.INCORRECT_DATA, null, null, false,
					null);
	}

	public synchronized Response<CommentContent> deleteComment(Optional<Comment> comment, Optional<User> user) {
		if (comment.isPresent()) {
			Optional<CommentContent> commentContent = commentContentRepository.findById(comment.get().get_id());
			if (commentContent.isPresent()) {
				commentContentRepository.delete(commentContent.get());
				commentRepository.delete(comment.get());
				user.ifPresent(x -> x.setLastActionTime(LocalDateTime.now()));
				user.ifPresent(userRepository::save);
				return jsonPrepare.prepareResponse(StatusMessage.SUCCESS, MessageText.DELETED, null, null, true, null);
			} else
				return jsonPrepare.prepareResponse(StatusMessage.ERROR, MessageText.INCORRECT_DATA, null, null, false,
						null);
		} else
			return jsonPrepare.prepareResponse(StatusMessage.ERROR, MessageText.INCORRECT_DATA, null, null, false,
					null);
	}

	public Response<CommentContent> prepareComments(User user) {
		return null;
	}

}
