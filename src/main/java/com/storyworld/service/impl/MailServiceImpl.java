package com.storyworld.service.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import javax.mail.internet.MimeMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.stereotype.Service;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;

import com.storyworld.domain.sql.Mail;
import com.storyworld.domain.sql.MailToken;
import com.storyworld.domain.sql.enums.Status;
import com.storyworld.repository.sql.MailReposiotory;
import com.storyworld.repository.sql.MailTokenRepository;
import com.storyworld.service.MailService;

import freemarker.template.Configuration;

@Service
public class MailServiceImpl implements MailService {

	@Autowired
	private JavaMailSender mailSender;

	@Autowired
	private MailReposiotory mailReposiotory;

	@Autowired
	private Configuration freemarkerConfiguration;

	@Autowired
	private MailTokenRepository mailTokenRepository;

	private static final String FROM = "storyworld@gamil.com";

	private static final Logger LOG = LoggerFactory.getLogger(MailServiceImpl.class);

	@Override
	public void send(Mail mail) {
		Optional<MailToken> mailToken = mailTokenRepository.findByUserAndTypeToken(mail.getUser(), mail.getTemplate());
		MimeMessagePreparator preparator = new MimeMessagePreparator() {
			public void prepare(MimeMessage mimeMessage) throws Exception {
				MimeMessageHelper message = new MimeMessageHelper(mimeMessage);
				message.setTo(mail.getUser().getMail());
				message.setFrom(FROM);
				message.setSubject(mail.getTemplate().toString());
				Map<String, Object> model = new HashMap<>();
				model.put("link",
						new StringBuffer("http://localhost:3000/token/").append(mailToken.get().getTypeToken())
								.append("/").append(mailToken.get().getUser().getId()).append("/")
								.append(mailToken.get().getToken()).toString());
				String text = geFreeMarkerTemplateContent(model, mail.getTemplate().toString());
				message.setText(text, true);
			}
		};
		try {
			mailSender.send(preparator);
			mail.setStatus(Status.FINISHED);
		} catch (Exception e) {
			LOG.error(e.getMessage());
			mail.setStatus(Status.ERROR);
		} finally {
			mailReposiotory.save(mail);
		}
	}

	private String geFreeMarkerTemplateContent(Map<String, Object> model, String template) {
		StringBuilder content = new StringBuilder();
		try {
			content.append(FreeMarkerTemplateUtils
					.processTemplateIntoString(freemarkerConfiguration.getTemplate(template), model));
			return content.toString();
		} catch (Exception e) {
			LOG.error("Exception occured while processing fmtemplate:" + e.getMessage());
		}
		return "";
	}

}
