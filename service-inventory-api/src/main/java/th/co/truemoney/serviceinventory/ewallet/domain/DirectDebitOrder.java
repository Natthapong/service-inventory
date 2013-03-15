package th.co.truemoney.serviceinventory.ewallet.domain;

import java.io.Serializable;
import java.math.BigDecimal;

public class DirectDebitOrder implements Serializable {

	public enum DirectDebitOrderStatus {
		ORDER, PRECONFIRM, PROCESSING, SUCCESS
	}

	private static final long serialVersionUID = -4268938060053093648L;

	private String accessToken;
	private BigDecimal amount;
	private String sourceOfFundID;
	private String bankCode;
	private String otp;
	private DirectDebitOrderStatus status;
	private DirectDebitOrderResult orderResult;

	public String getAccessToken() {
		return accessToken;
	}

	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public String getSourceOfFundID() {
		return sourceOfFundID;
	}

	public void setSourceOfFundID(String sourceOfFundID) {
		this.sourceOfFundID = sourceOfFundID;
	}

	public String getBankCode() {
		return bankCode;
	}

	public void setBankCode(String bankCode) {
		this.bankCode = bankCode;
	}

	public String getOtp() {
		return otp;
	}

	public void setOtp(String otp) {
		this.otp = otp;
	}

	public DirectDebitOrderStatus getStatus() {
		return status;
	}

	public void setStatus(DirectDebitOrderStatus status) {
		this.status = status;
	}

	public DirectDebitOrderResult getOrderResult() {
		return orderResult;
	}

	public void setOrderResult(DirectDebitOrderResult orderResult) {
		this.orderResult = orderResult;
	}

}
