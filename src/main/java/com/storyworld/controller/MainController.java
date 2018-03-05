package com.storyworld.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.storyworld.annotations.Secure;
import com.storyworld.domain.json.Response;

import reactor.core.publisher.Mono;

@RestController
public class MainController {

	@Secure
	@RequestMapping
	public Mono<Response<Object>> defaultHandler() {
		return Mono.just(new Response<Object>());
	}

}
