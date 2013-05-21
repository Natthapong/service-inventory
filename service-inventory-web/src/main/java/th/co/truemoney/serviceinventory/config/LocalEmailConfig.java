package th.co.truemoney.serviceinventory.config;

import java.io.InputStream;
import java.util.Properties;

import javax.mail.Session;
import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessagePreparator;

@Configurable
@Profile("local")
public class LocalEmailConfig {

    @Bean
    public JavaMailSender stubJavaMailSender() {
        return new JavaMailSender() {

            @Override
            public void send(SimpleMailMessage simpleMessage)
                    throws MailException {
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void send(SimpleMailMessage[] simpleMessages)
                    throws MailException {
                // TODO Auto-generated method stub
            }

            @Override
            public MimeMessage createMimeMessage() {
                return new MimeMessage(Session.getInstance(new Properties()));
            }

            @Override
            public MimeMessage createMimeMessage(InputStream contentStream)
                    throws MailException {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public void send(MimeMessage mimeMessage) throws MailException {
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void send(MimeMessage[] mimeMessages) throws MailException {
            }

            @Override
            public void send(MimeMessagePreparator mimeMessagePreparator)
                    throws MailException {
                // TODO Auto-generated method stub
            }

            @Override
            public void send(MimeMessagePreparator[] mimeMessagePreparators)
                    throws MailException {
                // TODO Auto-generated method stub
            }
        };
    }


    @Bean @Qualifier("emailEncoding")
    public String getEmailEncoding() {
        return "utf-8";
    }

    @Bean @Qualifier("emailSender")
    public String getEmailSender() {
        return "emailSender";
    }

    @Bean @Qualifier("welcomeSubject")
    public String getWelcomeSubject() {
        return "welcomeSubject";
    }

    @Bean @Qualifier("welcomeTemplate")
    public String getWelcomeTemplate() {
        return "welcome-email.ftl";
    }
	
	
}
