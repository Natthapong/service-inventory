package th.co.truemoney.serviceinventory.ewallet.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import th.co.truemoney.serviceinventory.ewallet.TopUpService;
import th.co.truemoney.serviceinventory.ewallet.client.config.EnvironmentConfig;
import th.co.truemoney.serviceinventory.ewallet.domain.OTP;
import th.co.truemoney.serviceinventory.ewallet.domain.QuoteRequest;
import th.co.truemoney.serviceinventory.ewallet.domain.TopUpOrder;
import th.co.truemoney.serviceinventory.ewallet.domain.TopUpQuote;
import th.co.truemoney.serviceinventory.ewallet.domain.TopUpStatus;
import th.co.truemoney.serviceinventory.exception.ServiceInventoryException;

@Service
public class TopupServiceClient implements TopUpService{
	
	@Autowired
	RestTemplate restTemplate;

	@Autowired
	private EnvironmentConfig environmentConfig;

	@Autowired
	private HttpHeaders headers;

	@Override
	public TopUpQuote createTopUpQuoteFromDirectDebit(String sourceOfFundId,
			QuoteRequest quoteRequest, String accessToken) {
		
		HttpEntity<TopUpQuote> requestEntity = new HttpEntity<TopUpQuote>(headers);

		ResponseEntity<TopUpQuote> responseEntity = restTemplate.exchange(
				environmentConfig.getCreateTopUpQuoteFromDirectDebitUrl(),
					HttpMethod.POST, requestEntity, TopUpQuote.class, sourceOfFundId, accessToken , quoteRequest );
		
		TopUpQuote topUpQuote = responseEntity.getBody();
		
		return topUpQuote;
	}
	
	@Override
	public TopUpQuote getTopUpQuoteDetails(String topupOrderId,
			String accessToken) {
		return null;
	}
	
	@Override
	public TopUpOrder requestPlaceOrder(String quoteId, String accessToken) {
		
		HttpEntity<TopUpOrder> requestEntity = new HttpEntity<TopUpOrder>(headers);

		ResponseEntity<TopUpOrder> responseEntity = restTemplate.exchange(
				environmentConfig.getRequestPlaceOrder(),
					HttpMethod.POST, requestEntity, TopUpOrder.class, quoteId, accessToken );
		
		TopUpOrder topUpOrder = responseEntity.getBody();
		
		return topUpOrder;
	}
	
	@Override
	public TopUpStatus getTopUpOrderStatus(String topupOrderId, String accessToken) {
		return null;
	}


	@Override
	public TopUpOrder getTopUpOrderDetails(String topUpOrderId,
			String accessToken) throws ServiceInventoryException {
		return null;
	}

	@Override
	public TopUpOrder confirmPlaceOrder(String topUpOrderId, OTP otp,
			String accessToken) throws ServiceInventoryException {
		return null;
	}
	
}
