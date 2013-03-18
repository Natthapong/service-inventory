package th.co.truemoney.serviceinventory.ewallet;

import th.co.truemoney.serviceinventory.ewallet.domain.OTP;
import th.co.truemoney.serviceinventory.ewallet.domain.QuoteRequest;
import th.co.truemoney.serviceinventory.ewallet.domain.TopUpOrder;
import th.co.truemoney.serviceinventory.ewallet.domain.TopUpQuote;
import th.co.truemoney.serviceinventory.ewallet.domain.TopUpStatus;
import th.co.truemoney.serviceinventory.exception.ServiceInventoryException;

public interface TopUpService {

	public TopUpQuote createTopUpQuoteFromDirectDebit(String sourceOfFundId, QuoteRequest quoteRequest , String accessToken) throws ServiceInventoryException;

	public TopUpQuote getTopUpQuoteDetails(String quoteId, String accessToken) throws ServiceInventoryException;

	public TopUpOrder requestPlaceOrder(String quoteId, String accessToken) throws ServiceInventoryException;

	public TopUpOrder confirmPlaceOrder(String topUpOrderId, OTP otp, String accessToken) throws ServiceInventoryException;

	public TopUpStatus getTopUpOrderStatus(String topUpOrderId, String accessToken) throws ServiceInventoryException;

	public TopUpOrder getTopUpOrderDetails(String topUpOrderId, String accessToken) throws ServiceInventoryException;

}
