package th.co.truemoney.serviceinventory.ewallet.impl;

import java.math.BigDecimal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import th.co.truemoney.serviceinventory.email.EmailService;
import th.co.truemoney.serviceinventory.ewallet.TmnProfileService;
import th.co.truemoney.serviceinventory.ewallet.domain.AccessToken;
import th.co.truemoney.serviceinventory.ewallet.domain.Login;
import th.co.truemoney.serviceinventory.ewallet.domain.OTP;
import th.co.truemoney.serviceinventory.ewallet.domain.TmnProfile;
import th.co.truemoney.serviceinventory.ewallet.repositories.AccessTokenRepository;
import th.co.truemoney.serviceinventory.ewallet.repositories.ProfileRepository;
import th.co.truemoney.serviceinventory.exception.ServiceInventoryException;
import th.co.truemoney.serviceinventory.exception.SignonServiceException;
import th.co.truemoney.serviceinventory.legacyfacade.ewallet.BalanceFacade;
import th.co.truemoney.serviceinventory.legacyfacade.ewallet.ProfileFacade;
import th.co.truemoney.serviceinventory.legacyfacade.ewallet.ProfileRegisteringFacade;
import th.co.truemoney.serviceinventory.sms.OTPService;

@Service
public class TmnProfileServiceImpl implements TmnProfileService {

	private static Logger logger = LoggerFactory.getLogger(TmnProfileServiceImpl.class);

	@Autowired
	private AccessTokenRepository accessTokenRepo;

	@Autowired
	private ProfileRepository profileRepository;

	@Autowired
	private OTPService otpService;

	@Autowired
	private ProfileFacade profileFacade;

	@Autowired
	private ProfileRegisteringFacade registeringFacade;

	@Autowired
	private BalanceFacade ewalletFacade;

	@Autowired
	private EmailService emailService;


	@Override
	public String login(Integer channelID, Login login)
				throws SignonServiceException {

		AccessToken accessToken = profileFacade.login(channelID, login);

		// add session id and mapping access token into redis
		logger.info("Access token created: " + accessToken);

		accessTokenRepo.save(accessToken);

		return accessToken.getAccessTokenID();
	}

	@Override
	public TmnProfile getTruemoneyProfile(String accessTokenID) throws ServiceInventoryException {

		AccessToken accessToken = accessTokenRepo.getAccessToken(accessTokenID);
		logger.debug("retrieve access Token: "+accessToken.toString());

		return profileFacade.getProfile(accessToken);
	}

	@Override
	public BigDecimal getEwalletBalance(String accessTokenID) throws ServiceInventoryException {
		AccessToken accessToken = accessTokenRepo.getAccessToken(accessTokenID);
		return ewalletFacade.getCurrentBalance(accessToken);
	}

	@Override
	public String logout(String accessTokenID) {

		AccessToken accessToken = accessTokenRepo.getAccessToken(accessTokenID);

		accessTokenRepo.remove(accessTokenID);

		profileFacade.logout(accessToken);

		return "";
	}

	@Override
    public String validateEmail(Integer channelID, String registeringEmail) throws ServiceInventoryException {
		registeringFacade.verifyValidRegisteringEmail(channelID, registeringEmail);
		return registeringEmail;
    }

	@Override
    public OTP createProfile(Integer channelID, TmnProfile tmnProfile) throws ServiceInventoryException {
		registeringFacade.verifyValidRegisteringMobileNumber(channelID, tmnProfile.getMobileNumber());
       	OTP otp = otpService.send(tmnProfile.getMobileNumber());
       	profileRepository.saveProfile(tmnProfile);
       	return otp;
    }

	@Override
	public TmnProfile confirmCreateProfile(Integer channelID, OTP otp) throws ServiceInventoryException {

		otpService.isValidOTP(otp);

		TmnProfile tmnProfile = profileRepository.getTmnProfile(otp.getMobileNumber());

		performCreateProfile(channelID, tmnProfile);

		sendOutWelcomeEmail(tmnProfile.getEmail());

		return tmnProfile;
	}

	private void performCreateProfile(Integer channelID, TmnProfile tmnProfile) throws ServiceInventoryException {
		registeringFacade.register(channelID, tmnProfile);
    }

	private void sendOutWelcomeEmail(String email) {
		emailService.sendWelcomeEmail(email, null);
	}

	public void setProfileFacade(ProfileFacade profileFacade) {
		this.profileFacade = profileFacade;
	}

	public void setRegisteringFacade(ProfileRegisteringFacade registeringFacade) {
		this.registeringFacade = registeringFacade;
	}

	public void setEwalletFacade(BalanceFacade ewalletFacade) {
		this.ewalletFacade = ewalletFacade;
	}

	public void setOtpService(OTPService otpService) {
		this.otpService = otpService;
	}

	public void setAccessTokenRepository(AccessTokenRepository accessTokenRepo) {
		this.accessTokenRepo = accessTokenRepo;
	}

	public void setProfileRepository(ProfileRepository profileRepository) {
		this.profileRepository = profileRepository;
	}

	public void setEmailService(EmailService emailService) {
		this.emailService = emailService;
	}



}
