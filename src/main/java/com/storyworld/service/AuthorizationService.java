package com.storyworld.service;

import com.storyworld.domain.json.Request;

public interface AuthorizationService {

	public boolean checkAccess(Request request);

	public boolean checkAccessToUser(Request request);

	public boolean checkAccessToComment(Request request);

	public boolean checkAccessAdmin(Request request);

}
