package th.co.truemoney.serviceinventory.ewallet;

import java.math.BigDecimal;

import th.co.truemoney.serviceinventory.ewallet.domain.ClientLogin;
import th.co.truemoney.serviceinventory.ewallet.domain.ChannelInfo;
import th.co.truemoney.serviceinventory.ewallet.domain.EWalletOwnerLogin;
import th.co.truemoney.serviceinventory.ewallet.domain.OTP;
import th.co.truemoney.serviceinventory.ewallet.domain.TmnProfile;
import th.co.truemoney.serviceinventory.exception.ServiceInventoryException;

public interface TmnProfileService {

	public String login(EWalletOwnerLogin ewalletLogin, ClientLogin clientLogin, ChannelInfo channelInfo) throws ServiceInventoryException;

    public TmnProfile getTruemoneyProfile(String accessTokenID) throws ServiceInventoryException;

    public BigDecimal getEwalletBalance(String accessTokenID) throws ServiceInventoryException;

    public String logout(String accessTokenID) throws ServiceInventoryException;

    public String validateEmail(Integer channelID, String email) throws ServiceInventoryException;

    public OTP createProfile(Integer channelID, TmnProfile tmnProfile) throws ServiceInventoryException;

    public TmnProfile confirmCreateProfile(Integer channelID, OTP otp) throws ServiceInventoryException;

}

