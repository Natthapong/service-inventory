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

    public String getTopUpPerformURL() {
	return host + "/service-inventory-web/v1/top-up/order/{quoteID}?accessTokenID={accessTokenID}";
    }

    public String getTopUpOrderStatusURL() {
        return host + "/service-inventory-web/v1/top-up/order/{orderID}/status?accessTokenID={accessTokenID}";
    }

    public String getTopUpOrderDetailsURL() {
        return host + "/service-inventory-web/v1/top-up/order/{orderID}?accessTokenID={accessTokenID}";
    }

    public String getValidateEmailURL() {
        return host + "/service-inventory-web/v1/ewallet/profiles/validate-email?channelID={channelID}";
    }

    public String getCreateTruemoneyProfileURL() {
        return host + "/service-inventory-web/v1/ewallet/profiles?channelID={channelID}";
    }

    public String getConfirmCreateTruemoneyProfileURL() {
        return host + "/service-inventory-web/v1/ewallet/profiles/verify-otp?channelID={channelID}";
    }

    public String getP2PCreateTransferDraftURL() {
        return host + "/service-inventory-web/v1/transfer/draft?accessTokenID={accessTokenID}";
    }

    public String getPersonalMessageURL() {
        return host + "/service-inventory-web/v1/transfer/draft/{transferDraftID}/update?personalMessage={personalMessage}&accessTokenID={accessTokenID}";
    }

    public String getP2PTransferDraftDetailsURL() {
        return host + "/service-inventory-web/v1/transfer/draft/{transferDraftID}?accessTokenID={accessTokenID}";
    }

    public String getP2PPerformTransferURL() {
	return host + "/service-inventory-web/v1/transfer/transaction/{transferDraftID}?accessTokenID={accessTokenID}";
    }

    public String getP2PTransactionStatusURL() {
        return host + "/service-inventory-web/v1/transfer/transaction/{transactionID}/status?accessTokenID={accessTokenID}";
    }

    public String getP2PTransactionInfoURL() {
        return host + "/service-inventory-web/v1/transfer/transaction/{transactionID}?accessTokenID={accessTokenID}";
    }

    public String getScanBarcodeServiceURL() {
        return host + "/service-inventory-web/v1/bill-payment/information?barcode={barcode}&accessTokenID={accessTokenID}";
    }

    public String getKeyInBillURL() {
        return host + "/service-inventory-web/v1/bill-payment/information?billCode={billCode}&ref1={ref1}&ref2={ref2}&amount={amount}&inquiry={inquiry}&favorite={isFavorited}&accessTokenID={accessTokenID}";
    }

    public String getBillOutStandingOnlineURL() {
        return host + "/service-inventory-web/v1/bill-payment/information/outstanding/{billCode}/{ref1}?ref2={ref2}&accessTokenID={accessTokenID}";
    }

    public String getUpdateBillInformationURL() {
        return host + "/service-inventory-web/v1/bill-payment/information?billID={billID}&ref1={ref1}&ref2={ref2}&amount={amount}&accessTokenID={accessTokenID}";
    }

    public String getCreateBillInvoiceURL() {
        return host + "/service-inventory-web/v1/bill-payment/invoice/{invoiceID}?accessTokenID={accessTokenID}";
    }

    public String getBillInvoiceDetailURL() {
        return host + "/service-inventory-web/v1/bill-payment/invoice/{invoiceID}?accessTokenID={accessTokenID}";
    }

    public String getBillPaymentPerformURL() {
	return host + "/service-inventory-web/v1/bill-payment/transaction/{invoiceID}?accessTokenID={accessTokenID}";
    }

    public String getBillPaymentStatusURL() {
        return host + "/service-inventory-web/v1/bill-payment/transaction/{transactionID}/status?accessTokenID={accessTokenID}";
    }

    public String getBillPaymentInfoURL() {
        return host + "/service-inventory-web/v1/bill-payment/transaction/{transactionID}?accessTokenID={accessTokenID}";
    }

    public String getVerifyTopupMobile() {
        return host + "/service-inventory-web/v1/top-up/mobile/draft?accessTokenID={accessTokenID}";
    }

    public String getPerformToppingMobileURL(){
	return host + "/service-inventory-web/v1/top-up/mobile/transaction/{topUpMobileDraftID}?accessTokenID={accessTokenID}";
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

    public String getDeleteFavoriteURL(){
        return host + "/service-inventory-web/v1/ewallet/favorites?serviceCode={serviceCode}&ref1={ref1}&accessTokenID={accessTokenID}";
    }

    public String getFavoritesURL() {
        return host + "/service-inventory-web/v1/ewallet/favorites?accessTokenID={accessTokenID}";
    }

    public String getTransactionRequestOTPURL() {
        return host + "/service-inventory-web/v1/authen/draft/{draftID}/otp?accessTokenID={accessTokenID}";
    }

    public String getTransactionVerifyOTPURL() {
        return host + "/service-inventory-web/v1/authen/draft/{draftID}/otp/{refCode}?accessTokenID={accessTokenID}";
    }

	public String getRequestForgotPasswordURL() {
		return host + "/service-inventory-web/v1/ewallet/profile/createforgotpassword?channelID={channelID}";
	}

	public String getVerifyResetPasswordURL() {
		return host + "/service-inventory-web/v1/ewallet/profile/password/verify-reset?channelID={channelID}";
	}
	
	public String getComfirmResetPasswordURL() {
		return host + "/service-inventory-web/v1/ewallet/profile/password/confirm-reset?channelID={channelID}";
	}

	public String getResendOTPResetPasswordURL() {
		return host + "/service-inventory-web/v1/ewallet/profile/password/resend-otp/{resetPasswordID}?channelID={channelID}";
	}

	public String getVerifyOTPResetPasswordURL() {
		return host + "/service-inventory-web/v1/ewallet/profile/password/verify-otp?channelID={channelID}";
	}

}
