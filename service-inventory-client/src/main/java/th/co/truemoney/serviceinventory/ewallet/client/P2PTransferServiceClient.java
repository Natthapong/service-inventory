package th.co.truemoney.serviceinventory.ewallet.client;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import th.co.truemoney.serviceinventory.ewallet.P2PTransferService;
import th.co.truemoney.serviceinventory.ewallet.client.config.EndPoints;
import th.co.truemoney.serviceinventory.ewallet.domain.OTP;
import th.co.truemoney.serviceinventory.ewallet.domain.P2PDraftTransaction;
import th.co.truemoney.serviceinventory.ewallet.domain.P2PTransaction;
import th.co.truemoney.serviceinventory.ewallet.domain.P2PTransactionStatus;
import th.co.truemoney.serviceinventory.exception.ServiceInventoryException;

@Service
public class P2PTransferServiceClient implements P2PTransferService {

	@Autowired
	private RestTemplate restTemplate;

	@Autowired
	private EndPoints endPoints;

	@Autowired
	private HttpHeaders headers;

	@Override
	public P2PDraftTransaction createDraftTransaction(String toMobileNo, BigDecimal amount, String accessTokenID) {

		P2PDraftTransaction draft = new P2PDraftTransaction(toMobileNo, amount);

		HttpEntity<P2PDraftTransaction> requestEntity = new HttpEntity<P2PDraftTransaction>(draft,headers);

		ResponseEntity<P2PDraftTransaction> responseEntity = restTemplate.exchange(
				endPoints.getCreateDraftTransactionUrl(),HttpMethod.POST,
				requestEntity, P2PDraftTransaction.class,
				accessTokenID);

		return responseEntity.getBody();
	}

	@Override
	public P2PDraftTransaction getDraftTransactionDetails(String draftTransactionID, String accessTokenID)  throws ServiceInventoryException {

		HttpEntity<String> requestEntity = new HttpEntity<String>(headers);

		ResponseEntity<P2PDraftTransaction> responseEntity = restTemplate.exchange(
				endPoints.getDraftTransactionDetails(), HttpMethod.POST,
				requestEntity, P2PDraftTransaction.class,
				draftTransactionID, accessTokenID);

		return responseEntity.getBody();
	}

	@Override
	public OTP sendOTP(String draftTransactionID, String accessTokenID) {

		HttpEntity<String> requestEntity = new HttpEntity<String>(headers);

		ResponseEntity<OTP> responseEntity = restTemplate.exchange(
				endPoints.getSendOTPUrl(), HttpMethod.PUT,
				requestEntity, OTP.class,
				draftTransactionID, accessTokenID);

		return responseEntity.getBody();
	}

	@Override
	public P2PTransactionStatus createTransaction(String draftTransactionID, OTP otp, String accessTokenID) {

		HttpEntity<OTP> requestEntity = new HttpEntity<OTP>(otp,headers);

		ResponseEntity<P2PTransactionStatus> responseEntity = restTemplate.exchange(
				endPoints.getCreateTransactionUrl(), HttpMethod.POST,
				requestEntity, P2PTransactionStatus.class,
				draftTransactionID, accessTokenID);

		return responseEntity.getBody();
	}

	@Override
	public P2PTransactionStatus getTransactionStatus(String transactionID, String accessTokenID) {

		HttpEntity<String> requestEntity = new HttpEntity<String>(headers);

		ResponseEntity<P2PTransactionStatus> responseEntity = restTemplate.exchange(
				endPoints.getTransactionStatusUrl(), HttpMethod.GET,
				requestEntity, P2PTransactionStatus.class,
				transactionID, accessTokenID);

		return responseEntity.getBody();
	}

	@Override
	public P2PTransaction getTransactionResult(String transactionID, String accessTokenID) {

		HttpEntity<String> requestEntity = new HttpEntity<String>(headers);

		ResponseEntity<P2PTransaction> responseEntity = restTemplate.exchange(
				endPoints.getTransactionStatusUrl(), HttpMethod.GET,
				requestEntity, P2PTransaction.class,
				transactionID, accessTokenID);

		return responseEntity.getBody();
	}

}
