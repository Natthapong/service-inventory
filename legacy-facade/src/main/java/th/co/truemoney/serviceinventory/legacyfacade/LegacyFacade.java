package th.co.truemoney.serviceinventory.legacyfacade;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;

import th.co.truemoney.serviceinventory.ewallet.domain.AccessToken;
import th.co.truemoney.serviceinventory.legacyfacade.facade.builders.BillPaymentOptionsBuilder;
import th.co.truemoney.serviceinventory.legacyfacade.facade.builders.ForgotPasswordBuilder;
import th.co.truemoney.serviceinventory.legacyfacade.facade.builders.P2PTransferBuilder;
import th.co.truemoney.serviceinventory.legacyfacade.facade.builders.ProfileRegisteringBuilder;
import th.co.truemoney.serviceinventory.legacyfacade.facade.builders.TopUpBuilder;
import th.co.truemoney.serviceinventory.legacyfacade.facade.builders.TopUpMobileBuilder;
import th.co.truemoney.serviceinventory.legacyfacade.facade.builders.UserProfileBuilder;
import th.co.truemoney.serviceinventory.legacyfacade.handlers.BillPaymentHandler;
import th.co.truemoney.serviceinventory.legacyfacade.handlers.EwalletBalanceHandler;
import th.co.truemoney.serviceinventory.legacyfacade.handlers.ForgotPasswordHandler;
import th.co.truemoney.serviceinventory.legacyfacade.handlers.MobileTopUpHandler;
import th.co.truemoney.serviceinventory.legacyfacade.handlers.ProfileRegisteringHandler;
import th.co.truemoney.serviceinventory.legacyfacade.handlers.TopUpSourceOfFundHandler;
import th.co.truemoney.serviceinventory.legacyfacade.handlers.UserProfileHandler;

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

	@Autowired(required = false)
	private ForgotPasswordHandler forgotPasswordFacade;

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
			.aUser(sessionID, tmnID).fromChannel(channelID);
	}

	public TopUpBuilder topUp(BigDecimal amount) {
		return new TopUpBuilder(balanceFacade).fromChannelID(channelID).withAmount(amount);
	}

	public P2PTransferBuilder transfer(BigDecimal amount) {
		return new P2PTransferBuilder(balanceFacade).fromChannelID(channelID).withAmount(amount);
	}

	public ForgotPasswordBuilder forgotPassword() {
		return new ForgotPasswordBuilder(forgotPasswordFacade).fromChannel(channelID);
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

	public LegacyFacade setForgotPasswordFacade(ForgotPasswordHandler forgotPasswordFacade) {
		this.forgotPasswordFacade = forgotPasswordFacade;
		return this;
	}

}
