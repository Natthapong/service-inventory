package th.co.truemoney.serviceinventory.ewallet.client;

import java.math.BigDecimal;
import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import th.co.truemoney.serviceinventory.ewallet.TopUpService;
import th.co.truemoney.serviceinventory.ewallet.client.config.EnvironmentConfig;
import th.co.truemoney.serviceinventory.ewallet.domain.DirectDebit;
import th.co.truemoney.serviceinventory.ewallet.domain.OTP;
import th.co.truemoney.serviceinventory.ewallet.domain.QuoteRequest;
import th.co.truemoney.serviceinventory.ewallet.domain.TopUpOrder;
import th.co.truemoney.serviceinventory.ewallet.domain.TopUpQuote;
import th.co.truemoney.serviceinventory.ewallet.domain.TopUpStatus;
import th.co.truemoney.serviceinventory.exception.ServiceInventoryException;

@Service
public class TopUpServiceClient implements TopUpService {

	@Autowired
	RestTemplate restTemplate;

	@Autowired
	private EnvironmentConfig environmentConfig;

	@Autowired
	private HttpHeaders headers;

	@Override
	@SuppressWarnings("rawtypes")
	public TopUpQuote createTopUpQuoteFromDirectDebit(String sourceOfFundId,
			QuoteRequest quoteRequest, String accessToken) {

		HttpEntity<QuoteRequest> requestEntity = new HttpEntity<QuoteRequest>(quoteRequest,headers);
		
		ResponseEntity<HashMap> responseEntity = restTemplate.exchange(
				environmentConfig.getCreateTopUpQuoteFromDirectDebitUrl(),
					HttpMethod.POST, requestEntity, HashMap.class, sourceOfFundId, accessToken ,quoteRequest);
	
		HashMap hashMap = responseEntity.getBody();
		
		TopUpQuote topUpQuote = new TopUpQuote();
		topUpQuote.setID(hashMap.get("id").toString());
		topUpQuote.setAmount(new BigDecimal(Integer.parseInt(hashMap.get("amount").toString())));
		topUpQuote.setUsername(hashMap.get("username").toString());
		
		HashMap sourceOfFundMap = (HashMap) hashMap.get("sourceOfFund");
		DirectDebit directDebit = new DirectDebit();
		directDebit.setBankCode(sourceOfFundMap.get("bankCode").toString());
		directDebit.setBankNameEn(sourceOfFundMap.get("bankNameEn").toString());
		directDebit.setBankNameTh(sourceOfFundMap.get("bankNameTh").toString());
		directDebit.setBankAccountNumber(sourceOfFundMap.get("bankAccountNumber").toString());
		directDebit.setMinAmount(new BigDecimal(Integer.parseInt(sourceOfFundMap.get("minAmount").toString())));
		directDebit.setMaxAmount(new BigDecimal(Integer.parseInt(sourceOfFundMap.get("maxAmount").toString())));
		
		topUpQuote.setSourceOfFund(directDebit);
		topUpQuote.setTopUpFee(new BigDecimal(Double.parseDouble(hashMap.get("topUpFee").toString())));
		topUpQuote.setAccessTokenID(hashMap.get("accessTokenID").toString());
		
		return topUpQuote;
	}

	@Override
	public TopUpQuote getTopUpQuoteDetails(String topupOrderId,
			String accessToken) {
		return null;
	}

	@Override
	public TopUpOrder requestPlaceOrder(String quoteId, String accessToken) {

		HttpEntity<TopUpOrder> requestEntity = new HttpEntity<TopUpOrder>(
				headers);

		ResponseEntity<TopUpOrder> responseEntity = restTemplate.exchange(
				environmentConfig.getRequestPlaceOrder(), HttpMethod.POST,
				requestEntity, TopUpOrder.class, quoteId, accessToken);

		TopUpOrder topUpOrder = responseEntity.getBody();

		return topUpOrder;
	}

	@Override
	public TopUpOrder confirmPlaceOrder(String topUpOrderId, OTP otp,
			String accessToken) throws ServiceInventoryException {
		return null;
	}

	@Override
	public TopUpStatus getTopUpOrderStatus(String topupOrderId,
			String accessToken) {
		return null;
	}

	@Override
	public TopUpOrder getTopUpOrderDetails(String topUpOrderId,
			String accessToken) throws ServiceInventoryException {
		return null;
	}

}
