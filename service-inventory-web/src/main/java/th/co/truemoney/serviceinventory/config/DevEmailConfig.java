package th.co.truemoney.serviceinventory.config;

import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;


@Configurable
@Profile("dev")
public class DevEmailConfig {

	@Value("${email.host}")
	private String emailHost;
	
	@Value("${email.port}")
	private Integer emailPort;
	
	@Value("${email.protocol}")
	private String emailProtocol;
	
	@Value("${email.username}")
	private String emailUsername;
	
	@Value("${email.password}")
	private String emailPassword;
		
	@Value("${email.encoding}")
	private String emailEncoding;
	
	@Value("${email.sender}")
	private String emailSender;
	
	@Value("${email.welcome.subject}")
	private String welcomeSubject;
	
	@Value("${email.welcome.template}")
	private String welcomeTemplate;

	@Bean @Qualifier("emailEncoding")
	public String getEmailEncoding() {
		return emailEncoding;
	}
	
	@Bean @Qualifier("emailSender")
	public String getEmailSender() {
		return emailSender;
	}

	@Bean @Qualifier("welcomeSubject")
	public String getWelcomeSubject() {
		return welcomeSubject;
	}

	@Bean @Qualifier("welcomeTemplate")
	public String getWelcomeTemplate() {
		return welcomeTemplate;
	}
	
	@Bean
	public JavaMailSender javaMailSender() {
		JavaMailSenderImpl javaMailSender = new JavaMailSenderImpl();
		javaMailSender.setHost(emailHost);
		javaMailSender.setPort(emailPort);
		javaMailSender.setProtocol(emailProtocol);
		javaMailSender.setUsername(emailUsername);
		javaMailSender.setPassword(emailPassword);
		javaMailSender.setDefaultEncoding(emailEncoding);
		return javaMailSender;
	}	
	
	
	@Bean
	public static PropertyPlaceholderConfigurer emailProperties(){
	  PropertyPlaceholderConfigurer ppc = new PropertyPlaceholderConfigurer();
	  Resource[] resources = new ClassPathResource[ ]
	    { new ClassPathResource( "email.properties" ) };
	  ppc.setLocations( resources );
	  ppc.setIgnoreUnresolvablePlaceholders( true );
	  return ppc;
	}
	
}
