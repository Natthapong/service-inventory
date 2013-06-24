package th.co.truemoney.serviceinventory.persona.proxies.bills;

import th.co.truemoney.serviceinventory.engine.client.domain.SIEngineResponse;
import th.co.truemoney.serviceinventory.engine.client.domain.SourceFee;
import th.co.truemoney.serviceinventory.engine.client.domain.services.GetBarcodeRequest;
import th.co.truemoney.serviceinventory.engine.client.domain.services.GetBarcodeResponse;
import th.co.truemoney.serviceinventory.engine.client.domain.services.GetBillRequest;
import th.co.truemoney.serviceinventory.engine.client.domain.services.GetBillResponse;
import th.co.truemoney.serviceinventory.engine.client.domain.services.InquiryOutstandingBillRequest;
import th.co.truemoney.serviceinventory.engine.client.domain.services.InquiryOutstandingBillResponse;
import th.co.truemoney.serviceinventory.engine.client.exception.SIEngineException;

public class TrueConvergentBill extends Bill {

    public GetBarcodeResponse getBarcodeInformation(GetBarcodeRequest barcodeRequest, SIEngineResponse billResponse) throws SIEngineException {

        billResponse.setResultCode("0");
        billResponse.setResultDesc("Success");
        billResponse.setReqTransactionID("4410A0318");
        billResponse.setTransactionID("130401012303");
        billResponse.setResponseMessage("Success");

        billResponse.addParameterElement("service_min_amount", "100");
        billResponse.addParameterElement("service_fee", "100");
        billResponse.addParameterElement("title_en", "Convergence Postpay");
        billResponse.addParameterElement("service_fee_type", "THB");
        billResponse.addParameterElement("title_th", "ค่าใช้บริการบริษัทในกลุ่มทรู");
        billResponse.addParameterElement("ref1", "010004552");
        billResponse.addParameterElement("ref2", "010520120200015601");
        billResponse.addParameterElement("ref1_title_th", "โทรศัพท์พื้นฐาน");
        billResponse.addParameterElement("ref1_title_en", "Fix Line");
        billResponse.addParameterElement("ref2_title_th", "รหัสลูกค้า");
        billResponse.addParameterElement("ref2_title_en", "Customer ID");
        billResponse.addParameterElement("call_center", "1331");
        billResponse.addParameterElement("logo", "../img/tcg.png");
        billResponse.addParameterElement("target", "tcg");
        billResponse.addParameterElement("amount", "85950");
        billResponse.addParameterElement("partial_payment", "Y");
        billResponse.addParameterElement("service_max_amount", "1000000");
        billResponse.addParameterElement("duedate_bill", "310153");

        SourceFee source1 = createSourceFee("EW", "300", "THB", "50", "10000");
        SourceFee source2 = createSourceFee("MMCC", "700", "THB", "100", "10000");
        addSourceFee(billResponse, source1, source2);

        return new GetBarcodeResponse(billResponse);
    }

    public GetBillResponse getBillCodeInformation(GetBillRequest request, SIEngineResponse billResponse) throws SIEngineException {

        billResponse.setResultCode("0");
        billResponse.setResultDesc("Success");
        billResponse.setReqTransactionID("4410A0318");
        billResponse.setTransactionID("130401012303");
        billResponse.setResponseMessage("Success");

        billResponse.addParameterElement("service_min_amount", "100");
        billResponse.addParameterElement("service_fee", "100");
        billResponse.addParameterElement("title_en", "Convergence Postpay");
        billResponse.addParameterElement("service_fee_type", "THB");
        billResponse.addParameterElement("title_th", "ค่าใช้บริการบริษัทในกลุ่มทรู");
        billResponse.addParameterElement("ref1", "010004552");
        billResponse.addParameterElement("amount", "85950");
        billResponse.addParameterElement("ref2", "010520120200015601");
        billResponse.addParameterElement("ref1_title_th", "โทรศัพท์พื้นฐาน");
        billResponse.addParameterElement("ref1_title_en", "Fix Line");
        billResponse.addParameterElement("ref2_title_th", "รหัสลูกค้า");
        billResponse.addParameterElement("ref2_title_en", "Customer ID");
        billResponse.addParameterElement("call_center", "1331");
        billResponse.addParameterElement("logo", "../img/tcg.png");
        billResponse.addParameterElement("partial_payment", "Y");
        billResponse.addParameterElement("target", "tcg");
        billResponse.addParameterElement("service_max_amount", "1000000");

        SourceFee source1 = createSourceFee("EW", "300", "THB", "50", "10000");
        SourceFee source2 = createSourceFee("MMCC", "700", "THB", "100", "10000");
        addSourceFee(billResponse, source1, source2);

        return new GetBillResponse(billResponse);
    }

    public InquiryOutstandingBillResponse inquiryOutstandingBill(InquiryOutstandingBillRequest inquiryOutstandingBillRequest, SIEngineResponse billResponse) throws SIEngineException {
        billResponse.setResultCode("0");
        billResponse.setResultDesc("Success");
        billResponse.setReqTransactionID("4410A0318");
        billResponse.setTransactionID("130401012303");
        billResponse.setResponseMessage("Success");

        billResponse.addParameterElement("customer_name", "Mr.Jeerapun");
        billResponse.addParameterElement("address", "บางกะปิ");
        billResponse.addParameterElement("reference1", "123456789");
        billResponse.addParameterElement("reference2", "13311188899");
        billResponse.addParameterElement("due_date", "20140618");
        billResponse.addParameterElement("status", "0");
        billResponse.addParameterElement("amount", "1500");
        billResponse.addParameterElement("invoice_date", "20140603");
        billResponse.addParameterElement("discount_amount", "1000");

        return new InquiryOutstandingBillResponse(billResponse);
    }

}
