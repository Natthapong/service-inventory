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
import th.co.truemoney.serviceinventory.ewallet.domain.TopUpConfirmationInfo;
import th.co.truemoney.serviceinventory.ewallet.domain.TopUpOrder;
import th.co.truemoney.serviceinventory.ewallet.domain.TopUpQuote;
import th.co.truemoney.serviceinventory.ewallet.domain.TopUpStatus;
import th.co.truemoney.serviceinventory.exception.ServiceInventoryException;

@Service
public class TmnTopUpServiceClient implements TopUpService {

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
		topUpQuote.setAmount(new BigDecimal(Double.parseDouble(hashMap.get("amount").toString())));
		topUpQuote.setUsername(hashMap.get("username").toString());
		
		HashMap sourceOfFundMap = (HashMap) hashMap.get("sourceOfFund");
		DirectDebit directDebit = new DirectDebit();
		directDebit.setBankCode(sourceOfFundMap.get("bankCode").toString());
		directDebit.setBankNameEn(sourceOfFundMap.get("bankNameEn").toString());
		directDebit.setBankNameTh(sourceOfFundMap.get("bankNameTh").toString());
		directDebit.setBankAccountNumber(sourceOfFundMap.get("bankAccountNumber").toString());
		directDebit.setMinAmount(new BigDecimal(Double.parseDouble(sourceOfFundMap.get("minAmount").toString())));
		directDebit.setMaxAmount(new BigDecimal(Double.parseDouble(sourceOfFundMap.get("maxAmount").toString())));
		
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
	@SuppressWarnings("rawtypes")
	public TopUpOrder requestPlaceOrder(String quoteId, String accessToken) {

		HttpEntity<TopUpOrder> requestEntity = new HttpEntity<TopUpOrder>(
				headers);

		ResponseEntity<HashMap> responseEntity = restTemplate.exchange(
				environmentConfig.getRequestPlaceOrder(), HttpMethod.POST,
				requestEntity, HashMap.class, quoteId, accessToken);

		HashMap hashMap = responseEntity.getBody();
		
		TopUpOrder topUpOrder = new TopUpOrder();
		topUpOrder.setID(hashMap.get("id").toString());
		topUpOrder.setAmount(new BigDecimal(Double.parseDouble(hashMap.get("amount").toString())));
		topUpOrder.setUsername(hashMap.get("username").toString());
		
		HashMap sourceOfFundMap = (HashMap) hashMap.get("sourceOfFund");
		DirectDebit directDebit = new DirectDebit();
		directDebit.setBankCode(sourceOfFundMap.get("bankCode").toString());
		directDebit.setBankNameEn(sourceOfFundMap.get("bankNameEn").toString());
		directDebit.setBankNameTh(sourceOfFundMap.get("bankNameTh").toString());
		directDebit.setBankAccountNumber(sourceOfFundMap.get("bankAccountNumber").toString());
		directDebit.setMinAmount(new BigDecimal(Double.parseDouble(sourceOfFundMap.get("minAmount").toString())));
		directDebit.setMaxAmount(new BigDecimal(Double.parseDouble(sourceOfFundMap.get("maxAmount").toString())));
		
		TopUpConfirmationInfo confirmationInfo = new TopUpConfirmationInfo();
		confirmationInfo.setTransactionDate(null);
		confirmationInfo.setTransactionID(null);
		
		topUpOrder.setOtpReferenceCode(hashMap.get("otpReferenceCode").toString());
		topUpOrder.setConfirmationInfo(confirmationInfo);
		topUpOrder.setSourceOfFund(directDebit);
		topUpOrder.setTopUpFee(new BigDecimal(Double.parseDouble(hashMap.get("topUpFee").toString())));
		topUpOrder.setAccessTokenID(hashMap.get("accessTokenID").toString());
		
		return topUpOrder;
	}

	@Override
	public TopUpOrder confirmPlaceOrder(String topUpOrderId, OTP otp,
			String accessTokenId) throws ServiceInventoryException {
		
		HttpEntity<OTP> requestEntity = new HttpEntity<OTP>(otp, headers);
		
		ResponseEntity<TopUpOrder> responseEntity = restTemplate.exchange(
				environmentConfig.getConfirmPlaceOrderUrl(),
					HttpMethod.POST, requestEntity, TopUpOrder.class, topUpOrderId, accessTokenId, otp);
		TopUpOrder topUpOrder = responseEntity.getBody();
				
		return topUpOrder;
	}

	@Override
	public TopUpStatus getTopUpOrderStatus(String topupOrderId,
			String accessToken) {
		
		HttpEntity<TopUpStatus> requestEntity = new HttpEntity<TopUpStatus>(headers);
		
		ResponseEntity<TopUpStatus> responseEntity = restTemplate.exchange(
				environmentConfig.getTopUpOrderStatusUrl(),
					HttpMethod.GET, requestEntity, TopUpStatus.class, topupOrderId , accessToken);
	
		TopUpStatus topUpStatus = responseEntity.getBody();
		
		return topUpStatus;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public TopUpOrder getTopUpOrderDetails(String topUpOrderId,
			String accessToken) throws ServiceInventoryException {
		
		HttpEntity<TopUpOrder> requestEntity = new HttpEntity<TopUpOrder>(
				headers);

		ResponseEntity<HashMap> responseEntity = restTemplate.exchange(
				environmentConfig.getTopUpOrderDetailsUrl(), HttpMethod.GET,
				requestEntity, HashMap.class, accessToken);

		HashMap hashMap = responseEntity.getBody();
		
		TopUpOrder topUpOrder = new TopUpOrder();
		topUpOrder.setID(hashMap.get("id").toString());
		topUpOrder.setAmount(new BigDecimal(Double.parseDouble(hashMap.get("amount").toString())));
		topUpOrder.setUsername(hashMap.get("username").toString());
		
		HashMap sourceOfFundMap = (HashMap) hashMap.get("sourceOfFund");
		DirectDebit directDebit = new DirectDebit();
		directDebit.setBankCode(sourceOfFundMap.get("bankCode").toString());
		directDebit.setBankNameEn(sourceOfFundMap.get("bankNameEn").toString());
		directDebit.setBankNameTh(sourceOfFundMap.get("bankNameTh").toString());
		directDebit.setBankAccountNumber(sourceOfFundMap.get("bankAccountNumber").toString());
		directDebit.setMinAmount(new BigDecimal(Double.parseDouble(sourceOfFundMap.get("minAmount").toString())));
		directDebit.setMaxAmount(new BigDecimal(Double.parseDouble(sourceOfFundMap.get("maxAmount").toString())));
		
		HashMap confirmationInfoMap = (HashMap) hashMap.get("confirmationInfo");
		TopUpConfirmationInfo confirmationInfo = new TopUpConfirmationInfo();
		confirmationInfo.setTransactionDate(confirmationInfoMap.get("transactionDate").toString());
		confirmationInfo.setTransactionID(confirmationInfoMap.get("transactionID").toString());
		
		topUpOrder.setOtpReferenceCode(hashMap.get("otpReferenceCode").toString());
		topUpOrder.setConfirmationInfo(confirmationInfo);
		topUpOrder.setSourceOfFund(directDebit);
		topUpOrder.setTopUpFee(new BigDecimal(Double.parseDouble(hashMap.get("topUpFee").toString())));
		topUpOrder.setAccessTokenID(hashMap.get("accessTokenID").toString());
		return null;
	}

}
