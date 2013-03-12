package th.co.truemoney.serviceinventory.bean;

import java.io.Serializable;

public class ErrorBean implements Serializable {

	private static final long serialVersionUID = 3740830271398781986L;
	private String errorCode;
	private String errorDescription;
	private String errorNamespace;	
	
	public ErrorBean(String errorCode, String errorDescription) {
		this.errorCode = errorCode;
		this.errorDescription = errorDescription;
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
