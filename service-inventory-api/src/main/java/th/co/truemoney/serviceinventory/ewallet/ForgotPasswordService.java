package th.co.truemoney.serviceinventory.ewallet;

import th.co.truemoney.serviceinventory.ewallet.domain.ForgotPassword;
import th.co.truemoney.serviceinventory.ewallet.domain.ResetPassword;
import th.co.truemoney.serviceinventory.ewallet.domain.VerifyResetPassword;
import th.co.truemoney.serviceinventory.exception.ServiceInventoryException;

public interface ForgotPasswordService {
	
	public ForgotPassword requestForgotPassword(ForgotPassword request) 
			throws ServiceInventoryException;
	
	public VerifyResetPassword verifyResetPassword(Integer channelID, ResetPassword request) 
			throws ServiceInventoryException;
			
	public String confirmResetPassword(Integer channelID, VerifyResetPassword verifyResetPassword) 
			throws ServiceInventoryException;
	
}
