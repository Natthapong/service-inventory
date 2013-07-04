package th.co.truemoney.serviceinventory.ewallet.repositories;

import th.co.truemoney.serviceinventory.ewallet.domain.ResetPassword;

public interface ForgotPasswordRepository {

	public ResetPassword findResetPassword(String resetPasswordToken);
	public void saveResetPassword(String resetPasswordToken, ResetPassword resetPassword);
	
}
