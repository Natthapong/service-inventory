package th.co.truemoney.serviceinventory.exception;

public class ResourceNotFoundException extends ServiceInventoryWebException {

	private static final long serialVersionUID = 96671233071850217L;

	public ResourceNotFoundException(String code, String description) {
		super(404, code, description);
	}

}
