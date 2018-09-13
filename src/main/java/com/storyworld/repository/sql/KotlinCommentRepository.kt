package com.storyworld.repository.sql

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.storyworld.domain.sql.Comment;
import com.storyworld.domain.sql.Story;
import com.storyworld.domain.sql.User;

interface KotlinCommentRepository : JpaRepository<Comment, Long> {

	fun findByStory(story: Story, pageRequest: Pageable): Page<Comment>

	fun findByAuthorAndStory(author: User, story: Story): Optional<Comment>

	fun findByAuthor(author: User, pageRequest: Pageable): Page<Comment>

	fun findBy_id(_id: String): Optional<Comment>

	fun findByAuthor(author: User): List<Comment>

}