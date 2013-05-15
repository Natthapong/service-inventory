package th.co.truemoney.serviceinventory.exception;

public class UnVerifiedFavoritePaymentException extends ServiceInventoryWebException {
	private static final long serialVersionUID = 4864620021367934505L;

	public UnVerifiedFavoritePaymentException() {
		super(400, Code.FAVORITE_PAYMENT_UNVERIFIED, "unverify with truemoneyID and favorite", ServiceInventoryWebException.NAMESPACE);
	}
}
