package com.storyworld.repository.sql

import org.springframework.data.jpa.repository.JpaRepository
import com.storyworld.domain.sql.LikeTypeComment
import com.storyworld.domain.sql.User
import com.storyworld.domain.sql.Comment
import java.util.Optional

interface KotlinLikeTypeCommentRepository : JpaRepository<LikeTypeComment, Long> {

	fun findByUserAndComment(user: User, comment: Comment): Optional<LikeTypeComment>

}