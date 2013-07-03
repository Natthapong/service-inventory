package th.co.truemoney.serviceinventory.ewallet;

import th.co.truemoney.serviceinventory.ewallet.domain.ForgotPassword;
import th.co.truemoney.serviceinventory.exception.ServiceInventoryException;

public interface ForgotPasswordService {
	
	public ForgotPassword requestForgotPassword(ForgotPassword request) 
			throws ServiceInventoryException;
			
}
