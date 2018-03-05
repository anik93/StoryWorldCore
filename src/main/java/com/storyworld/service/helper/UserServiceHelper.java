package com.storyworld.service.helper;

import static com.storyworld.conditions.UserPredicate.validTokenWithTime;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import com.storyworld.domain.json.Message;
import com.storyworld.domain.json.Request;
import com.storyworld.domain.json.Response;
import com.storyworld.domain.json.enums.MessageText;
import com.storyworld.domain.json.enums.StatusMessage;
import com.storyworld.domain.sql.Mail;
import com.storyworld.domain.sql.MailToken;
import com.storyworld.domain.sql.Role;
import com.storyworld.domain.sql.User;
import com.storyworld.domain.sql.enums.Status;
import com.storyworld.domain.sql.enums.TypeToken;
import com.storyworld.functional.JSONPrepare;
import com.storyworld.repository.sql.MailReposiotory;
import com.storyworld.repository.sql.MailTokenRepository;
import com.storyworld.repository.sql.RoleRepository;
import com.storyworld.repository.sql.UserRepository;

@Component
public class UserServiceHelper {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private RoleRepository roleRepository;

	@Autowired
	private MailTokenRepository mailTokenRepository;

	@Autowired
	private MailReposiotory mailReposiotory;

	private JSONPrepare<User> jsonPrepare = (statusMessage, message, user, list, success,
			counter) -> new Response<User>(new Message(statusMessage, message), user, list, success, counter);

	public Response<User> successLogin(User user) {
		user.setToken(UUID.randomUUID().toString());
		user.setLastActionTime(LocalDateTime.now());
		user.setBlock(false);
		user.setIncorrectLogin(0);
		user.setLastIncorrectLogin(null);
		userRepository.save(user);
		return jsonPrepare.prepareResponse(StatusMessage.SUCCESS, MessageText.LOGIN, user, null, true, null);
	}

	public Response<User> unsuccessLogin(User user) {
		int incrementIncorrect = user.getIncorrectLogin();
		incrementIncorrect++;
		user.setIncorrectLogin(incrementIncorrect);
		if (user.getIncorrectLogin() == 5) {
			user.setBlock(true);
			user.setLastIncorrectLogin(LocalDateTime.now());
		}
		userRepository.save(user);
		return jsonPrepare.prepareResponse(StatusMessage.ERROR, MessageText.INCORRECT_DATA, null, null, false, null);
	}

	public Response<User> getUsersFromDB(int page, int sizePage) {
		return Optional.ofNullable(userRepository.findAll(PageRequest.of(page, sizePage)))
				.map(users -> jsonPrepare.prepareResponse(null, null, null, users.getContent(), true, null))
				.orElseGet(() -> jsonPrepare.prepareResponse(StatusMessage.ERROR, MessageText.INCORRECT_DATA, null,
						null, false, null));
	}

	public Response<User> confirmRegisterInDB(MailToken mailToken) {
		return Optional.ofNullable(userRepository.findById(mailToken.getUser().getId())).map(user -> {
			user.get().setBlock(false);
			userRepository.save(user.get());
			mailTokenRepository.delete(mailToken);
			return jsonPrepare.prepareResponse(StatusMessage.SUCCESS, MessageText.SUCCESS_REGISTERED, null, null, true,
					null);
		}).orElseGet(() -> jsonPrepare.prepareResponse(StatusMessage.ERROR, MessageText.INCORRECT_DATA, null, null,
				false, null));
	}

	public Response<User> updateUserNameOrMail(User user) {
		Optional.ofNullable(user.getName()).ifPresent(user::setName);
		Optional.ofNullable(user.getMail()).ifPresent(user::setMail);
		user.setLastActionTime(LocalDateTime.now());
		userRepository.save(user);
		return jsonPrepare.prepareResponse(StatusMessage.SUCCESS, MessageText.SUCCESS_UPDATED, user, null, true, null);
	}

