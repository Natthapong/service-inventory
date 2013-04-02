package th.co.truemoney.serviceinventory.legacyfacade.ewallet;

import java.math.BigDecimal;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;

import th.co.truemoney.serviceinventory.ewallet.domain.AccessToken;
import th.co.truemoney.serviceinventory.ewallet.domain.SourceOfFund;
import th.co.truemoney.serviceinventory.ewallet.domain.TopUpConfirmationInfo;
import th.co.truemoney.serviceinventory.ewallet.domain.TopUpQuote;
import th.co.truemoney.serviceinventory.ewallet.proxy.ewalletsoap.EwalletSoapProxy;
import th.co.truemoney.serviceinventory.ewallet.proxy.message.AddMoneyRequest;
import th.co.truemoney.serviceinventory.ewallet.proxy.message.SecurityContext;
import th.co.truemoney.serviceinventory.ewallet.proxy.message.StandardMoneyResponse;
import th.co.truemoney.serviceinventory.ewallet.proxy.message.VerifyAddMoneyRequest;

public class TopUpFacade {

	java.text.SimpleDateFormat df= new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm");

	@Autowired
	private EwalletSoapProxy ewalletProxy;

	public void verifyToppingUpCapability(BigDecimal amount, SourceOfFund sof,
			AccessToken accessToken) {

		VerifyAddMoneyRequest request = new VerifyAddMoneyRequest();

		request.setAmount(amount);
		request.setSourceId(sof.getSourceOfFundID());
		request.setSourceType(sof.getSourceOfFundType());
		request.setChannelId(accessToken.getChannelID());
		request.setSecurityContext(new SecurityContext(accessToken.getSessionID(), accessToken.getTruemoneyID()));

		ewalletProxy.verifyAddMoney(request);
	}

	public TopUpConfirmationInfo topUpMoney(TopUpQuote quote, AccessToken accessToken) {

		AddMoneyRequest addMoneyRequest = new AddMoneyRequest();
		addMoneyRequest.setAmount(quote.getAmount());
		addMoneyRequest.setChannelId(accessToken.getChannelID());
		addMoneyRequest.setSecurityContext(new SecurityContext(accessToken.getSessionID(), accessToken.getTruemoneyID()));
		addMoneyRequest.setSourceId(quote.getSourceOfFund().getSourceOfFundID());
		addMoneyRequest.setSourceType(quote.getSourceOfFund().getSourceOfFundType());

		StandardMoneyResponse moneyResponse = ewalletProxy.addMoney(addMoneyRequest);

		TopUpConfirmationInfo confirmationInfo = new TopUpConfirmationInfo();
		confirmationInfo.setTransactionID(moneyResponse.getTransactionId());
		confirmationInfo.setTransactionDate(df.format(new Date()));

		return confirmationInfo;
	}
}
