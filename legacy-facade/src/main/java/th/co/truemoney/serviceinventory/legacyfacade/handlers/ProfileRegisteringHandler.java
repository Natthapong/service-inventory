package th.co.truemoney.serviceinventory.legacyfacade.handlers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import th.co.truemoney.serviceinventory.ewallet.domain.TmnProfile;
import th.co.truemoney.serviceinventory.ewallet.proxy.TmnProfileAdminProxyClient;
import th.co.truemoney.serviceinventory.ewallet.proxy.TmnProfileProxyClient;
import th.co.truemoney.serviceinventory.ewallet.proxy.util.HashPasswordUtil;
import th.co.truemoney.serviceinventory.exception.ServiceInventoryException;

import com.tmn.core.api.message.AdminSecurityContext;
import com.tmn.core.api.message.CreateTmnProfileRequest;
import com.tmn.core.api.message.IsCreatableRequest;

public class ProfileRegisteringHandler {

    private static final Logger logger = LoggerFactory.getLogger(ProfileRegisteringHandler.class);
    
	@Autowired
	private TmnProfileAdminProxyClient tmnProfileAdminProxyClient;

	@Autowired
	private TmnProfileProxyClient tmnProfileProxyClient;

	@Autowired @Qualifier("tmnProfileInitiator")
	private String tmnProfileInitiator;

	@Autowired @Qualifier("tmnProfilePin")
	private String tmnProfilePin;


	public void verifyValidRegisteringEmail(Integer channelID, String registeringEmail) throws ServiceInventoryException {
		try {
			String requestTransactionID = Long.toString(System.currentTimeMillis());
			IsCreatableRequest isCreatableRequest = createIsCreatableRequest(requestTransactionID, channelID, registeringEmail);
			tmnProfileAdminProxyClient.isCreatable(isCreatableRequest);
		} catch (ServiceInventoryException e) {
			e.putDate("email", registeringEmail);
			throw e;
		}
    }

	public void verifyValidRegisteringMobileNumber(Integer channelID, String registeringMobileNumber) throws ServiceInventoryException {
		try {
			String requestTransactionID = Long.toString(System.currentTimeMillis());
			IsCreatableRequest isCreatableRequest = createIsCreatableRequest(requestTransactionID, channelID, registeringMobileNumber);
			tmnProfileAdminProxyClient.isCreatable(isCreatableRequest);
		} catch (ServiceInventoryException e) {
			e.putDate("mobileNumber", registeringMobileNumber);
			throw e;
		}
    }

    public void register(Integer channelID, TmnProfile profile) {
    	verifyValidRegisteringEmail(channelID, profile.getEmail());
    	verifyValidRegisteringMobileNumber(channelID, profile.getMobileNumber());
    	CreateTmnProfileRequest createProfileRequest = createTmnProfileRequest(channelID, profile);
    	tmnProfileProxyClient.createTmnProfile(createProfileRequest);
    }

    private IsCreatableRequest createIsCreatableRequest(String requestTransactionID, Integer channelID, String loginID) {
        IsCreatableRequest isCreatableRequest = new IsCreatableRequest();
        isCreatableRequest.setRequestTransactionId(requestTransactionID);
        isCreatableRequest.setChannelId(channelID);
        isCreatableRequest.setLoginId(loginID);
        isCreatableRequest.setAdminSecurityContext(createSecurityContext(requestTransactionID));
        return isCreatableRequest;
    }

	private String encryptSHA1(String requestTransactionID) {
		String initiator = tmnProfileInitiator != null ? tmnProfileInitiator.toLowerCase() : "";
		logger.debug("==============================================================");
		logger.debug("initiator : "+initiator);
		logger.debug("tmnProfilePin : "+tmnProfilePin);
		logger.debug("requestTransactionID : "+requestTransactionID);
		String tempEncrypted = HashPasswordUtil.encryptSHA1(initiator + tmnProfilePin).toLowerCase();
		logger.debug("tempEncrypted = HashPasswordUtil.encryptSHA1(initiator + tmnProfilePin).toLowerCase() "+tempEncrypted);
		return HashPasswordUtil.encryptSHA1(requestTransactionID + tempEncrypted).toUpperCase();
	}
	
	private AdminSecurityContext createSecurityContext(String requestTransactionID) {
		String encryptedPin = encryptSHA1(requestTransactionID);
		logger.debug("encryptedPin = HashPasswordUtil.encryptSHA1(requestTransactionID + tempEncrypted).toUpperCase() "+encryptedPin);
		logger.debug("==============================================================");
		AdminSecurityContext adminSecurityContext = new AdminSecurityContext();
		adminSecurityContext.setInitiator(tmnProfileInitiator);
		adminSecurityContext.setPin(encryptedPin);
		return adminSecurityContext;
	}
	
    private CreateTmnProfileRequest createTmnProfileRequest(Integer channelID, TmnProfile tmnProfile) {
		CreateTmnProfileRequest tmnProfileRequest = new CreateTmnProfileRequest();
		tmnProfileRequest.setChannelId(channelID);
		tmnProfileRequest.setEmail(tmnProfile.getEmail());
		tmnProfileRequest.setFullName(tmnProfile.getFullname());
		tmnProfileRequest.setMobile(tmnProfile.getMobileNumber());
		tmnProfileRequest.setPassword(tmnProfile.getPassword());
		tmnProfileRequest.setThaiId(tmnProfile.getThaiID());
		tmnProfileRequest.setEmailStatus(0);
		tmnProfileRequest.setMobileStatus(1);
		return tmnProfileRequest;
	}

    public void setTmnProfileProxy(TmnProfileProxyClient tmnProfileProxyClient) {
		this.tmnProfileProxyClient = tmnProfileProxyClient;
	}

	public void setTmnProfileAdminProxy(TmnProfileAdminProxyClient tmnProfileAdminProxyClient) {
		this.tmnProfileAdminProxyClient = tmnProfileAdminProxyClient;
	}

	public void setTmnProfileInitiator(String tmnProfileInitiator) {
		this.tmnProfileInitiator = tmnProfileInitiator;
	}

	public void setTmnProfilePin(String tmnProfilePin) {
		this.tmnProfilePin = tmnProfilePin;
	}
	
}
