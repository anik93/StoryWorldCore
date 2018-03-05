package com.storyworld.messaging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import com.storyworld.domain.sql.User;

@Component
public class JmsQueueReceiver {

	private static final Logger LOG = LoggerFactory.getLogger(JmsQueueReceiver.class);

	@JmsListener(destination = "test.QueueUser")
	public void receive(User user) {
		LOG.error(user.toString());
	}

}
