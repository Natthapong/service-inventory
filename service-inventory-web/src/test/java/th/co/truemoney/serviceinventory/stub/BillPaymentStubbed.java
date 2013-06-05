package th.co.truemoney.serviceinventory.stub;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import th.co.truemoney.serviceinventory.bill.domain.Bill;
import th.co.truemoney.serviceinventory.bill.domain.BillPaymentDraft;
import th.co.truemoney.serviceinventory.bill.domain.OutStandingBill;
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
		billPaymentInfo.setMinAmount(new BigDecimal("100"));
		billPaymentInfo.setMaxAmount(new BigDecimal("300000"));

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
		sourceOfFundFee.setMaxFeeAmount(new BigDecimal("300000"));
		sourceOfFundFees[0] = sourceOfFundFee;
		billPaymentInfo.setSourceOfFundFees(sourceOfFundFees);

		return billPaymentInfo;
	}
	
	public static Bill createOverDueBillPaymentInfo() throws Exception {

		Bill billPaymentInfo = new Bill();
		billPaymentInfo.setTarget("water");
		billPaymentInfo.setLogoURL("https://secure.truemoney-dev.com/m/tmn_webview/images/logo_bill/water@2x.png");
		billPaymentInfo.setTitleTH("ค่าน้ำประปานครหลวง");
		billPaymentInfo.setTitleEN("Water Metro");

		billPaymentInfo.setRef1TitleTH("Ref1TitleTH");
		billPaymentInfo.setRef1TitleEN("Ref1TitleEN");
		billPaymentInfo.setRef1("010004552");

		billPaymentInfo.setRef2TitleTH("Ref2TitleTH");
		billPaymentInfo.setRef2TitleEN("Ref2TitleEN");
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
		Date dueDate = new SimpleDateFormat("ddMMyy", new Locale("TH","th")).parse("310153");
		billPaymentInfo.setDueDate(dueDate);

		return billPaymentInfo;
	}

	public static ServiceInventoryWebException createFailBillPaymentInfo() {
		return new ServiceInventoryWebException("-1", "Error Description");
	}

	public static BillPaymentDraft createStubbedBillPaymentDraftInfo() {
		return new BillPaymentDraft("billID", createSuccessBillPaymentInfo(), new BigDecimal("10000"), "transactionID", BillPaymentDraft.Status.CREATED);
	}

	public static Bill createNotOverDueBillPaymentInfo() {
		Bill billPaymentInfo = new Bill();
		billPaymentInfo.setTarget("water");
		billPaymentInfo.setLogoURL("https://secure.truemoney-dev.com/m/tmn_webview/images/logo_bill/water@2x.png");
		billPaymentInfo.setTitleTH("ค่าน้ำประปานครหลวง");
		billPaymentInfo.setTitleEN("Water Metro");

		billPaymentInfo.setRef1TitleTH("Ref1TitleTH");
		billPaymentInfo.setRef1TitleEN("Ref1TitleEN");
		billPaymentInfo.setRef1("010004552");

		billPaymentInfo.setRef2TitleTH("Ref2TitleTH");
		billPaymentInfo.setRef2TitleEN("Ref2TitleEN");
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
		billPaymentInfo.setDueDate(new Date());

		return billPaymentInfo;
	}
	
	public static OutStandingBill createSuccessOutStandingBill() {
		
		OutStandingBill successOutStandingBill = new OutStandingBill();
		successOutStandingBill.setBillCode("mea");
		successOutStandingBill.setRef1("123456789");
		successOutStandingBill.setRef2("987654321");
		successOutStandingBill.setInvoiceDate(new Date());
		successOutStandingBill.setDueDate(new Date());
		successOutStandingBill.setOutStandingBalance(new BigDecimal("1520").setScale(2));
		
		return successOutStandingBill;
	}

}
