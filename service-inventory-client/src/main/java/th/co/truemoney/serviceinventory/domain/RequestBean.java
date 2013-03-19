package th.co.truemoney.serviceinventory.domain;

public class RequestBean {
	private String accessToken;
	private String checkSum;
	private String deviceID;
	private String deviceType;
	private String deviceVersion;
	private String channelID;
	private String clientIP;

	public RequestBean() {
	}

	public RequestBean(String accessToken, String username, String deviceID,
			String deviceType, String deviceVersion, String channelID,
			String clientIP) {
		super();
		this.accessToken = accessToken;
		this.checkSum = username;
		this.deviceID = deviceID;
		this.deviceType = deviceType;
		this.deviceVersion = deviceVersion;
		this.channelID = channelID;
		this.clientIP = clientIP;
	}

	public String getAccessToken() {
		return accessToken;
	}

	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}

	public String getCheckSum() {
		return checkSum;
	}

	public void setCheckSum(String checkSum) {
		this.checkSum = checkSum;
	}

	public String getDeviceID() {
		return deviceID;
	}

	public void setDeviceID(String deviceID) {
		this.deviceID = deviceID;
	}

	public String getDeviceType() {
		return deviceType;
	}

	public void setDeviceType(String deviceType) {
		this.deviceType = deviceType;
	}

	public String getDeviceVersion() {
		return deviceVersion;
	}

	public void setDeviceVersion(String deviceVersion) {
		this.deviceVersion = deviceVersion;
	}

	public String getChannelID() {
		return channelID;
	}

	public void setChannelID(String channelID) {
		this.channelID = channelID;
	}

	public String getClientIP() {
		return clientIP;
	}

	public void setClientIP(String clientIP) {
		this.clientIP = clientIP;
	}

}
