package th.co.truemoney.serviceinventory.ewallet.domain;

public class ForgotPassword {
	private String username;
	private String idcard;
	private Integer channelID;
	
	public ForgotPassword(){
		super();
	}
	
	public ForgotPassword(String username, String idcard) {
		super();
		this.username = username;
		this.idcard = idcard;
	}
	
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getIdcard() {
		return idcard;
	}
	public void setIdcard(String idcard) {
		this.idcard = idcard;
	}
	public Integer getChannelID() {
		return channelID;
	}
	public void setChannelID(Integer channelID) {
		this.channelID = channelID;
	}
}
