package th.co.truemoney.serviceinventory.exception;

public abstract class ServiceInventoryException extends RuntimeException {
	
	private static final long serialVersionUID = 3961433822995791383L;
	
	private String code;
	private String description;
	private String namespace;
	
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getNamespace() {
		return namespace;
	}
	public void setNamespace(String namespace) {
		this.namespace = namespace;
	}
	
	
}
