package com.storyworld.domain.sql;

import java.time.LocalDateTime;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.storyworld.domain.sql.basic.BasicEntity;
import com.storyworld.domain.sql.enums.TypeToken;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@Entity
@Table(name = "MAIL_TOKEN")
public class MailToken extends BasicEntity {

	@Enumerated(EnumType.STRING)
	private TypeToken typeToken;

	private String token;

	private LocalDateTime validationTime;

	@ManyToOne(fetch = FetchType.EAGER)
	private User user;

	public MailToken(TypeToken typeToken, String token, LocalDateTime validationTime, User user) {
		super();
		this.typeToken = typeToken;
		this.token = token;
		this.validationTime = validationTime;
		this.user = user;
	}

}
