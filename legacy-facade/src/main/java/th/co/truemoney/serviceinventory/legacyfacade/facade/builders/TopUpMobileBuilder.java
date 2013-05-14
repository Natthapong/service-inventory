package th.co.truemoney.serviceinventory.legacyfacade.facade.builders;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;

import org.apache.commons.lang.Validate;
import org.springframework.beans.factory.annotation.Autowired;

import th.co.truemoney.serviceinventory.engine.client.domain.services.ConfirmTopUpAirtimeRequest;
import th.co.truemoney.serviceinventory.engine.client.domain.services.VerifyTopUpAirtimeRequest;
import th.co.truemoney.serviceinventory.legacyfacade.handlers.MobileTopUpHandler;
import th.co.truemoney.serviceinventory.topup.domain.TopUpMobile;
import th.co.truemoney.serviceinventory.topup.domain.TopUpMobileConfirmationInfo;

public class TopUpMobileBuilder {
	
	private MobileTopUpHandler topUpMobileFacade;
	
	private String channel;
	private String channelDetail;
	
	private String appUser;
	private String appPassword;
	private String appKey;
	
	private String sessionID;
	private String tmnID;
	private String commandAction;
	
	private String targetMobileNumber;
	private String ref1;
	private BigDecimal amount;
	private BigDecimal serviceFee;
	private BigDecimal sourceOfFundFee;
	private String sourceOfFundSourceType;

	@Autowired(required = false)
	public TopUpMobileBuilder(MobileTopUpHandler topUpMobileFacade) {
		this.topUpMobileFacade = topUpMobileFacade;
	}
	
	public TopUpMobileBuilder fromTopUpChannel(String channel, String channelDetail) {
		this.channel = channel;
		this.channelDetail = channelDetail;
		return this;
	}

	public TopUpMobileBuilder fromApp(String appUser, String appPassword, String appKey) {
		this.appUser = appUser;
		this.appPassword = appPassword;
		this.appKey = appKey;
		return this;
	}
	
	public TopUpMobileBuilder fromUser(String sessionID, String tmnID) {
		this.sessionID = sessionID;
		this.tmnID = tmnID;
		return this;
	}
	
	public TopUpMobileBuilder toMobileNumber(String targetMobileNumber) {
		this.targetMobileNumber = targetMobileNumber;
		this.ref1 = targetMobileNumber;
		return this;
	}
	
	public TopUpMobileBuilder usingSourceOfFund(String sourceOfFundSourceType) {
		this.commandAction = sourceOfFundSourceType;
		this.sourceOfFundSourceType = sourceOfFundSourceType;
		return this;
	}

	public TopUpMobileBuilder withAmount(BigDecimal amount) {
		this.amount = amount;
		return this;
	}
	
	public TopUpMobileBuilder andFee(BigDecimal serviceFee, BigDecimal sourceOfFundFee) {
		this.serviceFee = serviceFee;
		this.sourceOfFundFee = sourceOfFundFee;
		return this;
	}
	
	public TopUpMobile verifyTopUpAirtime() {
		Validate.notNull(targetMobileNumber, "data missing. verify topUp by mobile number?");
		Validate.notNull(amount, "data missing. how much to topUp?");
		Validate.notNull(ref1, "data missing. ref1 missing?");

		Validate.notNull(tmnID, "data missing. missing ewallet source of fund user detail?");
		Validate.notNull(sessionID, "data missing. missing ewallet source of fund user detail?");

		Validate.notNull(appUser, "data missing.verify topping from which source?");
		Validate.notNull(appPassword, "data missing. verify topping from which source?");
		Validate.notNull(appKey, "data missing. verify topping from which source?");
		Validate.notNull(channel, "data missing. verify topping from which channel?");
		Validate.notNull(channelDetail, "data missing. verify topping from which channel detail.");
		Validate.notNull(commandAction, "data missing. verify topping from which command action.");
		
		VerifyTopUpAirtimeRequest verifyRequest = new VerifyTopUpAirtimeRequest();

		verifyRequest.setAppUser(appUser);
		verifyRequest.setAppPassword(appPassword);
		verifyRequest.setAppKey(appKey);
		
		verifyRequest.setChannel(channel);
		verifyRequest.setChannelDetail(channelDetail);
		verifyRequest.setCommandAction(commandAction);
		
		verifyRequest.setTmnID(tmnID);
		verifyRequest.setSession(sessionID);

		verifyRequest.setMsisdn(targetMobileNumber);
		verifyRequest.setRef1(ref1);
		verifyRequest.setAmount(convertMoney(amount));
		
		verifyRequest.setControlFlag("01");
		verifyRequest.setOperator("true");
		
		return topUpMobileFacade.verifyTopUpMobile(verifyRequest);
	}
	
