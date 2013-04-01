package th.co.truemoney.serviceinventory.controller;

import javax.validation.constraints.NotNull;

public class ChannelIDParam {

	@NotNull
	private Integer channelID;

	public ChannelIDParam() {
	}
	
	public Integer getChannelID() {
		return channelID;
	}

	public void setChannelID(Integer channelID) {
		this.channelID = channelID;
	}

}
