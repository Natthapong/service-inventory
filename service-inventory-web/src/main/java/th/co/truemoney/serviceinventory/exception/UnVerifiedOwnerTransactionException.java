package th.co.truemoney.serviceinventory.exception;

public class UnVerifiedOwnerTransactionException extends ServiceInventoryWebException {

	private static final long serialVersionUID = -3866846068356120287L;

	public UnVerifiedOwnerTransactionException() {
		super(400, Code.OWNER_UNVERIFIED, "owner needs to confirm OTP first", ServiceInventoryWebException.NAMESPACE);
	}

}
