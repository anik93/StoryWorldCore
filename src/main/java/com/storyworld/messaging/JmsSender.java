package com.storyworld.messaging;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.storyworld.domain.sql.User;

@Component
public class JmsSender {

	@Autowired
	private JmsTemplate jmsTemplate;

//	@Scheduled(fixedRate = 10000)
	public void send() {
		jmsTemplate.convertAndSend("test.QueueUser", new User(1, "Test"));
	}

}
