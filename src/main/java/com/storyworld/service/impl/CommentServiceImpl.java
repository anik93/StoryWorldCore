package com.storyworld.service.impl;

import static com.storyworld.conditions.CommonPredicate.validatePageAndPageSize;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.storyworld.domain.elastic.CommentContent;
import com.storyworld.domain.json.Message;
import com.storyworld.domain.json.Request;
import com.storyworld.domain.json.Response;
import com.storyworld.domain.json.enums.MessageText;
import com.storyworld.domain.json.enums.StatusMessage;
import com.storyworld.domain.sql.Comment;
import com.storyworld.domain.sql.LikeTypeComment;
import com.storyworld.domain.sql.Story;
import com.storyworld.domain.sql.User;
import com.storyworld.domain.sql.enums.LikeType;
import com.storyworld.functional.JSONPrepare;
import com.storyworld.repository.elastic.CommentContentRepository;
import com.storyworld.repository.sql.CommentRepository;
import com.storyworld.repository.sql.LikeTypeCommentRepository;
import com.storyworld.repository.sql.StoryRepository;
import com.storyworld.repository.sql.UserRepository;
import com.storyworld.service.CommentService;
import com.storyworld.service.helper.CommentServiceHelper;

@Service
public class CommentServiceImpl implements CommentService {

	@Autowired
	private CommentRepository commentRepository;

	@Autowired
	private StoryRepository storyRepository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private CommentContentRepository commentContentRepository;

	@Autowired
	private LikeTypeCommentRepository likeTypeCommentRepository;

	@Autowired
	private CommentServiceHelper commentServiceHelper;

	private JSONPrepare<CommentContent> jsonPrepare = (statusMessage, message, commentContent, list, success,
			counter) -> new Response<CommentContent>(new Message(statusMessage, message), commentContent, list, success,
					counter);

	@Override
	public Response<CommentContent> get(Long idStory, int page, int pageSize) {
		Optional<Story> story = storyRepository.findById(idStory);
		return story.isPresent() && validatePageAndPageSize.test(page, pageSize)
				? commentServiceHelper.prepareCommentContent(story.get(), page, pageSize)
				: jsonPrepare.prepareResponse(StatusMessage.ERROR, MessageText.INCORRECT_DATA, null, null, false, null);
	}

	@Override
	public Response<CommentContent> save(Request request) {
		return userRepository.findByToken(request.getToken())
				.map(user -> commentServiceHelper.prepareToSaveComment(request, user)).orElseGet(() -> jsonPrepare
						.prepareResponse(StatusMessage.ERROR, MessageText.INCORRECT_DATA, null, null, false, null));
	}

	@Override
	public Response<CommentContent> update(Request request) {
		return userRepository.findByToken(request.getToken()).map(user -> {
			Optional<Comment> comment = commentRepository.findBy_id(request.getCommentContent().getId());
			return comment.isPresent() && request.getCommentContent() != null
					? commentServiceHelper.updateCommentContent(comment, user, request.getCommentContent())
					: jsonPrepare.prepareResponse(StatusMessage.ERROR, MessageText.INCORRECT_DATA, null, null, false,
							null);
		}).orElseGet(() -> jsonPrepare.prepareResponse(StatusMessage.ERROR, MessageText.INCORRECT_DATA, null, null,
				false, null));
	}

	@Override
	public Response<CommentContent> delete(Request request) {
		Optional<User> user = userRepository.findByToken(request.getToken());
		Optional<Comment> comment = commentRepository.findBy_id(request.getComment().get_id());
		return user.isPresent() && comment.isPresent() ? commentServiceHelper.deleteComment(comment, user)
				: jsonPrepare.prepareResponse(StatusMessage.ERROR, MessageText.INCORRECT_DATA, null, null, false, null);
	}

