package th.co.truemoney.serviceinventory.exception;


public class ValidationException extends ServiceInventoryWebException {

	private static final long serialVersionUID = 6974722292694736686L;

	public ValidationException(String code, String description) {
		super(412, code, description);
	}
}
