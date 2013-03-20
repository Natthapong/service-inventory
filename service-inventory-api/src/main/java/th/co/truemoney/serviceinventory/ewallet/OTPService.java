package th.co.truemoney.serviceinventory.ewallet;

import th.co.truemoney.serviceinventory.exception.ServiceInventoryException;

public interface OTPService {
	
	public String send(String mobileno) throws ServiceInventoryException;
	public String getOTPString(String mobileno) throws ServiceInventoryException;
	
}
