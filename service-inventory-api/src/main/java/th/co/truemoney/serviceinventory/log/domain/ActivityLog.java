package th.co.truemoney.serviceinventory.log.domain;

import java.io.Serializable;
import java.sql.Timestamp;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(Include.NON_NULL)
public class ActivityLog implements Serializable {

	private static final long serialVersionUID = -2777155015181057L;

	private Long logID;
	private String trackingID;
	private Short workerTypeID;
	private String accessID;
	private String truemoneyID;
	private String loginID;
	private String workerName;
	private String activityName;
	private Short httpStatus;
	private String resultCode;
	private String resultNamespace;
	private String transactionID;
	private String processState;
	private String refTransID;
	private Timestamp createdDate;
	private Timestamp responseDate;
	private Integer durationTime;
	private String detailInput;
	private String detailOutput;
	
	public ActivityLog(Long logID, String trackingID, Short workerTypeID, String accessID, String truemoneyID, String loginID,
			String activityName, String workerName, Short httpStatus,
			String resultCode, String resultNamespace, String transactionID,
			String processState, String refTransID, Timestamp createdDate,
			Timestamp responseDate, Integer durationTime, String detailInput, 
			String detailOutput) {
		this();
		this.trackingID = trackingID;
		this.truemoneyID = truemoneyID;
		this.loginID = loginID;
		this.activityName = activityName;
		this.workerName = workerName;
		this.httpStatus = httpStatus;
		this.resultCode = resultCode;
		this.resultNamespace = resultNamespace;
		this.transactionID = transactionID;
		this.processState = processState;
		this.refTransID = refTransID;
		this.createdDate = createdDate;
		this.responseDate = responseDate;
		this.durationTime = durationTime;
		this.detailInput = detailInput;
		this.detailOutput = detailOutput;
	}
	
	public ActivityLog() {
		super();
	}

	public Long getLogID() {
		return logID;
	}

	public void setLogID(Long logID) {
		this.logID = logID;
	}

	public String getTrackingID() {
		return trackingID;
	}

	public void setTrackingID(String trackingID) {
		this.trackingID = trackingID;
	}

	public Short getWorkerTypeID() {
		return workerTypeID;
	}

	public void setWorkerTypeID(Short workerTypeID) {
		this.workerTypeID = workerTypeID;
	}

	public String getAccessID() {
		return accessID;
	}

	public void setAccessID(String accessID) {
		this.accessID = accessID;
	}

	public String getTruemoneyID() {
		return truemoneyID;
	}

	public void setTruemoneyID(String truemoneyID) {
		this.truemoneyID = truemoneyID;
	}

	public String getLoginID() {
		return loginID;
	}

	public void setLoginID(String loginID) {
		this.loginID = loginID;
	}

	public String getWorkerName() {
		return workerName;
	}

	public void setWorkerName(String workerName) {
		this.workerName = workerName;
	}

	public String getActivityName() {
		return activityName;
	}

	public void setActivityName(String activityName) {
		this.activityName = activityName;
	}

	public Short getHttpStatus() {
		return httpStatus;
	}

	public void setHttpStatus(Short httpStatus) {
		this.httpStatus = httpStatus;
	}

	public String getResultCode() {
		return resultCode;
	}

	public void setResultCode(String resultCode) {
		this.resultCode = resultCode;
	}

	public String getResultNamespace() {
		return resultNamespace;
	}

	public void setResultNamespace(String resultNamespace) {
		this.resultNamespace = resultNamespace;
	}

	public String getTransactionID() {
		return transactionID;
	}

	public void setTransactionID(String transactionID) {
		this.transactionID = transactionID;
	}

	public String getProcessState() {
		return processState;
	}

	public void setProcessState(String processState) {
		this.processState = processState;
	}

	public String getRefTransID() {
		return refTransID;
	}

	public void setRefTransID(String refTransID) {
		this.refTransID = refTransID;
	}

	public Timestamp getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Timestamp createdDate) {
		this.createdDate = createdDate;
	}

	public Timestamp getResponseDate() {
		return responseDate;
	}

	public void setResponseDate(Timestamp responseDate) {
		this.responseDate = responseDate;
	}

	public Integer getDurationTime() {
		return durationTime;
	}

	public void setDurationTime(Integer durationTime) {
		this.durationTime = durationTime;
	}

	public String getDetailInput() {
		return detailInput;
	}

	public void setDetailInput(String detailInput) {
		this.detailInput = detailInput;
	}

	public String getDetailOutput() {
		return detailOutput;
	}

	public void setDetailOutput(String detailOutput) {
		this.detailOutput = detailOutput;
	}

	@JsonIgnore
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
            .append("logID", this.getLogID())
            .append("trackingID", this.getTrackingID())
            .append("workerTypeID", this.getWorkerTypeID())
            .append("accessID", this.getAccessID())
            .append("truemoneyID", this.getTruemoneyID())
            .append("loginID", this.getLoginID())
            .append("workerName", this.getWorkerName())
            .append("activityName", this.getActivityName())
            .append("httpStatus", this.getHttpStatus())
            .append("resultCode", this.getResultCode())
            .append("resultNamespace", this.getResultNamespace())
            .append("transactionID", this.getTransactionID())
            .append("processState", this.getProcessState())
            .append("refTransID", this.getRefTransID())
            .append("createdDate", this.getCreatedDate())
            .append("responseDate", this.getResponseDate())
            .append("durationTime", this.getDurationTime())
            .append("detailInput", this.getDetailInput())
            .append("detailOutput", this.getDetailOutput())
            .toString();
    }
	
}
