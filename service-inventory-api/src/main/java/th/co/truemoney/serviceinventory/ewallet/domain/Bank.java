package th.co.truemoney.serviceinventory.ewallet.domain;

import java.io.Serializable;

public class Bank implements Serializable {

	private static final long serialVersionUID = -1915269026231553326L;
	private String bankCode;
	private String bankName;
	
	public Bank() {
		super();
	}

	public Bank(String bankCode, String bankName) {
		this.bankCode = bankCode;
		this.bankName = bankName;
	}
	
	public String getBankCode() {
		return bankCode;
	}
	public void setBankCode(String bankCode) {
		this.bankCode = bankCode;
	}
	public String getBankName() {
		return bankName;
	}
	public void setBankName(String bankName) {
		this.bankName = bankName;
	}
	
}
