package th.co.truemoney.serviceinventory.ewallet.client;

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
import th.co.truemoney.serviceinventory.ewallet.domain.P2PDraftRequest;
import th.co.truemoney.serviceinventory.ewallet.domain.P2PDraftTransaction;
import th.co.truemoney.serviceinventory.ewallet.domain.P2PTransaction;
import th.co.truemoney.serviceinventory.ewallet.domain.P2PTransactionStatus;
import th.co.truemoney.serviceinventory.exception.ServiceInventoryException;

@Service
public class P2PTransferServiceClient implements P2PTransferService {

	@Autowired
	RestTemplate restTemplate;

	@Autowired
	private EndPoints endPoints;

	@Autowired
	private HttpHeaders headers;

	@Override
	public P2PDraftTransaction createDraftTransaction(
			P2PDraftRequest p2pDraftRequest, String accessTokenID) {
		// waiting for Utiba
		return null;
	}

	@Override
	public P2PDraftTransaction getDraftTransactionDetail(
			String draftTransactionID, String accessTokenID)  throws ServiceInventoryException {

		HttpEntity<P2PDraftTransaction> requestEntity = new HttpEntity<P2PDraftTransaction>(headers);

		ResponseEntity<P2PDraftTransaction> responseEntity = restTemplate.exchange(
				endPoints.getDraftTransactionDetail(),
					HttpMethod.GET, requestEntity, P2PDraftTransaction.class, draftTransactionID,accessTokenID);

		P2PDraftTransaction p2pDraftTransaction = responseEntity.getBody();
		return p2pDraftTransaction;
	}

	@Override
	public P2PDraftTransaction sendOTP(String draftTransactionID,
			String accessTokenID) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public P2PTransaction createTransaction(String draftTransactionID, OTP otp,
			String accessTokenID) {
		// waiting for Utiba
		return null;
	}

	@Override
	public P2PTransactionStatus getTransactionStatus(String transactionID,
			String accessTokenID) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public P2PTransaction getTransactionDetail(String transactionID,
			String accessTokenID) {
		// TODO Auto-generated method stub
		return null;
	}

}
