package th.co.truemoney.serviceinventory.bill.domain;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class OutStandingBill implements Serializable {
	
	private static final long serialVersionUID = -5171110220491668594L;
	
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
	
	@JsonIgnore
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
        	.append("billCode", this.getBillCode())
        	.append("ref1", this.getRef1())
        	.append("ref2", this.getRef2())
        	.append("invoiceDate", this.getInvoiceDate())
        	.append("dueDate", this.getDueDate())
        	.append("outStandingBalance", this.getOutStandingBalance())
        	.toString();
    }
}
