package th.co.truemoney.serviceinventory.ewallet.domain;

import java.io.Serializable;

public class TmnProfile implements Serializable {
	
	private static final long serialVersionUID = 8341135419244797163L;
	private String fullname;
	private String mobileno;
	private String balance;
	private String type;
	private String status;
	
	public TmnProfile() {
		
	}
	
	public TmnProfile(String fullname, String balance) {
		this.fullname = fullname;
		this.balance = balance;
	}
	
	public String getFullname() {
		return fullname;
	}
	public void setFullname(String fullname) {
		this.fullname = fullname;
	}
	public String getMobileno() {
		return mobileno;
	}
	public void setMobileno(String mobileno) {
		this.mobileno = mobileno;
	}
	public String getBalance() {
		return balance;
	}
	public void setBalance(String balance) {
		this.balance = balance;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}	
	
}
