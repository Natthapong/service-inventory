package th.co.truemoney.serviceinventory.persona.proxies;

import java.util.ArrayList;
import java.util.List;

import th.co.truemoney.serviceinventory.engine.client.domain.ExtraXML;
import th.co.truemoney.serviceinventory.engine.client.domain.SIEngineResponse;
import th.co.truemoney.serviceinventory.engine.client.domain.SourceFee;
import th.co.truemoney.serviceinventory.engine.client.domain.services.ConfirmTopUpAirtimeRequest;
import th.co.truemoney.serviceinventory.engine.client.domain.services.ConfirmTopUpAirtimeResponse;
import th.co.truemoney.serviceinventory.engine.client.domain.services.VerifyTopUpAirtimeRequest;
import th.co.truemoney.serviceinventory.engine.client.domain.services.VerifyTopUpAirtimeResponse;
import th.co.truemoney.serviceinventory.engine.client.exception.SIEngineException;
import th.co.truemoney.serviceinventory.engine.client.proxy.impl.TopUpMobileProxy;

public class TopUpTruemoneyProxy implements TopUpMobileProxy {
    @Override
    public VerifyTopUpAirtimeResponse verifyTopUpAirtime(VerifyTopUpAirtimeRequest request) throws SIEngineException {

        SIEngineResponse response = new SIEngineResponse();
        response.setResultNamespace("core");
        response.setResultCode("0");
        response.setResultDesc("SUCCESS");
        response.setReqTransactionID("3310R0041");
        response.setTransactionID("130419013811");
        response.setResponseMessage("SUCCESS");

        response.addParameterElement(
                "logo",
                "http://www3.truecorp.co.th/assets/layouts/truecorp/images/logo/logo_truemoveh.png");
        response.addParameterElement("service_min_amount", "0");
        response.addParameterElement("login_id", "user1.test.v1@gmail.com");
        response.addParameterElement("title_th", "ทรูมูฟ เอช แบบเติมเงิน");
        response.addParameterElement("amount", "10100");
        response.addParameterElement("msisdn", "0864041515");
        response.addParameterElement("service_max_amount", "200000");
        response.addParameterElement("title_en", "TrueMove-H Prepaid");
        response.addParameterElement("service_fee", "1400");
        response.addParameterElement("total_service_fee", "1400");
        response.addParameterElement("target", "TRMVH");
        response.addParameterElement("remaining_balance", "284800");
        response.addParameterElement("service_fee_type", "THB");

        ExtraXML extraXML = new ExtraXML();

        List<SourceFee> sourceFeeList = new ArrayList<SourceFee>();

        SourceFee source1 = new SourceFee();
        source1.setSource("EW");
        source1.setSourceFee("300");
        source1.setSourceFeeType("THB");
        source1.setMinAmount("1000");
        source1.setMaxAmount("1000000");

        sourceFeeList.add(source1);

        extraXML.setSourceFeeList(sourceFeeList);
        response.setExtraXML(extraXML);

        return new VerifyTopUpAirtimeResponse(response);
    }

    @Override
    public ConfirmTopUpAirtimeResponse confirmTopUpAirtime(ConfirmTopUpAirtimeRequest request) throws SIEngineException {
        SIEngineResponse response = new SIEngineResponse();
        response.setResultNamespace("ENGINE");
        response.setResultCode("0");
        response.setResultDesc("SUCCESS");
        response.setReqTransactionID("3310R0041");
        response.setTransactionID("20130419174529049074");
        response.setResponseMessage("SUCCESS");

        response.addParameterElement("trans_relation", "27e8c5d9e37746c7e");
        response.addParameterElement("remaining_balance", "284800");
        response.addParameterElement("amount", "10100");
        response.addParameterElement("msisdn", "0864041515");
        response.addParameterElement("approve_code", "35138505");

        return new ConfirmTopUpAirtimeResponse(response);
    }
}