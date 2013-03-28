package th.co.truemoney.serviceinventory.ewallet.client.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

public class EndPoints {

	@Autowired @Qualifier("endpoint.host")
	private String host = "https://dev.truemoney.co.th";

	public String getLoginUrl() {
		return host + "/service-inventory-web/v1/ewallet/login?channelID={channelID}";
	}

	public String getLogoutUrl() {
		return host + "/service-inventory-web/v1/ewallet/logout/{accessTokenID}";
	}

	public String getUserDirectDebitSourceOfFundsUrl() {
		return host + "/service-inventory-web/v1/user/{username}/source-of-fund/direct-debits?accessTokenID={accessTokenID}";
	}

	public String getUserProfileUrl() {
		return host + "/service-inventory-web/v1/ewallet/profile/{accesstokenID}";
	}

	public String getBalance() {
		return host + "/service-inventory-web/v1/ewallet/balance/{accesstokenID}";
	}

	public String getCreateTopUpQuoteFromDirectDebitUrl() {
		return host + "/service-inventory-web/v1/directdebit/{sourceOfFundID}/quote?accessTokenID={accessTokenID}";
	}

	public String getTopUpQuoteDetailsUrl() {
		return host + "/service-inventory-web/v1/top-up/quote/{quoteID}?accessTokenID={accessTokenID}";
	}

	public String getsendOTPConfirmUrl() {
		return host + "/service-inventory-web/v1/top-up/quote/{quoteID}/otp?accessTokenID={accessTokenID}";
	}

	public String getConfirmOTPUrl() {
		return host + "/service-inventory-web/v1/top-up/quote/{quoteID}/otp/{refCode}?accessTokenID={accessTokenID}";
	}

	public String getTopUpOrderStatusUrl() {
		return host + "/service-inventory-web/v1/top-up/order/{orderID}/status?accessTokenID={accessTokenID}";
	}

	public String getTopUpOrderDetailsUrl() {
		return host + "/service-inventory-web/v1/top-up/order/{orderID}?accessTokenID={accessTokenID}";
	}
	
	public String getValidateEmailUrl(){
		return host + "/service-inventory-web/v1/ewallet/profiles/validate-email?channelID={channelID}";
	}
	
	public String getCreateTruemoneyProfileUrl(){
		return host + "/service-inventory-web/v1/ewallet/profiles?channelID={channelID}";
	}
	
	public String getConfirmCreateTruemoneyProfileUrl(){
		return host + "/service-inventory-web/v1/ewallet/profiles/{mobileNumber}/verify-otp?channelID={channelID}";
	}
	
	public String getCreateDraftTransactionUrl(){
		return host + "/service-inventory-web/v1/transfer/draft-transaction?accessTokenID={accessTokenID}";
	}
	
	public String getDraftTransactionDetails(){
		return host + "/service-inventory-web/v1/transfer/draft-transaction/{draftTransactionID}?accessTokenID={accessTokenID}";
	}
	
	public String getSendOTPUrl(){
		return host + "/service-inventory-web/v1/transfer/draft-transaction/{draftTransactionID}/send-otp?accessTokenID={accessTokenID}";
	}
	
	public String getCreateTransactionUrl(){
		return host + "/service-inventory-web/v1/transfer/transaction/{draftTransactionID}?accessTokenID={accessTokenID}";
	}
	
	public String getTransactionStatusUrl(){
		return host + "/service-inventory-web/v1/transfer/transaction/{transactionID}/status?accessTokenID={accessTokenID}";
	}
	
	public String getTransactionInfoUrl(){
		return host + "/service-inventory-web/v1/transaction/{transactionID}?accessTokenID={accessTokenID}";
	}
}
