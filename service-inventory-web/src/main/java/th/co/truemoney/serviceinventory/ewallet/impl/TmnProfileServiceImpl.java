package th.co.truemoney.serviceinventory.ewallet.impl;

import java.math.BigDecimal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import th.co.truemoney.serviceinventory.email.EmailService;
import th.co.truemoney.serviceinventory.ewallet.TmnProfileService;
import th.co.truemoney.serviceinventory.ewallet.domain.AccessToken;
import th.co.truemoney.serviceinventory.ewallet.domain.ClientCredential;
import th.co.truemoney.serviceinventory.ewallet.domain.EWalletOwnerCredential;
import th.co.truemoney.serviceinventory.ewallet.domain.OTP;
import th.co.truemoney.serviceinventory.ewallet.domain.TmnProfile;
import th.co.truemoney.serviceinventory.ewallet.repositories.AccessTokenRepository;
import th.co.truemoney.serviceinventory.ewallet.repositories.RegisteringProfileRepository;
import th.co.truemoney.serviceinventory.exception.ServiceInventoryException;
import th.co.truemoney.serviceinventory.exception.SignonServiceException;
import th.co.truemoney.serviceinventory.legacyfacade.LegacyFacade;
import th.co.truemoney.serviceinventory.sms.OTPService;

@Service
public class TmnProfileServiceImpl implements TmnProfileService {

	private static Logger logger = LoggerFactory.getLogger(TmnProfileServiceImpl.class);

	@Autowired
	private LegacyFacade legacyFacade;

	@Autowired
	private OTPService otpService;

	@Autowired
	private EmailService emailService;

	@Autowired
	private AccessTokenRepository accessTokenRepo;

	@Autowired
	private RegisteringProfileRepository registeringProfileRepo;

	@Override
	public String login(EWalletOwnerCredential userLogin, ClientCredential clientLogin)
				throws SignonServiceException {

		//TODO: verify client login first???

		Integer channelID = userLogin.getChannelId();
		String initiator = userLogin.getLoginKey();
		String secret = userLogin.getLoginSecret();

		AccessToken accessToken = legacyFacade.login(channelID, initiator, secret);

		accessToken.setClientCredential(clientLogin);

		logger.info("Access token created: " + accessToken);

		accessTokenRepo.save(accessToken);

		return accessToken.getAccessTokenID();
	}

	@Override
	public TmnProfile getTruemoneyProfile(String accessTokenID) throws ServiceInventoryException {

		AccessToken accessToken = accessTokenRepo.findAccessToken(accessTokenID);

		return legacyFacade.userProfile(accessToken.getSessionID(), accessToken.getTruemoneyID())
						   .fromChannel(accessToken.getChannelID())
						   .getProfile();
	}

	@Override
	public BigDecimal getEwalletBalance(String accessTokenID) throws ServiceInventoryException {

		AccessToken accessToken = accessTokenRepo.findAccessToken(accessTokenID);

		return legacyFacade.userProfile(accessToken.getSessionID(), accessToken.getTruemoneyID())
						   .fromChannel(accessToken.getChannelID())
				   		   .getCurrentBalance();
	}

	@Override
	public String logout(String accessTokenID) {

		AccessToken accessToken = accessTokenRepo.findAccessToken(accessTokenID);

		accessTokenRepo.remove(accessTokenID);

		legacyFacade.userProfile(accessToken.getSessionID(), accessToken.getTruemoneyID())
					.fromChannel(accessToken.getChannelID())
					.logout();

		return "";
	}

	@Override
    public String validateEmail(Integer channelID, String registeringEmail) throws ServiceInventoryException {

		legacyFacade.registering()
						.fromChannel(channelID)
						.verifyEmail(registeringEmail);

		return registeringEmail;
    }

	@Override
    public OTP createProfile(Integer channelID, TmnProfile tmnProfile) throws ServiceInventoryException {

		legacyFacade.registering()
						.fromChannel(channelID)
						.verifyMobileNumber(tmnProfile.getMobileNumber());

       	OTP otp = otpService.send(tmnProfile.getMobileNumber());

       	registeringProfileRepo.saveRegisteringProfile(tmnProfile);

       	return otp;
    }

