package th.co.truemoney.serviceinventory.legacyfacade.facade.builders;

import org.apache.commons.lang.Validate;
import org.springframework.beans.factory.annotation.Autowired;

import th.co.truemoney.serviceinventory.ewallet.domain.TmnProfile;
import th.co.truemoney.serviceinventory.legacyfacade.handlers.ProfileRegisteringHandler;

public class ProfileRegisteringBuilder {

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
		Validate.notNull(channelID,	"data missing. registering from which channel?");
		Validate.notNull(registeringEmail, "data missing. registering email missing?");

		profileRegisteringFacade.verifyValidRegisteringEmail(channelID,	registeringEmail);
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