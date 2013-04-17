package th.co.truemoney.serviceinventory.ewallet.domain;

import java.io.Serializable;

import org.apache.commons.lang.builder.ToStringBuilder;

public class AccessToken implements Serializable {

	private static final long serialVersionUID = -1447834526746021542L;

	private String accessTokenID;
	private String sessionID;
	private String truemoneyID;
	private String mobileNumber;
	private String email;
	private Integer channelID;

	public AccessToken() {
	}

	public AccessToken(String token) {
		this.accessTokenID = token;
	}

	public AccessToken(String token, String sessionID, String tmnID, Integer channelID) {
		this(token, sessionID, tmnID, null, null, channelID);
	}

	public AccessToken(String token,
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

	@Override
	public String toString() {
		return new ToStringBuilder(this)
			.append("accessTokenID: ", this.accessTokenID)
			.append("sessionID: ", this.sessionID)
			.append("truemoneyID: ", this.truemoneyID)
			.append("mobileNumber: ", this.mobileNumber)
			.append("email: ", this.email)
			.append("channelID: ", this.channelID)
			.toString();
	}
}
