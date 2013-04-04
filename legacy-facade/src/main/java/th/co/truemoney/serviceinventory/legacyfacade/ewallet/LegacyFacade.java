package th.co.truemoney.serviceinventory.legacyfacade.ewallet;

import java.math.BigDecimal;
import java.util.List;

import org.apache.commons.lang.Validate;
import org.springframework.beans.factory.annotation.Autowired;

import th.co.truemoney.serviceinventory.ewallet.domain.AccessToken;
import th.co.truemoney.serviceinventory.ewallet.domain.DirectDebit;
import th.co.truemoney.serviceinventory.ewallet.domain.P2PTransactionConfirmationInfo;
import th.co.truemoney.serviceinventory.ewallet.domain.TmnProfile;
import th.co.truemoney.serviceinventory.ewallet.domain.TopUpConfirmationInfo;

public class LegacyFacade {

	private Integer channelID;

	@Autowired(required = false)
	private BalanceFacade balanceFacade;

	@Autowired(required = false)
	private ProfileFacade profileFacade;

	@Autowired(required = false)
	private SourceOfFundFacade sourceOfFundFacade;

	@Autowired(required = false)
	private ProfileRegisteringFacade profileRegisteringFacade;

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

	public ProfileRegsisteringBuilder registering() {
		return new ProfileRegsisteringBuilder(profileRegisteringFacade).fromChannel(channelID);
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


		public void verify() {

			Validate.notNull(tmnID, "data missing. transfer money from whom?");
			Validate.notNull(sessionID, "data missing. transfer money from whom?");
			Validate.notNull(channelID, "data missing. transfer money from which channel?");
			Validate.notNull(amount, "data missing. how much to transfer?");
			Validate.notNull(targetMobileNumber, "data missing. whom to transfer money to?");

			balanceFacade.verifyP2PTransfer(amount, targetMobileNumber, channelID, sessionID, tmnID);
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

	public static class ProfileRegsisteringBuilder {

		private ProfileRegisteringFacade profileRegisteringFacade;

		private Integer channelID;

		@Autowired(required = false)
		public ProfileRegsisteringBuilder(ProfileRegisteringFacade profileRegisteringFacade) {
			this.profileRegisteringFacade = profileRegisteringFacade;
		}

		public ProfileRegsisteringBuilder fromChannel(Integer channelID) {
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

}
