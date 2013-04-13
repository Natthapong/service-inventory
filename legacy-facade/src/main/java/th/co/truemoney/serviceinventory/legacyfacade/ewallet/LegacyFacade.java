package th.co.truemoney.serviceinventory.legacyfacade.ewallet;

import java.math.BigDecimal;
import java.util.List;

import org.apache.commons.lang.Validate;
import org.springframework.beans.factory.annotation.Autowired;

import th.co.truemoney.serviceinventory.bill.domain.BillInfo;
import th.co.truemoney.serviceinventory.bill.domain.BillPaymentConfirmationInfo;
import th.co.truemoney.serviceinventory.bill.domain.BillRequest;
import th.co.truemoney.serviceinventory.bill.domain.SourceOfFundFee;
import th.co.truemoney.serviceinventory.bill.domain.services.GetBarcodeRequest;
import th.co.truemoney.serviceinventory.bill.util.EncryptionUtil;
import th.co.truemoney.serviceinventory.ewallet.domain.AccessToken;
import th.co.truemoney.serviceinventory.ewallet.domain.DirectDebit;
import th.co.truemoney.serviceinventory.ewallet.domain.TmnProfile;
import th.co.truemoney.serviceinventory.ewallet.domain.TopUpConfirmationInfo;
import th.co.truemoney.serviceinventory.legacyfacade.ewallet.BillPaymentFacade.UnknownServiceFeeType;
import th.co.truemoney.serviceinventory.transfer.domain.P2PTransactionConfirmationInfo;

public class LegacyFacade {

	private Integer channelID;
	
	private String channel;
	
	private String channelDetail; 

	@Autowired(required = false)
	private BalanceFacade balanceFacade;

	@Autowired(required = false)
	private ProfileFacade profileFacade;

	@Autowired(required = false)
	private SourceOfFundFacade sourceOfFundFacade;

	@Autowired(required = false)
	private BillPaymentFacade billPaymentFacade;

	@Autowired(required = false)
	private ProfileRegisteringFacade profileRegisteringFacade;

	@Autowired(required = false)
	private AccessToken accessToken;
	
	public LegacyFacade fromChannel(Integer channelID) {
		this.channelID = channelID;
		return this;
	}
	
	public LegacyFacade fromAcccessToken(AccessToken accessToken) {
		this.accessToken = accessToken;
		return this;
	}

	public AccessToken login(String credentialUsername, String credentialSecret) {
		return login(channelID, credentialUsername, credentialSecret);
	}

	public AccessToken login(Integer channelID, String credentialUsername, String credentialSecret) {
		this.channelID = channelID;
		return profileFacade.login(channelID, credentialUsername, credentialSecret);
	}

	public void logout(String sessionID, String truemoneyID) {
		logout(channelID, sessionID, truemoneyID);
	}

	public void logout(Integer channelID, String sessionID, String truemoneyID) {
		Validate.notNull(channelID, "from which channel");
		Validate.notNull(sessionID, "missing sessionID");
		Validate.notNull(truemoneyID, "missing TruemoneyID");

		profileFacade.logout(channelID, sessionID, truemoneyID);
	}

	public UserProfileBuilder userProfile(String sessionID, String tmnID) {
		return new UserProfileBuilder(balanceFacade, profileFacade, sourceOfFundFacade)
					.aUser(sessionID, tmnID)
					.fromChannel(channelID);
	}

	public TopUpBuilder topUp(BigDecimal amount) {
		return new TopUpBuilder(balanceFacade)
					.fromChannelID(channelID)
					.withAmount(amount);
	}

	public P2PTransferBuilder transfer(BigDecimal amount) {
		return new P2PTransferBuilder(balanceFacade)
					.fromChannelID(channelID)
					.withAmount(amount);
	}
	
	public BillPaymentBuilder payBill(BillInfo billPaymentInfo) {
		return new BillPaymentBuilder(billPaymentFacade)
					.fromBillChannel(channel, channelDetail)
					.usingBillInfo(billPaymentInfo);
	}
	
	public BillPaymentBuilder billPayment() {
		return new BillPaymentBuilder(billPaymentFacade);
	}

	public ProfileRegisteringBuilder registering() {
		return new ProfileRegisteringBuilder(profileRegisteringFacade).fromChannel(channelID);
	}

	public LegacyFacade setBalanceFacade(BalanceFacade balanceFacade) {
		this.balanceFacade = balanceFacade;
		return this;
	}

