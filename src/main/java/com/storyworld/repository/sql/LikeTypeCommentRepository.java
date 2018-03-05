package com.storyworld.repository.sql;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.storyworld.domain.sql.Comment;
import com.storyworld.domain.sql.LikeTypeComment;
import com.storyworld.domain.sql.User;

public interface LikeTypeCommentRepository extends JpaRepository<LikeTypeComment, Long> {

	public Optional<LikeTypeComment> findByUserAndComment(User user, Comment comment);

}
