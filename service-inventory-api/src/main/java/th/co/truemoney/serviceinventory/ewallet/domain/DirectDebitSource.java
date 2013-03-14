package th.co.truemoney.serviceinventory.ewallet.domain;

import java.io.Serializable;

public class DirectDebitSource implements Serializable {
	
	private static final long serialVersionUID = -4919139902060027547L;
	private String sourceId;
	private String sourceType;
	private Bank bank;
		
	public DirectDebitSource() {
		super();
	}

	public DirectDebitSource(String sourceId, String sourceType) {
		this.sourceId = sourceId;
		this.sourceType = sourceType;
	}
	
	public String getSourceId() {
		return sourceId;
	}
	public void setSourceId(String sourceId) {
		this.sourceId = sourceId;
	}
	public String getSourceType() {
		return sourceType;
	}
	public void setSourceType(String sourceType) {
		this.sourceType = sourceType;
	}
	public Bank getBank() {
		return bank;
	}
	public void setBank(Bank bank) {
		this.bank = bank;
	}
	
}
