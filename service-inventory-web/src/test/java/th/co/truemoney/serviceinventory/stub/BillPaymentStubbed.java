package th.co.truemoney.serviceinventory.stub;

import java.math.BigDecimal;

import th.co.truemoney.serviceinventory.bill.domain.Bill;
import th.co.truemoney.serviceinventory.bill.domain.BillPaymentDraft;
import th.co.truemoney.serviceinventory.bill.domain.ServiceFeeInfo;
import th.co.truemoney.serviceinventory.bill.domain.SourceOfFund;
import th.co.truemoney.serviceinventory.exception.ServiceInventoryWebException;

public class BillPaymentStubbed {

	public static Bill createSuccessBillPaymentInfo() {

		Bill billPaymentInfo = new Bill();
		billPaymentInfo.setTarget("tcg");
		billPaymentInfo.setLogoURL("https://secure.truemoney-dev.com/m/tmn_webview/images/logo_bill/tmvh@2x.png");
		billPaymentInfo.setTitleTH("ค่าใช้บริการบริษัทในกลุ่มทรู");
		billPaymentInfo.setTitleEN("Convergence Postpay");

		billPaymentInfo.setRef1TitleTH("โทรศัพท์พื้นฐาน");
		billPaymentInfo.setRef1TitleEN("Fix Line");
		billPaymentInfo.setRef1("010004552");

		billPaymentInfo.setRef2TitleTH("รหัสลูกค้า");
		billPaymentInfo.setRef2TitleEN("Customer ID");
		billPaymentInfo.setRef2("010520120200015601");

		billPaymentInfo.setAmount(new BigDecimal("10000"));

		ServiceFeeInfo serviceFee = new ServiceFeeInfo();
		serviceFee.setFeeRate(new BigDecimal("1000"));
		serviceFee.setFeeRateType("THB");
		billPaymentInfo.setServiceFee(serviceFee);

		SourceOfFund[] sourceOfFundFees = new SourceOfFund[1];
		SourceOfFund sourceOfFundFee = new SourceOfFund();
		sourceOfFundFee.setSourceType("EW");
		sourceOfFundFee.setFeeRate(new BigDecimal("1000"));
		sourceOfFundFee.setFeeRateType("THB");
		sourceOfFundFee.setMinFeeAmount(new BigDecimal("100"));
		sourceOfFundFee.setMaxFeeAmount(new BigDecimal("2500"));
		sourceOfFundFees[0] = sourceOfFundFee;
		billPaymentInfo.setSourceOfFundFees(sourceOfFundFees);

		return billPaymentInfo;
	}

	public static ServiceInventoryWebException createFailBillPaymentInfo() {
		return new ServiceInventoryWebException("-1", "Error Description");
	}

	public static BillPaymentDraft createStubbedBillPaymentDraftInfo() {
		return new BillPaymentDraft("billID", createSuccessBillPaymentInfo(), new BigDecimal("10000"), "transactionID", BillPaymentDraft.Status.CREATED);
	}

}
