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
import th.co.truemoney.serviceinventory.ewallet.domain.OTP;
import th.co.truemoney.serviceinventory.ewallet.domain.TopUpOrder;
import th.co.truemoney.serviceinventory.ewallet.domain.TopUpQuote;
import th.co.truemoney.serviceinventory.ewallet.domain.Transaction.Status;
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
	public TopUpQuote createAndVerifyTopUpQuote(String sourceOfFundID, BigDecimal amount, String accessTokenID) {

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
	public OTP requestOTP(String quoteID, String accessTokenID) {

		HttpEntity<TopUpOrder> requestEntity = new HttpEntity<TopUpOrder>(headers);

		ResponseEntity<OTP> responseEntity = restTemplate.exchange(
				endPoints.getTopUpRequestOTPURL(), HttpMethod.POST,
				requestEntity, OTP.class,
				quoteID, accessTokenID);

		return responseEntity.getBody();
	}

	@Override
	public TopUpQuote.Status verifyOTP(String quoteID, OTP otp, String accessTokenID) throws ServiceInventoryException {

		HttpEntity<OTP> requestEntity = new HttpEntity<OTP>(otp, headers);

		ResponseEntity<TopUpQuote.Status> responseEntity = restTemplate.exchange(
				endPoints.getTopUpVerifyOTPURL(), HttpMethod.PUT,
				requestEntity, TopUpQuote.Status.class,
				quoteID, otp.getReferenceCode(), accessTokenID);

		return responseEntity.getBody();
	}

	@Override
	public Status performTopUp(String quoteID, String accessTokenID)
			throws ServiceInventoryException {

		HttpEntity<TopUpQuote> requestEntity = new HttpEntity<TopUpQuote>(headers);

		ResponseEntity<TopUpOrder.Status> responseEntity = restTemplate.exchange(
				endPoints.getTopUpPerformURL(), HttpMethod.PUT,
				requestEntity, TopUpOrder.Status.class,
				quoteID, accessTokenID);

		return responseEntity.getBody();
	}

	@Override
	public TopUpOrder.Status getTopUpProcessingStatus(String orderID, String accessTokenID) {

		HttpEntity<TopUpOrder.Status> requestEntity = new HttpEntity<TopUpOrder.Status>(headers);

		ResponseEntity<TopUpOrder.Status> responseEntity = restTemplate.exchange(
				endPoints.getTopUpOrderStatusURL(), HttpMethod.GET,
				requestEntity, TopUpOrder.Status.class,
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
