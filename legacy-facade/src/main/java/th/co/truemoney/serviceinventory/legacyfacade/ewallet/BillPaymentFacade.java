package th.co.truemoney.serviceinventory.legacyfacade.ewallet;

import org.springframework.beans.factory.annotation.Autowired;

import th.co.truemoney.serviceinventory.bill.domain.BillPaymentInfo;
import th.co.truemoney.serviceinventory.billpay.domain.BillPayRequest;
import th.co.truemoney.serviceinventory.billpay.domain.BillPayResponse;
import th.co.truemoney.serviceinventory.billpay.exception.BillException;
import th.co.truemoney.serviceinventory.billpay.exception.FailResultCodeException;
import th.co.truemoney.serviceinventory.billpay.proxy.impl.BillPayProxy;
import th.co.truemoney.serviceinventory.exception.ServiceInventoryException;


public class BillPaymentFacade {
	
	@Autowired
	private BillPayProxy billPayProxy;
	
	private String appUser;	
	private String appPassword;
	private String channel;
	private String channelDetail;
	private String command;
	private String functionID;
	private String serviceNo;
	
	private String getBarcodeInformationURL;
	
	public BillPaymentInfo verify(BillPaymentInfo billpayInfo) {
		// parse billpayInfo to billpayRequest + functionID
		
		// parse obj to str xml and call billpay service
		
		// check billpayResponse result_code="0"

		return null;
	}
	
	public BillPaymentInfo getBarcodeInformation(String barcode) {		
		BillPayRequest billPayRequest = new BillPayRequest();	
		billPayRequest.setAppUser(appUser);
		billPayRequest.setAppPassword(appPassword);
		billPayRequest.setChannel(channel);
		billPayRequest.setCommand(command);
		billPayRequest.setFunctionID(functionID);
		billPayRequest.setServiceNo(serviceNo);
		billPayRequest.addParameterElement("md5", "999999999");
		billPayRequest.addParameterElement("command_action", "");
		billPayRequest.addParameterElement("channel_detail", channelDetail);
		billPayRequest.addParameterElement("fund_name", "barcode");
		billPayRequest.addParameterElement("amount", "000");
		billPayRequest.addParameterElement("currency", "THB");
		billPayRequest.addParameterElement("barcode", barcode);

		try {
			BillPayResponse billPayResponse = billPayProxy.getBarcodeInformation(billPayRequest, getBarcodeInformationURL);

			BillPaymentInfo billPaymentInfo = new BillPaymentInfo();
			billPaymentInfo.setTarget(billPayResponse.getParameterValue().get("target"));

			return billPaymentInfo;

		} catch (FailResultCodeException ex) {
			String errorNamespace = ex.getNamespace();
			if (errorNamespace.equals("TPP")) {
				throw new TPPSystemTransactionFailException(ex);
			} else if (errorNamespace.equalsIgnoreCase("UMARKET")) {
				throw new UMarketSystemTransactionFailException(ex);
			} else {
				throw new UnknownSystemTransactionFailException(ex);
			}
		}
	}
	
	public static class TPPSystemTransactionFailException extends ServiceInventoryException {

		private static final long serialVersionUID = 5955708376116171195L;

		public TPPSystemTransactionFailException(BillException ex) {
			super(500, ex.getCode(), "bill system fail with code: " + ex.getCode(), ex.getNamespace(), ex.getMessage());
		}
	}
	
	public static class UMarketSystemTransactionFailException extends ServiceInventoryException {
		private static final long serialVersionUID = -162603460464737250L;

		public UMarketSystemTransactionFailException(BillException ex) {
			super(500, ex.getCode(), "umarket system fail with code: " + ex.getCode(), ex.getNamespace(), ex.getMessage());
		}
	}

	public static class UnknownSystemTransactionFailException extends ServiceInventoryException {
		private static final long serialVersionUID = 8166679317640543498L;

		public UnknownSystemTransactionFailException(BillException ex) {
			super(500, ex.getCode(),  "unknown system fail with code: " + ex.getCode(), ex.getNamespace(), ex.getMessage());
		}
	}

}