	@Override
	public TmnProfile confirmCreateProfile(Integer channelID, OTP otp) throws ServiceInventoryException {

		otpService.isValidOTP(otp);

		TmnProfile tmnProfile = registeringProfileRepo.findRegisteringProfileByMobileNumber(otp.getMobileNumber());

		performCreateProfile(channelID, tmnProfile);

		sendOutWelcomeEmail(tmnProfile.getEmail());

		return tmnProfile;
	}

	@Override
	public TmnProfile changeFullname(String accessTokenID, String fullname) 
			throws ServiceInventoryException {
		
		AccessToken accessToken = accessTokenRepo.findAccessToken(accessTokenID);
		
		legacyFacade.userProfile(accessToken.getSessionID(), accessToken.getTruemoneyID())
				.fromChannel(accessToken.getChannelID())
				.withFullname(fullname)
				.changeFullName();
		
		return legacyFacade.userProfile(accessToken.getSessionID(), accessToken.getTruemoneyID())
				   .fromChannel(accessToken.getChannelID())
				   .getProfile();		
	}

	@Override
	public String changePin(String accessTokenID, String oldPin, String newPin)
			throws ServiceInventoryException {
		
		AccessToken accessToken = accessTokenRepo.findAccessToken(accessTokenID);
		
		legacyFacade.userProfile(accessToken.getSessionID(), accessToken.getTruemoneyID())
				.fromChannel(accessToken.getChannelID())
				.withPin(oldPin, newPin)
				.withLoginID(accessToken.getMobileNumber())
				.changePin();
		
		return accessToken.getMobileNumber();
		
	}

	@Override
	public String changePassword(String accessTokenID, String oldPassword, String newPassword) 
			throws ServiceInventoryException {
		
		AccessToken accessToken = accessTokenRepo.findAccessToken(accessTokenID);
		
		legacyFacade.userProfile(accessToken.getSessionID(), accessToken.getTruemoneyID())
			.fromChannel(accessToken.getChannelID())
			.withPassword(oldPassword, newPassword)
			.withLoginID(accessToken.getEmail())
			.changePassword();
		
		return accessToken.getEmail();
	}
	
	@Override
	public TmnProfile changeProfileImage(String accessTokenID, String imageFileName)
			throws ServiceInventoryException {
		AccessToken accessToken = accessTokenRepo.findAccessToken(accessTokenID);
		
		legacyFacade.userProfile(accessToken.getSessionID(), accessToken.getTruemoneyID())
				.fromChannel(accessToken.getChannelID())
				.withImageName(imageFileName)
				.changeProfileImage();
		
		return legacyFacade.userProfile(accessToken.getSessionID(), accessToken.getTruemoneyID())
				   .fromChannel(accessToken.getChannelID())
				   .getProfile();
	}
	
	@Override
	public String verifyAccessToken(String accessTokenID)
			throws ServiceInventoryException {

		accessTokenRepo.findAccessToken(accessTokenID);

		return accessTokenID;
	}

	private void performCreateProfile(Integer channelID, TmnProfile tmnProfile) throws ServiceInventoryException {
		legacyFacade.fromChannel(channelID)
			.registering()
			.register(tmnProfile);
    }

	private void sendOutWelcomeEmail(String email) {
		emailService.sendWelcomeEmail(email, null);
	}

	public void setLegacyFacade(LegacyFacade legacyFacade) {
		this.legacyFacade = legacyFacade;
	}

	public void setOtpService(OTPService otpService) {
		this.otpService = otpService;
	}

	public void setAccessTokenRepository(AccessTokenRepository accessTokenRepo) {
		this.accessTokenRepo = accessTokenRepo;
	}

	public void setRegisteringProfileRepository(RegisteringProfileRepository registeringProfileRepo) {
		this.registeringProfileRepo = registeringProfileRepo;
	}

	public void setEmailService(EmailService emailService) {
		this.emailService = emailService;
	}

}
