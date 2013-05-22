package th.co.truemoney.serviceinventory.exception;

import java.util.LinkedHashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true, value="status")
@JsonInclude(Include.NON_NULL)
public class ServiceInventoryException extends RuntimeException {

	private static final long serialVersionUID = 3961433822995791383L;

	protected Integer httpStatus;
	protected String errorCode;
	protected String errorDescription;
	protected String errorNamespace;
	protected String developerMessage;
	protected Map<String, Object> data;

	public Map<String, Object> getData() {
		return data;
	}

	public void setData(Map<String, Object> data) {
		this.data = data;
	}

	public ServiceInventoryException() {
	}

	public ServiceInventoryException(Integer httpStatus, String errorCode, String errorDescription, String errorNamespace) {
		this(httpStatus, errorCode, errorDescription, errorNamespace, null, null);
	}

	public ServiceInventoryException(Integer httpStatus, String errorCode, String errorDescription,
			String errorNamespace, String developerMessage) {
		this(httpStatus, errorCode, errorDescription, errorNamespace, developerMessage, null);
	}

	public ServiceInventoryException(
			@JsonProperty("httpStatus") Integer httpStatus,
			@JsonProperty("errorCode") String errorCode,
			@JsonProperty("errorDescription") String errorDescription,
			@JsonProperty("errorNamespace") String errorNamespace,
			@JsonProperty("developerMessage") String developerMessage,
			@JsonProperty("data") Map<String, Object> data) {
		super(errorDescription);
		this.httpStatus = httpStatus;
		this.errorCode = errorCode;
		this.errorDescription = errorDescription;
		this.errorNamespace = errorNamespace;
		this.developerMessage = developerMessage;
		this.data = data;
	}

	public Integer getHttpStatus() {
		return httpStatus;
	}

	public void setHttpStatus(Integer httpStatus) {
		this.httpStatus = httpStatus;
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

	public void putDate(String key, String value) {
		if (this.data == null) {
			this.data = new LinkedHashMap<String, Object>();
		}

		this.data.put(key, value);
	}

}
