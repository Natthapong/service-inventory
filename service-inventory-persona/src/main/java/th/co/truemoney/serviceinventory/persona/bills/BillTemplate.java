package th.co.truemoney.serviceinventory.persona.bills;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

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

public abstract class BillTemplate {

    public GetBarcodeResponse getBarcodeInformation(GetBarcodeRequest barcodeRequest, SIEngineResponse billResponse)
            throws SIEngineException {

        billResponse.setResultCode("0");
        billResponse.setResultDesc("Success");
        billResponse.setTransactionID(randomTransactionID());
        billResponse.setResponseMessage("Success");


        BarcodeResult barcodeResult = extractBarcode(barcodeRequest.getBarcode());

        constructBillResponseTemplate(billResponse);

        billResponse.setReqTransactionID(barcodeRequest.getReqTransactionId());
        billResponse.addParameterElement("ref1", barcodeResult.getRef1());

        if (barcodeResult.getRef2() != null) {
            billResponse.addParameterElement("ref2", barcodeResult.getRef2());
        }

        billResponse.addParameterElement("amount", barcodeResult.getAmount());

        return new GetBarcodeResponse(billResponse);
    }

    private BarcodeResult extractBarcode(String barcode) {
        BarcodeExtractor barcodeExtractor = BarcodeExtractorFactory.getInstance(barcode);
        return barcodeExtractor.extract(barcode);
    }

    public GetBillResponse getBillCodeInformation(GetBillRequest billCodeRequest, SIEngineResponse inquiryResponse)
            throws SIEngineException {

        inquiryResponse.setResultCode("0");
        inquiryResponse.setResultDesc("Success");
        inquiryResponse.setTransactionID(randomTransactionID());
        inquiryResponse.setResponseMessage("Success");

        constructBillResponseTemplate(inquiryResponse);
        inquiryResponse.setReqTransactionID(billCodeRequest.getReqTransactionId());

        return new GetBillResponse(inquiryResponse);
    }

    public InquiryOutstandingBillResponse inquiryOutstandingBill(InquiryOutstandingBillRequest inquiryRequest, SIEngineResponse billResponse) throws SIEngineException {
        throw new OnlineInquiryNotSupportException(inquiryRequest);
    }

    public abstract boolean isMyBillBarcode(String taxID, String ref1, String ref2, String amount);

    public abstract boolean isMyBillBillCode(String billCode);

    protected abstract void constructBillResponseTemplate(SIEngineResponse billResponse);

    protected static void addSourceFee(SIEngineResponse billResponse, SourceFee ... sourceFees) {

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

    protected static SourceFee createSourceFee(String sourceType, String amount, String feeType, String min, String max) {
        SourceFee sourceFee = new SourceFee();
        sourceFee.setSource(sourceType);
        sourceFee.setSourceFee(amount);
        sourceFee.setSourceFeeType(feeType);
        sourceFee.setMinAmount(min);
        sourceFee.setMaxAmount(max);

        return sourceFee;
    }

    protected static String randomTransactionID() {
        return new Random().nextLong() + "";
    }


}
