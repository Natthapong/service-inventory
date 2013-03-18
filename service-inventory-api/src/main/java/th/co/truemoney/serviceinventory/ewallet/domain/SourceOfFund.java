package th.co.truemoney.serviceinventory.ewallet.domain;

import java.io.Serializable;

public abstract class SourceOfFund implements Serializable {

	private static final long serialVersionUID = 9157552125582604595L;
	private String sourceOfFundId;
	private String sourceOfFundType;
	
	public SourceOfFund() {
		super();
	}

	public String getSourceOfFundId() {
		return sourceOfFundId;
	}

	public void setSourceOfFundId(String sourceOfFundId) {
		this.sourceOfFundId = sourceOfFundId;
	}

	public String getSourceOfFundType() {
		return sourceOfFundType;
	}

	public void setSourceOfFundType(String sourceOfFundType) {
		this.sourceOfFundType = sourceOfFundType;
	}

	
}
