package th.co.truemoney.serviceinventory.bill.domain;

public class BillPaymentInfo {
	private String ID;

	public BillPaymentInfo() {
	}
	
	public BillPaymentInfo(String ID) {
		this.ID = ID;
	}

	public String getID() {
		return ID;
	}

	public void setID(String ID) {
		this.ID = ID;
	}
}
