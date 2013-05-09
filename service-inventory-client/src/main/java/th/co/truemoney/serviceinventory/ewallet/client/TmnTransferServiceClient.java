package th.co.truemoney.serviceinventory.ewallet.client;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import th.co.truemoney.serviceinventory.ewallet.client.config.EndPoints;
import th.co.truemoney.serviceinventory.ewallet.domain.OTP;
import th.co.truemoney.serviceinventory.ewallet.domain.Transaction.Status;
import th.co.truemoney.serviceinventory.exception.ServiceInventoryException;
import th.co.truemoney.serviceinventory.transfer.P2PTransferService;
import th.co.truemoney.serviceinventory.transfer.domain.P2PTransferDraft;
import th.co.truemoney.serviceinventory.transfer.domain.P2PTransferTransaction;

@Service
public class TmnTransferServiceClient implements P2PTransferService {

	@Autowired
	private RestTemplate restTemplate;

	@Autowired
	private EndPoints endPoints;

	@Autowired
	private HttpHeaders headers;

	@Override
	public P2PTransferDraft createAndVerifyTransferDraft(String toMobileNo, BigDecimal amount, String accessTokenID) {

		P2PTransferDraft draft = new P2PTransferDraft(toMobileNo, amount);

		HttpEntity<P2PTransferDraft> requestEntity = new HttpEntity<P2PTransferDraft>(draft,headers);

		ResponseEntity<P2PTransferDraft> responseEntity = restTemplate.exchange(
				endPoints.getP2PCreateTransferDraftURL(),HttpMethod.POST,
				requestEntity, P2PTransferDraft.class,
				accessTokenID);

		return responseEntity.getBody();
	}

	@Override
	public P2PTransferDraft getTransferDraftDetails(String transferDraftID, String accessTokenID)  throws ServiceInventoryException {

		HttpEntity<String> requestEntity = new HttpEntity<String>(headers);

		ResponseEntity<P2PTransferDraft> responseEntity = restTemplate.exchange(
				endPoints.getP2PTransferDraftDetailsURL(), HttpMethod.GET,
				requestEntity, P2PTransferDraft.class,
				transferDraftID, accessTokenID);

		return responseEntity.getBody();
	}

	@Override
	public OTP requestOTP(String transferDraftID, String accessTokenID) {

		HttpEntity<String> requestEntity = new HttpEntity<String>(headers);

		ResponseEntity<OTP> responseEntity = restTemplate.exchange(
				endPoints.getP2PRequestOTPURL(), HttpMethod.POST,
				requestEntity, OTP.class,
				transferDraftID, accessTokenID);

		return responseEntity.getBody();
	}

	@Override
	public P2PTransferDraft.Status verifyOTP(String transferDraftID, OTP otp, String accessTokenID) {

		HttpEntity<OTP> requestEntity = new HttpEntity<OTP>(otp,headers);

		ResponseEntity<P2PTransferDraft.Status> responseEntity = restTemplate.exchange(
				endPoints.getP2PVerifyOTPURL(), HttpMethod.PUT,
				requestEntity, P2PTransferDraft.Status.class,
				transferDraftID, otp.getReferenceCode(), accessTokenID);

		return responseEntity.getBody();
	}

	@Override
	public Status performTransfer(String transferDraftID, String accessTokenID)
			throws ServiceInventoryException {

		HttpEntity<String> requestEntity = new HttpEntity<String>(headers);

		ResponseEntity<P2PTransferTransaction.Status> responseEntity = restTemplate.exchange(
				endPoints.getP2PPerformTransferURL(), HttpMethod.PUT,
				requestEntity, P2PTransferTransaction.Status.class,
				transferDraftID,  accessTokenID);

		return responseEntity.getBody();
	}

	@Override
	public P2PTransferTransaction.Status getTransferringStatus(String transactionID, String accessTokenID) {

		HttpEntity<String> requestEntity = new HttpEntity<String>(headers);

		ResponseEntity<P2PTransferTransaction.Status> responseEntity = restTemplate.exchange(
				endPoints.getP2PTransactionStatusURL(), HttpMethod.GET,
				requestEntity, P2PTransferTransaction.Status.class,
				transactionID, accessTokenID);

		return responseEntity.getBody();
	}

	@Override
	public P2PTransferTransaction getTransactionResult(String transactionID, String accessTokenID) {

		HttpEntity<String> requestEntity = new HttpEntity<String>(headers);

		ResponseEntity<P2PTransferTransaction> responseEntity = restTemplate.exchange(
				endPoints.getP2PTransactionInfoURL(), HttpMethod.GET,
				requestEntity, P2PTransferTransaction.class,
				transactionID, accessTokenID);

		return responseEntity.getBody();
	}

}
