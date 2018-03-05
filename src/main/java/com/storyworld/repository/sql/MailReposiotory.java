package com.storyworld.repository.sql;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.storyworld.domain.sql.Mail;
import com.storyworld.domain.sql.User;
import com.storyworld.domain.sql.enums.Status;

public interface MailReposiotory extends JpaRepository<Mail, Long> {

	public Optional<List<Mail>> findByStatus(Status status);

	public Optional<Mail> findByUser(User user);

}
