package com.storyworld.repository.sql;

import org.springframework.data.jpa.repository.JpaRepository;

import com.storyworld.domain.sql.StoryRule;

public interface StoryRuleRepository extends JpaRepository<StoryRule, Long> {

}
