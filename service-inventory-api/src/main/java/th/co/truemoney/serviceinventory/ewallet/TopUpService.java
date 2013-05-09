package th.co.truemoney.serviceinventory.ewallet;

import java.math.BigDecimal;

import th.co.truemoney.serviceinventory.ewallet.domain.OTP;
import th.co.truemoney.serviceinventory.ewallet.domain.TopUpOrder;
import th.co.truemoney.serviceinventory.ewallet.domain.TopUpQuote;
import th.co.truemoney.serviceinventory.exception.ServiceInventoryException;

public interface TopUpService {

	public TopUpQuote createAndVerifyTopUpQuote(String sourceOfFundID, BigDecimal amount, String accessTokenID) throws ServiceInventoryException;

	public TopUpQuote getTopUpQuoteDetails(String quoteID, String accessTokenID) throws ServiceInventoryException;

	public OTP requestOTP(String quoteID, String accessTokenID) throws ServiceInventoryException;

	public TopUpQuote.Status verifyOTP(String quoteID, OTP otp, String accessTokenID) throws ServiceInventoryException;

	public TopUpOrder.Status performTopUp(String quoteID, String accessTokenID) throws ServiceInventoryException;

	public TopUpOrder.Status getTopUpProcessingStatus(String topUpOrderID, String accessTokenID) throws ServiceInventoryException;

	public TopUpOrder getTopUpOrderResults(String topUpOrderID, String accessTokenID) throws ServiceInventoryException;

}
