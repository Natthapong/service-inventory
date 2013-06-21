package th.co.truemoney.serviceinventory.exception;


public class OTPAlreadyConfirmedException extends ServiceInventoryWebException {

	private static final long serialVersionUID = 1L;

	public OTPAlreadyConfirmedException() {
		super(400, Code.OWNER_ALREADY_CONFIRMED, "OTP already confirmed", ServiceInventoryWebException.NAMESPACE);
	}
}
