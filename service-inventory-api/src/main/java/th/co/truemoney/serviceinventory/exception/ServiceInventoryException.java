package th.co.truemoney.serviceinventory.exception;

import java.util.HashMap;

@SuppressWarnings("rawtypes")
public class ServiceInventoryException extends RuntimeException {
	
	private static final long serialVersionUID = 3961433822995791383L;
	
	private String errorCode;
	private String errorDescription;
	private String errorNamespace;
	private HashMap data; 
	
	public HashMap getData() {
		return data;
	}

	public void setData(HashMap data) {
		this.data = data;
	}

	public ServiceInventoryException() {
	}
	
	public ServiceInventoryException(String errorCode, String errorDescription, String errorNamespace) {
		this.errorCode = errorCode;
		this.errorDescription = errorDescription;
		this.errorNamespace = errorNamespace;
	}

	public String getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}

	public String getErrorDescription() {
		return errorDescription;
	}

	public void setErrorDescription(String errorDescription) {
		this.errorDescription = errorDescription;
	}

	public String getErrorNamespace() {
		return errorNamespace;
	}

	public void setErrorNamespace(String errorNamespace) {
		this.errorNamespace = errorNamespace;
	}
	
	
	
	
}