	public LegacyFacade setProfileFacade(ProfileFacade profileFacade) {
		this.profileFacade = profileFacade;
		return this;
	}

	public LegacyFacade setProfileRegisteringFacade(ProfileRegisteringFacade profileRegisteringFacade) {
		this.profileRegisteringFacade = profileRegisteringFacade;
		return this;
	}

	public LegacyFacade setSourceOfFundFacade(SourceOfFundFacade sourceOfFundFacade) {
		this.sourceOfFundFacade = sourceOfFundFacade;
		return this;
	}

	public LegacyFacade setBillPaymentFacade(BillPaymentFacade billPaymentFacade) {
		this.billPaymentFacade = billPaymentFacade;
		return this;
	}

	public static class UserProfileBuilder {

		private Integer channelID;
		private String sessionID;
		private String tmnID;

		private BalanceFacade balanceFacade;
		private ProfileFacade profileFacade;
		private SourceOfFundFacade sourceOfFundFacade;

		@Autowired(required = false)
		public UserProfileBuilder(BalanceFacade balanceFacade, ProfileFacade profileFacade, SourceOfFundFacade sourceOfFundFacade) {
			this.balanceFacade = balanceFacade;
			this.profileFacade = profileFacade;
			this.sourceOfFundFacade = sourceOfFundFacade;
		}

		public UserProfileBuilder aUser(String sessionID, String tmnID) {
			this.sessionID = sessionID;
			this.tmnID = tmnID;
			return this;
		}

		public UserProfileBuilder fromChannel(Integer channelID) {
			this.channelID = channelID;
			return this;
		}

		public TmnProfile getProfile() {
			Validate.notNull(channelID, "from which channel");
			Validate.notNull(sessionID, "missing sessionID");
			Validate.notNull(tmnID, "missing TruemoneyID");

			return profileFacade.getProfile(this.channelID, this.sessionID, this.tmnID);
		}

		public BigDecimal getCurrentBalance() {
			Validate.notNull(tmnID, "data missing. get balance of whom?");
			Validate.notNull(sessionID, "data missing. get balance of whom?");
			Validate.notNull(channelID, "data missing. get balance from which channel?");

			return balanceFacade.getCurrentBalance(this.channelID, this.sessionID, this.tmnID);
		}

		public List<DirectDebit> getDirectDebitSourceOfFundList() {
			Validate.notNull(tmnID, "data missing. get direct debit list of whom?");
			Validate.notNull(sessionID, "data missing. get direct debit list of whom?");
			Validate.notNull(channelID, "data missing. get direct debit list from which channel?");

			return sourceOfFundFacade.getAllDirectDebitSourceOfFunds(this.channelID, this.sessionID, this.tmnID);
		}

		public TopUpBuilder topUp(BigDecimal amount) {
			return new TopUpBuilder(balanceFacade)
						.fromChannelID(channelID)
						.fromUser(sessionID, tmnID)
						.withAmount(amount);
		}

		public P2PTransferBuilder transfer(BigDecimal amount) {
			return new P2PTransferBuilder(balanceFacade)
						.fromChannelID(channelID)
						.fromUser(sessionID, tmnID)
						.withAmount(amount);
		}
		
	}

	public static class TopUpBuilder {

		private Integer channelID;

		private String sessionID;
		private String tmnID;

		private BigDecimal amount;

		private String sourceOfFundID;
		private String sourceOfFundType;

		private BalanceFacade balanceFacade;

		@Autowired(required = false)
		public TopUpBuilder(BalanceFacade balanceFacade) {
			this.balanceFacade = balanceFacade;
		}

		public TopUpBuilder fromChannelID(Integer channelID) {
			this.channelID = channelID;
			return this;
		}

		public TopUpBuilder fromUser(String sessionID, String tmnID) {
			this.sessionID = sessionID;
			this.tmnID = tmnID;
			return this;
		}

		public TopUpBuilder withAmount(BigDecimal amount) {
			this.amount = amount;
			return this;
		}

		public TopUpBuilder usingSourceOFFund(String sourceOfFundID, String sourceOfFundType) {
			this.sourceOfFundID = sourceOfFundID;
			this.sourceOfFundType = sourceOfFundType;
			return this;
		}

