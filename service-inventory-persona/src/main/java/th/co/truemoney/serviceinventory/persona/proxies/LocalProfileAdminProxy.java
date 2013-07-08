package th.co.truemoney.serviceinventory.persona.proxies;

import th.co.truemoney.serviceinventory.ewallet.exception.EwalletException;
import th.co.truemoney.serviceinventory.ewallet.exception.FailResultCodeException;
import th.co.truemoney.serviceinventory.ewallet.proxy.message.ConfirmForgotPasswordRequest;
import th.co.truemoney.serviceinventory.ewallet.proxy.message.CreateForgotPasswordRequest;
import th.co.truemoney.serviceinventory.ewallet.proxy.message.CreateForgotPasswordResponse;
import th.co.truemoney.serviceinventory.ewallet.proxy.message.IsCreatableRequest;
import th.co.truemoney.serviceinventory.ewallet.proxy.message.ResetPinRequest;
import th.co.truemoney.serviceinventory.ewallet.proxy.message.StandardBizResponse;
import th.co.truemoney.serviceinventory.ewallet.proxy.message.UpdateAccountRequest;
import th.co.truemoney.serviceinventory.ewallet.proxy.message.VerifyForgotPasswordRequest;
import th.co.truemoney.serviceinventory.ewallet.proxy.message.VerifyForgotPasswordResponse;
import th.co.truemoney.serviceinventory.ewallet.proxy.tmnprofile.admin.TmnProfileAdminProxy;

public class LocalProfileAdminProxy implements TmnProfileAdminProxy {

    @Override
    public VerifyForgotPasswordResponse verifyForgotPassword(
            VerifyForgotPasswordRequest verifyForgotPasswordRequest)
            throws EwalletException {
        if (verifyForgotPasswordRequest != null) {
            if ("tokenID".equals(verifyForgotPasswordRequest.getForgotToken())) {
            	VerifyForgotPasswordResponse response = new VerifyForgotPasswordResponse("1", "0", "EWALLET-PROXY", 
            			new String[] {  }, new String[] {  }, "tmnId", "xxx@tmn.com", "0891111111");
                return response;  
            } else if ("tokenIDFake".equals(verifyForgotPasswordRequest.getForgotToken())) {
            	throw new FailResultCodeException("2", "CORE");                   
            } else {
            	throw new FailResultCodeException("401", "EWALLET-PROXY");
            }
        } else {
            throw new FailResultCodeException("401", "EWALLET-PROXY");
        }
    }

    @Override
    public StandardBizResponse confirmForgotPassword(ConfirmForgotPasswordRequest confirmForgotPasswordRequest)
            throws EwalletException {
        if (confirmForgotPasswordRequest != null) {
            if ("tokenID".equals(confirmForgotPasswordRequest.getForgotToken()) 
            		&& "newPassword".equals(confirmForgotPasswordRequest.getNewPin())
            		&& "xxx@tmn.com".equals(confirmForgotPasswordRequest.getLoginId())) {
            	StandardBizResponse response = new StandardBizResponse("1", "0", "EWALLET-PROXY", new String[] { }, new String[] { });
                return response;         
            } else if ("tokenIDFake".equals(confirmForgotPasswordRequest.getForgotToken())) {
            	throw new FailResultCodeException("3", "CORE");                         
            } else {
            	throw new FailResultCodeException("401", "EWALLET-PROXY");
            }
        } else {
            throw new FailResultCodeException("401", "EWALLET-PROXY");
        }
    }

    @Override
    public CreateForgotPasswordResponse createForgotPassword(CreateForgotPasswordRequest createForgotPasswordRequest)
            throws EwalletException {
        if (createForgotPasswordRequest != null) {
            if ("xxx@tmn.com".equals(createForgotPasswordRequest.getLoginId())) {
            	CreateForgotPasswordResponse response = new CreateForgotPasswordResponse("1", "0", "EWALLET-PROXY", new String[] {  }, new String[] {  }, "tokenID");
                return response;          
            } else if ("yyy@tmn.com".equals(createForgotPasswordRequest.getLoginId())) {
            	throw new FailResultCodeException("1", "CORE");   
            } else {
            	throw new FailResultCodeException("401", "EWALLET-PROXY");
            }
        } else {
            throw new FailResultCodeException("401", "EWALLET-PROXY");
        }
    }

    @Override
    public StandardBizResponse isCreatable(IsCreatableRequest isCreatableRequest) throws EwalletException {
        if(isCreatableRequest != null) {
            if ("local@tmn.com".equals(isCreatableRequest.getLoginId())) {
                throw new FailResultCodeException("401", "EWALLET-PROXY");
            } else {
                return new StandardBizResponse("1", "0", "EWALLET-PROXY", new String[] { "email" }, new String[] { isCreatableRequest.getLoginId() });
            }

        }else{
            throw new FailResultCodeException("401", "EWALLET-PROXY");
        }
    }

    @Override
    public StandardBizResponse updateAccount(UpdateAccountRequest updateAccountRequest)
            throws EwalletException {
        return null;
    }

	@Override
	public StandardBizResponse resetPin(ResetPinRequest resetPinRequest)
			throws EwalletException {
		return null;
	}

}
