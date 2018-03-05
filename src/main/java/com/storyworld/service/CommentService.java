package com.storyworld.service;

import com.storyworld.domain.elastic.CommentContent;
import com.storyworld.domain.json.Request;
import com.storyworld.domain.json.Response;

public interface CommentService {

	public Response<CommentContent> get(Long idStory, int page, int pageSize);

	public Response<CommentContent> save(Request request);

	public Response<CommentContent> update(Request request);

	public Response<CommentContent> delete(Request request);

	public Response<CommentContent> like(Request request);

	public Response<CommentContent> dislike(Request request);

	public Response<CommentContent> getByUser(String token);

}
