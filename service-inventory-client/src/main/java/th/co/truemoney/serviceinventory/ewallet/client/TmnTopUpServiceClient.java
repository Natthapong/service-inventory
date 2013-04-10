package th.co.truemoney.serviceinventory.ewallet.client;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import th.co.truemoney.serviceinventory.ewallet.TopUpService;
import th.co.truemoney.serviceinventory.ewallet.client.config.EndPoints;
import th.co.truemoney.serviceinventory.ewallet.domain.DraftTransaction;
import th.co.truemoney.serviceinventory.ewallet.domain.OTP;
import th.co.truemoney.serviceinventory.ewallet.domain.TopUpOrder;
import th.co.truemoney.serviceinventory.ewallet.domain.TopUpQuote;
import th.co.truemoney.serviceinventory.ewallet.domain.Transaction;
import th.co.truemoney.serviceinventory.exception.ServiceInventoryException;

@Service
public class TmnTopUpServiceClient implements TopUpService {

	@Autowired
	RestTemplate restTemplate;

	@Autowired
	private EndPoints endPoints;

	@Autowired
	private HttpHeaders headers;

	@Override
	public TopUpQuote verifyAndCreateTopUpQuote(String sourceOfFundID, BigDecimal amount, String accessTokenID) {

		TopUpQuote quote = new TopUpQuote(amount);

		HttpEntity<TopUpQuote> requestEntity = new HttpEntity<TopUpQuote>(quote, headers);

		ResponseEntity<TopUpQuote> responseEntity = restTemplate.exchange(
				endPoints.getCreateTopUpQuoteFromDirectDebitURL(), HttpMethod.POST,
				requestEntity, TopUpQuote.class,
				sourceOfFundID, accessTokenID);

		return responseEntity.getBody();
	}

	@Override
	public TopUpQuote getTopUpQuoteDetails(String quoteID, String accessTokenID) {

		HttpEntity<TopUpQuote> requestEntity = new HttpEntity<TopUpQuote>(headers);

		ResponseEntity<TopUpQuote> responseEntity = restTemplate.exchange(
				endPoints.getTopUpQuoteDetailsURL(), HttpMethod.GET,
				requestEntity, TopUpQuote.class,
				quoteID, accessTokenID);

		return responseEntity.getBody();

	}

	@Override
	public OTP sendOTPConfirm(String quoteID, String accessTokenID) {

		HttpEntity<TopUpOrder> requestEntity = new HttpEntity<TopUpOrder>(headers);

		ResponseEntity<OTP> responseEntity = restTemplate.exchange(
				endPoints.getsendOTPConfirmURL(), HttpMethod.POST,
				requestEntity, OTP.class,
				quoteID, accessTokenID);

		return responseEntity.getBody();
	}

	@Override
	public DraftTransaction.Status confirmOTP(String quoteID, OTP otp, String accessTokenID) throws ServiceInventoryException {

		HttpEntity<OTP> requestEntity = new HttpEntity<OTP>(otp, headers);

		ResponseEntity<DraftTransaction.Status> responseEntity = restTemplate.exchange(
				endPoints.getConfirmOTPURL(), HttpMethod.PUT,
				requestEntity, DraftTransaction.Status.class,
				quoteID, otp.getReferenceCode(), accessTokenID);

		return responseEntity.getBody();
	}

	@Override
	public Transaction.Status getTopUpProcessingStatus(String orderID, String accessTokenID) {

		HttpEntity<Transaction.Status> requestEntity = new HttpEntity<Transaction.Status>(headers);

		ResponseEntity<Transaction.Status> responseEntity = restTemplate.exchange(
				endPoints.getTopUpOrderStatusURL(), HttpMethod.GET,
				requestEntity, Transaction.Status.class,
				orderID , accessTokenID);

		return responseEntity.getBody();
	}

	@Override
	public TopUpOrder getTopUpOrderResults(String orderID, String accessTokenID) throws ServiceInventoryException {

		HttpEntity<TopUpOrder> requestEntity = new HttpEntity<TopUpOrder>(headers);

		ResponseEntity<TopUpOrder> responseEntity = restTemplate.exchange(
				endPoints.getTopUpOrderDetailsURL(), HttpMethod.GET,
				requestEntity, TopUpOrder.class, orderID, accessTokenID);

		return responseEntity.getBody();
	}

}
