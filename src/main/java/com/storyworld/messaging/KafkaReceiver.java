package com.storyworld.messaging;

import java.util.concurrent.CountDownLatch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class KafkaReceiver {

	private CountDownLatch latch = new CountDownLatch(1);

	private static final Logger LOG = LoggerFactory.getLogger(KafkaReceiver.class);

	public CountDownLatch getLatch() {
		return latch;
	}

//	@KafkaListener(topics = "anik")
	public void receive(String something) {
		LOG.error(something);
		latch.countDown();
	}

}
