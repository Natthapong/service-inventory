package th.co.truemoney.serviceinventory.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.ui.freemarker.FreeMarkerConfigurationFactory;

import th.co.truemoney.serviceinventory.config.EmailConfig;
import th.co.truemoney.serviceinventory.email.EmailService;
import freemarker.core.Environment;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { EmailConfig.class })
public class EmailServiceTest {
	
	@Autowired @Qualifier("emailSender")
	private String emailSender;
	
	@Autowired @Qualifier("emailEncoding")
	private String emailEncoding;
	
	@Autowired @Qualifier("welcomeTemplate")
	private String welcomeTemplate;
	
	@Autowired @Qualifier("welcomeSubject")
	private String welcomeSubject;
	
	FreeMarkerConfigurationFactory freeMarkerConfigurationFactory;
	
	@Autowired
	FreeMarkerConfigurationFactory freeMarker;
	
	private EmailService emailService;
	private JavaMailSenderImpl javaMailSender;
	private MimeMessage mimeMessage;
	private Configuration configuration;
	private Template template;

	@Before
	public void setup() throws IOException, TemplateException {
		emailService = new EmailService();
		
		freeMarkerConfigurationFactory = mock(FreeMarkerConfigurationFactory.class);
		javaMailSender = mock(JavaMailSenderImpl.class);
		mimeMessage = mock(MimeMessage.class);
		configuration = mock(Configuration.class);
		template = mock(Template.class);
		
		when(javaMailSender.createMimeMessage()).thenReturn(mimeMessage);
		when(configuration.getTemplate(anyString())).thenReturn(template);
		when(freeMarkerConfigurationFactory.createConfiguration()).thenReturn(configuration);
		
		emailService.setJavaMailSender(javaMailSender);
		emailService.setFreeMarkerConfigurationFactory(freeMarkerConfigurationFactory);
		emailService.setWelcomeSubject(welcomeSubject);
		emailService.setEmailSender("smart@tmn.com");		
	}
	
	@After
	public void teardown() {
		reset(freeMarkerConfigurationFactory);
		reset(javaMailSender);
		reset(mimeMessage);
		reset(configuration);
		reset(template);
	}
	
	@Test
	public void emailConfig() throws IOException, TemplateException {
		assertEquals("utf-8", emailEncoding);
		assertEquals("ยินดีต้อนรับสู่บริการ True Money Smart Wallet", welcomeSubject);
		
		Configuration configuration = freeMarker.createConfiguration();
		assertNotNull(configuration.getTemplate("welcome-email.ftl"));		
	}
	
	@Test
	public void sendEmail() throws IOException, TemplateException, Exception {	
		emailService.sendEmail("mart@tmn.com", welcomeSubject, welcomeTemplate, null);
		verify(javaMailSender).send(any(MimeMessage.class));
	}
	
	@Test
	public void sendWelcomeEmail() throws IOException, TemplateException, Exception {				
		emailService.sendWelcomeEmail("mart@tmn.com", null);
		verify(javaMailSender).send(any(MimeMessage.class));
	}
	
	@Test
	public void sendWelcomeEmailFailMessagingException() {	
		try {
			doThrow(new MessagingException("MessagingException")).when(javaMailSender).send(any(MimeMessage.class));
			emailService.sendWelcomeEmail("mart@tmn.com", null);
			verify(javaMailSender).send(any(MimeMessage.class));
		} catch (Exception e) {
			assertNotNull(e);
		}
	}
	
	@Test
	public void sendWelcomeEmailFailIOException() throws TemplateException, Exception {	
		try {
			when(configuration.getTemplate(anyString())).thenThrow(new IOException("IOException"));
			emailService.sendWelcomeEmail("mart@tmn.com", null);
			verify(configuration).getTemplate(anyString());
		} catch (IOException e) {
			assertNotNull(e);			
		}
	}
	
	@Test
	public void sendWelcomeEmailFailTemplateException() throws TemplateException, Exception {	
		try {
			when(freeMarkerConfigurationFactory.createConfiguration()).thenThrow(new TemplateException("fail", Environment.getCurrentEnvironment()));
			emailService.sendWelcomeEmail("mart@tmn.com", null);
			verify(freeMarkerConfigurationFactory).createConfiguration();
		} catch (TemplateException e) {
			assertNotNull(e);			
		}
	}
	
}
