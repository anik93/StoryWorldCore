package com.storyworld.controller

import org.springframework.web.bind.annotation.RestController
import org.springframework.beans.factory.annotation.Autowired
import com.storyworld.repository.sql.UserRepository
import org.springframework.web.bind.annotation.*

import com.storyworld.domain.json.Request
import com.storyworld.service.UserService
import com.storyworld.service.AuthorizationService

import reactor.core.publisher.Mono

@RestController
@RequestMapping("/kotlin/user")
class KotlinUserController {

	@Autowired
	lateinit var userService: UserService

	@Autowired
	lateinit var authorizationService: AuthorizationService

	@PostMapping("/login")
	fun login(@RequestBody request: Request) = Mono.just(userService.login(request))

	@PostMapping("/register")
	fun register(@RequestBody request: Request) = Mono.just(userService.register(request))

	@PostMapping("/restartPassword")
	fun restart(@RequestBody request: Request) = Mono.just(userService.restartPassword(request))

	@PutMapping("/remindPassword")
	fun remindPassword(@RequestBody request: Request) = Mono.just(userService.remindPassword(request))

	@PostMapping("/confirmRegister")
	fun confirmRegister(@RequestBody request: Request) = Mono.just(userService.confirmRegister(request))

	@PutMapping("/changePassword")
	fun changePassword(@RequestBody request: Request) = Mono.just(userService.changePassword(request))

	@PutMapping("/updateUser")
	fun updateUser(@RequestBody request: Request) = if (authorizationService.checkAccessToUser(request)) Mono.just(userService.update(request)) else Mono.empty()

	@PutMapping("/block")
	fun blockUser(@RequestBody request: Request) = if (authorizationService.checkAccessAdmin(request)) Mono.just(userService.block(request)) else Mono.empty()

	@PostMapping("/getUsers")
	fun getUsers(@RequestBody request: Request) = if (authorizationService.checkAccess(request)) Mono.just(userService.getUsers(request)) else Mono.empty()

	@PostMapping("/logout")
	fun logout(@RequestBody request: Request) = Mono.just(userService.logout(request))

	@DeleteMapping("/{id}")
	fun delete(@PathVariable(value = "id") id: Long, @RequestHeader("Token") token: String) = if (authorizationService.checkAccessToUser(Request(token))) Mono.just(userService.delete(id))
	else Mono.empty()

	@GetMapping("/{id}")
	fun get(@RequestHeader("Token") token: String) = Mono.just(userService.getUser(Request(token)))
}