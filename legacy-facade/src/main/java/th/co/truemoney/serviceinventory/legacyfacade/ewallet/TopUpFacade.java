package th.co.truemoney.serviceinventory.legacyfacade.ewallet;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.lang.Validate;
import org.springframework.beans.factory.annotation.Autowired;

import th.co.truemoney.serviceinventory.ewallet.domain.AccessToken;
import th.co.truemoney.serviceinventory.ewallet.domain.SourceOfFund;
import th.co.truemoney.serviceinventory.ewallet.domain.TopUpConfirmationInfo;
import th.co.truemoney.serviceinventory.ewallet.proxy.ewalletsoap.EwalletSoapProxy;
import th.co.truemoney.serviceinventory.ewallet.proxy.message.AddMoneyRequest;
import th.co.truemoney.serviceinventory.ewallet.proxy.message.SecurityContext;
import th.co.truemoney.serviceinventory.ewallet.proxy.message.StandardMoneyResponse;
import th.co.truemoney.serviceinventory.ewallet.proxy.message.VerifyAddMoneyRequest;

public class TopUpFacade {

	private SimpleDateFormat df= new SimpleDateFormat("dd/MM/yyyy HH:mm");

	@Autowired
	private EwalletSoapProxy ewalletProxy;

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

		StandardMoneyResponse moneyResponse = ewalletProxy.addMoney(addMoneyRequest);

		TopUpConfirmationInfo confirmationInfo = new TopUpConfirmationInfo();
		confirmationInfo.setTransactionID(moneyResponse.getTransactionId());
		confirmationInfo.setTransactionDate(df.format(new Date()));

		return confirmationInfo;
	}

	public void setEwalletProxy(EwalletSoapProxy ewalletProxy) {
		this.ewalletProxy = ewalletProxy;
	}

	public DSLBuilder getDSL() {
		return new DSLBuilder(this);
	}

	public static class DSLBuilder {

		private TopUpFacade facade;
		private AccessToken accessToken;
		private BigDecimal amount;
		private SourceOfFund sourceOfFund;

		@Autowired(required = false)
		public DSLBuilder(TopUpFacade facade) {
			this.facade = facade;
		}

		public DSLBuilder withAmount(BigDecimal amount) {
			this.amount = amount;
			return this;
		}

		public DSLBuilder usingSourceOfFund(SourceOfFund sourceOfFund) {
			this.sourceOfFund = sourceOfFund;
			return this;
		}

		public DSLBuilder fromUser(AccessToken accessToken) {
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

}
