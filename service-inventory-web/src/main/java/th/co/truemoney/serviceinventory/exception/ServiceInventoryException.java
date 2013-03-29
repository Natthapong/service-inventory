package th.co.truemoney.serviceinventory.exception;


public class ServiceInventoryException extends BaseException {

	private static final long serialVersionUID = 7328535407875381185L;

	public ServiceInventoryException(String code, String description) {
		super(code, description, ServiceInventoryException.NAMESPACE);		
	}

	public ServiceInventoryException(String code, String description, String namespace) {
		super(code, description, namespace);
	}
}
