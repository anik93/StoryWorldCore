package com.storyworld.repository.sql;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.storyworld.domain.sql.User;

public interface UserRepository extends JpaRepository<User, Long> {

	public Optional<User> findByName(String name);

	public Optional<User> findByToken(String token);

	public Optional<List<User>> findByTokenNotNull();

	public Optional<User> findByMail(String mail);

}
