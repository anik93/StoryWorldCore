package com.storyworld.service.impl;

import static com.storyworld.conditions.AuthorizationPredicate.*;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.storyworld.domain.json.Request;
import com.storyworld.domain.sql.Comment;
import com.storyworld.domain.sql.User;
import com.storyworld.repository.sql.CommentRepository;
import com.storyworld.repository.sql.UserRepository;
import com.storyworld.service.AuthorizationService;

@Service
public class AuthorizationServiceImpl implements AuthorizationService {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private CommentRepository commentRepository;

	@Override
	public boolean checkAccess(Request request) {
		return checkAccess.test(userRepository.findByToken(request.getToken()), 2L);
	}

	@Override
	public boolean checkAccessToUser(Request request) {
		return checkAccessToUser.test(userRepository.findByToken(request.getToken()), request);
	}

	@Override
	public boolean checkAccessToComment(Request request) {
		Optional<User> user = userRepository.findByToken(request.getToken());
		Optional<Comment> comment = commentRepository.findByAuthorAndStory(user.get(), request.getStory());
		return checkAccess.test(user, 2L) && (request.getComment() != null || request.getCommentContent() != null
				|| (comment.get().get_id().equals(request.getCommentContent().getId())
						|| comment.get().get_id().equals(request.getComment().get_id()))
				|| checkRole.test(user, "ADMIN"));
	}

	@Override
	public boolean checkAccessAdmin(Request request) {
		return checkRole.test(userRepository.findByToken(request.getToken()), "ADMIN");
	}

}
