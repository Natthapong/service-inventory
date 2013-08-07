package th.co.truemoney.serviceinventory.ewallet;

import java.math.BigDecimal;

import th.co.truemoney.serviceinventory.ewallet.domain.ChangePassword;
import th.co.truemoney.serviceinventory.ewallet.domain.ChangePin;
import th.co.truemoney.serviceinventory.ewallet.domain.ClientCredential;
import th.co.truemoney.serviceinventory.ewallet.domain.EWalletOwnerCredential;
import th.co.truemoney.serviceinventory.ewallet.domain.OTP;
import th.co.truemoney.serviceinventory.ewallet.domain.TmnProfile;
import th.co.truemoney.serviceinventory.exception.ServiceInventoryException;

public interface TmnProfileService {

	public String login(EWalletOwnerCredential ewalletLogin, ClientCredential clientLogin) throws ServiceInventoryException;

    public TmnProfile getTruemoneyProfile(String accessTokenID) throws ServiceInventoryException;

    public BigDecimal getEwalletBalance(String accessTokenID) throws ServiceInventoryException;

    public String logout(String accessTokenID) throws ServiceInventoryException;

    public String validateEmail(Integer channelID, String email) throws ServiceInventoryException;

    public OTP createProfile(Integer channelID, TmnProfile tmnProfile) throws ServiceInventoryException;

    public TmnProfile confirmCreateProfile(Integer channelID, OTP otp) throws ServiceInventoryException;
    
    public TmnProfile updateTruemoneyProfile(String accessTokenID, TmnProfile tmnProfile) throws ServiceInventoryException;
    
    public String changePin(String accessTokenID, ChangePin changePin) throws ServiceInventoryException;
    
    public String changePassword(Integer channelID, ChangePassword changePassword, String accessTokenID) throws ServiceInventoryException;

}

