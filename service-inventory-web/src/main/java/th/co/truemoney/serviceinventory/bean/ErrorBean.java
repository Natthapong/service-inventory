package th.co.truemoney.serviceinventory.bean;

import java.io.Serializable;
import java.util.Map;

public class ErrorBean implements Serializable {

	private static final long serialVersionUID = 3740830271398781986L;
	private String errorCode;
	private String errorDescription;
	private String developerMessage;
	private String errorNamespace;
	private Map<String, Object> data;

	public ErrorBean(String errorCode, String errorDescription) {
		this.errorCode = errorCode;
		this.errorDescription = errorDescription;
	}

	public ErrorBean(String errorCode, String errorDescription, String namespace) {
		this.errorCode = errorCode;
		this.errorDescription = errorDescription;
		this.errorNamespace = namespace;
	}

	public ErrorBean(String errorCode, String errorDescription, String namespace, String developerMessage) {
		this.errorCode = errorCode;
		this.errorDescription = errorDescription;
		this.errorNamespace = namespace;
		this.developerMessage = developerMessage;
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
	public String getDeveloperMessage() {
		return developerMessage;
	}
	public void setDeveloperMessage(String developerMessage) {
		this.developerMessage = developerMessage;
	}
	public String getErrorNamespace() {
		return errorNamespace;
	}
	public void setErrorNamespace(String errorNamespace) {
		this.errorNamespace = errorNamespace;
	}
	public Map<String, Object> getData() {
		return data;
	}
	public void setData(Map<String, Object> data) {
		this.data = data;
	}

}
