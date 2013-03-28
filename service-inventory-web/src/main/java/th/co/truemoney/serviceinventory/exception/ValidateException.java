package th.co.truemoney.serviceinventory.exception;


public class ValidateException extends BaseException {

	private static final long serialVersionUID = 6974722292694736686L;

	public ValidateException(String code, String description) {
		super(code, description);
	}
}
