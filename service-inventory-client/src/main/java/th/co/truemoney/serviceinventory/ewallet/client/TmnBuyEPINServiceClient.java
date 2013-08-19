package th.co.truemoney.serviceinventory.ewallet.client;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import th.co.truemoney.serviceinventory.buy.BuyEPINService;
import th.co.truemoney.serviceinventory.buy.domain.BuyEPINDraft;
import th.co.truemoney.serviceinventory.buy.domain.BuyEPINTransaction;
import th.co.truemoney.serviceinventory.ewallet.client.config.EndPoints;
import th.co.truemoney.serviceinventory.ewallet.domain.Transaction.Status;
import th.co.truemoney.serviceinventory.exception.ServiceInventoryException;

@Service
public class TmnBuyEPINServiceClient implements BuyEPINService {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private EndPoints endPoints;

    @Autowired
    private HttpHeaders headers;
    
	@Override
	public BuyEPINDraft createAndVerifyBuyEPINDraft(String toMobileNumber, BigDecimal amount, String accessTokenID)
			throws ServiceInventoryException {
		BuyEPINDraft draft = new BuyEPINDraft(toMobileNumber, amount);

		HttpEntity<BuyEPINDraft> requestEntity = new HttpEntity<BuyEPINDraft>(draft, headers);

		ResponseEntity<BuyEPINDraft> responseEntity = restTemplate.exchange(
				endPoints.getCreateBuyEPINDraftURL(),HttpMethod.POST,
				requestEntity, BuyEPINDraft.class,
				accessTokenID);

		return responseEntity.getBody();
	}

	@Override
	public BuyEPINDraft getBuyEPINDraftDetails(String buyEPINDraftID, String accessTokenID) throws ServiceInventoryException {
		HttpEntity<String> requestEntity = new HttpEntity<String>(headers);

		ResponseEntity<BuyEPINDraft> responseEntity = restTemplate.exchange(
				endPoints.getBuyEPINDraftDetailsURL(), HttpMethod.GET,
				requestEntity, BuyEPINDraft.class,
				buyEPINDraftID, accessTokenID);

		return responseEntity.getBody();
	}

	@Override
	public Status performBuyEPIN(String buyEPINDraftID, String accessTokenID) throws ServiceInventoryException {
		HttpEntity<String> requestEntity = new HttpEntity<String>(headers);

		ResponseEntity<BuyEPINTransaction.Status> responseEntity = restTemplate.exchange(
				endPoints.getPerformBuyEPINURL(), HttpMethod.PUT,
				requestEntity, BuyEPINTransaction.Status.class,
				buyEPINDraftID,  accessTokenID);

		return responseEntity.getBody();
	}

	@Override
	public Status getBuyEPINStatus(String transactionID, String accessTokenID) throws ServiceInventoryException {
		HttpEntity<String> requestEntity = new HttpEntity<String>(headers);

		ResponseEntity<BuyEPINTransaction.Status> responseEntity = restTemplate.exchange(
				endPoints.getBuyEPINStatusURL(), HttpMethod.GET,
				requestEntity, BuyEPINTransaction.Status.class,
				transactionID, accessTokenID);

		return responseEntity.getBody();
	}

	@Override
	public BuyEPINTransaction getBuyEPINResult(String transactionID, String accessTokenID) throws ServiceInventoryException {
		HttpEntity<String> requestEntity = new HttpEntity<String>(headers);

		ResponseEntity<BuyEPINTransaction> responseEntity = restTemplate.exchange(
				endPoints.getBuyEPINResultURL(), HttpMethod.GET,
				requestEntity, BuyEPINTransaction.class,
				transactionID, accessTokenID);

		return responseEntity.getBody();
	}

}
