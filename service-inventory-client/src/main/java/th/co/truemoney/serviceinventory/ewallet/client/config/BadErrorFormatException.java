package th.co.truemoney.serviceinventory.ewallet.client.config;

public class BadErrorFormatException extends RuntimeException {

	private static final long serialVersionUID = -622102304658867237L;

	public BadErrorFormatException(String string, Exception e) {
		super(string, e);
	}

}
