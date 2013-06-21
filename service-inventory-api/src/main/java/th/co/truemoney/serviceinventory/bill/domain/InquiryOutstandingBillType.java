package th.co.truemoney.serviceinventory.bill.domain;

public enum InquiryOutstandingBillType {
    ONLINE,OFFLINE;
	public static InquiryOutstandingBillType valueFromString(String inquiryType) {
		return "online".equals(inquiryType) ? ONLINE : OFFLINE;
	}	
}
