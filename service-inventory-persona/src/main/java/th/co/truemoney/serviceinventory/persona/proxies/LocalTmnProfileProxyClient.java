package th.co.truemoney.serviceinventory.persona.proxies;

import th.co.truemoney.serviceinventory.ewallet.exception.EwalletException;
import th.co.truemoney.serviceinventory.ewallet.proxy.TmnProfileProxyClient;
import th.co.truemoney.serviceinventory.persona.Adam;
import th.co.truemoney.serviceinventory.persona.Eve;
import th.co.truemoney.serviceinventory.persona.Simpsons;

import com.tmn.core.api.message.ChangePasswordRequest;
import com.tmn.core.api.message.ChangePinRequest;
import com.tmn.core.api.message.GetProfileRequest;
import com.tmn.core.api.message.GetProfileResponse;
import com.tmn.core.api.message.UpdateProfileRequest;

public class LocalTmnProfileProxyClient implements TmnProfileProxyClient {

    private Adam adam = new Adam();
    private Eve eve = new Eve();
    private Simpsons simpsons = new Simpsons();

    private String AdamTmnMoneyID = "AdamTmnMoneyId";
    private String EveTmnMoneyID = "EveTmnMoneyId";
    private String SimpsonsTmnMoneyID = "SimpsonsTmnMoneyId";
    
	@Override
	public com.tmn.core.api.message.StandardBizResponse changePassword(
			ChangePasswordRequest changePasswordRequest)
			throws EwalletException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public com.tmn.core.api.message.StandardBizResponse changePin(
			ChangePinRequest changePinRequest) throws EwalletException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public GetProfileResponse getProfile(GetProfileRequest getProfileRequest) throws EwalletException {
        if (getProfileRequest.getSecurityContext().getTmnId().equals(AdamTmnMoneyID)) {
            return adam.getTmnProfileClient().getProfile(getProfileRequest);
        } else if (getProfileRequest.getSecurityContext().getTmnId().equals(EveTmnMoneyID)) {
            return eve.getTmnProfileClient().getProfile(getProfileRequest);
        } else if (getProfileRequest.getSecurityContext().getTmnId().equals(SimpsonsTmnMoneyID)) {
            return simpsons.getTmnProfileClient().getProfile(getProfileRequest);
        } else {
        	GetProfileResponse profileResponse = new GetProfileResponse();
        	profileResponse.setTransactionId("1");
        	profileResponse.setResultCode("0");
        	profileResponse.setResultNamespace("core");
        	profileResponse.setFullName("local");
        	profileResponse.setMobile("0891234567");
        	profileResponse.setEmail("local@tmn.com");
        	return profileResponse;
        }
	}

	@Override
	public com.tmn.core.api.message.StandardBizResponse updateProfile(
			UpdateProfileRequest updateProfileRequest) throws EwalletException {
		// TODO Auto-generated method stub
		return null;
	}
}
