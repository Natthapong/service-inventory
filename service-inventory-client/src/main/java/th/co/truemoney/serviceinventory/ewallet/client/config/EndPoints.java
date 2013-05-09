package th.co.truemoney.serviceinventory.ewallet.client.config;



import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

public class EndPoints {

	@Autowired @Qualifier("endpoint.host")
	private String host = "https://dev.truemoney.co.th";

	public String getLoginURL() {
		return host + "/service-inventory-web/v1/ewallet/login";
	}

	public String getLogoutURL() {
		return host + "/service-inventory-web/v1/ewallet/logout/{accessTokenID}";
	}

	public String getUserDirectDebitSourceOfFundsURL() {
		return host + "/service-inventory-web/v1/ewallet/profile/source-of-fund/direct-debits?accessTokenID={accessTokenID}";
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

	public String getP2PCreateTransferDraftURL(){
		return host + "/service-inventory-web/v1/transfer/draft?accessTokenID={accessTokenID}";
	}

	public String getP2PTransferDraftDetailsURL(){
		return host + "/service-inventory-web/v1/transfer/draft/{transferDraftID}?accessTokenID={accessTokenID}";
	}

	public String getP2PSubmitTransferRequestURL(){
		return host + "/service-inventory-web/v1/transfer/draft/{transferDraftID}/otp?accessTokenID={accessTokenID}";
	}

	public String getP2PVerifyAndPerformTransferURL(){
		return host + "/service-inventory-web/v1/transfer/draft/{transferDraftID}/otp/{refCode}?accessTokenID={accessTokenID}";
	}

	public String getP2PTransactionStatusURL(){
		return host + "/service-inventory-web/v1/transfer/transaction/{transactionID}/status?accessTokenID={accessTokenID}";
	}

	public String getP2PTransactionInfoURL(){
		return host + "/service-inventory-web/v1/transfer/transaction/{transactionID}?accessTokenID={accessTokenID}";
	}

	public String getScanBarcodeServiceURL(){
		return host + "/service-inventory-web/v1/bill-payment/barcode/{barcode}?accessTokenID={accessTokenID}";
	}

	public String getCreateBillInvoiceURL(){
		return host + "/service-inventory-web/v1/bill-payment/invoice/{invoiceID}?accessTokenID={accessTokenID}";
	}

	public String getBillInvoiceDetailURL(){
		return host + "/service-inventory-web/v1/bill-payment/invoice/{invoiceID}?accessTokenID={accessTokenID}";
	}

	public String getBillPaymentSendOTPConfirmURL() {
		return host + "/service-inventory-web/v1/bill-payment/invoice/{invoiceID}/otp?accessTokenID={accessTokenID}";
	}

	public String getBillPayInvoiceOTPConfirmURL(){
		return host + "/service-inventory-web/v1/bill-payment/invoice/{invoiceID}/otp/{refCode}?accessTokenID={accessTokenID}";
	}

	public String getBillPaymentStatusURL(){
		return host + "/service-inventory-web/v1/bill-payment/transaction/{transactionID}/status?accessTokenID={accessTokenID}";
	}

	public String getBillPaymentInfoURL(){
		return host + "/service-inventory-web/v1/bill-payment/transaction/{transactionID}?accessTokenID={accessTokenID}";
	}

	public String getVerifyTopupMobile() {
		return host + "/service-inventory-web/v1/top-up/mobile/draft?accessTokenID={accessTokenID}";
	}
	
	public String getSendOTPTopUpMobileURL(){
		return host + "/service-inventory-web/v1/top-up/mobile/draft/{topUpMobileDraftID}/otp?accessTokenID={accessTokenID}";
	}
	
	public String getVerifyOTPAndPerformToppingMobile(){
		return host + "/service-inventory-web/v1/top-up/mobile/draft/{topUpMobileDraftID}/otp/{refCode}?accessTokenID={accessTokenID}";
	}

	public String getTopUpMobileDraftDetailURL() {
		return host + "/service-inventory-web/v1/top-up/mobile/draft/{topUpMobileDraftID}?accessTokenID={accessTokenID}";
	}

	public String getTopUpMobileStatusURL() {
		return host + "/service-inventory-web/v1/top-up/mobile/transaction/{transactionID}/status?accessTokenID={accessTokenID}";
	}

	public String getTopUpMobileResultURL() {
		return host + "/service-inventory-web/v1/top-up/mobile/transaction/{transactionID}?accessTokenID={accessTokenID}";
	}

	public String getActivitiesListURL() {
		return host + "/service-inventory-web/v1/ewallet/activities/{accessTokenID}";
	}

	public String getActivityDetailURL() {
		return host + "/service-inventory-web/v1/ewallet/activities/{accessTokenID}/detail/{reportID}";
	}
	
	public String getAddFavoriteURL(){
		return host + "/service-inventory-web/v1/ewallet/favorites/{accessTokenID}";
	}
	
	public String getFavoritesURL() {
		return host + "/service-inventory-web/v1/ewallet/favorites?serviceType={serviceType}&accessTokenID={accessTokenID}";
	}
}
