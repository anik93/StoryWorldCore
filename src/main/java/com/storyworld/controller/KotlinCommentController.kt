package com.storyworld.controller

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

import com.storyworld.domain.elastic.CommentContent
import com.storyworld.domain.json.Request
import com.storyworld.domain.json.Response
import com.storyworld.domain.sql.Comment
import com.storyworld.service.AuthorizationService
import com.storyworld.service.CommentService

import reactor.core.publisher.Mono

@RestController
@RequestMapping("/kotlin/comment")
class KotlinCommentController {

	@Autowired
	lateinit var commentService: CommentService;

	@Autowired
	lateinit var authorizationService: AuthorizationService;

	@GetMapping
	fun getCommetsByUser(@RequestHeader("Token") token: String) = Mono.just(commentService.getByUser(token))


	@GetMapping("/{idStory}/{page}/{pageSize}")
	fun getCommetsByStory(@PathVariable(value = "idStory") idStory: Long,
						  @PathVariable(value = "page") page: Int, @PathVariable(value = "pageSize") pageSize: Int) = Mono.just(commentService.get(idStory, page, pageSize))

	@PostMapping
	fun saveCommet(@RequestBody request: Request) = if (authorizationService.checkAccess(request)) Mono.just(commentService.save(request)) else Mono.empty()

	@PostMapping("/like")
	fun like(@RequestBody request: Request) = if (authorizationService.checkAccess(request)) Mono.just(commentService.like(request)) else Mono.empty()

	@PostMapping("/dislike")
	fun dislike(@RequestBody request: Request) = if (authorizationService.checkAccess(request)) Mono.just(commentService.dislike(request)) else Mono.empty()

	@PutMapping
	fun updateCommet(@RequestBody request: Request) = if (authorizationService.checkAccessToComment(request)) Mono.just(commentService.update(request)) else Mono.empty()

	@DeleteMapping("/{id}")
	fun deleteCommet(@PathVariable(value = "id") _id: String, @RequestHeader("Token") token: String) = if (authorizationService.checkAccessToComment(Request(token, Comment(_id))))
		Mono.just(commentService.delete(Request(token, Comment(_id)))) else Mono.empty()
}