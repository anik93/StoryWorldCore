package com.storyworld.domain.sql;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import org.hibernate.validator.constraints.Length;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;
import com.storyworld.domain.sql.basic.BasicWithNameEntity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString
@NoArgsConstructor
@Entity
@Table(name = "USER")
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
public class User extends BasicWithNameEntity {

	private static final long serialVersionUID = -351631306623818778L;

	@NotNull
	@Length(min = 6, max = 255)
	@JsonProperty(access = Access.WRITE_ONLY)
	private String password;

	@NotNull
	@Column(unique = true)
	@Length(min = 1, max = 255)
	@Pattern(regexp = "[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,4}")
	private String mail;

	@JsonProperty(access = Access.READ_ONLY)
	private String token;

	@JsonIgnore
	private LocalDateTime lastActionTime;

	@JsonIgnore
	private int incorrectLogin;

	private boolean block;

	@JsonIgnore
	private LocalDateTime lastIncorrectLogin;

	@JsonIgnore
	private boolean deleted;

	@ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	private Set<Role> roles = new HashSet<>();

	public User(long id, String name) {
		super(id, name);
	}

	public User(String name, String password, String mail, String token, LocalDateTime lastActionTime,
			int incorrectLogin, boolean block, LocalDateTime lastIncorrectLogin, boolean deleted, Set<Role> roles) {
		super();
		this.name = name;
		this.password = password;
		this.mail = mail;
		this.token = token;
		this.lastActionTime = lastActionTime;
		this.incorrectLogin = incorrectLogin;
		this.block = block;
		this.lastIncorrectLogin = lastIncorrectLogin;
		this.deleted = deleted;
		this.roles = roles;
	}

}