	public Response<User> updateUserPassword(String newPassword, User user, MailToken mailToken) {
		user.setPassword(newPassword);
		userRepository.save(user);
		Optional.ofNullable(mailToken).ifPresent(token -> mailTokenRepository.delete(mailToken));
		return jsonPrepare.prepareResponse(StatusMessage.SUCCESS, MessageText.PASSWORD_CHANGED, user, null, true, null);
	}

	public Response<User> changePasswordIfMailTokenIsValid(Request request, MailToken mailToken) {
		return validTokenWithTime.test(mailToken, request)
				? Optional.ofNullable(userRepository.findById(mailToken.getUser().getId()))
						.map(user -> updateUserPassword(request.getUser().getPassword(), user.get(), mailToken))
						.orElseGet(() -> jsonPrepare.prepareResponse(StatusMessage.ERROR, MessageText.INCORRECT_DATA,
								null, null, false, null))
				: jsonPrepare.prepareResponse(StatusMessage.ERROR, MessageText.INCORRECT_DATA, null, null, false, null);
	}

	public Response<User> prepareUserToSave(User user) {
		user.setBlock(false);
		user.setDeleted(false);
		User userRegister = userRepository.save(user);
		Optional<Role> role = roleRepository.findById(1L);
		Set<Role> roles = new HashSet<>();
		role.ifPresent(r -> roles.add(r));
		userRegister.setRoles(roles);
		userRepository.save(userRegister);
		createMailToken(TypeToken.REGISTER, userRegister);
		Mail mail = new Mail(TypeToken.REGISTER, Status.READY, userRegister);
		mailReposiotory.save(mail);
		return jsonPrepare.prepareResponse(StatusMessage.SUCCESS, MessageText.REGISTER, null, null, true, null);
	}

	public void createMailToken(TypeToken typeToken, User user) {
		MailToken mailToken = new MailToken(typeToken, UUID.randomUUID().toString(), LocalDateTime.now(), user);
		if (typeToken.equals(TypeToken.RESTART_PASSWORD))
			mailTokenRepository.saveAll(mailTokenRepository.findByUser(user).map(tokens -> {
				tokens.add(mailToken);
				return tokens;
			}).get());
		else {
			Set<MailToken> tokens = new HashSet<>();
			tokens.add(mailToken);
			mailTokenRepository.saveAll(tokens);
		}
	}

	public Response<User> addMailToMailerAfterRestartPassword(User user) {
		return mailTokenRepository.findByUserAndTypeToken(user, TypeToken.RESTART_PASSWORD)
				.map(mailToken -> saveMailTokenAfterRequestForRestartPassword(mailToken, user))
				.orElseGet(() -> jsonPrepare.prepareResponse(StatusMessage.ERROR, MessageText.INCORRECT_DATA, null,
						null, false, null));
	}

	public Response<User> saveMailTokenAfterRequestForRestartPassword(MailToken mailToken, User user) {
		long id = mailToken.getId();
		mailTokenRepository.findByUser(user).map(tokens -> {
			tokens.removeIf(x -> x.getId() == id);
			mailToken.setValidationTime(LocalDateTime.now());
			mailToken.setToken(UUID.randomUUID().toString());
			tokens.add(mailToken);
			mailTokenRepository.saveAll(tokens);
			return jsonPrepare.prepareResponse(StatusMessage.INFO, MessageText.RESTARTED, null, null, true, null);
		}).orElseGet(() -> {
			createMailToken(TypeToken.RESTART_PASSWORD, user);
			return jsonPrepare.prepareResponse(StatusMessage.INFO, MessageText.RESTARTED, null, null, true, null);
		});
		Mail mail = new Mail(TypeToken.RESTART_PASSWORD, Status.READY, user);
		mailReposiotory.save(mail);
		return jsonPrepare.prepareResponse(StatusMessage.INFO, MessageText.RESTARTED, null, null, true, null);
	}

}
