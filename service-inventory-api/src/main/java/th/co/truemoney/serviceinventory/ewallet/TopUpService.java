package th.co.truemoney.serviceinventory.ewallet;

import th.co.truemoney.serviceinventory.ewallet.domain.OTP;
import th.co.truemoney.serviceinventory.ewallet.domain.QuoteRequest;
import th.co.truemoney.serviceinventory.ewallet.domain.TopUpOrder;
import th.co.truemoney.serviceinventory.ewallet.domain.TopUpQuote;
import th.co.truemoney.serviceinventory.ewallet.domain.TopUpStatus;
import th.co.truemoney.serviceinventory.exception.ServiceInventoryException;

public interface TopUpService {

	public TopUpQuote createTopUpQuoteFromDirectDebit(String sourceOfFundID, QuoteRequest quoteRequest , String accessTokenID) throws ServiceInventoryException;

	public TopUpQuote getTopUpQuoteDetails(String quoteID, String accessTokenID) throws ServiceInventoryException;

	public TopUpOrder requestPlaceOrder(String quoteID, String accessTokenID) throws ServiceInventoryException;

	public TopUpOrder confirmPlaceOrder(String topUpOrderID, OTP otp, String accessTokenID) throws ServiceInventoryException;

	public TopUpStatus getTopUpOrderStatus(String topUpOrderID, String accessTokenID) throws ServiceInventoryException;

	public TopUpOrder getTopUpOrderDetails(String topUpOrderID, String accessTokenID) throws ServiceInventoryException;

}
