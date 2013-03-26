package th.co.truemoney.serviceinventory.exception;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(Include.NON_NULL)
public class ServiceInventoryException extends RuntimeException {

	private static final long serialVersionUID = 3961433822995791383L;

	private String errorCode;
	private String errorDescription;
	private String errorNamespace;
	private String developerMessage;
	private Map<String, Object> data;

	public Map<String, Object> getData() {
		return data;
	}

	public void setData(Map<String, Object> data) {
		this.data = data;
	}

	public ServiceInventoryException() {
	}

	public ServiceInventoryException(String errorCode, String errorDescription, String errorNamespace) {
		this(errorCode, errorDescription, errorNamespace, null, null);
	}

	public ServiceInventoryException(String errorCode, String errorDescription, String errorNamespace, String developerMessage) {
		this(errorCode, errorDescription, errorNamespace, developerMessage, null);
	}

	public ServiceInventoryException(@JsonProperty("errorCode") String errorCode,
			@JsonProperty("errorDescription") String errorDescription,
			@JsonProperty("errorNamespace")  String errorNamespace,
			@JsonProperty("developerMessage") String developerMessage,
			@JsonProperty("data") HashMap<String, Object> data) {
		super(errorDescription);
		this.errorCode = errorCode;
		this.errorDescription = errorDescription;
		this.errorNamespace = errorNamespace;
		this.developerMessage = developerMessage;
		this.data = data;
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

	public String getDeveloperMessage() {
		return developerMessage;
	}

	public void setDeveloperMessage(String developerMessage) {
		this.developerMessage = developerMessage;
	}

}
