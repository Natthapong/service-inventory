package th.co.truemoney.serviceinventory.ewallet.repositories.impl;

import java.util.LinkedHashMap;
import java.util.Map;

import th.co.truemoney.serviceinventory.ewallet.domain.ResetPassword;
import th.co.truemoney.serviceinventory.ewallet.repositories.ForgotPasswordRepository;
import th.co.truemoney.serviceinventory.exception.ResourceNotFoundException;
import th.co.truemoney.serviceinventory.exception.ServiceInventoryException;
import th.co.truemoney.serviceinventory.exception.ServiceInventoryWebException.Code;

public class ForgotPasswordMemoryRepository implements ForgotPasswordRepository {

	private Map<String, ResetPassword> map = new LinkedHashMap<String, ResetPassword>();
	
	@Override
	public ResetPassword findResetPassword(String resetPasswordToken) throws ServiceInventoryException  {
		ResetPassword resetPassword = map.get(resetPasswordToken);
		if(resetPassword == null) {
			throw new ResourceNotFoundException(Code.RESET_PASSWORD_TOKEN_NOT_FOUND, "reset password token not found.");
		}
		return resetPassword;
	}

	@Override
	public void saveResetPassword(String resetPasswordToken, ResetPassword resetPassword) {
		if (resetPassword != null) {
			map.put(resetPasswordToken, resetPassword);
		}
	}

}