	public TopUpMobileConfirmationInfo topUpAirtime(String transactionID, String target) {		
		Validate.notNull(transactionID, "data missing. confirm topUp by transactionID?");
		Validate.notNull(target, "data missing. confirm topUp by target?");
		Validate.notNull(targetMobileNumber, "data missing. verify topUp by mobile number?");
		Validate.notNull(amount, "data missing. how much to topUp?");
		Validate.notNull(ref1, "data missing. ref1 missing?");

		Validate.notNull(tmnID, "data missing. missing ewallet source of fund user detail?");
		Validate.notNull(sessionID, "data missing. missing ewallet source of fund user detail?");

		Validate.notNull(appUser, "data missing.verify topping from which source?");
		Validate.notNull(appPassword, "data missing. verify topping from which source?");
		Validate.notNull(appKey, "data missing. verify topping from which source?");
		Validate.notNull(channel, "data missing. verify topping from which channel?");
		Validate.notNull(channelDetail, "data missing. verify topping from which channel detail.");
		Validate.notNull(commandAction, "data missing. verify topping from which command action.");
		
		Validate.notNull(serviceFee, "data missing. missing service fee value");
		Validate.notNull(sourceOfFundFee, "data missing. missing source of fund fee value");
		Validate.notNull(sourceOfFundSourceType, "data missing. missing source of fund value");
		
		ConfirmTopUpAirtimeRequest confirmRequest = new ConfirmTopUpAirtimeRequest();
		
		confirmRequest.setAppUser(appUser);
		confirmRequest.setAppPassword(appPassword);
		confirmRequest.setAppKey(appKey);
		
		confirmRequest.setChannel(channel);
		confirmRequest.setChannelDetail(channelDetail);
		confirmRequest.setCommandAction(commandAction);

		confirmRequest.setTmnID(tmnID);
		confirmRequest.setSession(sessionID);

		confirmRequest.setMsisdn(targetMobileNumber);
		confirmRequest.setRef1(ref1);
		confirmRequest.setAmount(convertMoney(amount));
		
		confirmRequest.setSource(sourceOfFundSourceType);
		confirmRequest.setSourceFeeType("THB");
		confirmRequest.setTotalSourceFee(convertMoney(sourceOfFundFee));
		confirmRequest.setServiceFeeType("THB");
		confirmRequest.setTotalServiceFee(convertMoney(serviceFee));
		
		confirmRequest.setTransRef(transactionID);
		confirmRequest.setTarget(target);
		
		confirmRequest.setControlFlag("01");
		confirmRequest.setOperator("true");
		
		return topUpMobileFacade.topUpMobile(confirmRequest);
	}

	
	
	private String convertMoney(BigDecimal value) {
		BigDecimal scaled = value.setScale(2, RoundingMode.HALF_UP);
		String formatedString = new DecimalFormat("#0.00").format(scaled);
		int lastIndex = formatedString.lastIndexOf('.');
		return formatedString.substring(0, lastIndex) + formatedString.substring(lastIndex + 1);
	}
	
}
