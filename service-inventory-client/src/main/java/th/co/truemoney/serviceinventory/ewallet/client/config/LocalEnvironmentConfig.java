package th.co.truemoney.serviceinventory.ewallet.client.config;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;


@Component
@Profile("local")
public class LocalEnvironmentConfig implements EnvironmentConfig {

	@Override
	public String getLoginUrl() {
		return "http://localhost:8585/service-inventory-web/v1/ewallet/login?channelID={channelID}";
	}
	
	@Override
	public String getLogoutUrl() {
		return "http://localhost:8585/service-inventory-web/v1/ewallet/logout/{accessTokenID}";
	}

	@Override
	public String getUserDirectDebitSourceOfFundsUrl() {
		return "http://localhost:8585/service-inventory-web/v1/user/{username}/source-of-fund/direct-debits?accessTokenID={accessTokenID}";
	}

	@Override
	public String getUserProfileUrl() {
		return "http://localhost:8585/service-inventory-web/v1/ewallet/profile/{accesstokenID}";
	}
	
	@Override
	public String getBalance() {
		return "http://localhost:8585/service-inventory-web/v1/ewallet/balance/{accesstokenID}";
	}

	@Override
	public String getCreateTopUpQuoteFromDirectDebitUrl() {
		return "http://localhost:8585/service-inventory-web/v1/direct-debit/{sourceOfFundID}/quote?accessTokenID={accessTokenID}";
	}

	@Override
	public String getTopUpQuoteDetailsUrl() {
		return "http://localhost:8585/service-inventory-web/v1/top-up/quote/{quoteId}";
	}

	@Override
	public String getRequestPlaceOrder() {
		return "http://localhost:8585/service-inventory-web/v1/top-up/order/{quoteId}?accessTokenID={accessTokenID}";
	}

	@Override
	public String getConfirmPlaceOrderUrl() {
		return "http://localhost:8585/service-inventory-web/v1/top-up/order/{topUpOrderID}/confirm?accessTokenID={accessTokenID}";
	}

	@Override
	public String getTopUpOrderStatusUrl() {
		return "http://localhost:8585/service-inventory-web/v1/top-up/order/{topUpOrderID}/status?accessTokenID={accessTokenID}";
	}

	@Override
	public String getTopUpOrderDetailsUrl() {
		return "http://localhost:8585/service-inventory-web/v1/top-up/order/{topUpOrderID}?accessTokenID={accessTokenID}";
	}	
	
	@Override
	public String createDraftTransactionUrl() {
		return "http://localhost:8585/service-inventory-web/v1/transfer/draft-transaction/{draftTransactionID}?accessTokenID={accessTokenID}";
	}
	
	@Override
	public String getgetDraftTransactionInfoUrl() {
		return "http://localhost:8585/service-inventory-web/v1/transfer/draft-transaction?accessTokenID={accessTokenID}";
	}

	@Override
	public String sendOTPUrl() {
		return "http://localhost:8585/service-inventory-web/v1/transfer/draft-transaction/{draftTransactionID}/send-otp?accessTokenID={accessTokenID}";		
	}

	@Override
	public String createTransactionUrl() {
		return "http://localhost:8585/service-inventory-web/v1/transfer/transaction/{draftTransactionID}?accessTokenID={accessTokenID}";
	}

	@Override
	public String getTransactionStatusUrl() {
		return "http://localhost:8585/service-inventory-web/v1/transfer/transaction/{transactionID}/status?accessTokenID={accessTokenID}";
	}

	@Override
	public String getTransactionInfoUrl() {
		return "http://localhost:8585/service-inventory-web/v1/transfer/transaction/{transactionID}?accessTokenID={accessTokenID}";
	}

	@Override
	public String isExistRegisteredUrl() {
		return "http://localhost:8585/service-inventory-web/v1/ewallet//profiles/validate-email";
	}

	@Override
	public String createProfileUrl() {
		return "http://localhost:8585/service-inventory-web/v1/ewallet/profiles";
	}

	@Override
	public String confirmCreateProfile() {
		return "http://localhost:8585/service-inventory-web/v1/ewallet/profiles/{mobileno}/verify-otp";
	}	
}
