package th.co.truemoney.serviceinventory.email;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.Future;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.ui.freemarker.FreeMarkerConfigurationFactory;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

public class EmailService {
	private static final Logger logger = LoggerFactory.getLogger(EmailService.class);
	
	@Autowired
	private JavaMailSender javaMailSender;
	
	@Autowired @Qualifier("emailSender")
	private String emailSender;
	
	@Autowired @Qualifier("emailEncoding")
	private String emailEncoding;
	
	@Autowired @Qualifier("welcomeTemplate")
	private String welcomeTemplate;
	
	@Autowired @Qualifier("welcomeSubject")
	private String welcomeSubject;
	
	@Autowired
	FreeMarkerConfigurationFactory freeMarkerConfigurationFactory;	
	
	@Async
	public void sendWelcomeEmail(String receiverEmail, Map<String, String> map) {
		sendEmail(receiverEmail, welcomeSubject, welcomeTemplate, map);
	}
		
	public Future<MimeMessage> sendEmail(String receiverEmail, String subject, String emailTemplate, Map<String, String> map) {
		MimeMessage mimeMessage = javaMailSender.createMimeMessage();
		MimeMessageHelper helper;
		try {
			helper = new MimeMessageHelper(mimeMessage, false, emailEncoding);			
			
			helper.setTo(receiverEmail);
			helper.setSubject(subject);
			helper.setFrom(emailSender);
			Configuration configuration = freeMarkerConfigurationFactory.createConfiguration();
			Template template = configuration.getTemplate(emailTemplate, emailEncoding);
			String message = FreeMarkerTemplateUtils.processTemplateIntoString(template, map);
			helper.setText(message, true);
			javaMailSender.send(helper.getMimeMessage());
		} catch (MessagingException e) {
			logger.error(e.getMessage());			
			logger.error("EmailService.sendWelcomeEmail.receiver.email : "+receiverEmail);
		} catch (IOException e) {
			logger.error(e.getMessage());			
			logger.error("EmailService.sendWelcomeEmail.receiver.email : "+receiverEmail);
		} catch (TemplateException e) {
			logger.error(e.getMessage());			
			logger.error("EmailService.sendWelcomeEmail.receiver.email : "+receiverEmail);
		} catch (Exception e) {
			logger.error(e.getMessage());			
			logger.error("EmailService.sendWelcomeEmail.receiver.email : "+receiverEmail);
		}	
		return new AsyncResult<MimeMessage> (mimeMessage);
	}
	
	public void setJavaMailSender(JavaMailSender javaMailSender) {
		this.javaMailSender = javaMailSender;
	}

	public void setEmailSender(String emailSender) {
		this.emailSender = emailSender;
	}

	public void setWelcomeSubject(String welcomeSubject) {
		this.welcomeSubject = welcomeSubject;
	}
	
	public void setEmailEncoding(String emailEncoding) {
		this.emailEncoding = emailEncoding;
	}

	public void setWelcomeTemplate(String welcomeTemplate) {
		this.welcomeTemplate = welcomeTemplate;
	}

	public void setFreeMarkerConfigurationFactory(
			FreeMarkerConfigurationFactory freeMarkerConfigurationFactory) {
		this.freeMarkerConfigurationFactory = freeMarkerConfigurationFactory;
	}
	
}
