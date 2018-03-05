package com.storyworld.service.impl;

import static com.storyworld.conditions.CommonPredicate.validatePageAndPageSize;
import static com.storyworld.conditions.UserPredicate.*;

import java.util.Optional;

import javax.persistence.PersistenceException;

import org.hibernate.exception.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.storyworld.domain.json.Message;
import com.storyworld.domain.json.Request;
import com.storyworld.domain.json.Response;
import com.storyworld.domain.json.enums.MessageText;
import com.storyworld.domain.json.enums.StatusMessage;
import com.storyworld.domain.sql.User;
import com.storyworld.functional.JSONPrepare;
import com.storyworld.repository.sql.MailTokenRepository;
import com.storyworld.repository.sql.UserRepository;
import com.storyworld.service.UserService;
import com.storyworld.service.helper.UserServiceHelper;

@Service
public class UserServiceImpl implements UserService {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private MailTokenRepository mailTokenRepository;

	@Autowired
	private UserServiceHelper userServiceHelper;

	private JSONPrepare<User> jsonPrepare = (statusMessage, message, user, list, success,
			counter) -> new Response<User>(new Message(statusMessage, message), user, list, success, counter);

	private static final Logger LOG = LoggerFactory.getLogger(UserServiceImpl.class);

	@Override
	public void removeToken(User user) {
		if (checkValidTimeInMinutes.test(user.getLastActionTime(), 120)) {
			user.setLastActionTime(null);
			user.setToken(null);
			userRepository.save(user);
		}
	}

	@Override
	public Response<User> login(Request request) {
		return userRepository.findByName(request.getUser().getName())
				.map(user -> vaildUserLogin.test(user, request) ? userServiceHelper.successLogin(user)
						: userServiceHelper.unsuccessLogin(user))
				.orElseGet(() -> jsonPrepare.prepareResponse(StatusMessage.ERROR, MessageText.INCORRECT_DATA, null,
						null, false, null));
	}

	@Override
	public Response<User> register(Request request) {
		try {
			return Optional.ofNullable(request.getUser()).map(user -> userServiceHelper.prepareUserToSave(user))
					.orElseGet(() -> jsonPrepare.prepareResponse(StatusMessage.ERROR, MessageText.INCORRECT_DATA, null,
							null, false, null));
		} catch (PersistenceException e) {
			LOG.error(e.toString());
			return jsonPrepare.prepareResponse(StatusMessage.ERROR, MessageText.UNIQUE_NAME_OR_EMAIL, null, null, false,
					null);
		} catch (Exception e) {
			LOG.error(e.toString());
			return jsonPrepare.prepareResponse(StatusMessage.ERROR, MessageText.INCORRECT_DATA, null, null, false,
					null);
		}
	}

	@Override
	public Response<User> restartPassword(Request request) {
		return userRepository.findByMail(request.getUser().getMail())
				.map(user -> userServiceHelper.addMailToMailerAfterRestartPassword(user)).orElseGet(() -> jsonPrepare
						.prepareResponse(StatusMessage.ERROR, MessageText.INCORRECT_DATA, null, null, false, null));
	}

	@Override
	public Response<User> remindPassword(Request request) {
		return mailTokenRepository.findByToken(request.getToken())
				.map(mailToken -> userServiceHelper.changePasswordIfMailTokenIsValid(request, mailToken))
				.orElseGet(() -> jsonPrepare.prepareResponse(StatusMessage.ERROR, MessageText.INCORRECT_DATA, null,
						null, false, null));
	}

	@Override
	public Response<User> confirmRegister(Request request) {
		return mailTokenRepository.findByToken(request.getToken())
				.map(mailToken -> validMailToken.test(mailToken, request)
						? userServiceHelper.confirmRegisterInDB(mailToken)
						: jsonPrepare.prepareResponse(StatusMessage.ERROR, MessageText.INCORRECT_DATA, null, null,
								false, null))
				.orElseGet(() -> jsonPrepare.prepareResponse(StatusMessage.ERROR, MessageText.INCORRECT_DATA, null,
						null, false, null));
	}

