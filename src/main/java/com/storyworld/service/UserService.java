package com.storyworld.service;

import com.storyworld.domain.json.Request;
import com.storyworld.domain.json.Response;
import com.storyworld.domain.sql.User;

public interface UserService {

	public void removeToken(User user);

	public Response<User> login(Request request);

	public Response<User> register(Request request);

	public Response<User> restartPassword(Request request);

	public Response<User> remindPassword(Request request);

	public Response<User> confirmRegister(Request request);

	public Response<User> changePassword(Request request);

	public Response<User> update(Request request);

	public Response<User> getUser(Request requeste);

	public Response<User> logout(Request request);

	public Response<User> getUsers(Request request);

	public Response<User> delete(Long id);

	public Response<User> block(Request request);

}
