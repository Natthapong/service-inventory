package th.co.truemoney.serviceinventory.ewallet.domain;

import java.io.Serializable;

public abstract class SourceOfFund implements Serializable {

	private static final long serialVersionUID = 9157552125582604595L;
	private String sourceId;
	
	public SourceOfFund() {
		super();
	}

	public String getSourceId() {
		return sourceId;
	}

	public void setSourceId(String sourceId) {
		this.sourceId = sourceId;
	}
	
}
