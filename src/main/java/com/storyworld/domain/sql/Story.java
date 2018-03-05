package com.storyworld.domain.sql;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Length;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.storyworld.domain.sql.basic.BasicWithNameEntity;
import com.storyworld.domain.sql.enums.SchedulerStatus;
import com.storyworld.domain.sql.enums.StoryType;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "STORY")
public class Story extends BasicWithNameEntity {

	@NotNull
	private String contentId;

	@NotNull
	@Length(min = 4, max = 255)
	private String description;

	@Enumerated(EnumType.STRING)
	@NotNull
	private StoryType type;

	private Float avgRate;

	@JsonIgnore
	@Enumerated(EnumType.STRING)
	private SchedulerStatus status;

	@Transient
	private String rawText;

	@Transient
	private List<String> pages;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "authorId")
	@NotNull
	private User author;

	public Story(String name, String description, StoryType type, User author) {
		super(name);
		this.description = description;
		this.type = type;
		this.author = author;
	}

}
