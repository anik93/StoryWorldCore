package com.storyworld.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.storyworld.annotations.Secure;
import com.storyworld.domain.json.Request;
import com.storyworld.domain.json.Response;
import com.storyworld.domain.sql.Story;
import com.storyworld.service.AuthorizationService;
import com.storyworld.service.StoryService;

import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/story")
public class StoryController {

	@Autowired
	private AuthorizationService authorizationService;

	@Autowired
	private StoryService storyService;

	@GetMapping
	public Mono<Response<Story>> getStoryByUser(@RequestHeader("Token") String token) {
		return Mono.just(storyService.getStoriesByUser(token));
	}

	@GetMapping("/{id}")
	public Mono<Response<Story>> getStory(@PathVariable(value = "id") Long id) {
		return Mono.just(storyService.getStory(id));
	}

	@GetMapping("/{page}/{size}")
	public ResponseEntity<Response<Story>> getStories(@PathVariable(value = "page") int page,
			@PathVariable(value = "size") int size) {
		return new ResponseEntity<Response<Story>>(storyService.getStories(page, size, null), HttpStatus.OK);
	}

	@GetMapping("/{page}/{size}/{text}")
	public Mono<Response<Story>> searchStories(@PathVariable(value = "page") int page,
			@PathVariable(value = "size") int size, @PathVariable(value = "text") String text) {
		return Mono.just(storyService.getStories(page, size, text));
	}

	@Secure()
	@PostMapping("/add")
	public Mono<Response<Story>> updateUser(@RequestBody Request request) {
		return authorizationService.checkAccess(request) ? Mono.just(storyService.addStory(request)) : Mono.empty();
	}
}
