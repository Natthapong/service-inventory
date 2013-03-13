package th.co.truemoney.serviceinventory.ewallet.domain;

import java.io.Serializable;
import java.util.UUID;

import com.google.common.base.Objects;

public class AccessToken implements Serializable {
	
	private static final long serialVersionUID = -1447834526746021542L;

	private String accessTokenId;
	private String sessionId;
	private String truemoneyId;
	private String username;
	private Integer channelId;
	
	public AccessToken() {
	}
	
	public AccessToken(String token, String sessionId, String truemoneyId, String username, Integer channelId) {
		this.accessTokenId = token;
		this.sessionId = sessionId;
		this.truemoneyId = truemoneyId;
		this.username = username;
		this.channelId = channelId;
	}
	
	public String getAccessTokenId() {
		return accessTokenId;
	}
	
	public void setAccessTokenId(String accessTokenId) {
		this.accessTokenId = accessTokenId;
	}
	
	public String getSessionId() {
		return sessionId;
	}
	
	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}
	
	public String getTruemoneyId() {
		return truemoneyId;
	}

	public void setTruemoneyId(String truemoneyId) {
		this.truemoneyId = truemoneyId;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}	
	
	public Integer getChannelId() {
		return channelId;
	}

	public void setChannelId(Integer channelId) {
		this.channelId = channelId;
	}

	@Override
	public String toString() {
		return Objects.toStringHelper(this).toString();
	}
	
	public static AccessToken generateNewToken(String sessionId, String truemoneyId, String username, Integer channelId) {
		String accessToken = UUID.randomUUID().toString();		
		return new AccessToken(accessToken, sessionId, truemoneyId, username, channelId);
	}
	
}
