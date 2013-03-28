package th.co.truemoney.serviceinventory.email;

import java.io.IOException;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import freemarker.template.TemplateException;

public class StubEmailService extends EmailService {
	private static final Logger logger = LoggerFactory.getLogger(StubEmailService.class);

	@Override
	public void sendWelcomeEmail(String receiverEmail, Map<String, String> map)
			throws IOException, TemplateException {
		logger.debug("StubEmailService.sendWelcomeEmail : "+receiverEmail);
		sendEmail(receiverEmail, "subject", "emailTemplate", map);
	}

	@Override
	public void sendEmail(String receiverEmail, String subject,
			String emailTemplate, Map<String, String> map) throws IOException,
			TemplateException {
		logger.debug("StubEmailService.sendEmail : "+receiverEmail);
	}
	
	
	
}