	@Override
	public Response<User> changePassword(Request request) {
		try {
			return userRepository.findById(request.getUser().getId())
					.map(user -> userServiceHelper.updateUserPassword(request.getUser().getPassword(), user, null))
					.orElseGet(() -> jsonPrepare.prepareResponse(StatusMessage.ERROR, MessageText.INCORRECT_DATA, null,
							null, false, null));
		} catch (Exception e) {
			LOG.error(e.toString());
			return jsonPrepare.prepareResponse(StatusMessage.ERROR, MessageText.INCORRECT_DATA, null, null, false,
					null);
		}
	}

	@Override
	public Response<User> update(Request request) {
		try {
			return userRepository.findById(request.getUser().getId())
					.map(user -> userServiceHelper.updateUserNameOrMail(user)).orElseGet(() -> jsonPrepare
							.prepareResponse(StatusMessage.ERROR, MessageText.INCORRECT_DATA, null, null, false, null));
		} catch (PersistenceException e) {
			LOG.error(e.toString());
			return e.getCause() instanceof ConstraintViolationException
					? jsonPrepare.prepareResponse(StatusMessage.ERROR, MessageText.UNIQUE_NAME_OR_EMAIL, null, null,
							false, null)
					: jsonPrepare.prepareResponse(StatusMessage.ERROR, MessageText.INCORRECT_DATA, null, null, false,
							null);
		} catch (Exception e) {
			LOG.error(e.toString());
			return jsonPrepare.prepareResponse(StatusMessage.ERROR, MessageText.INCORRECT_DATA, null, null, false,
					null);
		}
	}

	@Override
	public Response<User> getUser(Request request) {
		User userGet = Optional.ofNullable(request.getUser()).map(user -> userRepository.findById(user.getId()).get())
				.orElseGet(() -> userRepository.findByToken(request.getToken()).get());

		return Optional.ofNullable(userGet).map(user -> {
			user.setToken(null);
			return jsonPrepare.prepareResponse(null, null, user, null, true, null);
		}).orElseGet(() -> jsonPrepare.prepareResponse(StatusMessage.ERROR, MessageText.INCORRECT_DATA, null, null,
				false, null));
	}

	@Override
	public Response<User> logout(Request request) {
		return userRepository.findByToken(request.getToken()).map(user -> {
			user.setToken(null);
			user.setLastActionTime(null);
			userRepository.save(user);
			return jsonPrepare.prepareResponse(StatusMessage.SUCCESS, MessageText.LOGOUT, null, null, true, null);
		}).orElseGet(() -> jsonPrepare.prepareResponse(StatusMessage.ERROR, MessageText.INCORRECT_DATA, null, null,
				false, null));
	}

	@Override
	public Response<User> getUsers(Request request) {
		return validatePageAndPageSize.test(request.getPage(), request.getSizePage())
				? userServiceHelper.getUsersFromDB(request.getPage(), request.getSizePage())
				: jsonPrepare.prepareResponse(StatusMessage.ERROR, MessageText.INCORRECT_DATA, null, null, false, null);
	}

	@Override
	public Response<User> delete(Long id) {
		return userRepository.findById(id).map(user -> {
			user.setDeleted(true);
			userRepository.save(user);
			return jsonPrepare.prepareResponse(StatusMessage.SUCCESS, MessageText.DELTED, user, null, true, null);
		}).orElseGet(() -> jsonPrepare.prepareResponse(StatusMessage.ERROR, MessageText.INCORRECT_DATA, null, null,
				false, null));
	}

	@Override
	public Response<User> block(Request request) {
		return userRepository.findById(request.getUser().getId()).map(user -> {
			user.setBlock(request.getUser().isBlock());
			userRepository.save(user);
			return request.getUser().isBlock()
					? jsonPrepare.prepareResponse(StatusMessage.SUCCESS, MessageText.BLOCKED, user, null, true, null)
					: jsonPrepare.prepareResponse(StatusMessage.SUCCESS, MessageText.UNBLOCKED, user, null, true, null);
		}).orElseGet(() -> jsonPrepare.prepareResponse(StatusMessage.ERROR, MessageText.INCORRECT_DATA, null, null,
				false, null));
	}

}
