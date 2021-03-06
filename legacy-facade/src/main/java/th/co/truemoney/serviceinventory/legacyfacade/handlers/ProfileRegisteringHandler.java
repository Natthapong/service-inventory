package th.co.truemoney.serviceinventory.legacyfacade.handlers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import th.co.truemoney.serviceinventory.ewallet.domain.TmnProfile;
import th.co.truemoney.serviceinventory.ewallet.proxy.message.AdminSecurityContext;
import th.co.truemoney.serviceinventory.ewallet.proxy.message.CreateTmnProfileRequest;
import th.co.truemoney.serviceinventory.ewallet.proxy.message.IsCreatableRequest;
import th.co.truemoney.serviceinventory.ewallet.proxy.tmnprofile.TmnProfileProxy;
import th.co.truemoney.serviceinventory.ewallet.proxy.tmnprofile.admin.TmnProfileAdminProxy;
import th.co.truemoney.serviceinventory.ewallet.proxy.util.HashPasswordUtil;
import th.co.truemoney.serviceinventory.exception.ServiceInventoryException;

public class ProfileRegisteringHandler {

	@Autowired
	private TmnProfileAdminProxy tmnProfileAdminProxy;

	@Autowired
	private TmnProfileProxy tmnProfileProxy;

	@Autowired @Qualifier("tmnProfileInitiator")
	private String tmnProfileInitiator;

	@Autowired @Qualifier("tmnProfilePin")
	private String tmnProfilePin;


	public void verifyValidRegisteringEmail(Integer channelID, String registeringEmail) throws ServiceInventoryException {
		try {
			String requestTransactionID = Long.toString(System.currentTimeMillis());
			IsCreatableRequest isCreatableRequest = createIsCreatableRequest(requestTransactionID, channelID, registeringEmail);
			tmnProfileAdminProxy.isCreatable(isCreatableRequest);
		} catch (ServiceInventoryException e) {
			e.putDate("email", registeringEmail);
			throw e;
		}
    }

	public void verifyValidRegisteringMobileNumber(Integer channelID, String registeringMobileNumber) throws ServiceInventoryException {
		try {
			String requestTransactionID = Long.toString(System.currentTimeMillis());
			IsCreatableRequest isCreatableRequest = createIsCreatableRequest(requestTransactionID, channelID, registeringMobileNumber);
			tmnProfileAdminProxy.isCreatable(isCreatableRequest);
		} catch (ServiceInventoryException e) {
			e.putDate("mobileNumber", registeringMobileNumber);
			throw e;
		}
    }

    public void register(Integer channelID, TmnProfile profile) {

    	verifyValidRegisteringEmail(channelID, profile.getEmail());
    	verifyValidRegisteringMobileNumber(channelID, profile.getMobileNumber());

    	CreateTmnProfileRequest createProfileRequest = createTmnProfileRequest(channelID, profile);
    	tmnProfileProxy.createTmnProfile(createProfileRequest);
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
		String tempEncrypted = HashPasswordUtil.encryptSHA1(initiator + tmnProfilePin).toLowerCase();
		return HashPasswordUtil.encryptSHA1(requestTransactionID + tempEncrypted).toUpperCase();
	}
	
	private AdminSecurityContext createSecurityContext(String requestTransactionID) {
		String encryptedPin = encryptSHA1(requestTransactionID);
        return new AdminSecurityContext(tmnProfileInitiator, encryptedPin);
	}
	
    private CreateTmnProfileRequest createTmnProfileRequest(Integer channelID, TmnProfile tmnProfile) {
		CreateTmnProfileRequest tmnProfileRequest = new CreateTmnProfileRequest();
		tmnProfileRequest.setChannelId(channelID);
		tmnProfileRequest.setEmail(tmnProfile.getEmail());
		tmnProfileRequest.setFullName(tmnProfile.getFullname());
		tmnProfileRequest.setMobile(tmnProfile.getMobileNumber());
		tmnProfileRequest.setPassword(tmnProfile.getPassword());
		tmnProfileRequest.setThaiId(tmnProfile.getThaiID());

		return tmnProfileRequest;
	}

    public void setTmnProfileProxy(TmnProfileProxy tmnProfileProxy) {
		this.tmnProfileProxy = tmnProfileProxy;
	}

	public void setTmnProfileAdminProxy(TmnProfileAdminProxy tmnProfileAdminProxy) {
		this.tmnProfileAdminProxy = tmnProfileAdminProxy;
	}

	public void setTmnProfileInitiator(String tmnProfileInitiator) {
		this.tmnProfileInitiator = tmnProfileInitiator;
	}

	public void setTmnProfilePin(String tmnProfilePin) {
		this.tmnProfilePin = tmnProfilePin;
	}
}
