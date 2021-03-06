package th.co.truemoney.serviceinventory.ewallet.domain;

import java.io.Serializable;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class AccessToken implements Serializable {

	private static final long serialVersionUID = -1447834526746021542L;

	private String accessTokenID;

	private String loginID;
	private String sessionID;
	private String truemoneyID;
	private Integer channelID;

	private String mobileNumber;
	private String email;

	private ClientCredential clientCredential;

	public AccessToken() {
	}

	public AccessToken(String token) {
		this.accessTokenID = token;
	}

	public AccessToken(String token, String loginID, String sessionID, String tmnID, Integer channelID) {
		this(token, loginID, sessionID, tmnID, null, null, channelID);
	}

	public AccessToken(String token, String loginID,
			String sessionID, String truemoneyID,
			String mobileNumber, String email,
			Integer channelID) {

		this.accessTokenID = token;
		this.sessionID = sessionID;
		this.truemoneyID = truemoneyID;
		this.mobileNumber = mobileNumber;
		this.email = email;
		this.channelID = channelID;
	}

	public String getAccessTokenID() {
		return accessTokenID;
	}

	public void setAccessTokenID(String accessTokenID) {
		this.accessTokenID = accessTokenID;
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

	public Integer getChannelID() {
		return channelID;
	}

	public void setChannelID(Integer channelID) {
		this.channelID = channelID;
	}

	public ClientCredential getClientCredential() {
		return clientCredential;
	}

	public void setClientCredential(ClientCredential clientCredential) {
		this.clientCredential = clientCredential;
	}

	public String getMobileNumber() {
		return mobileNumber;
	}

	public void setMobileNumber(String mobileNumber) {
		this.mobileNumber = mobileNumber;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getLoginID() {
		return loginID;
	}

	public void setLoginID(String loginID) {
		this.loginID = loginID;
	}

	@JsonIgnore
	public String toString() {
		return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
			.append("accessTokenID: ", this.accessTokenID)
			.append("sessionID: ", this.sessionID)
			.append("truemoneyID: ", this.truemoneyID)
			.append("mobileNumber: ", this.mobileNumber)
			.append("email: ", this.email)
			.append("channelID: ", this.channelID)
			.toString();
	}
}
