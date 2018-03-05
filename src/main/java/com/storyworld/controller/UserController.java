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

import com.storyworld.domain.json.Request;
import com.storyworld.domain.json.Response;
import com.storyworld.domain.sql.User;
import com.storyworld.service.AuthorizationService;
import com.storyworld.service.UserService;

import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/user")
public class UserController {

	@Autowired
	private UserService userService;

	@Autowired
	private AuthorizationService authorizationService;

	@PostMapping("/login")
	public Mono<Response<User>> login(@RequestBody Request request) {
		return Mono.just(userService.login(request));
	}

	@PostMapping("/register")
	public Mono<Response<User>> register(@RequestBody Request request) {
		return Mono.just(userService.register(request));
	}

	@PostMapping("/restartPassword")
	public Mono<Response<User>> restart(@RequestBody Request request) {
		return Mono.just(userService.restartPassword(request));
	}

	@PutMapping("/remindPassword")
	public Mono<Response<User>> remindPassword(@RequestBody Request request) {
		return Mono.just(userService.remindPassword(request));
	}

	@PostMapping("/confirmRegister")
	public Mono<Response<User>> confirmRegister(@RequestBody Request request) {
		return Mono.just(userService.confirmRegister(request));
	}

	@PutMapping("/changePassword")
	public Mono<Response<User>> changePassword(Request request) {
		return Mono.just(userService.changePassword(request));
	}

	@PutMapping("/updateUser")
	public Mono<Response<User>> updateUser(@RequestBody Request request) {
		return authorizationService.checkAccessToUser(request) ? Mono.just(userService.update(request)) : Mono.empty();

	}

	@PutMapping("/block")
	public Mono<Response<User>> blockUser(@RequestBody Request request) {
		return authorizationService.checkAccessAdmin(request) ? Mono.just(userService.block(request)) : Mono.empty();
	}

	@PostMapping("/getUsers")
	public Mono<Response<User>> getUsers(@RequestBody Request request) {
		return authorizationService.checkAccess(request) ? Mono.just(userService.getUsers(request)) : Mono.empty();
	}

	@PostMapping("/logout")
	public Mono<Response<User>> logout(@RequestBody Request request) {
		return Mono.just(userService.logout(request));
	}

	@DeleteMapping("/{id}")
	public Mono<Response<User>> delete(@PathVariable(value = "id") Long id, @RequestHeader("Token") String token) {
		return authorizationService.checkAccessToUser(new Request(token)) ? Mono.just(userService.delete(id))
				: Mono.empty();
	}

	@GetMapping("/{id}")
	public Mono<Response<User>> get(@PathVariable(value = "id") Long id, @RequestHeader("Token") String token) {
		return Mono.just(userService.getUser(new Request(token)));
	}

}
