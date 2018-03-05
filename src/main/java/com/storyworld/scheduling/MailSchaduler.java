package com.storyworld.scheduling;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.storyworld.domain.sql.enums.Status;
import com.storyworld.repository.sql.MailReposiotory;
import com.storyworld.service.MailService;

@Component
public class MailSchaduler {

	@Autowired
	private MailReposiotory mailRepository;

	@Autowired
	private MailService mailService;

	@Scheduled(fixedRate = 10000)
	public void sendMail() {
		mailRepository.findByStatus(Status.READY).ifPresent(x -> x.forEach(mailService::send));
	}

}
