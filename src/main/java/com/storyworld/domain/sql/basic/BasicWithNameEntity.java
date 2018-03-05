package com.storyworld.domain.sql.basic;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Length;
import org.springframework.data.annotation.LastModifiedDate;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString
@AllArgsConstructor
@NoArgsConstructor
@MappedSuperclass
@JsonInclude(Include.NON_NULL)
public class BasicWithNameEntity extends BasicEntity {

	private static final long serialVersionUID = 5728401069192040967L;

	@NotNull
	@Column(unique = true)
	@Length(min = 4, max = 255)
	protected String name;

	@LastModifiedDate
	private Date lastModifiedDate;

	public BasicWithNameEntity(Long id, String name) {
		super(id);
		this.name = name;
	}

	public BasicWithNameEntity(String name) {
		this.name = name;
	}

}
