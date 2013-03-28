package th.co.truemoney.serviceinventory.ewallet.domain;

import java.io.Serializable;
import java.util.UUID;

import org.apache.commons.lang.builder.ToStringBuilder;

public class AccessToken implements Serializable {
	
	private static final long serialVersionUID = -1447834526746021542L;

	private String accessTokenID;
	private String sessionID;
	private String truemoneyID;
	private String username;
	private String mobileNumber;
	private String email;
	private Integer channelID;

	
	public AccessToken() {
		super();
	}
	
	public AccessToken(String token, String sessionID, String truemoneyID, String username, String mobileNumber, String email, Integer channelID) {
		this.accessTokenID = token;
		this.sessionID = sessionID;
		this.truemoneyID = truemoneyID;
		this.username = username;
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

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
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
			.append("username: ", this.username)
			.append("mobileNumber: ", this.mobileNumber)
			.append("email: ", this.email)
			.append("channelID: ", this.channelID)
			.toString();
	}
	
	public static AccessToken generateNewToken(String sessionID, String truemoneyID, String username, String mobileNumber, String email, Integer channelID) {
		String accessTokenID = UUID.randomUUID().toString();		
		return new AccessToken(accessTokenID, sessionID, truemoneyID, username,  mobileNumber, email, channelID);
	}
	
}
