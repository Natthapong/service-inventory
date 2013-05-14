package th.co.truemoney.serviceinventory.legacyfacade;

import java.math.BigDecimal;
import java.util.List;

import org.apache.commons.lang.Validate;
import org.springframework.beans.factory.annotation.Autowired;

import th.co.truemoney.serviceinventory.ewallet.domain.AccessToken;
import th.co.truemoney.serviceinventory.ewallet.domain.DirectDebit;
import th.co.truemoney.serviceinventory.ewallet.domain.Favorite;
import th.co.truemoney.serviceinventory.ewallet.domain.TmnProfile;
import th.co.truemoney.serviceinventory.ewallet.domain.TopUpConfirmationInfo;
import th.co.truemoney.serviceinventory.legacyfacade.facade.builders.BillPaymentBuilder;
import th.co.truemoney.serviceinventory.legacyfacade.facade.builders.GetBillInfoBuilder;
import th.co.truemoney.serviceinventory.legacyfacade.facade.builders.TopUpMobileBuilder;
import th.co.truemoney.serviceinventory.legacyfacade.handlers.BillPaymentHandler;
import th.co.truemoney.serviceinventory.legacyfacade.handlers.EwalletBalanceHandler;
import th.co.truemoney.serviceinventory.legacyfacade.handlers.MobileTopUpHandler;
import th.co.truemoney.serviceinventory.legacyfacade.handlers.ProfileRegisteringHandler;
import th.co.truemoney.serviceinventory.legacyfacade.handlers.TopUpSourceOfFundHandler;
import th.co.truemoney.serviceinventory.legacyfacade.handlers.UserProfileHandler;
import th.co.truemoney.serviceinventory.transfer.domain.P2PTransactionConfirmationInfo;

public class LegacyFacade {

	private Integer channelID;

	@Autowired(required = false)
	private EwalletBalanceHandler balanceFacade;

	@Autowired(required = false)
	private UserProfileHandler profileFacade;

	@Autowired(required = false)
	private TopUpSourceOfFundHandler sourceOfFundFacade;

	@Autowired(required = false)
	private BillPaymentHandler billPaymentFacade;
	
	@Autowired(required = false)
	private ProfileRegisteringHandler profileRegisteringFacade;
	
	@Autowired(required = false)
	private MobileTopUpHandler topUpMobileFacade;

	public LegacyFacade fromChannel(Integer channelID) {
		this.channelID = channelID;
		return this;
	}

	public AccessToken login(String credentialUsername, String credentialSecret) {
		return login(channelID, credentialUsername, credentialSecret);
	}

