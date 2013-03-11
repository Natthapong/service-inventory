package th.co.truemoney.serviceinventory.ewallet.domain;

public class TmnProfile {
	
	private String sessionID;
	private String truemoneyID;
	private String fullname;
	private String ewalletBalance;
	private String mobileno;
	
	public TmnProfile(String sessionID, String truemoneyID) {
		this.sessionID = sessionID;
		this.truemoneyID = truemoneyID;
	}
	
	public String getSessionID() {
		return sessionID;
	}
	public void setSessionID(String sessionID) {
		this.sessionID = sessionID;
	}
	public String getTruemoneyID() {
		return truemoneyID;
	}
	public void setTruemoneyID(String truemoneyID) {
		this.truemoneyID = truemoneyID;
	}
	public String getFullname() {
		return fullname;
	}
	public void setFullname(String fullname) {
		this.fullname = fullname;
	}
	public String getEwalletBalance() {
		return ewalletBalance;
	}
	public void setEwalletBalance(String ewalletBalance) {
		this.ewalletBalance = ewalletBalance;
	}
	public String getMobileno() {
		return mobileno;
	}
	public void setMobileno(String mobileno) {
		this.mobileno = mobileno;
	}
			
}
