package th.co.truemoney.serviceinventory.ewallet.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import th.co.truemoney.serviceinventory.authen.TransactionAuthenService;
import th.co.truemoney.serviceinventory.ewallet.client.config.EndPoints;
import th.co.truemoney.serviceinventory.ewallet.domain.DraftTransaction;
import th.co.truemoney.serviceinventory.ewallet.domain.OTP;
import th.co.truemoney.serviceinventory.exception.ServiceInventoryException;

@Service
public class TransactionAuthenServiceClient implements TransactionAuthenService {

	@Autowired
	RestTemplate restTemplate;

	@Autowired
	private EndPoints endPoints;

	@Autowired
	private HttpHeaders headers;


	@Override
	public OTP requestOTP(String quoteID, String accessTokenID) {

		HttpEntity<String> requestEntity = new HttpEntity<String>(headers);

		ResponseEntity<OTP> responseEntity = restTemplate.exchange(
				endPoints.getTransactionRequestOTPURL(), HttpMethod.POST,
				requestEntity, OTP.class,
				quoteID, accessTokenID);

		return responseEntity.getBody();
	}

	@Override
	public DraftTransaction.Status verifyOTP(String quoteID, OTP otp, String accessTokenID) throws ServiceInventoryException {

		HttpEntity<OTP> requestEntity = new HttpEntity<OTP>(otp, headers);

		ResponseEntity<DraftTransaction.Status> responseEntity = restTemplate.exchange(
				endPoints.getTransactionVerifyOTPURL(), HttpMethod.PUT,
				requestEntity, DraftTransaction.Status.class,
				quoteID, otp.getReferenceCode(), accessTokenID);

		return responseEntity.getBody();
	}

}