	public AccessToken login(Integer channelID, String credentialUsername, String credentialSecret) {
		this.channelID = channelID;
		return profileFacade.login(channelID, credentialUsername, credentialSecret);
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

	public BillPaymentOptionsBuilder billing() {
		return new BillPaymentOptionsBuilder(billPaymentFacade);
	}

	public ProfileRegisteringBuilder registering() {
		return new ProfileRegisteringBuilder(profileRegisteringFacade).fromChannel(channelID);
	}
	
	public TopUpMobileBuilder topUpMobile() {
		return new TopUpMobileBuilder(topUpMobileFacade);
	}
	
	public LegacyFacade setBalanceFacade(EwalletBalanceHandler balanceFacade) {
		this.balanceFacade = balanceFacade;
		return this;
	}

	public LegacyFacade setProfileFacade(UserProfileHandler profileFacade) {
		this.profileFacade = profileFacade;
		return this;
	}

	public LegacyFacade setProfileRegisteringFacade(ProfileRegisteringHandler profileRegisteringFacade) {
		this.profileRegisteringFacade = profileRegisteringFacade;
		return this;
	}

	public LegacyFacade setSourceOfFundFacade(TopUpSourceOfFundHandler sourceOfFundFacade) {
		this.sourceOfFundFacade = sourceOfFundFacade;
		return this;
	}

	public LegacyFacade setBillPaymentFacade(BillPaymentHandler billPaymentFacade) {
		this.billPaymentFacade = billPaymentFacade;
		return this;
	}
	
	public LegacyFacade setTopUpMobileFacade(MobileTopUpHandler topUpMobileFacade) {
		this.topUpMobileFacade = topUpMobileFacade;
		return this;
	}

	public static class UserProfileBuilder {

		private Integer channelID;
		private String sessionID;
		private String tmnID;
		
		private String serviceType;
		private String serviceCode;
		private String reference1;
		
		private EwalletBalanceHandler balanceFacade;
		private UserProfileHandler profileFacade;
		private TopUpSourceOfFundHandler sourceOfFundFacade;
		
		private Favorite favorite;
		
		@Autowired(required = false)
		public UserProfileBuilder(EwalletBalanceHandler balanceFacade, UserProfileHandler profileFacade, TopUpSourceOfFundHandler sourceOfFundFacade) {
			this.balanceFacade = balanceFacade;
			this.profileFacade = profileFacade;
			this.sourceOfFundFacade = sourceOfFundFacade;
		}

		public UserProfileBuilder() {
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
		
		public UserProfileBuilder withServiceType(String serviceType){
			this.serviceType = serviceType;
			return this;
		}
		
		public UserProfileBuilder withServiceCode(String serviceCode){
			this.serviceCode = serviceCode;
			return this;
		}
		
		public UserProfileBuilder withRefernce1(String reference1){
			this.reference1 = reference1;
			return this;
		}

		public UserProfileBuilder withFavorite(Favorite favorite) {
			this.favorite = favorite;
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

		public List<Favorite> getListFavorite(){
			Validate.notNull(tmnID, "data missing. get direct debit list of whom?");
			Validate.notNull(sessionID, "data missing. get direct debit list of whom?");
			Validate.notNull(channelID, "data missing. get direct debit list from which channel?");
			
			return profileFacade.getListFavorite(this.channelID, this.sessionID, this.tmnID, this.serviceType);
		}
		
		public Boolean isFavoritable(){
			Validate.notNull(tmnID, "data missing. get direct debit list of whom?");
			Validate.notNull(sessionID, "data missing. get direct debit list of whom?");
			Validate.notNull(channelID, "data missing. get direct debit list from which channel?");
			Validate.notNull(serviceCode, "data missing. get serviceCode?");
			Validate.notNull(reference1, "data missing. get reference1?");
			
			return profileFacade.isFavoritable(this.channelID, this.sessionID, this.tmnID,this.serviceType, this.serviceCode, this.reference1);
		}
		
		public Boolean isFavorited(){
			Validate.notNull(tmnID, "data missing. get direct debit list of whom?");
			Validate.notNull(sessionID, "data missing. get direct debit list of whom?");
			Validate.notNull(channelID, "data missing. get direct debit list from which channel?");
			Validate.notNull(serviceCode, "data missing. get serviceCode?");
			Validate.notNull(reference1, "data missing. get reference1?");
			
			return profileFacade.isFavorited(this.channelID, this.sessionID, this.tmnID,this.serviceType, this.serviceCode, this.reference1);
		}
		
		public Favorite addFavorite(){
			Validate.notNull(tmnID, "data missing. get direct debit list of whom?");
			Validate.notNull(sessionID, "data missing. get direct debit list of whom?");
			Validate.notNull(channelID, "data missing. get direct debit list from which channel?");
			
			return profileFacade.addFavorite(this.channelID, this.sessionID, this.tmnID, this.favorite);
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

		public void logout() {
			Validate.notNull(channelID, "from which channel");
			Validate.notNull(sessionID, "missing sessionID");
			Validate.notNull(tmnID, "missing TruemoneyID");

			profileFacade.logout(channelID, sessionID, tmnID);
		}

	}

	public static class TopUpBuilder {

		private Integer channelID;

		private String sessionID;
		private String tmnID;

		private BigDecimal amount;

		private String sourceOfFundID;
		private String sourceOfFundType;

		private EwalletBalanceHandler balanceFacade;

		@Autowired(required = false)
		public TopUpBuilder(EwalletBalanceHandler balanceFacade) {
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

		private EwalletBalanceHandler balanceFacade;

		@Autowired(required = false)
		public P2PTransferBuilder(EwalletBalanceHandler balanceFacade) {
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

		private ProfileRegisteringHandler profileRegisteringFacade;

		private Integer channelID;

		@Autowired(required = false)
		public ProfileRegisteringBuilder(ProfileRegisteringHandler profileRegisteringFacade) {
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

	public static class BillPaymentOptionsBuilder {

		private BillPaymentHandler billPaymentFacade;

		public BillPaymentOptionsBuilder(BillPaymentHandler billPaymentFacade) {
			this.billPaymentFacade = billPaymentFacade;
		}

		public GetBillInfoBuilder readBillInfoWithBarcode(String barcode) {
			return new GetBillInfoBuilder(billPaymentFacade).withBarcode(barcode);
		}
		
		public GetBillInfoBuilder readBillInfoWithBillCode(String billCode) {
			return new GetBillInfoBuilder(billPaymentFacade).withBillCode(billCode);
		}

		public BillPaymentBuilder fromBill(String ref1, String ref2, String targetAgent) {
			return new BillPaymentBuilder(billPaymentFacade)
						.forBillBarcode(ref1, ref2, targetAgent);
		}

	}

}
