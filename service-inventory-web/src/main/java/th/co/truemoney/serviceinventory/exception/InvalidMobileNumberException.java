package th.co.truemoney.serviceinventory.exception;

public class InvalidMobileNumberException extends ServiceInventoryWebException {

	/**
	 * Generated Serial Version ID
	 */
	private static final long serialVersionUID = -4422972651181293392L;

	public InvalidMobileNumberException(String description) {
		super(ServiceInventoryWebException.Code.INVALID_MOBILE_NUMBER, description);
	}

}
