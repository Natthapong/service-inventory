package th.co.truemoney.serviceinventory.legacyfacade.ewallet;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.lang.Validate;
import org.springframework.beans.factory.annotation.Autowired;

import th.co.truemoney.serviceinventory.ewallet.domain.AccessToken;
import th.co.truemoney.serviceinventory.ewallet.domain.SourceOfFund;
import th.co.truemoney.serviceinventory.ewallet.domain.TopUpConfirmationInfo;
import th.co.truemoney.serviceinventory.ewallet.exception.EwalletException;
import th.co.truemoney.serviceinventory.ewallet.exception.FailResultCodeException;
import th.co.truemoney.serviceinventory.ewallet.proxy.ewalletsoap.EwalletSoapProxy;
import th.co.truemoney.serviceinventory.ewallet.proxy.message.AddMoneyRequest;
import th.co.truemoney.serviceinventory.ewallet.proxy.message.SecurityContext;
import th.co.truemoney.serviceinventory.ewallet.proxy.message.StandardBizRequest;
import th.co.truemoney.serviceinventory.ewallet.proxy.message.StandardMoneyResponse;
import th.co.truemoney.serviceinventory.ewallet.proxy.message.VerifyAddMoneyRequest;
import th.co.truemoney.serviceinventory.exception.ServiceInventoryException;

public class BalanceFacade {

	private SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm");

	@Autowired
	private EwalletSoapProxy ewalletProxy;

	public BigDecimal getCurrentBalance(AccessToken accessToken) {

		SecurityContext securityContext = new SecurityContext(accessToken.getSessionID(), accessToken.getTruemoneyID());
		StandardBizRequest standardBizRequest = new StandardBizRequest();

		standardBizRequest.setChannelId(accessToken.getChannelID());
		standardBizRequest.setSecurityContext(securityContext);

		return this.ewalletProxy.getBalance(standardBizRequest).getCurrentBalance();
	}

	public void verifyToppingUpCapability(BigDecimal amount, SourceOfFund sof, AccessToken accessToken) {

		VerifyAddMoneyRequest request = new VerifyAddMoneyRequest();

		request.setAmount(amount);
		request.setSourceId(sof.getSourceOfFundID());
		request.setSourceType(sof.getSourceOfFundType());
		request.setChannelId(accessToken.getChannelID());
		request.setSecurityContext(new SecurityContext(accessToken.getSessionID(), accessToken.getTruemoneyID()));

		ewalletProxy.verifyAddMoney(request);
	}

	public TopUpConfirmationInfo topUpMoney(BigDecimal amount, SourceOfFund sof, AccessToken accessToken) {

		AddMoneyRequest addMoneyRequest = new AddMoneyRequest();
		addMoneyRequest.setAmount(amount);
		addMoneyRequest.setChannelId(accessToken.getChannelID());
		addMoneyRequest.setSecurityContext(new SecurityContext(accessToken.getSessionID(), accessToken.getTruemoneyID()));
		addMoneyRequest.setSourceId(sof.getSourceOfFundID());
		addMoneyRequest.setSourceType(sof.getSourceOfFundType());

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
				throw new TopUpBankSystemFailException(ex);
			} else if (
				errorCode.equals("5")  ||
				errorCode.equals("6")  ||
				errorCode.equals("7")  ||
				errorCode.equals("19") ||
				errorCode.equals("27") ||
				errorCode.equals("38")) {
				throw new TopUpUMarketSystemFailException(ex);
			} else {
				throw new TopUpUnknownSystemFailException(ex);
			}
		}
	}

	public void setEwalletProxy(EwalletSoapProxy ewalletProxy) {
		this.ewalletProxy = ewalletProxy;
	}

	public TopUpBuilder setupTopUp() {
		return new TopUpBuilder(this);
	}

	public static class TopUpBuilder {

		private BalanceFacade facade;
		private AccessToken accessToken;
		private BigDecimal amount;
		private SourceOfFund sourceOfFund;

		@Autowired(required = false)
		public TopUpBuilder(BalanceFacade facade) {
			this.facade = facade;
		}

		public TopUpBuilder withAmount(BigDecimal amount) {
			this.amount = amount;
			return this;
		}

		public TopUpBuilder usingSourceOfFund(SourceOfFund sourceOfFund) {
			this.sourceOfFund = sourceOfFund;
			return this;
		}

		public TopUpBuilder fromUser(AccessToken accessToken) {
			this.accessToken = accessToken;
			return this;
		}

		public void verifyTopUp() {
			Validate.notNull(accessToken, "data missing. topup from whom?");
			Validate.notNull(amount, "data missing. how much to top up?");
			Validate.notNull(sourceOfFund, "data missing. using withc source of fund to top up?");

			facade.verifyToppingUpCapability(amount, sourceOfFund, accessToken);
		}

		public TopUpConfirmationInfo performTopUp() {
			Validate.notNull(accessToken, "data missing. topup from whom?");
			Validate.notNull(amount, "data missing. how much to top up?");
			Validate.notNull(sourceOfFund, "data missing. using withc source of fund to top up?");

			return facade.topUpMoney(amount, sourceOfFund, accessToken);
		}
	}


	public static class TopUpBankSystemFailException extends ServiceInventoryException {
		private static final long serialVersionUID = -118404790410428078L;

		public TopUpBankSystemFailException(EwalletException ex) {
			super(500, ex.getCode(), "bank system fail with code: " + ex.getCode(), ex.getNamespace(), ex.getMessage());
		}
	}

	public static class TopUpUMarketSystemFailException extends ServiceInventoryException {
		private static final long serialVersionUID = -162603460464737250L;

		public TopUpUMarketSystemFailException(EwalletException ex) {
			super(500, ex.getCode(), "umarket system fail with code: " + ex.getCode(), ex.getNamespace(), ex.getMessage());
		}
	}

	public static class TopUpUnknownSystemFailException extends ServiceInventoryException {
		private static final long serialVersionUID = 8166679317640543498L;

		public TopUpUnknownSystemFailException(EwalletException ex) {
			super(500, ex.getCode(),  "unknown system fail with code: " + ex.getCode(), ex.getNamespace(), ex.getMessage());
		}
	}

}
