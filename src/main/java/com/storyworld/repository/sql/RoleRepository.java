package com.storyworld.repository.sql;

import org.springframework.data.jpa.repository.JpaRepository;

import com.storyworld.domain.sql.Role;

public interface RoleRepository extends JpaRepository<Role, Long> {

}
