package th.co.truemoney.serviceinventory.ewallet;

import java.math.BigDecimal;

import th.co.truemoney.serviceinventory.ewallet.domain.DraftTransaction;
import th.co.truemoney.serviceinventory.ewallet.domain.OTP;
import th.co.truemoney.serviceinventory.ewallet.domain.TopUpOrder;
import th.co.truemoney.serviceinventory.ewallet.domain.TopUpQuote;
import th.co.truemoney.serviceinventory.ewallet.domain.Transaction;
import th.co.truemoney.serviceinventory.exception.ServiceInventoryException;

public interface TopUpService {

	public TopUpQuote verifyAndCreateTopUpQuote(String sourceOfFundID, BigDecimal amount, String accessTokenID) throws ServiceInventoryException;

	public TopUpQuote getTopUpQuoteDetails(String quoteID, String accessTokenID) throws ServiceInventoryException;

	public OTP sendOTPConfirm(String quoteID, String accessTokenID) throws ServiceInventoryException;

	public DraftTransaction.Status confirmOTP(String quoteID, OTP otp, String accessTokenID) throws ServiceInventoryException;

	public Transaction.Status getTopUpProcessingStatus(String topUpOrderID, String accessTokenID) throws ServiceInventoryException;

	public TopUpOrder getTopUpOrderResults(String topUpOrderID, String accessTokenID) throws ServiceInventoryException;

}
