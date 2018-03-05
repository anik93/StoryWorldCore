package com.storyworld.repository.sql;

import java.util.Optional;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;

import com.storyworld.domain.sql.MailToken;
import com.storyworld.domain.sql.User;
import com.storyworld.domain.sql.enums.TypeToken;

public interface MailTokenRepository extends JpaRepository<MailToken, Long> {

	public Optional<MailToken> findByToken(String token);

	public Optional<Set<MailToken>> findByUser(User user);

	public Optional<MailToken> findByUserAndTypeToken(User user, TypeToken typeToken);

}
