package th.co.truemoney.serviceinventory.ewallet.client.config;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("dev")
public class DevEnvironmentConfig implements EnvironmentConfig {

	@Override
	public String getLoginUrl() {
		return "https://dev.truemoney.co.th/service-inventory-web/v1/ewallet/login?channelID={channelID}";
	}
	
	@Override
	public String getLogoutUrl() {
		return "https://dev.truemoney.co.th/service-inventory-web/v1/ewallet/logout/{accessTokenID}";
	}

	@Override
	public String getUserDirectDebitSourceOfFundsUrl() {
		return "https://dev.truemoney.co.th/service-inventory-web/v1/user/{username}/source-of-fund/direct-debits?accessTokenID={accessTokenID}";
	}

	@Override
	public String getUserProfileUrl() {
		return "https://dev.truemoney.co.th/service-inventory-web/v1/ewallet/profile/{accesstokenID}";
	}
	
	@Override
	public String getBalance() {
		return "https://dev.truemoney.co.th/service-inventory-web/v1/ewallet/balance/{accesstokenID}";
	}

	@Override
	public String getCreateTopUpQuoteFromDirectDebitUrl() {
		return "https://dev.truemoney.co.th/service-inventory-web/v1/direct-debit/{sourceOfFundID}/quote?accessTokenID={accessTokenID}";
	}
	
	@Override
	public String getTopUpQuoteDetailsUrl() {
		return "https://dev.truemoney.co.th/service-inventory-web/v1/top-up/quote/{quoteId}";
	}

	@Override
	public String getRequestPlaceOrder() {
		return "https://dev.truemoney.co.th/service-inventory-web/v1/top-up/order/{quoteId}?accessTokenID={accessTokenID}";
	}

	@Override
	public String getConfirmPlaceOrderUrl() {
		return "https://dev.truemoney.co.th/service-inventory-web/v1/top-up/order/{topUpOrderID}/confirm?accessTokenID={accessTokenID}";
	}

	@Override
	public String getTopUpOrderStatusUrl() {
		return "https://dev.truemoney.co.th/service-inventory-web/v1/top-up/order/{topUpOrderID}/status?accessTokenID={accessTokenID}";
	}

	@Override
	public String getTopUpOrderDetailsUrl() {
		return "https://dev.truemoney.co.th/service-inventory-web/v1/top-up/order/{topUpOrderID}?accessTokenID={accessTokenID}";
	}

	@Override
	public String createDraftTransactionUrl() {
		return "https://dev.truemoney.co.th/service-inventory-web/v1/transfer/draft-transaction?accessTokenID={accessTokenID}";
	}
	
	@Override
	public String getgetDraftTransactionInfoUrl() {
		return "https://dev.truemoney.co.th/service-inventory-web/v1/transfer/draft-transaction/{draftTransactionID}?accessTokenID={accessTokenID}";
	}

	@Override
	public String sendOTPUrl() {
		return "https://dev.truemoney.co.th/service-inventory-web/v1/transfer/draft-transaction/{draftTransactionID}/send-otp?accessTokenID={accessTokenID}";
	}
	
	@Override
	public String createTransactionUrl() {
		return "https://dev.truemoney.co.th/service-inventory-web/v1/transfer/transaction/{draftTransactionID}?accessTokenID={accessTokenID}";
	}

	@Override
	public String getTransactionStatusUrl() {
		return "https://dev.truemoney.co.th/service-inventory-web/v1/transfer/transaction/{transactionID}/status?accessTokenID={accessTokenID}";
	}

	@Override
	public String getTransactionInfoUrl() {
		return "https://dev.truemoney.co.th/service-inventory-web/v1/transfer/transaction/{transactionID}?accessTokenID={accessTokenID}";
	}
	
	@Override
	public String isExistRegisteredUrl() {
		return "https://dev.truemoney.co.th/service-inventory-web/v1/ewallet//profiles/validate-email";
	}

	@Override
	public String createProfileUrl() {
		return "https://dev.truemoney.co.th/service-inventory-web/v1/ewallet/profiles";
	}

	@Override
	public String confirmCreateProfile() {
		return "https://dev.truemoney.co.th/service-inventory-web/v1/ewallet/profiles/{mobileno}/verify-otp";
	}
}
