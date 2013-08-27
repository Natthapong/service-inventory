package th.co.truemoney.serviceinventory.ewallet.impl;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import th.co.truemoney.serviceinventory.buy.domain.SendEpinSms;
import th.co.truemoney.serviceinventory.config.EndPoints;
import th.co.truemoney.serviceinventory.ewallet.ActivityService;
import th.co.truemoney.serviceinventory.ewallet.domain.AccessToken;
import th.co.truemoney.serviceinventory.ewallet.domain.Activity;
import th.co.truemoney.serviceinventory.ewallet.domain.ActivityDetail;
import th.co.truemoney.serviceinventory.ewallet.repositories.AccessTokenRepository;
import th.co.truemoney.serviceinventory.exception.ServiceInventoryException;
import th.co.truemoney.serviceinventory.legacyfacade.LegacyFacade;
import th.co.truemoney.serviceinventory.sms.SendEpinService;
import th.co.truemoney.serviceinventory.util.MaskingUtil;
import th.co.truemoney.serviceinventory.util.SecurityManager;

public class ActivityServiceImpl implements ActivityService {

	@Autowired
	private RestTemplate restTemplate;
	
	@Autowired
	private AccessTokenRepository accessTokenRepository;
	
	@Autowired
	private LegacyFacade legacyFacade;
	
	@Autowired @Qualifier("jsonHttpHeader")
	private HttpHeaders headers;
	
	@Autowired
	private EndPoints endPoints;
	
	@Autowired
	private SendEpinService sendEpinService;
	
	@Autowired
	private SecurityManager securityManager;
	
	@Override
	public List<Activity> getActivities(String accessTokenID) throws ServiceInventoryException {
		
		AccessToken accessToken = accessTokenRepository.findAccessToken(accessTokenID);
		
		ResponseEntity<Activity[]> response = restTemplate.exchange(endPoints.getListAllReport(), 
															HttpMethod.GET, new HttpEntity<String>(headers), 
															Activity[].class, accessToken.getTruemoneyID());
		return Arrays.asList(response.getBody());
	}

	@Override
	public ActivityDetail getActivityDetail(Long reportID, String accessTokenID) throws ServiceInventoryException {

		AccessToken accessToken = accessTokenRepository.findAccessToken(accessTokenID);
		
		ResponseEntity<ActivityDetail> response = restTemplate.exchange(endPoints.getReportDetail(), 
															HttpMethod.GET, new HttpEntity<String>(headers), 
															ActivityDetail.class, accessToken.getTruemoneyID(), reportID );
		ActivityDetail activityDetail = response.getBody();
		activityDetail = censorSensitiveData(activityDetail);
		activityDetail.setFavoritable(isFavoritable(accessToken, activityDetail));
		activityDetail.setFavorited(IsFavorited(accessToken, activityDetail));

		return activityDetail;
	}
	
	@Override
	public Boolean resendEPIN(Long reportID, String accessTokenID) throws ServiceInventoryException {
		AccessToken accessToken = accessTokenRepository.findAccessToken(accessTokenID);
		ResponseEntity<ActivityDetail> response = restTemplate.exchange(endPoints.getReportDetail(), 
															HttpMethod.GET, new HttpEntity<String>(headers), 
															ActivityDetail.class, accessToken.getTruemoneyID(), reportID );
		ActivityDetail activityDetail = response.getBody();
		
		SendEpinSms buyEpinSms = createSendEpinSms(accessToken, activityDetail);
		
		sendEpinService.send(buyEpinSms);
		
		return Boolean.TRUE;
	}

	private SendEpinSms createSendEpinSms(AccessToken accessToken, ActivityDetail activityDetail) {
		SendEpinSms buyEpinSms = new SendEpinSms();
		buyEpinSms.setAmount(activityDetail.getAmount().toString());
		buyEpinSms.setRecipientMobileNumber(activityDetail.getRef1());
		buyEpinSms.setAccount(accessToken.getMobileNumber());
		buyEpinSms.setPin(activityDetail.getAdditionalData());
		buyEpinSms.setSerial(activityDetail.getRef2());
		buyEpinSms.setTxnID(activityDetail.getTransactionID());
		return buyEpinSms;
	}

	private ActivityDetail censorSensitiveData(ActivityDetail activityDetail) {
		if (activityDetail != null) {
			if ("transfer".equalsIgnoreCase(activityDetail.getType())) {
				String maskedFullname = MaskingUtil.maskFullName(activityDetail.getRef2());
				activityDetail.setRef2(maskedFullname);
			} else if ("buy_cashcard".equalsIgnoreCase(activityDetail.getType())) {
				String encryptedTxt = activityDetail.getAdditionalData();
				activityDetail.setAdditionalData(securityManager.decryptRSA(encryptedTxt));
			}
		}
		return activityDetail;
	}
	
	private Boolean isFavoritable(AccessToken accessToken, ActivityDetail activityDetail) {
		return legacyFacade.userProfile(accessToken.getSessionID(), accessToken.getTruemoneyID())
							.fromChannel(accessToken.getChannelID())
							.withServiceCode(activityDetail.getAction().replaceAll("_c", ""))
							.withRefernce1(activityDetail.getRef1())
							.isFavoritable();
	}
	
	private Boolean IsFavorited(AccessToken accessToken, ActivityDetail activityDetail) {
		return legacyFacade.userProfile(accessToken.getSessionID(), accessToken.getTruemoneyID())
		        			.fromChannel(accessToken.getChannelID())
		        			.withServiceCode(activityDetail.getAction().replaceAll("_c", ""))
		        			.withRefernce1(activityDetail.getRef1())
		        			.isFavorited();
	}

	public void setRestTemplate(RestTemplate restTemplate) {
		this.restTemplate = restTemplate;
	}

	public void setAccessTokenRepository(AccessTokenRepository accessTokenRepo) {
		this.accessTokenRepository = accessTokenRepo;
	}

	public void setLegacyFacade(LegacyFacade legacyFacade) {
		this.legacyFacade = legacyFacade;
	}
	
}