		public void verify() {

			Validate.notNull(tmnID, "data missing. topup from whom?");
			Validate.notNull(sessionID, "data missing. topup from whom?");
			Validate.notNull(channelID, "data missing. topup from which channel?");
			Validate.notNull(amount, "data missing. how much to top up?");
			Validate.notNull(sourceOfFundID, "data missing. using which source of fund to top up?");
			Validate.notNull(sourceOfFundType, "data missing. using which source of fund to top up?");

			balanceFacade.verifyToppingUpCapability(
					amount,
					sourceOfFundID,
					sourceOfFundType,
					channelID,
					sessionID,
					tmnID);
		}

		public TopUpConfirmationInfo performTopUp() {

			Validate.notNull(tmnID, "data missing. topup from whom?");
			Validate.notNull(sessionID, "data missing. topup from whom?");
			Validate.notNull(channelID, "data missing. topup from which channel?");
			Validate.notNull(amount, "data missing. how much to top up?");
			Validate.notNull(sourceOfFundID, "data missing. using withc source of fund to top up?");
			Validate.notNull(sourceOfFundType, "data missing. using withc source of fund to top up?");

			return balanceFacade.topUpMoney(
					amount,
					sourceOfFundID,
					sourceOfFundType,
					channelID,
					sessionID,
					tmnID);
		}
	}

	public static class P2PTransferBuilder {

		private Integer channelID;

		private String sessionID;
		private String tmnID;

		private BigDecimal amount;

		private String targetMobileNumber;

		private BalanceFacade balanceFacade;

		@Autowired(required = false)
		public P2PTransferBuilder(BalanceFacade balanceFacade) {
			this.balanceFacade = balanceFacade;
		}

		public P2PTransferBuilder fromChannelID(Integer channelID) {
			this.channelID = channelID;
			return this;
		}

		public P2PTransferBuilder fromUser(String sessionID, String tmnID) {
			this.sessionID = sessionID;
			this.tmnID = tmnID;
			return this;
		}

		public P2PTransferBuilder toTargetUser(String targetMobileNumber) {
			this.targetMobileNumber = targetMobileNumber;
			return this;
		}

		public P2PTransferBuilder withAmount(BigDecimal amount) {
			this.amount = amount;
			return this;
		}


		public String verify() {

			Validate.notNull(tmnID, "data missing. transfer money from whom?");
			Validate.notNull(sessionID, "data missing. transfer money from whom?");
			Validate.notNull(channelID, "data missing. transfer money from which channel?");
			Validate.notNull(amount, "data missing. how much to transfer?");
			Validate.notNull(targetMobileNumber, "data missing. whom to transfer money to?");

			return balanceFacade.verifyP2PTransfer(amount, targetMobileNumber, channelID, sessionID, tmnID);
		}

		public P2PTransactionConfirmationInfo performTransfer() {

			Validate.notNull(tmnID, "data missing. transfer money from whom?");
			Validate.notNull(sessionID, "data missing. transfer money from whom?");
			Validate.notNull(channelID, "data missing. transfer money from which channel?");
			Validate.notNull(amount, "data missing. how much to transfer?");
			Validate.notNull(targetMobileNumber, "data missing. whom to transfer money to?");

			return balanceFacade.transferEwallet(amount, targetMobileNumber, channelID, sessionID, tmnID);
		}
	}

	public static class ProfileRegisteringBuilder {

		private ProfileRegisteringFacade profileRegisteringFacade;

		private Integer channelID;

		@Autowired(required = false)
		public ProfileRegisteringBuilder(ProfileRegisteringFacade profileRegisteringFacade) {
			this.profileRegisteringFacade = profileRegisteringFacade;
		}

		public ProfileRegisteringBuilder fromChannel(Integer channelID) {
			this.channelID = channelID;
			return this;
		}

		public void verifyEmail(String registeringEmail) {
			Validate.notNull(channelID, "data missing. registering from which channel?");
			Validate.notNull(registeringEmail, "data missing. registering email missing?");

			profileRegisteringFacade.verifyValidRegisteringEmail(channelID, registeringEmail);
		}

		public void verifyMobileNumber(String registeringMobileNumber) {
			Validate.notNull(channelID, "data missing. registering from which channel?");
			Validate.notNull(registeringMobileNumber, "data missing. registering mobile number missing?");

			profileRegisteringFacade.verifyValidRegisteringMobileNumber(channelID, registeringMobileNumber);
		}

		public void register(TmnProfile profile) {
			Validate.notNull(channelID, "data missing. registering from which channel?");
			Validate.notNull(profile, "data missing. registering profile missing?");

			profileRegisteringFacade.register(channelID, profile);
		}

	}
	
	public static class BillPaymentBuilder {

