package th.co.truemoney.serviceinventory.stub;

import java.math.BigDecimal;

import th.co.truemoney.serviceinventory.bill.domain.BillInfo;
import th.co.truemoney.serviceinventory.bill.domain.ServiceFee;
import th.co.truemoney.serviceinventory.bill.domain.SourceOfFundFee;
import th.co.truemoney.serviceinventory.exception.ServiceInventoryWebException;

public class BillPaymentStubbed {

	public static BillInfo createSuccessBillPaymentInfo() {
		
		BillInfo billPaymentInfo = new BillInfo();
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
		
		ServiceFee serviceFee = new ServiceFee();
		serviceFee.setFee(new BigDecimal("1000"));
		serviceFee.setFeeType("THB");
		serviceFee.setTotalFee(new BigDecimal("1000"));
		serviceFee.setMinFeeAmount(new BigDecimal("100"));
		serviceFee.setMaxFeeAmount(new BigDecimal("2500"));		
		billPaymentInfo.setServiceFee(serviceFee);
		
		SourceOfFundFee[] sourceOfFundFees = new SourceOfFundFee[1];
		SourceOfFundFee sourceOfFundFee = new SourceOfFundFee();
		sourceOfFundFee.setSourceType("EW");
		sourceOfFundFee.setFee(new BigDecimal("1000"));
		sourceOfFundFee.setFeeType("THB");
		sourceOfFundFee.setTotalFee(new BigDecimal("1000"));
		sourceOfFundFee.setMinFeeAmount(new BigDecimal("100"));
		sourceOfFundFee.setMaxFeeAmount(new BigDecimal("2500"));
		sourceOfFundFees[0] = sourceOfFundFee;		
		billPaymentInfo.setSourceOfFundFees(sourceOfFundFees);
		
		return billPaymentInfo;
	}

	public static ServiceInventoryWebException createFailBillPaymentInfo() {
		return new ServiceInventoryWebException("-1", "Error Description");
	}

}
