package th.co.truemoney.serviceinventory.ewallet.domain;

import java.io.Serializable;

import th.co.truemoney.serviceinventory.transfer.domain.P2PTransaction;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "@class")
@JsonSubTypes(value = { 
		@Type(value = TopUpOrder.class, name = "topUpOrder"),
		@Type(value = P2PTransaction.class, name = "p2pTransaction") 
		})
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(Include.NON_NULL)
public abstract class Transaction implements Serializable {

	private static final long serialVersionUID = 4025763625819165975L;

	protected String ID;
	protected String type;
	protected DraftTransaction draftTransaction;
	protected Status status;

	public enum Status {

		CREATED("CREATED"), VERIFIED("VERIFIED"), PROCESSING("PROCESSING"),
		SUCCESS("SUCCESS"), FAILED("FAILED");

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

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public DraftTransaction getDraftTransaction() {
		return draftTransaction;
	}

	public void setDraftTransaction(DraftTransaction draftTransaction) {
		this.draftTransaction = draftTransaction;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

}
