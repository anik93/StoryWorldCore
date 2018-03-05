package com.storyworld.scheduling;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.storyworld.domain.sql.Story;
import com.storyworld.domain.sql.StoryRule;
import com.storyworld.domain.sql.enums.SchedulerStatus;
import com.storyworld.repository.sql.StoryRepository;
import com.storyworld.repository.sql.StoryRuleRepository;
import com.storyworld.service.ValidationByRuleService;

@Component
public class ValidateStoryScheduler {

	@Autowired
	private StoryRepository storyRepository;

	@Autowired
	private StoryRuleRepository storyRuleRepository;

	@Autowired
	private ValidationByRuleService validationByRuleService;

	private static final Logger LOG = LoggerFactory.getLogger(ValidateStoryScheduler.class);

	@Scheduled(fixedDelay = 10000)
	public void checkQueue() {
		Optional<Story> story = storyRepository.findTopByStatus(SchedulerStatus.NEW);
		if (story.isPresent() && !storyRepository.findTopByStatus(SchedulerStatus.VALIDATION).isPresent()) {
			List<StoryRule> rules = storyRuleRepository.findAll();
			Runnable task = () -> {
				try {
					validationByRuleService.validate(story.get(), rules);
				} catch (NoSuchMethodException | SecurityException e) {
					LOG.error(e.getMessage());
				}
			};
			Thread thread = new Thread(task);
			thread.start();
		}
	}
}
