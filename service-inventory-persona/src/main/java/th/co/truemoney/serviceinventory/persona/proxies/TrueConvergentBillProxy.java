package th.co.truemoney.serviceinventory.persona.proxies;

import th.co.truemoney.serviceinventory.engine.client.domain.SIEngineResponse;
import th.co.truemoney.serviceinventory.engine.client.domain.services.ConfirmBillPayRequest;
import th.co.truemoney.serviceinventory.engine.client.domain.services.ConfirmBillPayResponse;
import th.co.truemoney.serviceinventory.engine.client.domain.services.GetBarcodeRequest;
import th.co.truemoney.serviceinventory.engine.client.domain.services.GetBarcodeResponse;
import th.co.truemoney.serviceinventory.engine.client.domain.services.GetBillRequest;
import th.co.truemoney.serviceinventory.engine.client.domain.services.GetBillResponse;
import th.co.truemoney.serviceinventory.engine.client.domain.services.InquiryOutstandingBillRequest;
import th.co.truemoney.serviceinventory.engine.client.domain.services.InquiryOutstandingBillResponse;
import th.co.truemoney.serviceinventory.engine.client.domain.services.VerifyBillPayRequest;
import th.co.truemoney.serviceinventory.engine.client.domain.services.VerifyBillPayResponse;
import th.co.truemoney.serviceinventory.engine.client.exception.FailResultCodeException;
import th.co.truemoney.serviceinventory.engine.client.exception.SIEngineException;
import th.co.truemoney.serviceinventory.engine.client.proxy.impl.BillProxy;
import th.co.truemoney.serviceinventory.persona.bills.BarcodeExtractor;
import th.co.truemoney.serviceinventory.persona.bills.BarcodeExtractorFactory;
import th.co.truemoney.serviceinventory.persona.bills.BarcodeResult;
import th.co.truemoney.serviceinventory.persona.bills.KnownBills;

public class TrueConvergentBillProxy implements BillProxy {

    @Override
    public GetBarcodeResponse getBarcodeInformation(
            GetBarcodeRequest barcodeRequest) throws SIEngineException {

        String barcode = barcodeRequest.getBarcode();
        BarcodeExtractor barcodeExtractor = BarcodeExtractorFactory.getInstance(barcode);
        BarcodeResult barcodeResult = barcodeExtractor.extract(barcode);

        KnownBills bill = KnownBills.getBillTemplateFromBarcode(barcodeResult.getTaxID(), barcodeResult.getRef1(), barcodeResult.getRef2(), barcodeResult.getAmount());
        return bill.getBillTemplate().getBarcodeInformation(barcodeRequest, new SIEngineResponse());
    }


    @Override
    public GetBillResponse getBillCodeInformation(GetBillRequest billCodeRequest)
            throws SIEngineException {

        String billCode = billCodeRequest.getBillCode();
        KnownBills bill = KnownBills.getBillTemplateFromBillCode(billCode);
        return bill.getBillTemplate().getBillCodeInformation(billCodeRequest, new SIEngineResponse());
    }

    @Override
    public InquiryOutstandingBillResponse inquiryOutstandingBill(InquiryOutstandingBillRequest inquiryRequest)
            throws SIEngineException {

        String billCode = inquiryRequest.getBillCode();
        KnownBills bill = KnownBills.getBillTemplateFromBillCode(billCode);
        return bill.getBillTemplate().inquiryOutstandingBill(inquiryRequest, new SIEngineResponse());

    }

    @Override
    public VerifyBillPayResponse verifyBillPay(VerifyBillPayRequest billPayRequest) throws SIEngineException {

        SIEngineResponse billResponse = new SIEngineResponse();

        billResponse.setResultCode("0");
        billResponse.setResultNamespace("PCS");
        billResponse.setResultDesc("This Bill is verified");
        billResponse.setReqTransactionID("4410A0322");
        billResponse.setTransactionID("130401012310");
        billResponse.setResponseMessage("This Action is successful");

        billResponse.addParameterElement("ref1", "02110004198411");
        billResponse.addParameterElement("ref2", "22060100300001");
        billResponse.addParameterElement("amount", "100");
        billResponse.addParameterElement("source", "EW");

        return new VerifyBillPayResponse(billResponse);
    }

    @Override
    public ConfirmBillPayResponse confirmBillPay(ConfirmBillPayRequest billPayRequest) throws SIEngineException {
    	
    	if (isFailCase(billPayRequest)) {
    		throw new FailResultCodeException("666666", "UMC-SERVICE");
    	}
    	
        SIEngineResponse billResponse = new SIEngineResponse();

        billResponse.setResultCode("0");
        billResponse.setResultNamespace("ENGINE");
        billResponse.setResultDesc("This Transaction is completed");
        billResponse.setReqTransactionID("4410A0334");
        billResponse.setTransactionID("20130401125936048554");
        billResponse.setResponseMessage("Success");

        billResponse.addParameterElement("trans_relation", "27e8c5d9e37746c7e");
        billResponse.addParameterElement("remaining_balance", "50000");
        billResponse.addParameterElement("amount", "100");
        billResponse.addParameterElement("msisdn", "0891267357");

        return new ConfirmBillPayResponse(billResponse);
    }
    
    private boolean isFailCase(ConfirmBillPayRequest req) {
    	String target = req.getBillRequest().getParameterValue("target");
    	return "umbrella_corporation".equalsIgnoreCase(target);
    }
}
