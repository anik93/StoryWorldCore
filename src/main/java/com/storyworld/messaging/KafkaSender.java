package com.storyworld.messaging;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class KafkaSender {

	@Autowired
	private KafkaTemplate<String, String> kafkaTemplate;

	private String kafkaTopic = "anik";

//	@Scheduled(fixedRate = 10000)
	public void send() {
		kafkaTemplate.send(kafkaTopic, "KAFKA");
	}

}
