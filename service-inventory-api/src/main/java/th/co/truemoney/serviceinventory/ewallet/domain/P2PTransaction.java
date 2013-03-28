package th.co.truemoney.serviceinventory.ewallet.domain;

import java.io.Serializable;
import java.math.BigDecimal;

public class P2PTransaction implements Serializable {

	private static final long serialVersionUID = -3546197537668299129L;
	private String mobileNumber;	
	private BigDecimal amount;
	private String ID;
	private String accessTokenID;
	private String fullname;
	private String otpReferenceCode;
	private P2PTransactionStatus status = P2PTransactionStatus.AWAITING_CONFIRM;
	private P2PTransactionConfirmationInfo confirmationInfo;
	
	public P2PTransaction(P2PDraftTransaction p2pDraftTransaction) {

	}
	public String getMobileNumber() {
		return mobileNumber;
	}
	public void setMobileNumber(String mobileNumber) {
		this.mobileNumber = mobileNumber;
	}
	public BigDecimal getAmount() {
		return amount;
	}
	public void setAmount(BigDecimal amount) {
		this.amount = amount;
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
	public String getFullname() {
		return fullname;
	}
	public void setFullname(String fullname) {
		this.fullname = fullname;
	}
	public String getOtpReferenceCode() {
		return otpReferenceCode;
	}
	public void setOtpReferenceCode(String otpReferenceCode) {
		this.otpReferenceCode = otpReferenceCode;
	}
	public P2PTransactionStatus getStatus() {
		return status;
	}
	public void setStatus(P2PTransactionStatus status) {
		this.status = status;
	}
	public P2PTransactionConfirmationInfo getConfirmationInfo() {
		return confirmationInfo;
	}
	public void setConfirmationInfo(P2PTransactionConfirmationInfo confirmationInfo) {
		this.confirmationInfo = confirmationInfo;
	}
	public AccessToken getDraftTransaction() {
		// TODO Auto-generated method stub
		return null;
	}
	
}
