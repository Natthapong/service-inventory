package th.co.truemoney.serviceinventory.log.domain;

import java.io.Serializable;
import java.sql.Timestamp;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(Include.NON_NULL)
public class ActivityLog implements Serializable {

	private static final long serialVersionUID = -2777155015181057L;

	private String trackingID;
	private String truemoneyID;
	private String loginID;
	private String activityName;
	private String workerName;
	private String httpStatus;
	private String resultCode;
	private String resultNamespace;
	private String transactionID;
	private String processState;
	private String refTransID;
	private Timestamp createdDate;
	private Timestamp responseDate;
	private Integer durationTime;
	private String details;
	
	public ActivityLog(String trackingID, String truemoneyID, String loginID,
			String activityName, String workerName, String httpStatus,
			String resultCode, String resultNamespace, String transactionID,
			String processState, String refTransID, Timestamp createdDate,
			Timestamp responseDate, Integer durationTime, String details) {
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
		this.details = details;
	}
	
	public ActivityLog() {
		super();
	}

	public String getTrackingID() {
		return trackingID;
	}

	public String getTruemoneyID() {
		return truemoneyID;
	}

	public String getLoginID() {
		return loginID;
	}

	public String getActivityName() {
		return activityName;
	}

	public String getWorkerName() {
		return workerName;
	}

	public String getHttpStatus() {
		return httpStatus;
	}

	public String getResultCode() {
		return resultCode;
	}

	public String getResultNamespace() {
		return resultNamespace;
	}

	public String getTransactionID() {
		return transactionID;
	}

	public String getProcessState() {
		return processState;
	}

	public String getRefTransID() {
		return refTransID;
	}

	public Timestamp getCreatedDate() {
		return createdDate;
	}

	public Timestamp getResponseDate() {
		return responseDate;
	}

	public Integer getDurationTime() {
		return durationTime;
	}

	public String getDetails() {
		return details;
	}
	
}
