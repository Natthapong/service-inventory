package th.co.truemoney.serviceinventory.legacyfacade.facade.builders;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;

import org.apache.commons.lang.Validate;
import org.springframework.beans.factory.annotation.Autowired;

import th.co.truemoney.serviceinventory.bill.domain.BillPaymentConfirmationInfo;
import th.co.truemoney.serviceinventory.engine.client.domain.services.ConfirmBillPayRequest;
import th.co.truemoney.serviceinventory.engine.client.domain.services.VerifyBillPayRequest;
import th.co.truemoney.serviceinventory.legacyfacade.facade.BillPaymentFacade;

public class BillPaymentBuilder {

	private static final String MOBILE_PAYPOINT_CODE = "mobile";

	private String channel;
	private String channelDetail;
	private String appUser;
	private String appPassword;
	private String appKey;
	private String sessionID;
	private String tmnID;
	private String msisdn;
	private String ref1;
	private String ref2;
	private String transType;
	private String target;
	private String payPointCode;
	private String payPointName;
	private BigDecimal amount;
	private String commandAction;

	private BillPaymentFacade billPaymentFacade;

	private BigDecimal serviceFee;

	private BigDecimal sourceOfFundFee;

	private String sourceOfFundSourceType;


	@Autowired(required = false)
	public BillPaymentBuilder(BillPaymentFacade billPaymentFacade) {
		this.billPaymentFacade = billPaymentFacade;
	}

	public BillPaymentBuilder fromBillChannel(String channel, String channelDetail) {
		this.channel = channel;
		this.channelDetail = channelDetail;
		return this;
	}

	public BillPaymentBuilder fromApp(String appUser, String appPassword, String appKey) {
		this.appUser = appUser;
		this.appPassword = appPassword;
		this.appKey = appKey;

		return this;
	}

	public BillPaymentBuilder aUser(String sessionID, String tmnID) {
		this.sessionID = sessionID;
		this.tmnID = tmnID;
		this.commandAction = "EW";
		this.sourceOfFundSourceType = "EW";
		return this;
	}

	public BillPaymentBuilder usingMobilePayPoint(String msisdn) {
		this.msisdn = msisdn;
		this.payPointCode = MOBILE_PAYPOINT_CODE;
		this.payPointName = msisdn;
		return this;
	}

	public BillPaymentBuilder forBillBarcode(String ref1, String ref2, String agentTarget) {
		this.ref1 = ref1;
		this.ref2 = ref2;
		this.target = agentTarget;
		this.transType = "01";
		return this;
	}

	public BillPaymentBuilder paying(BigDecimal amount, BigDecimal serviceFee, BigDecimal sourceOfFundFee) {
		this.amount = amount;
		this.serviceFee = serviceFee;
		this.sourceOfFundFee = sourceOfFundFee;
		return this;
	}

	public String verifyPayment() {
		Validate.notNull(ref1, "barcode ref1 missing?");

		Validate.notNull(target, "data missing. verify paying to which target agent?");
		Validate.notNull(msisdn, "data missing. verify paying from which mobile number?");
		Validate.notNull(amount, "data missing. how much to pay for this bill?");

		Validate.notNull(serviceFee, "data missing. missing service fee value");

		Validate.notNull(sourceOfFundFee, "data missing. missing source of fund fee value");
		Validate.notNull(tmnID, "data missing. missing ewallet source of fund user detail?");
		Validate.notNull(sessionID, "data missing. missing ewallet source of fund user detail?");

		Validate.notNull(appUser, "data missing.verify paying from which source?");
		Validate.notNull(appPassword, "data missing. verify paying from which source?");
		Validate.notNull(appKey, "data missing. verify paying from which source?");
		Validate.notNull(channel, "data missing. verify paying from which channel?");
		Validate.notNull(channelDetail, "data missing. verify paying from which channel detail.");
		Validate.notNull(payPointCode, "data missing. verify paying from which paypoint?");
		Validate.notNull(payPointName, "data missing. verify paying from which paypoint detail.");


		VerifyBillPayRequest verifyRequest = new VerifyBillPayRequest();

		verifyRequest.setAppUser(appUser);
		verifyRequest.setAppPassword(appPassword);
		verifyRequest.setAppKey(appKey);

		verifyRequest.setChannel(channel);
		verifyRequest.setChannelDetail(channelDetail);

		verifyRequest.setCommandAction(commandAction);
		verifyRequest.setSession(sessionID);
		verifyRequest.setTmnID(tmnID);

		verifyRequest.setRef1(ref1);
		verifyRequest.setRef2(ref2);
		verifyRequest.setTarget(target);
		verifyRequest.setTransType(transType);

		verifyRequest.setMsisdn(msisdn);
		verifyRequest.setPaypointName(payPointName);
		verifyRequest.setPaypointCode(payPointCode);

		verifyRequest.setAmount(convertMoney(amount));
		verifyRequest.setSource("EW");
		verifyRequest.setSourceFeeType("THB");
		verifyRequest.setTotalSourceFee(convertMoney(sourceOfFundFee));
		verifyRequest.setServiceFeeType("THB");
		verifyRequest.setTotalServiceFee(convertMoney(serviceFee));

		return billPaymentFacade.verify(verifyRequest);
	}