	@Override
	public synchronized Response<CommentContent> like(Request request) {
		Optional<User> userGet = userRepository.findByToken(request.getToken());
		return userGet.map(user -> {
			Optional<CommentContent> commentContentOpt = commentContentRepository
					.findById(request.getCommentContent().getId());
			if (commentContentOpt.isPresent()) {
				CommentContent commentContent = commentContentOpt.get();
				Optional<Comment> comment = commentRepository.findBy_id(commentContent.getId());
				Optional<LikeTypeComment> likeTypeComment = likeTypeCommentRepository.findByUserAndComment(user,
						comment.get());
				if ((likeTypeComment.isPresent() && likeTypeComment.get().getLikeType().equals(LikeType.DISLIKE))
						|| !likeTypeComment.isPresent()) {
					if (likeTypeComment.isPresent() && likeTypeComment.get().getLikeType().equals(LikeType.DISLIKE)) {
						int dislike = commentContent.getDislikes() - 1;
						commentContent.setDislikes(dislike);
						likeTypeComment.get().setLikeType(LikeType.LIKE);
						likeTypeCommentRepository.save(likeTypeComment.get());
					} else {
						LikeTypeComment likeTypeCommentSave = new LikeTypeComment(user, comment.get(), LikeType.LIKE);
						likeTypeCommentRepository.save(likeTypeCommentSave);
					}
					int like = commentContent.getLikes() + 1;
					commentContent.setLikes(like);
					commentContent = commentContentRepository.save(commentContent);
					user.setLastActionTime(LocalDateTime.now());
					userRepository.save(user);
					return jsonPrepare.prepareResponse(StatusMessage.SUCCESS, MessageText.LIKED, commentContent, null,
							true, null);
				} else
					return jsonPrepare.prepareResponse(StatusMessage.WARNING, MessageText.UNIQUE_LIKE, null, null, true,
							null);
			} else
				return jsonPrepare.prepareResponse(StatusMessage.ERROR, MessageText.INCORRECT_DATA, null, null, false,
						null);
		}).orElseGet(() -> jsonPrepare.prepareResponse(StatusMessage.ERROR, MessageText.INCORRECT_DATA, null, null,
				false, null));
	}

	@Override
	public synchronized Response<CommentContent> dislike(Request request) {
		Optional<User> userGet = userRepository.findByToken(request.getToken());
		return userGet.map(user -> {
			Optional<CommentContent> commentContentOpt = commentContentRepository
					.findById(request.getCommentContent().getId());
			if (commentContentOpt.isPresent()) {
				CommentContent commentContent = commentContentOpt.get();
				Optional<Comment> comment = commentRepository.findBy_id(commentContent.getId());
				Optional<LikeTypeComment> likeTypeComment = likeTypeCommentRepository.findByUserAndComment(user,
						comment.get());
				if ((likeTypeComment.isPresent() && likeTypeComment.get().getLikeType().equals(LikeType.LIKE))
						|| !likeTypeComment.isPresent()) {
					if (likeTypeComment.isPresent() && likeTypeComment.get().getLikeType().equals(LikeType.LIKE)) {
						int like = commentContent.getLikes() - 1;
						commentContent.setLikes(like);
						likeTypeComment.get().setLikeType(LikeType.DISLIKE);
						likeTypeCommentRepository.save(likeTypeComment.get());
					} else {
						LikeTypeComment likeTypeCommentSave = new LikeTypeComment(user, comment.get(),
								LikeType.DISLIKE);
						likeTypeCommentRepository.save(likeTypeCommentSave);
					}
					int dislike = commentContent.getDislikes() + 1;
					commentContent.setDislikes(dislike);
					commentContent = commentContentRepository.save(commentContent);
					user.setLastActionTime(LocalDateTime.now());
					userRepository.save(user);
					return jsonPrepare.prepareResponse(StatusMessage.SUCCESS, MessageText.DISLIKED, commentContent,
							null, true, null);
				} else
					return jsonPrepare.prepareResponse(StatusMessage.WARNING, MessageText.UNIQUE_LIKE, null, null, true,
							null);
			} else
				return jsonPrepare.prepareResponse(StatusMessage.ERROR, MessageText.INCORRECT_DATA, null, null, false,
						null);
		}).orElseGet(() -> jsonPrepare.prepareResponse(StatusMessage.ERROR, MessageText.INCORRECT_DATA, null, null,
				false, null));
	}

	@Override
	public Response<CommentContent> getByUser(String token) {
		Optional<User> userGet;
		try {
			Long id = Long.valueOf(token);
			userGet = userRepository.findById(id);
		} catch (Exception e) {
			userGet = userRepository.findByToken(token);
		}
		return userGet.map(user -> commentServiceHelper.prepareComments(user)).orElseGet(
				() -> jsonPrepare.prepareResponse(StatusMessage.ERROR, MessageText.NULL, null, null, false, null));
	}

}