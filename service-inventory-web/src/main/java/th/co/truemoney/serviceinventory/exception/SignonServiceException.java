package th.co.truemoney.serviceinventory.exception;


public class SignonServiceException extends BaseException {

	private static final long serialVersionUID = 7328535407875381185L;

	public SignonServiceException(String code, String description) {
		super(code, description);
	}

	public SignonServiceException(String code, String description, String namespace) {
		super(code, description, namespace);
	}
}
