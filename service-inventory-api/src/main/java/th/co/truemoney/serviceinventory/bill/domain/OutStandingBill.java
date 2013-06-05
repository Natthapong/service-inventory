package th.co.truemoney.serviceinventory.bill.domain;

import java.math.BigDecimal;
import java.util.Date;

public class OutStandingBill {

	private String billCode;
	private String ref1;
	private String ref2;
	private Date invoiceDate;
	private Date dueDate;
	private BigDecimal outStandingBalance;
	
	public String getBillCode() {
		return billCode;
	}
	public void setBillCode(String billCode) {
		this.billCode = billCode;
	}
	public String getRef1() {
		return ref1;
	}
	public void setRef1(String ref1) {
		this.ref1 = ref1;
	}
	public String getRef2() {
		return ref2;
	}
	public void setRef2(String ref2) {
		this.ref2 = ref2;
	}
	public Date getInvoiceDate() {
		return invoiceDate;
	}
	public void setInvoiceDate(Date invoiceDate) {
		this.invoiceDate = invoiceDate;
	}
	public Date getDueDate() {
		return dueDate;
	}
	public void setDueDate(Date dueDate) {
		this.dueDate = dueDate;
	}
	public BigDecimal getOutStandingBalance() {
		return outStandingBalance;
	}
	public void setOutStandingBalance(BigDecimal outStandingBalance) {
		this.outStandingBalance = outStandingBalance;
	}
}
