package th.co.truemoney.serviceinventory.ewallet.domain;

import java.io.Serializable;
import java.math.BigDecimal;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TmnProfile implements Serializable {
	
	private static final long serialVersionUID = 8341135419244797163L;
	private String email;
	private String password;
	private String fullname;
	private String thaiID;
	private String mobileNumber;
	private BigDecimal balance;
	private String imageURL;
	private Boolean hasPassword;
	private Boolean hasPin;
	private String type;
	private Integer status;
	
	public TmnProfile() {
		super();
	}
	
	public TmnProfile(String fullname, BigDecimal balance) {
		this.fullname = fullname;
		this.balance = balance;
	}

	public TmnProfile(String email, String password, String fullname,
			String thaiID, String mobileNumber, BigDecimal balance,
			String type, Integer status) {
		super();
		this.email = email;
		this.password = password;
		this.fullname = fullname;
		this.thaiID = thaiID;
		this.mobileNumber = mobileNumber;
		this.balance = balance;
		this.type = type;
		this.status = status;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getFullname() {
		return fullname;
	}

	public void setFullname(String fullname) {
		this.fullname = fullname;
	}

	public String getThaiID() {
		return thaiID;
	}

	public void setThaiID(String thaiID) {
		this.thaiID = thaiID;
	}

	public String getMobileNumber() {
		return mobileNumber;
	}

	public void setMobileNumber(String mobileNumber) {
		this.mobileNumber = mobileNumber;
	}

	public BigDecimal getBalance() {
		return balance;
	}

	public void setBalance(BigDecimal balance) {
		this.balance = balance;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}
		
	public String getImageURL() {
		return imageURL;
	}

	public void setImageURL(String imageURL) {
		this.imageURL = imageURL;
	}

	public Boolean getHasPassword() {
		return hasPassword;
	}

	public void setHasPassword(Boolean hasPassword) {
		this.hasPassword = hasPassword;
	}

	public Boolean getHasPin() {
		return hasPin;
	}

	public void setHasPin(Boolean hasPin) {
		this.hasPin = hasPin;
	}

	@JsonIgnore
	public String toString() {
		return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
				.append("email: ", this.getEmail())
				.append("mobileNumber: ", this.getMobileNumber())
				.append("balance: ", this.getBalance())
				.append("type: ", this.getType())
				.append("status: ", this.getStatus())
				.toString();
	}
	
}
