package th.co.truemoney.serviceinventory.exception;


public class SignonServiceException extends ServiceInventoryWebException {

	private static final long serialVersionUID = 7328535407875381185L;

	public SignonServiceException(String code, String description) {
		super(code, description);
	}

}
