package th.co.truemoney.serviceinventory.legacyfacade.builders;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;

import org.apache.commons.lang.Validate;

import th.co.truemoney.serviceinventory.engine.client.domain.services.VerifyTopUpAirtimeRequest;
import th.co.truemoney.serviceinventory.ewallet.domain.AccessToken;
import th.co.truemoney.serviceinventory.legacyfacade.ewallet.TopUpMobileFacade;
import th.co.truemoney.serviceinventory.topup.domain.TopUpMobile;
import th.co.truemoney.serviceinventory.topup.domain.TopUpMobileDraft;

public class TopUpMobileBuilder {
	
	private TopUpMobileFacade topUpMobileFacade;
	private TopUpMobileDraft topUpMobileDraft;
	private AccessToken accessToken;
	
	
	public TopUpMobileBuilder(TopUpMobileFacade topUpMobileFacade) {
		this.topUpMobileFacade = topUpMobileFacade;
	}

	public TopUpMobile verifyTopUpAirtime(TopUpMobileDraft topUpMobileDraft, AccessToken accessToken) {
		this.topUpMobileDraft = topUpMobileDraft;
		this.accessToken = accessToken;
		return verifyPayment();
	}

	public TopUpMobile verifyPayment() {
		Validate.notNull(topUpMobileDraft.getTopUpMobileInfo(), "barcode ref1 missing?");

		Validate.notNull(topUpMobileDraft.getTopUpMobileInfo().getMobileNumber(), 
				"data missing. verify topUp by mobile number?");
		Validate.notNull(topUpMobileDraft.getTopUpMobileInfo().getAmount(), 
				"data missing. how much to topUp?");

		VerifyTopUpAirtimeRequest verifyRequest = new VerifyTopUpAirtimeRequest();

		verifyRequest.setAppUser(accessToken.getClientCredential().getAppUser());
		verifyRequest.setAppPassword(accessToken.getClientCredential().getAppPassword());
		verifyRequest.setAppKey(accessToken.getClientCredential().getAppKey());

		verifyRequest.setChannel(accessToken.getClientCredential().getChannel());
		verifyRequest.setChannelDetail(accessToken.getClientCredential().getChannelDetail());

		verifyRequest.setCommandAction(topUpMobileDraft.getSelectedSourceOfFund().getSourceType());
		verifyRequest.setTmnID(accessToken.getTruemoneyID());
		verifyRequest.setControlFlag("01");

		verifyRequest.setRef1(topUpMobileDraft.getTopUpMobileInfo().getMobileNumber());
		verifyRequest.setOperator("true");

		verifyRequest.setMsisdn(topUpMobileDraft.getTopUpMobileInfo().getMobileNumber());
		verifyRequest.setAmount(convertMoney(topUpMobileDraft.getTopUpMobileInfo().getAmount()));
		
		return topUpMobileFacade.verifyTopUpMobile(verifyRequest);
	}
	
	private String convertMoney(BigDecimal value) {
		BigDecimal scaled = value.setScale(2, RoundingMode.HALF_UP);
		String formatedString = new DecimalFormat("#0.00").format(scaled);
		int lastIndex = formatedString.lastIndexOf('.');
		return formatedString.substring(0, lastIndex) + formatedString.substring(lastIndex + 1);
	}
	
}
