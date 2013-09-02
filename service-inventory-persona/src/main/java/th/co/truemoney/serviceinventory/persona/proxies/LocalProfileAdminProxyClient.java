package th.co.truemoney.serviceinventory.persona.proxies;

import th.co.truemoney.serviceinventory.ewallet.exception.EwalletException;
import th.co.truemoney.serviceinventory.ewallet.exception.FailResultCodeException;
import th.co.truemoney.serviceinventory.ewallet.proxy.TmnProfileAdminProxyClient;

import com.tmn.core.api.message.ConfirmForgotPasswordRequest;
import com.tmn.core.api.message.CreateForgotPasswordRequest;
import com.tmn.core.api.message.CreateForgotPasswordResponse;
import com.tmn.core.api.message.IsCreatableRequest;
import com.tmn.core.api.message.StandardBizResponse;
import com.tmn.core.api.message.VerifyForgotPasswordRequest;
import com.tmn.core.api.message.VerifyForgotPasswordResponse;

public class LocalProfileAdminProxyClient implements TmnProfileAdminProxyClient {

    @Override
    public VerifyForgotPasswordResponse verifyForgotPassword(
            VerifyForgotPasswordRequest verifyForgotPasswordRequest)
            throws EwalletException {
        if (verifyForgotPasswordRequest != null) {
            if ("tokenID".equals(verifyForgotPasswordRequest.getForgotToken())) {
            	VerifyForgotPasswordResponse response = new VerifyForgotPasswordResponse();
            	response.setTransactionId("1");
            	response.setResultCode("0");
            	response.setResultNamespace("EWALLET-PROXY");
            	response.setLoginId("xxx@tmn.com");
            	response.setMobile("0891111111");
            	response.setTmnId("tmnId");
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
            	StandardBizResponse response = new StandardBizResponse();
            	response.setTransactionId("1");
            	response.setResultCode("0");
            	response.setResultNamespace("EWALLET-PROXY");
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
            	CreateForgotPasswordResponse response = new CreateForgotPasswordResponse();
            	response.setTransactionId("1");
            	response.setResultCode("0");
            	response.setResultNamespace("EWALLET-PROXY");
            	response.setForgotToken("tokenID");
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
                StandardBizResponse response = new StandardBizResponse();
                response.setTransactionId("1");
            	response.setResultCode("0");
            	response.setResultNamespace("EWALLET-PROXY");
            	response.setDetailKey(new String[] { "email" });
            	response.setDetailValue(new String[] { isCreatableRequest.getLoginId() });
            	return response;
            }

        }else{
            throw new FailResultCodeException("401", "EWALLET-PROXY");
        }
    }

}
