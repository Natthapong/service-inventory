package th.co.truemoney.serviceinventory.persona.proxies.bills;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import th.co.truemoney.serviceinventory.engine.client.domain.ExtraXML;
import th.co.truemoney.serviceinventory.engine.client.domain.SIEngineResponse;
import th.co.truemoney.serviceinventory.engine.client.domain.SourceFee;
import th.co.truemoney.serviceinventory.engine.client.domain.services.GetBarcodeRequest;
import th.co.truemoney.serviceinventory.engine.client.domain.services.GetBarcodeResponse;
import th.co.truemoney.serviceinventory.engine.client.domain.services.GetBillRequest;
import th.co.truemoney.serviceinventory.engine.client.domain.services.GetBillResponse;
import th.co.truemoney.serviceinventory.engine.client.domain.services.InquiryOutstandingBillRequest;
import th.co.truemoney.serviceinventory.engine.client.domain.services.InquiryOutstandingBillResponse;
import th.co.truemoney.serviceinventory.engine.client.exception.SIEngineException;

public abstract class Bill {

    public abstract GetBarcodeResponse getBarcodeInformation(GetBarcodeRequest barcodeRequest, SIEngineResponse billResponse) throws SIEngineException;

    public abstract GetBillResponse getBillCodeInformation(GetBillRequest request, SIEngineResponse billResponse) throws SIEngineException;

    public abstract InquiryOutstandingBillResponse inquiryOutstandingBill(InquiryOutstandingBillRequest inquiryOutstandingBillRequest, SIEngineResponse billResponse) throws SIEngineException;

    protected void addSourceFee(SIEngineResponse billResponse, SourceFee ... sourceFees) {

        ExtraXML extraXML = billResponse.getExtraXML();
        if (extraXML == null) {
            extraXML = new ExtraXML();
        }

        List<SourceFee> sourceFeeList = new ArrayList<SourceFee>();
        if (sourceFees != null) {
            sourceFeeList.addAll(Arrays.asList(sourceFees));
        }

        extraXML.setSourceFeeList(sourceFeeList);
        billResponse.setExtraXML(extraXML);
    }

    protected SourceFee createSourceFee(String sourceType, String amount, String feeType, String min, String max) {
        SourceFee sourceFee = new SourceFee();
        sourceFee.setSource(sourceType);
        sourceFee.setSourceFee(amount);
        sourceFee.setSourceFeeType(feeType);
        sourceFee.setMinAmount(min);
        sourceFee.setMaxAmount(max);

        return sourceFee;
    }

}
