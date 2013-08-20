package th.co.truemoney.serviceinventory.ewallet.client;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import th.co.truemoney.serviceinventory.buy.BuyProductService;
import th.co.truemoney.serviceinventory.buy.domain.BuyProduct;
import th.co.truemoney.serviceinventory.buy.domain.BuyProductDraft;
import th.co.truemoney.serviceinventory.buy.domain.BuyProductTransaction;
import th.co.truemoney.serviceinventory.ewallet.client.config.EndPoints;
import th.co.truemoney.serviceinventory.ewallet.domain.Transaction.Status;
import th.co.truemoney.serviceinventory.exception.ServiceInventoryException;

@Service
public class TmnBuyProductServiceClient implements BuyProductService {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private EndPoints endPoints;

    @Autowired
    private HttpHeaders headers;
    
	@Override
	public BuyProductDraft createAndVerifyBuyProductDraft(String target, String recipientMobileNumber, BigDecimal amount, String accessTokenID)
			throws ServiceInventoryException {
		BuyProduct buyProduct = new BuyProduct();
		buyProduct.setAmount(amount);
		buyProduct.setTarget(target);
		
		BuyProductDraft draft = new BuyProductDraft();
		draft.setBuyProductInfo(buyProduct);
		draft.setRecipientMobileNumber(recipientMobileNumber);

		HttpEntity<BuyProductDraft> requestEntity = new HttpEntity<BuyProductDraft>(draft, headers);

		ResponseEntity<BuyProductDraft> responseEntity = restTemplate.exchange(
				endPoints.getCreateBuyProductURL(),HttpMethod.POST,
				requestEntity, BuyProductDraft.class,
				accessTokenID);

		return responseEntity.getBody();
	}

	@Override
	public BuyProductDraft getBuyProductDraftDetails(String buyEPINDraftID, String accessTokenID) throws ServiceInventoryException {
		HttpEntity<String> requestEntity = new HttpEntity<String>(headers);

		ResponseEntity<BuyProductDraft> responseEntity = restTemplate.exchange(
				endPoints.getBuyProductDraftDetailsURL(), HttpMethod.GET,
				requestEntity, BuyProductDraft.class,
				buyEPINDraftID, accessTokenID);

		return responseEntity.getBody();
	}

	@Override
	public Status performBuyProduct(String buyEPINDraftID, String accessTokenID) throws ServiceInventoryException {
		HttpEntity<String> requestEntity = new HttpEntity<String>(headers);

		ResponseEntity<BuyProductTransaction.Status> responseEntity = restTemplate.exchange(
				endPoints.getPerformBuyProductURL(), HttpMethod.PUT,
				requestEntity, BuyProductTransaction.Status.class,
				buyEPINDraftID,  accessTokenID);

		return responseEntity.getBody();
	}

	@Override
	public Status getBuyProductStatus(String transactionID, String accessTokenID) throws ServiceInventoryException {
		HttpEntity<String> requestEntity = new HttpEntity<String>(headers);

		ResponseEntity<BuyProductTransaction.Status> responseEntity = restTemplate.exchange(
				endPoints.getBuyProductStatusURL(), HttpMethod.GET,
				requestEntity, BuyProductTransaction.Status.class,
				transactionID, accessTokenID);

		return responseEntity.getBody();
	}

	@Override
	public BuyProductTransaction getBuyProductResult(String transactionID, String accessTokenID) throws ServiceInventoryException {
		HttpEntity<String> requestEntity = new HttpEntity<String>(headers);

		ResponseEntity<BuyProductTransaction> responseEntity = restTemplate.exchange(
				endPoints.getBuyProductResultURL(), HttpMethod.GET,
				requestEntity, BuyProductTransaction.class,
				transactionID, accessTokenID);

		return responseEntity.getBody();
	}

}
