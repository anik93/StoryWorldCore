package com.storyworld.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.storyworld.domain.elastic.CommentContent;
import com.storyworld.domain.json.Request;
import com.storyworld.domain.json.Response;
import com.storyworld.domain.sql.Comment;
import com.storyworld.service.AuthorizationService;
import com.storyworld.service.CommentService;

import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/comment")
public class CommentController {

	@Autowired
	private CommentService commentService;

	@Autowired
	private AuthorizationService authorizationService;

	@GetMapping
	public Mono<Response<CommentContent>> getCommetsByUser(@RequestHeader("Token") String token) {
		return Mono.just(commentService.getByUser(token));
	}

	@GetMapping("/{idStory}/{page}/{pageSize}")
	public Mono<Response<CommentContent>> getCommetsByStory(@PathVariable(value = "idStory") Long idStory,
			@PathVariable(value = "page") int page, @PathVariable(value = "pageSize") int pageSize) {
		return Mono.just(commentService.get(idStory, page, pageSize));
	}

	@PostMapping
	public Mono<Response<CommentContent>> saveCommet(@RequestBody Request request) {
		return authorizationService.checkAccess(request) ? Mono.just(commentService.save(request)) : Mono.empty();
	}

	@PostMapping("/like")
	public Mono<Response<CommentContent>> like(@RequestBody Request request) {
		return authorizationService.checkAccess(request) ? Mono.just(commentService.like(request)) : Mono.empty();
	}

	@PostMapping("/dislike")
	public Mono<Response<CommentContent>> dislike(@RequestBody Request request) {
		return authorizationService.checkAccess(request) ? Mono.just(commentService.dislike(request)) : Mono.empty();
	}

	@PutMapping
	public Mono<Response<CommentContent>> updateCommet(@RequestBody Request request) {
		return authorizationService.checkAccessToComment(request) ? Mono.just(commentService.update(request))
				: Mono.empty();
	}

	@DeleteMapping("/{id}")
	public Mono<Response<CommentContent>> deleteCommet(@PathVariable(value = "id") String _id,
			@RequestHeader("Token") String token) {
		return authorizationService.checkAccessToComment(new Request(token, new Comment(_id)))
				? Mono.just(commentService.delete(new Request(token, new Comment(_id))))
				: Mono.empty();
	}
}
