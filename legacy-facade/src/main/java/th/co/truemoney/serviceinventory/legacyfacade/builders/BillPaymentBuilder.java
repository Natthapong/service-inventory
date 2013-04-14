package th.co.truemoney.serviceinventory.legacyfacade.builders;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;

import org.apache.commons.lang.Validate;
import org.springframework.beans.factory.annotation.Autowired;

import th.co.truemoney.serviceinventory.bill.domain.BillPaySourceOfFund;
import th.co.truemoney.serviceinventory.bill.domain.BillPaymentConfirmationInfo;
import th.co.truemoney.serviceinventory.bill.domain.ServiceFee;
import th.co.truemoney.serviceinventory.bill.domain.services.ConfirmBillPayRequest;
import th.co.truemoney.serviceinventory.bill.domain.services.VerifyBillPayRequest;
import th.co.truemoney.serviceinventory.legacyfacade.ewallet.BillPaymentFacade;

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
	private BillPaySourceOfFund sourceOfFund;
	private BigDecimal amount;
	private ServiceFee serviceFee;
	private String commandAction;

	private BillPaymentFacade billPaymentFacade;


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
		return this;
	}

	public BillPaymentBuilder withMsisdn(String msisdn) {
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

	public BillPaymentBuilder paying(BigDecimal amount, ServiceFee serviceFee, BillPaySourceOfFund sourceOfFund) {
		this.amount = amount;
		this.serviceFee = serviceFee;
		this.sourceOfFund = sourceOfFund;
		return this;
	}

	public void verifyPayment() {
		Validate.notNull(ref1, "barcode ref1 missing?");
		Validate.notNull(ref2, "barcode ref2 missing?");

		Validate.notNull(target, "data missing. verify paying to which target agent?");
		Validate.notNull(msisdn, "data missing. verify paying from which mobile number?");
		Validate.notNull(amount, "data missing. how much to pay for this bill?");

		Validate.notNull(serviceFee, "data missing. missing service fee value");
		Validate.notNull(serviceFee.getFee(), "data missing. missing service fee value");
		Validate.notNull(serviceFee.getFeeType(), "data missing. missing service fee type");

		Validate.notNull(sourceOfFund, "data missing. verify paying from which source of fund");
		Validate.notNull(sourceOfFund.getSourceType(), "data missing. missing source of fund source type");
		Validate.isTrue(sourceOfFund.getSourceType().equals("EW"), "source of fund not supported");
		Validate.notNull(sourceOfFund.getFee(), "data missing. missing source of fund fee");
		Validate.notNull(sourceOfFund.getFeeType(), "data missing. missing source of fund fee type");

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
		verifyRequest.setSource(sourceOfFund.getSourceType());
		verifyRequest.setSourceFee(convertMoney(sourceOfFund.getFee()));
		verifyRequest.setSourceFeeType(sourceOfFund.getFeeType());
		verifyRequest.setServiceFee(convertMoney(serviceFee.getFee()));
		verifyRequest.setServiceFeeType(serviceFee.getFeeType());

		billPaymentFacade.verify(verifyRequest);
	}

	public BillPaymentConfirmationInfo performPayment() {
		Validate.notNull(ref1, "barcode ref1 missing?");
		Validate.notNull(ref2, "barcode ref2 missing?");

		Validate.notNull(target, "data missing. verify paying to which target agent?");
		Validate.notNull(msisdn, "data missing. verify paying from which mobile number?");
		Validate.notNull(amount, "data missing. how much to pay for this bill?");

		Validate.notNull(serviceFee, "data missing. missing service fee value");
		Validate.notNull(serviceFee.getFee(), "data missing. missing service fee value");
		Validate.notNull(serviceFee.getFeeType(), "data missing. missing service fee type");

		Validate.notNull(sourceOfFund, "data missing. verify paying from which source of fund");
		Validate.notNull(sourceOfFund.getSourceType(), "data missing. missing source of fund source type");
		Validate.isTrue(sourceOfFund.getSourceType().equals("EW"), "source of fund not supported");
		Validate.notNull(sourceOfFund.getFee(), "data missing. missing source of fund fee");
		Validate.notNull(sourceOfFund.getFeeType(), "data missing. missing source of fund fee type");

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
		confirmRequest.setSource(sourceOfFund.getSourceType());
		confirmRequest.setSourceFee(convertMoney(sourceOfFund.getFee()));
		confirmRequest.setSourceFeeType(sourceOfFund.getFeeType());
		confirmRequest.setServiceFee(convertMoney(serviceFee.getFee()));
		confirmRequest.setServiceFeeType(serviceFee.getFeeType());

		return billPaymentFacade.payBill(confirmRequest);
	}

	private String convertMoney(BigDecimal value) {
		BigDecimal scaled = value.setScale(2, RoundingMode.HALF_UP);
		String formatedString = new DecimalFormat("#0.00").format(scaled);
		int lastIndex = formatedString.lastIndexOf('.');
		return formatedString.substring(0, lastIndex) + formatedString.substring(lastIndex + 1);
	}

}
