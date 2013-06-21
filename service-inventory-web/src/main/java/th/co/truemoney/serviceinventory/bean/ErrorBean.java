package th.co.truemoney.serviceinventory.bean;

import java.io.Serializable;
import java.util.Map;

import org.apache.commons.lang.builder.ToStringBuilder;

import th.co.truemoney.serviceinventory.exception.ServiceInventoryException;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(Include.NON_NULL)
public class ErrorBean implements Serializable {

    private static final long serialVersionUID = 3740830271398781986L;
    private Integer httpStatus;
    private String errorCode;
    private String errorDescription;
    private String developerMessage;
    private String errorNamespace;
    private Map<String, Object> data;

    public ErrorBean(String errorCode, String errorDescription) {
	this.errorCode = errorCode;
	this.errorDescription = errorDescription;
    }

    public ErrorBean(Integer httpStatus, String errorCode, String errorDescription, String namespace) {
	this.httpStatus = httpStatus;
	this.errorCode = errorCode;
	this.errorDescription = errorDescription;
	this.errorNamespace = namespace;
    }

    public ErrorBean(Integer httpStatus, String errorCode, String errorDescription, String namespace, String developerMessage) {
	this.httpStatus = httpStatus;
	this.errorCode = errorCode;
	this.errorDescription = errorDescription;
	this.errorNamespace = namespace;
	this.developerMessage = developerMessage;
    }

    public ErrorBean(Integer httpStatus, String errorCode, String errorDescription, String namespace, String developerMessage, Map<String, Object> data) {
	this.httpStatus = httpStatus;
	this.errorCode = errorCode;
	this.errorDescription = errorDescription;
	this.errorNamespace = namespace;
	this.developerMessage = developerMessage;
	this.data = data;
    }

    public ErrorBean(ServiceInventoryException exception) {
	this.httpStatus = exception.getHttpStatus();
	this.errorCode = exception.getErrorCode();
	this.errorDescription = exception.getErrorDescription();
	this.errorNamespace = exception.getErrorNamespace();
	this.developerMessage = exception.getDeveloperMessage();
	this.data = exception.getData();
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

    @Override
    public String toString() {
	return new ToStringBuilder(this)
		    .append("errorCode", errorCode)
		    .append("errorDescription", errorDescription)
		    .append("errorNamesapce", errorNamespace)
		    .append("developerMessage", developerMessage)
		    .toString();
    }

}
