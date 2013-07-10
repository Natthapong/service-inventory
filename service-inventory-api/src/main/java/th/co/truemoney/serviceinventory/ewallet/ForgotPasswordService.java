package th.co.truemoney.serviceinventory.ewallet;

import th.co.truemoney.serviceinventory.ewallet.domain.ForgotPassword;
import th.co.truemoney.serviceinventory.ewallet.domain.ResetPassword;
import th.co.truemoney.serviceinventory.ewallet.domain.VerifyResetPassword;
import th.co.truemoney.serviceinventory.exception.ServiceInventoryException;

public interface ForgotPasswordService {
	
	public ForgotPassword createForgotPassword(Integer channelID, ForgotPassword request) 
			throws ServiceInventoryException;
	
	public VerifyResetPassword verifyResetPassword(Integer channelID, ResetPassword request) 
			throws ServiceInventoryException;
	
	public String verifyOTP(Integer channelID, VerifyResetPassword verifyResetPassword)
			throws ServiceInventoryException;
			
	public String confirmResetPassword(Integer channelID, ResetPassword request) 
			throws ServiceInventoryException;
	
	public VerifyResetPassword resendOTP(Integer channelID, String resetPasswordID)
			throws ServiceInventoryException;
	
}
