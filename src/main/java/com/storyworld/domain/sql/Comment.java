package com.storyworld.domain.sql;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import com.storyworld.domain.sql.basic.BasicEntity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "COMMENT")
public class Comment extends BasicEntity {

	private String _id;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "authorId")
	@NotNull
	private User author;

	@ManyToOne
	@JoinColumn(name = "storyId")
	@NotNull
	private Story story;

	public Comment(User author, Story story) {
		super();
		this.author = author;
		this.story = story;
	}

	public Comment(String _id) {
		super();
		this._id = _id;
	}

}
