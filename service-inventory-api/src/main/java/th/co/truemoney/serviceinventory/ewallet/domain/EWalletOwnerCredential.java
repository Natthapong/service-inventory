package th.co.truemoney.serviceinventory.ewallet.domain;

import java.io.Serializable;

import javax.validation.constraints.NotNull;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown=true)
public class EWalletOwnerCredential implements Serializable {

	private static final long serialVersionUID = -8973296808315132605L;

	@NotNull
	private String loginKey;

	@NotNull
	private String loginSecret;

	@NotNull
	private Integer channelId;

	public EWalletOwnerCredential() {
		super();
	}

	public EWalletOwnerCredential(String loginKey, String loginSecret, Integer channelID) {
		this.loginKey = loginKey;
		this.loginSecret = loginSecret;
		this.channelId = channelID;
	}

	public String getLoginKey() {
		return loginKey;
	}

	public void setLoginKey(String loginKey) {
		this.loginKey = loginKey;
	}

	public String getLoginSecret() {
		return loginSecret;
	}

	public void setLoginSecret(String loginSecret) {
		this.loginSecret = loginSecret;
	}

	public Integer getChannelId() {
		return channelId;
	}

	public void setChannelId(Integer channelId) {
		this.channelId = channelId;
	}

	@JsonIgnore
	public String toString() {
		return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
				.append("loginKey: ", this.getLoginKey())
				.append("loginSecret: ", this.getLoginSecret())
				.append("channelId: ", this.getChannelId())
				.toString();
	}
	
}
