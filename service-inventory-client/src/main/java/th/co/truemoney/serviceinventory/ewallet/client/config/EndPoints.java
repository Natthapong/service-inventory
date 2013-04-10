package th.co.truemoney.serviceinventory.ewallet.client.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

public class EndPoints {

	@Autowired @Qualifier("endpoint.host")
	private String host = "https://dev.truemoney.co.th";

	public String getLoginURL() {
		return host + "/service-inventory-web/v1/ewallet/login?channelID={channelID}";
	}

	public String getLogoutURL() {
		return host + "/service-inventory-web/v1/ewallet/logout/{accessTokenID}";
	}

	public String getUserDirectDebitSourceOfFundsURL() {
		return host + "/service-inventory-web/v1/ewallet/profile/{username}/source-of-fund/direct-debits?accessTokenID={accessTokenID}";
	}

	public String getUserProfileURL() {
		return host + "/service-inventory-web/v1/ewallet/profile/{accesstokenID}";
	}

	public String getBalanceURL() {
		return host + "/service-inventory-web/v1/ewallet/balance/{accesstokenID}";
	}

	public String getCreateTopUpQuoteFromDirectDebitURL() {
		return host + "/service-inventory-web/v1/directdebit/{sourceOfFundID}/quote?accessTokenID={accessTokenID}";
	}

	public String getTopUpQuoteDetailsURL() {
		return host + "/service-inventory-web/v1/top-up/quote/{quoteID}?accessTokenID={accessTokenID}";
	}

	public String getTopUpSendOTPConfirmURL() {
		return host + "/service-inventory-web/v1/top-up/quote/{quoteID}/otp?accessTokenID={accessTokenID}";
	}

	public String getTopUpConfirmOTPURL() {
		return host + "/service-inventory-web/v1/top-up/quote/{quoteID}/otp/{refCode}?accessTokenID={accessTokenID}";
	}

	public String getTopUpOrderStatusURL() {
		return host + "/service-inventory-web/v1/top-up/order/{orderID}/status?accessTokenID={accessTokenID}";
	}

	public String getTopUpOrderDetailsURL() {
		return host + "/service-inventory-web/v1/top-up/order/{orderID}?accessTokenID={accessTokenID}";
	}

	public String getValidateEmailURL(){
		return host + "/service-inventory-web/v1/ewallet/profiles/validate-email?channelID={channelID}";
	}

	public String getCreateTruemoneyProfileURL(){
		return host + "/service-inventory-web/v1/ewallet/profiles?channelID={channelID}";
	}

	public String getConfirmCreateTruemoneyProfileURL(){
		return host + "/service-inventory-web/v1/ewallet/profiles/verify-otp?channelID={channelID}";
	}

	public String getP2PCreateDraftTransactionURL(){
		return host + "/service-inventory-web/v1/transfer/draft-transaction?accessTokenID={accessTokenID}";
	}

	public String getP2PDraftTransactionDetailsURL(){
		return host + "/service-inventory-web/v1/transfer/draft-transaction/{draftTransactionID}?accessTokenID={accessTokenID}";
	}

	public String getP2PSendOTPURL(){
		return host + "/service-inventory-web/v1/transfer/draft-transaction/{draftTransactionID}/otp?accessTokenID={accessTokenID}";
	}

	public String getP2PConfirmDraftTransactionURL(){
		return host + "/service-inventory-web/v1/transfer/draft-transaction/{draftTransactionID}/otp/{refCode}?accessTokenID={accessTokenID}";
	}

	public String getP2PTransactionStatusURL(){
		return host + "/service-inventory-web/v1/transfer/transaction/{transactionID}/status?accessTokenID={accessTokenID}";
	}

	public String getP2PTransactionInfoURL(){
		return host + "/service-inventory-web/v1/transfer/transaction/{transactionID}?accessTokenID={accessTokenID}";
	}

	public String getCreateBillInvoiceUrl(){
		return host + "/service-inventory-web/v1/bill-payment/invoice?accessTokenID={accessTokenID}";
	}

	public String getBillPayInvoiceOTPConfirmURL(){
		return host + "/service-inventory-web/v1/bill-payment/invoice/{invoiceID}/otp/{refCode}?accessTokenID={accessTokenID}";
	}
}
