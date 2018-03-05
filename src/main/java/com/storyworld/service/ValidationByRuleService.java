package com.storyworld.service;

import java.util.List;

import com.storyworld.domain.sql.Story;
import com.storyworld.domain.sql.StoryRule;

public interface ValidationByRuleService {

	public void validate(Story story, List<StoryRule> rule) throws NoSuchMethodException, SecurityException;

}
