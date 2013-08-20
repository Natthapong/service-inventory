package th.co.truemoney.serviceinventory.ewallet.domain;

import java.io.Serializable;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import th.co.truemoney.serviceinventory.bill.domain.BillPaymentDraft;
import th.co.truemoney.serviceinventory.buy.domain.BuyProductDraft;
import th.co.truemoney.serviceinventory.topup.domain.TopUpMobileDraft;
import th.co.truemoney.serviceinventory.transfer.domain.P2PTransferDraft;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "@class")
@JsonSubTypes(value = {
	@Type(value = TopUpQuote.class, name = "topUpQuote"),
	@Type(value = P2PTransferDraft.class, name = "p2pTransferDraft"),
	@Type(value = BillPaymentDraft.class, name = "billInvoice"),
	@Type(value = TopUpMobileDraft.class, name = "topUpMobileDraft"),
	@Type(value = BuyProductDraft.class, name = "buyProductDraft")
	})
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(Include.NON_NULL)
public abstract class DraftTransaction implements Serializable {

    private static final long serialVersionUID = -6229800193403162563L;

    protected String ID;
    protected String accessTokenID;
    protected String type;
    protected Status status;
    protected String otpReferenceCode;

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

    public String getOtpReferenceCode() {
	return otpReferenceCode;
    }

    public void setOtpReferenceCode(String otpReferenceCode) {
	this.otpReferenceCode = otpReferenceCode;
    }

    @JsonIgnore
    public String toString() {
	return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
		.append("ID: ", this.getID())
		.append("accessTokenID: ", this.getAccessTokenID())
		.append("type: ", this.getType())
		.append("status: ", this.getStatus())
		.append("otpReferenceCode: ", this.getOtpReferenceCode())
		.toString();
    }

}
