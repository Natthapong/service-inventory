package th.co.truemoney.serviceinventory.legacyfacade.builders;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;

import org.apache.commons.lang.Validate;
import org.springframework.beans.factory.annotation.Autowired;

import th.co.truemoney.serviceinventory.engine.client.domain.services.ConfirmTopUpAirtimeRequest;
import th.co.truemoney.serviceinventory.engine.client.domain.services.VerifyTopUpAirtimeRequest;
import th.co.truemoney.serviceinventory.ewallet.domain.AccessToken;
import th.co.truemoney.serviceinventory.legacyfacade.ewallet.TopUpMobileFacade;
import th.co.truemoney.serviceinventory.topup.domain.TopUpMobile;
import th.co.truemoney.serviceinventory.topup.domain.TopUpMobileConfirmationInfo;

public class TopUpMobileBuilder {
	
	private TopUpMobileFacade topUpMobileFacade;
	private String targetMobileNumber;
	private BigDecimal amount;
	private AccessToken accessToken;
	private String transactionID;
	private BigDecimal serviceFee;
	private BigDecimal sourceOfFundFee;
	
	@Autowired(required = false)
	public TopUpMobileBuilder(TopUpMobileFacade topUpMobileFacade) {
		this.topUpMobileFacade = topUpMobileFacade;
	}

	public TopUpMobile verifyTopUpAirtime(String targetMobileNumber, BigDecimal amount, AccessToken accessToken) {
		this.targetMobileNumber = targetMobileNumber;
		this.amount = amount;
		this.accessToken = accessToken;
		return verifyPayment();
	}
	
	public TopUpMobileConfirmationInfo topUpAirtime(String targetMobileNumber, BigDecimal amount, BigDecimal serviceFee, BigDecimal sourceOfFundFee, String transactionID, AccessToken accessToken) {
		this.targetMobileNumber = targetMobileNumber;
		this.amount = amount;
		this.accessToken = accessToken;		
		this.transactionID = transactionID;
		this.serviceFee = serviceFee;
		this.sourceOfFundFee = sourceOfFundFee;
		return confirmPayment();
	}

	private TopUpMobileConfirmationInfo confirmPayment() {
		Validate.notNull(transactionID, "data missing. confirm topUp by transactionID?");
		Validate.notNull(targetMobileNumber, "data missing. confirm topUp by mobile number?");
		Validate.notNull(amount, "data missing. how much to topUp?");
		
		ConfirmTopUpAirtimeRequest confirmRequest = new ConfirmTopUpAirtimeRequest();
		
		confirmRequest.setAppUser(accessToken.getClientCredential().getAppUser());
		confirmRequest.setAppPassword(accessToken.getClientCredential().getAppPassword());
		confirmRequest.setAppKey(accessToken.getClientCredential().getAppKey());

		confirmRequest.setChannel(accessToken.getClientCredential().getChannel());
		confirmRequest.setChannelDetail(accessToken.getClientCredential().getChannelDetail());

		confirmRequest.setCommandAction("EW");
		confirmRequest.setTmnID(accessToken.getTruemoneyID());
		confirmRequest.setSession(accessToken.getSessionID());
		confirmRequest.setControlFlag("01");

		confirmRequest.setRef1(targetMobileNumber);
		confirmRequest.setOperator("true");

		confirmRequest.setTransRef(transactionID);
		confirmRequest.setMsisdn(targetMobileNumber);
		confirmRequest.setAmount(convertMoney(amount));
		
		confirmRequest.setSource("EW");
		confirmRequest.setSourceFeeType("THB");
		confirmRequest.setTotalSourceFee(convertMoney(sourceOfFundFee));
		confirmRequest.setServiceFeeType("THB");
		confirmRequest.setTotalServiceFee(convertMoney(serviceFee));
		
		return topUpMobileFacade.topUpMobile(confirmRequest);
	}

	public TopUpMobile verifyPayment() {
		Validate.notNull(targetMobileNumber, "data missing. verify topUp by mobile number?");
		Validate.notNull(amount, "data missing. how much to topUp?");

		VerifyTopUpAirtimeRequest verifyRequest = new VerifyTopUpAirtimeRequest();

		verifyRequest.setAppUser(accessToken.getClientCredential().getAppUser());
		verifyRequest.setAppPassword(accessToken.getClientCredential().getAppPassword());
		verifyRequest.setAppKey(accessToken.getClientCredential().getAppKey());

		verifyRequest.setChannel(accessToken.getClientCredential().getChannel());
		verifyRequest.setChannelDetail(accessToken.getClientCredential().getChannelDetail());

		verifyRequest.setCommandAction("EW");
		verifyRequest.setTmnID(accessToken.getTruemoneyID());
		verifyRequest.setSession(accessToken.getSessionID());
		verifyRequest.setControlFlag("01");

		verifyRequest.setRef1(targetMobileNumber);
		verifyRequest.setOperator("true");

		verifyRequest.setMsisdn(targetMobileNumber);
		verifyRequest.setAmount(convertMoney(amount));
		
		return topUpMobileFacade.verifyTopUpMobile(verifyRequest);
	}
	
	private String convertMoney(BigDecimal value) {
		BigDecimal scaled = value.setScale(2, RoundingMode.HALF_UP);
		String formatedString = new DecimalFormat("#0.00").format(scaled);
		int lastIndex = formatedString.lastIndexOf('.');
		return formatedString.substring(0, lastIndex) + formatedString.substring(lastIndex + 1);
	}
	
}
