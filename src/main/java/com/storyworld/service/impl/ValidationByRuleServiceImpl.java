package com.storyworld.service.impl;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Service;

import com.storyworld.domain.elastic.StoryContent;
import com.storyworld.domain.sql.Story;
import com.storyworld.domain.sql.StoryRule;
import com.storyworld.domain.sql.enums.SchedulerStatus;
import com.storyworld.repository.elastic.StoryContentRepository;
import com.storyworld.repository.sql.StoryRepository;
import com.storyworld.service.ValidationByRuleService;

@Service
public class ValidationByRuleServiceImpl implements ValidationByRuleService {

	@Autowired
	private StoryRepository storyRepository;

	@Autowired
	private StoryContentRepository storyContentRepository;

	private static final Logger LOG = LoggerFactory.getLogger(ValidationByRuleServiceImpl.class);

	@Override
	public void validate(Story story, List<StoryRule> rules) throws NoSuchMethodException, SecurityException {
		Optional<StoryContent> storyContentOpt = storyContentRepository.findById(story.getContentId());
		story.setStatus(SchedulerStatus.VALIDATION);
		storyRepository.save(story);
		ExpressionParser parser = new SpelExpressionParser();
		if (storyContentOpt.isPresent()) {
			StoryContent storyContent = storyContentOpt.get();
			StandardEvaluationContext inventorContext = new StandardEvaluationContext(storyContent);
			inventorContext.registerFunction("cenzoreWords", ValidationByRuleServiceImpl.class
					.getDeclaredMethod("cenzoreWords", new Class[] { List.class, String[].class }));
			try {
				rules.stream().map(StoryRule::getScript).forEach(
						script -> System.out.println(parser.parseExpression(script).getValue(inventorContext)));
				if (!storyContent.getTitle().equals(story.getName()))
					story.setName(storyContent.getTitle());
				System.out.println(storyContent.getPages().get(0));
				story.setStatus(SchedulerStatus.PUBLISHED);
				storyContentRepository.save(storyContent);
			} catch (Exception e) {
				LOG.error(e.getMessage());
				story.setStatus(SchedulerStatus.ERROR);
			} finally {
				storyRepository.save(story);
			}
		}
	}

	private static void cenzoreWords(List<String> pages, String[] words) {
		// pages.forEach(System.out::println);
		// System.out.println(words[0]);
	}

}