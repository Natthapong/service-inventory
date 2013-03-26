package th.co.truemoney.serviceinventory.ewallet;

import java.math.BigDecimal;

import th.co.truemoney.serviceinventory.ewallet.domain.Login;
import th.co.truemoney.serviceinventory.ewallet.domain.OTP;
import th.co.truemoney.serviceinventory.ewallet.domain.TmnProfile;
import th.co.truemoney.serviceinventory.exception.ServiceInventoryException;

public interface TmnProfileService {

	public String login(Integer channelID, Login login) throws ServiceInventoryException;

	public TmnProfile getTruemoneyProfile(String accessTokenID) throws ServiceInventoryException;
	
	public BigDecimal getEwalletBalance(String accessTokenID) throws ServiceInventoryException;

	public String logout(String accessTokenID) throws ServiceInventoryException;
	
	public String validateEmail(Integer channelID, String email);
	
	public String createProfile(Integer channelID, TmnProfile tmnProfile);
	
	public TmnProfile confirmCreateProfile(Integer channelID, String mobileno, OTP otp);
	
}

