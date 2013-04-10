package th.co.truemoney.serviceinventory.legacyfacade.ewallet;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;

import th.co.truemoney.serviceinventory.ewallet.domain.TopUpConfirmationInfo;
import th.co.truemoney.serviceinventory.ewallet.exception.EwalletException;
import th.co.truemoney.serviceinventory.ewallet.exception.FailResultCodeException;
import th.co.truemoney.serviceinventory.ewallet.proxy.ewalletsoap.EwalletSoapProxy;
import th.co.truemoney.serviceinventory.ewallet.proxy.message.AddMoneyRequest;
import th.co.truemoney.serviceinventory.ewallet.proxy.message.SecurityContext;
import th.co.truemoney.serviceinventory.ewallet.proxy.message.StandardBizRequest;
import th.co.truemoney.serviceinventory.ewallet.proxy.message.StandardMoneyResponse;
import th.co.truemoney.serviceinventory.ewallet.proxy.message.TransferRequest;
import th.co.truemoney.serviceinventory.ewallet.proxy.message.VerifyAddMoneyRequest;
import th.co.truemoney.serviceinventory.ewallet.proxy.message.VerifyTransferRequest;
import th.co.truemoney.serviceinventory.ewallet.proxy.message.VerifyTransferResponse;
import th.co.truemoney.serviceinventory.exception.ServiceInventoryException;
import th.co.truemoney.serviceinventory.transfer.domain.P2PTransactionConfirmationInfo;

public class BalanceFacade {

	private SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm");


	private EwalletSoapProxy ewalletProxy;

	@Autowired
	public BalanceFacade(EwalletSoapProxy ewalletProxy) {
		this.ewalletProxy = ewalletProxy;
	}

	public BigDecimal getCurrentBalance(Integer channelID, String sessionID, String tmnID) {

		SecurityContext securityContext = new SecurityContext(sessionID, tmnID);
		StandardBizRequest standardBizRequest = new StandardBizRequest();

		standardBizRequest.setChannelId(channelID);
		standardBizRequest.setSecurityContext(securityContext);

		return this.ewalletProxy.getBalance(standardBizRequest).getCurrentBalance();
	}

	public void verifyToppingUpCapability(BigDecimal amount, String sourceOfFundID, String sourceOfFundType, Integer channelID, String sessionID, String truemoneyID) {

		VerifyAddMoneyRequest request = new VerifyAddMoneyRequest();

		request.setAmount(amount);
		request.setSourceId(sourceOfFundID);
		request.setSourceType(sourceOfFundType);
		request.setChannelId(channelID);
		request.setSecurityContext(new SecurityContext(sessionID, truemoneyID));

		ewalletProxy.verifyAddMoney(request);
	}

	public TopUpConfirmationInfo topUpMoney(BigDecimal amount, String sourceOfFundID, String sourceOfFundType, Integer channelID, String sessionID, String truemoneyID) {

		AddMoneyRequest addMoneyRequest = new AddMoneyRequest();
		addMoneyRequest.setAmount(amount);
		addMoneyRequest.setChannelId(channelID);
		addMoneyRequest.setSecurityContext(new SecurityContext(sessionID, truemoneyID));
		addMoneyRequest.setSourceId(sourceOfFundID);
		addMoneyRequest.setSourceType(sourceOfFundType);

		try {

			StandardMoneyResponse moneyResponse = ewalletProxy.addMoney(addMoneyRequest);

			TopUpConfirmationInfo confirmationInfo = new TopUpConfirmationInfo();
			confirmationInfo.setTransactionID(moneyResponse.getTransactionId());
			confirmationInfo.setTransactionDate(df.format(new Date()));

			return confirmationInfo;

		} catch (FailResultCodeException ex) {

			String errorCode = ex.getCode();

			if (errorCode.equals("24003") ||
				errorCode.equals("24008") ||
				errorCode.equals("24010") ||
				errorCode.equals("25007")) {
				throw new BankSystemTransactionFailException(ex);
			} else if (
				errorCode.equals("5")  ||
				errorCode.equals("6")  ||
				errorCode.equals("7")  ||
				errorCode.equals("19") ||
				errorCode.equals("27") ||
				errorCode.equals("38")) {
				throw new UMarketSystemTransactionFailException(ex);
			} else {
				throw new UnknownSystemTransactionFailException(ex);
			}
		}
	}

	public String verifyP2PTransfer(BigDecimal amount, String targetMobileNumber, Integer channelID, String sessionID, String tmnID) {

		VerifyTransferRequest verifyRequest = new VerifyTransferRequest();
		verifyRequest.setChannelId(channelID);
		verifyRequest.setAmount(amount);
		verifyRequest.setTarget(targetMobileNumber);
		verifyRequest.setSecurityContext(new SecurityContext(sessionID, tmnID));

		VerifyTransferResponse verifyResponse = ewalletProxy.verifyTransfer(verifyRequest);

		return verifyResponse.getTargetFullname();
	}

	public P2PTransactionConfirmationInfo transferEwallet(BigDecimal amount, String targetMobileNumber, Integer channelID, String sessionID, String tmnID) {
		try {

			TransferRequest transferRequest = new TransferRequest();
			transferRequest.setAmount(amount);
			transferRequest.setChannelId(channelID);
			transferRequest.setSecurityContext(new SecurityContext(sessionID, tmnID));
			transferRequest.setTarget(targetMobileNumber);


			StandardMoneyResponse standardMoneyResponse = ewalletProxy.transfer(transferRequest);

			P2PTransactionConfirmationInfo info = new P2PTransactionConfirmationInfo();
			info.setTransactionID(standardMoneyResponse.getTransactionId());
			info.setTransactionDate(df.format(new Date()));

			return info;

		} catch (FailResultCodeException ex) {
			String errorCode = ex.getCode();

			if (errorCode.equals("5") ||
				errorCode.equals("6") ||
				errorCode.equals("7") ||
				errorCode.equals("19") ||
				errorCode.equals("27") ||
				errorCode.equals("38")) {
				throw new UMarketSystemTransactionFailException(ex);
			} else {
				throw new UnknownSystemTransactionFailException(ex);
			}
		}
	}

	public void setEwalletProxy(EwalletSoapProxy ewalletProxy) {
		this.ewalletProxy = ewalletProxy;
	}

	public static class BankSystemTransactionFailException extends ServiceInventoryException {
		private static final long serialVersionUID = -118404790410428078L;

		public BankSystemTransactionFailException(EwalletException ex) {
			super(500, ex.getCode(), "bank system fail with code: " + ex.getCode(), ex.getNamespace(), ex.getMessage());
		}
	}

	public static class UMarketSystemTransactionFailException extends ServiceInventoryException {
		private static final long serialVersionUID = -162603460464737250L;

		public UMarketSystemTransactionFailException(EwalletException ex) {
			super(500, ex.getCode(), "umarket system fail with code: " + ex.getCode(), ex.getNamespace(), ex.getMessage());
		}
	}

	public static class UnknownSystemTransactionFailException extends ServiceInventoryException {
		private static final long serialVersionUID = 8166679317640543498L;

		public UnknownSystemTransactionFailException(EwalletException ex) {
			super(500, ex.getCode(),  "unknown system fail with code: " + ex.getCode(), ex.getNamespace(), ex.getMessage());
		}
	}

}
