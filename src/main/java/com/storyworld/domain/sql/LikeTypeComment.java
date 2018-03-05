package com.storyworld.domain.sql;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import com.storyworld.domain.sql.basic.BasicEntity;
import com.storyworld.domain.sql.enums.LikeType;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@Entity
@Table(name = "LIKETYPECOMMENT")
public class LikeTypeComment extends BasicEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "userId")
	@NotNull
	private User user;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "commentId")
	@NotNull
	private Comment comment;

	@Enumerated(EnumType.STRING)
	@NotNull
	private LikeType likeType;

	public LikeTypeComment(User user, Comment comment, LikeType likeType) {
		super();
		this.user = user;
		this.comment = comment;
		this.likeType = likeType;
	}

}
