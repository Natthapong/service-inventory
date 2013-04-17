package th.co.truemoney.serviceinventory.ewallet.domain;

import java.io.Serializable;

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown=true)
public class ChannelInfo implements Serializable {

	private static final long serialVersionUID = 8200825869256869589L;

	@NotNull
	private	String channel;

	@NotNull
	private String channelDetail;

	@NotNull
	private Integer ewalletChannelId;

	public ChannelInfo() {
	}

	public ChannelInfo(Integer channelId, String channel, String channelDetail) {
		this.ewalletChannelId = channelId;
		this.channel = channel;
		this.channelDetail = channelDetail;
	}

	public String getChannel() {
		return channel;
	}
	public void setChannel(String channel) {
		this.channel = channel;
	}
	public String getChannelDetail() {
		return channelDetail;
	}
	public void setChannelDetail(String channelDetail) {
		this.channelDetail = channelDetail;
	}
	public Integer getEwalletChannelId() {
		return ewalletChannelId;
	}
	public void setEwalletChannelId(Integer ewalletChannelId) {
		this.ewalletChannelId = ewalletChannelId;
	}


}
