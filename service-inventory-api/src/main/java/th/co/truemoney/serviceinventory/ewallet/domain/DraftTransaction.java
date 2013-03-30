package th.co.truemoney.serviceinventory.ewallet.domain;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "@class")
@JsonSubTypes(value = { @Type(value = TopUpQuote.class, name = "topUpQuote") })
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(Include.NON_NULL)
public abstract class DraftTransaction implements Serializable {

	private static final long serialVersionUID = -6229800193403162563L;

	protected String ID;
	protected String accessTokenID;
	protected String type;
	protected Status status;

	public static enum Status {
		CREATED("CREATED"), OTP_SENT("SENT"), OTP_CONFIRMED("CONFIRMED");

		private String status;

		private Status(String status) {
			this.status = status;
		}

		public String getStatus() {
			return status;
		}
	}

	public String getID() {
		return ID;
	}

	public void setID(String iD) {
		ID = iD;
	}

	public String getAccessTokenID() {
		return accessTokenID;
	}

	public void setAccessTokenID(String accessTokenID) {
		this.accessTokenID = accessTokenID;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

}