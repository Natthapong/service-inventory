package th.co.truemoney.serviceinventory.ewallet;

import th.co.truemoney.serviceinventory.ewallet.domain.OTP;
import th.co.truemoney.serviceinventory.exception.ServiceInventoryException;

public interface OTPService {

	public OTP send(String mobileno) throws ServiceInventoryException;

}
