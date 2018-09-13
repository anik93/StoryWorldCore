package com.storyworld.controller

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

import com.storyworld.domain.json.Request
import com.storyworld.domain.json.Response
import com.storyworld.domain.sql.Story
import com.storyworld.service.AuthorizationService
import com.storyworld.service.StoryService

import reactor.core.publisher.Mono

@RestController
@RequestMapping("/kotlin/story")
class KotlinStoryController {

	@Autowired
	lateinit var authorizationService: AuthorizationService

	@Autowired
	lateinit var storyService: StoryService

	@GetMapping
	fun getStoryByUser(@RequestHeader("Token") token: String) = Mono.just(storyService.getStoriesByUser(token))

	@GetMapping("/{id}")
	fun getStory(@PathVariable(value = "id") id: Long) = Mono.just(storyService.getStory(id))

	@GetMapping("/{page}/{size}")
	fun getStories(@PathVariable(value = "page") page: Int, @PathVariable(value = "size") size: Int) = Mono.just(ResponseEntity<Response<Story>>(storyService.getStories(page, size, null), HttpStatus.OK))

	@GetMapping("/{page}/{size}/{text}")
	fun searchStories(@PathVariable(value = "page") page: Int, @PathVariable(value = "size") size: Int, @PathVariable(value = "text") text: String) = Mono.just(storyService.getStories(page, size, text))

	@PostMapping("/add")
	fun updateUser(@RequestBody request: Request) = if (authorizationService.checkAccess(request)) Mono.just(storyService.addStory(request)) else Mono.empty()

}