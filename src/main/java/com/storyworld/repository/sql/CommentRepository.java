package com.storyworld.repository.sql;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.storyworld.domain.sql.Comment;
import com.storyworld.domain.sql.Story;
import com.storyworld.domain.sql.User;

public interface CommentRepository extends JpaRepository<Comment, Long> {

	public Page<Comment> findByStory(Story story, Pageable pageRequest);

	public Optional<Comment> findByAuthorAndStory(User author, Story story);

	public Page<Comment> findByAuthor(User author, Pageable pageRequest);

	public Optional<Comment> findBy_id(String _id);

	public List<Comment> findByAuthor(User author);
}