	public BillPaymentConfirmationInfo performPayment(String transactionID) {
		Validate.notNull(transactionID, "missing verification transaction ID");
		Validate.notNull(ref1, "barcode ref1 missing?");

		Validate.notNull(target, "data missing. verify paying to which target agent?");
		Validate.notNull(msisdn, "data missing. verify paying from which mobile number?");
		Validate.notNull(amount, "data missing. how much to pay for this bill?");

		Validate.notNull(serviceFee, "data missing. missing service fee value");

		Validate.notNull(sourceOfFundFee, "data missing. missing source of fund fee value");

		Validate.notNull(tmnID, "data missing. missing ewallet source of fund user detail?");
		Validate.notNull(sessionID, "data missing. missing ewallet source of fund user detail?");

		Validate.notNull(appUser, "data missing.verify paying from which source?");
		Validate.notNull(appPassword, "data missing. verify paying from which source?");
		Validate.notNull(appKey, "data missing. verify paying from which source?");
		Validate.notNull(channel, "data missing. verify paying from which channel?");
		Validate.notNull(channelDetail, "data missing. verify paying from which channel detail.");
		Validate.notNull(payPointCode, "data missing. verify paying from which paypoint?");
		Validate.notNull(payPointName, "data missing. verify paying from which paypoint detail.");


		ConfirmBillPayRequest confirmRequest = new ConfirmBillPayRequest();

		confirmRequest.setTransRef(transactionID);
		confirmRequest.setAppUser(appUser);
		confirmRequest.setAppPassword(appPassword);
		confirmRequest.setAppKey(appKey);

		confirmRequest.setChannel(channel);
		confirmRequest.setChannelDetail(channelDetail);

		confirmRequest.setCommandAction(commandAction);
		confirmRequest.setSession(sessionID);
		confirmRequest.setTmnID(tmnID);

		confirmRequest.setRef1(ref1);
		confirmRequest.setRef2(ref2);
		confirmRequest.setTarget(target);
		confirmRequest.setTransType(transType);

		confirmRequest.setMsisdn(msisdn);
		confirmRequest.setPaypointName(payPointName);
		confirmRequest.setPaypointCode(payPointCode);

		confirmRequest.setAmount(convertMoney(amount));
		confirmRequest.setAmount(convertMoney(amount));
		confirmRequest.setSource(sourceOfFundSourceType);
		confirmRequest.setSourceFeeType("THB");
		confirmRequest.setTotalSourceFee(convertMoney(sourceOfFundFee));
		confirmRequest.setServiceFeeType("THB");
		confirmRequest.setTotalServiceFee(convertMoney(serviceFee));

		return billPaymentFacade.payBill(confirmRequest);
	}

	private String convertMoney(BigDecimal value) {
		BigDecimal scaled = value.setScale(2, RoundingMode.HALF_UP);
		String formatedString = new DecimalFormat("#0.00").format(scaled);
		int lastIndex = formatedString.lastIndexOf('.');
		return formatedString.substring(0, lastIndex) + formatedString.substring(lastIndex + 1);
	}

}
