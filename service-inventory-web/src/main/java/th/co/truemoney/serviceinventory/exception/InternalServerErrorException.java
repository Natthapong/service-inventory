package th.co.truemoney.serviceinventory.exception;

public class InternalServerErrorException extends ServiceInventoryWebException {

	private static final long serialVersionUID = 2377935096616446162L;

	public InternalServerErrorException(String code, String description, Exception ex) {
		super(503, code, description, ex.getMessage());
	}

}
