package com.storyworld.domain.json;

import java.util.EnumMap;
import java.util.Map;

import org.springframework.data.domain.Sort;

import com.storyworld.domain.elastic.CommentContent;
import com.storyworld.domain.sql.Comment;
import com.storyworld.domain.sql.Story;
import com.storyworld.domain.sql.User;
import com.storyworld.domain.sql.enums.TypeToken;

import lombok.Data;
import lombok.ToString;
import lombok.NoArgsConstructor;

@Data
@ToString
@NoArgsConstructor
public class Request {

	private String token;

	private User user;

	private TypeToken tokenType;

	private CommentContent commentContent;

	private Comment comment;

	private Story story;

	private int page;

	private int sizePage;

	private Map<Sort.Direction, String[]> sorts = new EnumMap<>(Sort.Direction.class);

	public Request(String token) {
		this.token = token;
	}

	public Request(String token, Comment comment) {
		this.token = token;
		this.setComment(comment);
	}

}
