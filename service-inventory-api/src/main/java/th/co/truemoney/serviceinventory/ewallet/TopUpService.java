package th.co.truemoney.serviceinventory.ewallet;

import java.math.BigDecimal;

import th.co.truemoney.serviceinventory.ewallet.domain.TopUpOrder;
import th.co.truemoney.serviceinventory.ewallet.domain.TopUpStatus;

public interface TopUpService {

	public TopUpQuote createTopUpQuoteFromDirectDebit(String sourceOfFundId, BigDecimal amount, String accessToken);

	public TopUpQuote getTopUpQuoteDetails(String quoteId, String accessToken);

	public TopUpOrder requestPlaceOrder(String quoteId, String accessToken);

	public void confirmPlaceOrder(String topUpOrderId, String otpString, String accessToken);

	public TopUpStatus getTopUpOrderStatus(String topUpOrderId, String accessToken);

	public TopUpOrder getTopUpOrderDetails(String topUpOrderId, String accessToken);
}
