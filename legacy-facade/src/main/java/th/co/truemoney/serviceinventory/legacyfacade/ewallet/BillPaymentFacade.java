package th.co.truemoney.serviceinventory.legacyfacade.ewallet;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.springframework.beans.factory.annotation.Autowired;

import th.co.truemoney.serviceinventory.bill.domain.BillInfo;
import th.co.truemoney.serviceinventory.bill.domain.BillResponse;
import th.co.truemoney.serviceinventory.bill.domain.ServiceFee;
import th.co.truemoney.serviceinventory.bill.exception.BillException;
import th.co.truemoney.serviceinventory.bill.exception.FailResultCodeException;
import th.co.truemoney.serviceinventory.bill.proxy.impl.BillProxy;
import th.co.truemoney.serviceinventory.exception.ServiceInventoryException;


public class BillPaymentFacade {

	@Autowired
	private BillProxy billPayProxy;

	public BillInfo verify(BillInfo billpayInfo) {
		// parse billpayInfo to billpayRequest + functionID

		// parse obj to str xml and call billpay service

		// check billpayResponse result_code="0"

		return null;
	}

	public BillInfo getBarcodeInformation(Integer channelID, String barcode) {
		try {
			BillResponse billResponse = billPayProxy.getBarcodeInformation(channelID, barcode);

			BillInfo billInfo = new BillInfo();
			billInfo.setTarget(billResponse.getParameterValue().get("target"));
			billInfo.setLogoURL(billResponse.getParameterValue().get("logo"));
			billInfo.setTitleTH(billResponse.getParameterValue().get("title_th"));
			billInfo.setTitleEN(billResponse.getParameterValue().get("title_en"));

			billInfo.setRef1TitleTH(billResponse.getParameterValue().get("ref1_title_th"));
			billInfo.setRef1TitleEN(billResponse.getParameterValue().get("ref1_title_en"));
			billInfo.setRef1(billResponse.getParameterValue().get("ref1"));

			billInfo.setRef2TitleTH(billResponse.getParameterValue().get("ref2_title_th"));
			billInfo.setRef2TitleEN(billResponse.getParameterValue().get("ref2_titie_en"));
			billInfo.setRef2(billResponse.getParameterValue().get("ref2"));

			billInfo.setPartialPayment(billResponse.getParameterValue().get("partial_payment"));
			billInfo.setCallCenterNumber(billResponse.getParameterValue().get("call_center"));

			String amount = billResponse.getParameterValue().get("amount");
			BigDecimal decimalAmount = new BigDecimal(amount).divide(new BigDecimal("100"));
			billInfo.setAmount(decimalAmount.setScale(2, RoundingMode.HALF_UP));

			String minAmount = billResponse.getParameterValue().get("service_min_amount");
			BigDecimal decimalMinAmount = new BigDecimal(minAmount).divide(new BigDecimal("100"));
			billInfo.setMinAmount(decimalMinAmount.setScale(2, RoundingMode.HALF_UP));

			String maxAmount = billResponse.getParameterValue().get("service_max_amount");
			BigDecimal decimalMaxAmount = new BigDecimal(maxAmount).divide(new BigDecimal("100"));
			billInfo.setMaxAmount(decimalMaxAmount.setScale(2, RoundingMode.HALF_UP));

			ServiceFee serviceFee = new ServiceFee();
			serviceFee.setFeeType(billResponse.getParameterValue().get("service_fee_type"));

			BigDecimal decimalServiceFee = BigDecimal.ZERO;
			if (serviceFee.getFeeType().equals("THB")) {
				// fee type = fix
				String fee = billResponse.getParameterValue().get("service_fee");
				decimalServiceFee = new BigDecimal(fee).divide(new BigDecimal("100"));
			} else {
				// fee type = percent
				String fee = billResponse.getParameterValue().get("service_fee");
				decimalServiceFee = new BigDecimal(fee);
			}
			serviceFee.setFee(decimalServiceFee.setScale(2, RoundingMode.HALF_UP));

			String totalServiceFee = billResponse.getParameterValue().get("total_service_fee");
			BigDecimal decimalTotalServiceFee = new BigDecimal(totalServiceFee).divide(new BigDecimal("100"));
			serviceFee.setTotalFee(decimalTotalServiceFee.setScale(2, RoundingMode.HALF_UP));

			billInfo.setServiceFee(serviceFee);
			billInfo.setSourceOfFundFees(null);

			return billInfo;

		} catch (FailResultCodeException ex) {
			String errorNamespace = ex.getNamespace();
			if (errorNamespace.equals("SIENGINE")) {
				throw new SIEngineTransactionFailException(ex);
			} else if (errorNamespace.equalsIgnoreCase("UMARKET")) {
				throw new UMarketSystemTransactionFailException(ex);
			} else {
				throw new UnknownSystemTransactionFailException(ex);
			}
		}
	}

	public static class SIEngineTransactionFailException extends ServiceInventoryException {
		private static final long serialVersionUID = 5955708376116171195L;

		public SIEngineTransactionFailException(BillException ex) {
			super(500, ex.getCode(), "bill system fail with code: " + ex.getCode(), ex.getNamespace(), ex.getMessage());
		}
	}

	public static class UMarketSystemTransactionFailException extends ServiceInventoryException {
		private static final long serialVersionUID = 3748885497125818864L;

		public UMarketSystemTransactionFailException(BillException ex) {
			super(500, ex.getCode(), "umarket system fail with code: " + ex.getCode(), ex.getNamespace(), ex.getMessage());
		}
	}

	public static class UnknownSystemTransactionFailException extends ServiceInventoryException {
		private static final long serialVersionUID = 5899038317339162588L;

		public UnknownSystemTransactionFailException(BillException ex) {
			super(500, ex.getCode(),  "unknown system fail with code: " + ex.getCode(), ex.getNamespace(), ex.getMessage());
		}
	}

}
