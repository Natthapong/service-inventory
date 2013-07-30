package th.co.truemoney.serviceinventory.exception;


public class AppleUserConfigException extends ServiceInventoryWebException {

	private static final long serialVersionUID = 1L;

	public AppleUserConfigException() {
		super(400, Code.APPLE_USER_ERROR, "Apple user config error", ServiceInventoryWebException.NAMESPACE);
	}
}
