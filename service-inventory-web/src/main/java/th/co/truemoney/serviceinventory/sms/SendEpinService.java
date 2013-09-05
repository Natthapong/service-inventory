package th.co.truemoney.serviceinventory.sms;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import th.co.truemoney.serviceinventory.buy.domain.SendEpinSms;
import th.co.truemoney.serviceinventory.exception.ServiceInventoryWebException;
import th.co.truemoney.serviceinventory.exception.ServiceInventoryWebException.Code;
import th.co.truemoney.serviceinventory.firsthop.message.SmsRequest;
import th.co.truemoney.serviceinventory.firsthop.message.SmsResponse;
import th.co.truemoney.serviceinventory.firsthop.proxy.SmsProxy;
import th.co.truemoney.serviceinventory.util.SecurityManager;

public class SendEpinService {

	private static final String SMS_EPIN_TEMPLATE = "คุณได้รับบัตรเงินสด %s บ. "+
													"รหัสเติมเงิน คือ %s "+
													"จากหมายเลข %s "+ 
													"(transaction %s, "+
													"serial %s)";
	
	@Autowired 
	@Qualifier("smsSender")
	private String smsSender;

	@Autowired
	private SmsProxy smsProxyImpl;
	
	@Autowired
	private SecurityManager securityManager;
	
	public void send(SendEpinSms buyEpinSms) throws ServiceInventoryWebException {
		
		boolean result = sendSMS(buyEpinSms);
		
		if (!result) {
			throw new ServiceInventoryWebException(Code.SEND_EPIN_FAIL, "send EPIN failed.");
		}
	}
	
	private boolean sendSMS(SendEpinSms buyEpinSms) {
		String msg = String.format(SMS_EPIN_TEMPLATE, 
				convertToAbsoluteValue(buyEpinSms.getAmount()), 
				securityManager.decryptRSA(buyEpinSms.getPin()),
				buyEpinSms.getAccount(),
				buyEpinSms.getTxnID(),
				buyEpinSms.getSerial());
		SmsResponse smsResponse = smsProxyImpl.send(new SmsRequest(smsSender, buyEpinSms.getRecipientMobileNumber(), msg));
		return smsResponse.isSuccess();
	}
	
	private String convertToAbsoluteValue(String amount) {
		try {
			return new BigDecimal(amount).setScale(0).abs().toString();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

}
