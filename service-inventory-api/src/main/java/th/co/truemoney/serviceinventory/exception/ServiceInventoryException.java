package th.co.truemoney.serviceinventory.exception;

public abstract class ServiceInventoryException extends RuntimeException {
	
	private static final long serialVersionUID = 3961433822995791383L;
	
	public abstract String getCode();
	public abstract String getNamespace();
	public abstract String getDescription();
}
