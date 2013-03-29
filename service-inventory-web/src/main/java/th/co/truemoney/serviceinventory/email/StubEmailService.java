package th.co.truemoney.serviceinventory.email;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StubEmailService extends EmailService {
	private static final Logger logger = LoggerFactory.getLogger(StubEmailService.class);

	@Override
	public void sendWelcomeEmail(String receiverEmail, Map<String, String> map) {
		logger.debug("StubEmailService.sendWelcomeEmail : "+receiverEmail);
		sendEmail(receiverEmail, "subject", "emailTemplate", map);
	}

	@Override
	public void sendEmail(String receiverEmail, String subject,
			String emailTemplate, Map<String, String> map) {
		logger.debug("StubEmailService.sendEmail : "+receiverEmail);
	}
	
	
	
}