		private BillPaymentFacade billPaymentFacade;

		private String barcode;
		
		private AccessToken accessToken;

		private String sessionID;
		private String tmnID;
		private String msisdn;

		private BigDecimal amount;

		private SourceOfFundFee selectedSourceOfFundFee;
		
		private String ref1;
		private String ref2;
		private String targetBiller;
		
		private String paypointCode;
		private String paypointName;
		private String transactionID;
		
		private BillInfo billPaymentInfo;

		private String transactionRelation;

		private String serviceFeeType;

		private BigDecimal serviceFee;



		private String appUser;

		private String appPassword;
		
		private String appKey;

		private String channel;

		private String channelDetail;

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
		
		public BillPaymentBuilder fromAccessToken(AccessToken accessToken) {
			this.accessToken = accessToken;
			return this;
		}

		public BillPaymentBuilder withBarcode(String barcode) {
			this.barcode = barcode;
			return this;
		}

		public BillInfo getInformation() {
			Validate.notNull(channel, "data missing. get barcode information from which channel?");
			Validate.notNull(channelDetail, "missing channel detail.");
			Validate.notNull(barcode, "data missing. barcode missing?");
			Validate.notNull(appUser, "data missing. from which app user?");
			Validate.notNull(appPassword, "data missing. missing app password.");
			Validate.notNull(appKey, "data missing. missing app key.");
			
			GetBarcodeRequest billRequest = new GetBarcodeRequest();
			
			billRequest.setChannel(channel);
			billRequest.setChannelDetail(channelDetail);
			
			billRequest.setAppUser(appUser);
			billRequest.setAppPassword(appPassword);
			billRequest.setAppKey(appKey);
			
			billRequest.setBarcode(barcode);
			billRequest.setMD5(billRequest.generateMD5());
			
			return billPaymentFacade.getBarcodeInformation(billRequest);
		}

		public BillInfo verify(BillInfo billpayInfo) {
			
			Validate.notNull(billpayInfo, "Null Object. Did it send right object?");
			
			BillRequest billPayRequest = new BillRequest();
			
			String md5 = EncryptionUtil.appendForMD5("f7cb0d495ea6d989", "MOBILE_IPHONE", "810010002", "4410A0322", 
					billpayInfo.getAmount().toString(), accessToken.getSessionID());

			billPayRequest.setAppUser("MOBILE_IPHONE");
			billPayRequest.setAppPassword("IPHONE+1");
			billPayRequest.setChannel("iPhone");
			billPayRequest.setCommand("doService");
			billPayRequest.setFunctionID("810010002");
			billPayRequest.setServiceNo("any");		
			billPayRequest.setReqTransactionId(Long.toString(System.currentTimeMillis()));
			billPayRequest.addParameterElement("md5", EncryptionUtil.MD5(md5));
			billPayRequest.addParameterElement("session", accessToken.getSessionID());
			billPayRequest.addParameterElement("amount", billpayInfo.getAmount().toString());
			billPayRequest.addParameterElement("msisdn", accessToken.getMobileNumber());
			billPayRequest.addParameterElement("service_fee", "10");
			billPayRequest.addParameterElement("service_fee_type", "THB");
			billPayRequest.addParameterElement("source", "EW");
			billPayRequest.addParameterElement("source_fee", "10050");
			//TODO Change Source Fee Type.
			billPayRequest.addParameterElement("source_fee_type", "0");
			billPayRequest.addParameterElement("ref1", billpayInfo.getRef1().toString());
			billPayRequest.addParameterElement("ref2", billpayInfo.getRef2().toString());
			billPayRequest.addParameterElement("paypointcode", "mobile");
			billPayRequest.addParameterElement("paypointname", "EW-APP");
			billPayRequest.addParameterElement("channel_detail", "IPHONE");
			billPayRequest.addParameterElement("command_action", "EW");
			billPayRequest.addParameterElement("fund_name", "verifybillpay");
			billPayRequest.addParameterElement("tmn_id", accessToken.getTruemoneyID());
			billPayRequest.addParameterElement("target", billpayInfo.getTarget().toString());
			billPayRequest.addParameterElement("trans_type", "01");
			
			return billPaymentFacade.verify(billPayRequest);
		}

		public BillPaymentBuilder fromUser(String sessionID, String tmnID, String msisdn) {
			this.sessionID = sessionID;
			this.tmnID = tmnID;
			this.msisdn = msisdn;
			return this;
		}
		
