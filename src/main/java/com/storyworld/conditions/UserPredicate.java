package com.storyworld.conditions;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.function.BiPredicate;

import com.storyworld.domain.json.Request;
import com.storyworld.domain.sql.MailToken;
import com.storyworld.domain.sql.User;
import com.storyworld.domain.sql.enums.TypeToken;

public class UserPredicate {

	private UserPredicate() {

	}

	public static final BiPredicate<LocalDateTime, Integer> checkValidTimeInMinutes = (time,
			howLong) -> ChronoUnit.MINUTES.between(time, LocalDateTime.now()) >= howLong;

	public static final BiPredicate<MailToken, Request> validMailToken = (mailToken,
			request) -> mailToken.getTypeToken().equals(TypeToken.REGISTER)
					&& mailToken.getToken().equals(request.getToken());

	public static final BiPredicate<MailToken, Request> validTokenWithTime = (mailToken,
			request) -> validMailToken.test(mailToken, request)
					&& checkValidTimeInMinutes.test(mailToken.getValidationTime(), 1440);

	public static final BiPredicate<User, Request> vaildUserLogin = (user,
			request) -> user.getName().equals(request.getUser().getName())
					&& user.getPassword().equals(request.getUser().getPassword())
					&& (!user.isBlock() || (user.getLastIncorrectLogin() != null
							&& checkValidTimeInMinutes.test(user.getLastIncorrectLogin(), 10)));

}
