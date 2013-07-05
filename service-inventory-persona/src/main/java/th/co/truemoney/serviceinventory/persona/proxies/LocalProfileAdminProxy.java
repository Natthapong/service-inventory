package th.co.truemoney.serviceinventory.persona.proxies;

import th.co.truemoney.serviceinventory.ewallet.exception.EwalletException;
import th.co.truemoney.serviceinventory.ewallet.exception.FailResultCodeException;
import th.co.truemoney.serviceinventory.ewallet.proxy.message.ConfirmForgotPasswordRequest;
import th.co.truemoney.serviceinventory.ewallet.proxy.message.CreateForgotPasswordRequest;
import th.co.truemoney.serviceinventory.ewallet.proxy.message.CreateForgotPasswordResponse;
import th.co.truemoney.serviceinventory.ewallet.proxy.message.IsCreatableRequest;
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
        return new VerifyForgotPasswordResponse();
    }

    @Override
    public StandardBizResponse confirmForgotPassword(
            ConfirmForgotPasswordRequest confirmForgotPasswordRequest)
            throws EwalletException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public CreateForgotPasswordResponse createForgotPassword(
            CreateForgotPasswordRequest createForgotPasswordRequest)
            throws EwalletException {
        // TODO Auto-generated method stub
        return null;
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

}