		public BillPaymentBuilder withAmount(BigDecimal amount) {
			this.amount = amount;
			return this;
		}

		public BillPaymentBuilder usingSourceOfFundFee(SourceOfFundFee sourceOfFundFee) {
			this.selectedSourceOfFundFee = sourceOfFundFee;
			return this;
		}
		
		public BillPaymentBuilder usingService(String serviceFeeType, BigDecimal serviceFee) {
			this.serviceFeeType = serviceFeeType;
			this.serviceFee = serviceFee;
			return this;
		}
		
		public BillPaymentBuilder usingRefCode(String ref1, String ref2) {
			this.ref1 = ref1;
			this.ref2 = ref2;
			return this;
		}
		
		public BillPaymentBuilder usingTransaction(String transactionID) {
			this.transactionID = transactionID;
			return this;
		}
		
		public BillPaymentBuilder usingPaypoint(String paypointCode, String paypointName) {
			this.paypointCode = paypointCode;
			this.paypointName = paypointName;
			return this;
		}
		
		public BillPaymentBuilder usingTargetBiller(String targetBiller) {
			this.targetBiller = targetBiller;
			return this;
		}
		
		public BillPaymentBuilder usingTransactionRelation(String transactionRelation) {
			this.transactionRelation = transactionRelation;
			return this;
		}
		
		public BillPaymentBuilder usingBillInfo(BillInfo billPaymentInfo) {
			this.billPaymentInfo = billPaymentInfo;
			return this;
		}
		
		public BillPaymentConfirmationInfo performBillPayment() {
			Validate.notNull(tmnID, "data missing. who pay?");
			Validate.notNull(sessionID, "data missing. who pay?");
			Validate.notNull(channel, "data missing. bill pay from which channel?");
			Validate.notNull(amount, "data missing. how much to pay?");
			Validate.notNull(ref1, "data missing. ref code 1 is missing.");
			Validate.notNull(serviceFeeType, "data missing. service fee type is missing.");
			Validate.notNull(serviceFee, "data missing. service fee is missing.");
			Validate.notNull(targetBiller, "data missing. target biller is missing.");
			Validate.notNull(selectedSourceOfFundFee, "data missing. no selected source of fund.");
			Validate.notNull(msisdn, "data missing. msisdn is missing.");
			
			String key = "f7cb0d495ea6d989";
			String appUser = "MOBILE_IPHONE";
			String functionID = "910010003";
			
			String md5Hash = EncryptionUtil.appendForMD5(key, appUser, functionID, transactionID, amount.toString(), sessionID);
			
			BillRequest billRequest = new BillRequest();
			
			billRequest.addParameterElement("md5", md5Hash);
			billRequest.addParameterElement("ref1", ref1);
			billRequest.addParameterElement("ref2", ref2);
			billRequest.addParameterElement("msisdn", msisdn);
			billRequest.addParameterElement("amount", amount.toString());
			billRequest.addParameterElement("fund_name", "billpay");
			billRequest.addParameterElement("target", targetBiller);
			billRequest.addParameterElement("total_service_fee", this.calculateTotalServiceFee(serviceFeeType, serviceFee, amount).toString());
			billRequest.addParameterElement("service_fee_type", serviceFeeType);
			billRequest.addParameterElement("source", selectedSourceOfFundFee.getSourceType());
			billRequest.addParameterElement("total_source_fee", selectedSourceOfFundFee.getTotalFee().toString());
			billRequest.addParameterElement("source_fee_type", selectedSourceOfFundFee.getFeeType());
			billRequest.addParameterElement("channel_detail", "iPhone");
			billRequest.addParameterElement("tmn_id", tmnID);
			billRequest.addParameterElement("paypointcode", paypointCode);
			billRequest.addParameterElement("paypointname", paypointName);
			billRequest.addParameterElement("trans_type","01");
			
			return billPaymentFacade.payBill(billRequest);
		}
		
		private BigDecimal calculateTotalServiceFee(String serviceFeeType, BigDecimal serviceFee, BigDecimal amount) {
			
			BigDecimal fee = serviceFee != null ? serviceFee : BigDecimal.ZERO;
			
			if ("THB".equals(serviceFeeType)) {
				return fee;
			} else if ("percent".equals(serviceFeeType)) {
				return fee.multiply(amount).divide(new BigDecimal(100));
			} else if (serviceFeeType != null) {
				throw new UnknownServiceFeeType(serviceFeeType);
			}
			
			return BigDecimal.ZERO;
		}

	}

}
