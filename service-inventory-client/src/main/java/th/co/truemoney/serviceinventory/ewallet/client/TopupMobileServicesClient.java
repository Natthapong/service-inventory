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
import th.co.truemoney.serviceinventory.ewallet.domain.DraftTransaction.Status;
import th.co.truemoney.serviceinventory.ewallet.domain.OTP;
import th.co.truemoney.serviceinventory.exception.ServiceInventoryException;
import th.co.truemoney.serviceinventory.topup.TopUpMobileService;
import th.co.truemoney.serviceinventory.topup.domain.TopUpMobile;
import th.co.truemoney.serviceinventory.topup.domain.TopUpMobileDraft;
import th.co.truemoney.serviceinventory.topup.domain.TopUpMobileTransaction;

@Service
public class TopupMobileServicesClient implements TopUpMobileService{
	
	@Autowired
	RestTemplate restTemplate;
	
	public RestTemplate getRestTemplate() {
		return restTemplate;
	}

	public void setRestTemplate(RestTemplate restTemplate) {
		this.restTemplate = restTemplate;
	}

	@Autowired
	private EndPoints endPoints;

	public EndPoints getEndPoints() {
		return endPoints;
	}

	public void setEndPoints(EndPoints endPoints) {
		this.endPoints = endPoints;
	}

	@Autowired
	private HttpHeaders headers;

	@Override
	public TopUpMobileDraft verifyAndCreateTopUpMobileDraft(
			String targetMobileNumber, BigDecimal amount, String accessTokenID)
			throws ServiceInventoryException {
		
		TopUpMobile topUpMobile = new TopUpMobile();
		topUpMobile.setMobileNumber(targetMobileNumber);
		topUpMobile.setAmount(amount);
		
		TopUpMobileDraft topUpMobileDraft = new TopUpMobileDraft();
		topUpMobileDraft.setTopUpMobileInfo(topUpMobile);
		
		HttpEntity<TopUpMobileDraft> requestEntity = new HttpEntity<TopUpMobileDraft>(topUpMobileDraft, headers);
		
		ResponseEntity<TopUpMobileDraft> responseEntity = restTemplate.exchange(
				endPoints.getVerifyTopupMobile(), HttpMethod.POST,
				requestEntity, TopUpMobileDraft.class,accessTokenID);
		
		return responseEntity.getBody();
	}
	
	
	@Override
	public TopUpMobileDraft getTopUpMobileDraftDetail(String draftID,
			String accessTokenID) throws ServiceInventoryException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public OTP sendOTP(String draftID, String accessTokenID)
			throws ServiceInventoryException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Status confirmTopUpMobile(String draftID, OTP otp,
			String accessTokenID) throws ServiceInventoryException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public th.co.truemoney.serviceinventory.ewallet.domain.Transaction.Status getTopUpMobileStatus(
			String transactionID, String accessTokenID)
			throws ServiceInventoryException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TopUpMobileTransaction getTopUpMobileResult(String transactionID,
			String accessTokenID) throws ServiceInventoryException {
		// TODO Auto-generated method stub
		return null;
	}


}
