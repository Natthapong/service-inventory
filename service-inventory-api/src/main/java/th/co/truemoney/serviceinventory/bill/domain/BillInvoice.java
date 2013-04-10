package th.co.truemoney.serviceinventory.bill.domain;

import th.co.truemoney.serviceinventory.ewallet.domain.DraftTransaction;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(Include.NON_NULL)
public class BillInvoice extends DraftTransaction {

	private static final long serialVersionUID = 2049045334050859727L;
	
	private static final String DRAFT_TYPE = "billInvoice";

	private BillPaymentInfo billPaymentInfo;
	private String otpReferenceCode;

	public BillInvoice() {
		type = DRAFT_TYPE;
	}
	
	public BillInvoice(BillPaymentInfo billPaymentInfo) {
		type = DRAFT_TYPE;
		this.billPaymentInfo = billPaymentInfo;
	}

	public BillPaymentInfo getBillPaymentInfo() {
		return billPaymentInfo;
	}

	public void setBillPaymentInfo(BillPaymentInfo billPaymentInfo) {
		this.billPaymentInfo = billPaymentInfo;
	}

	public String getOtpReferenceCode() {
		return otpReferenceCode;
	}

	public void setOtpReferenceCode(String otpReferenceCode) {
		this.otpReferenceCode = otpReferenceCode;
	}
	
}
