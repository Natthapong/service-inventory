package th.co.truemoney.serviceinventory.persona.bills;

import th.co.truemoney.serviceinventory.engine.client.domain.SIEngineResponse;
import th.co.truemoney.serviceinventory.engine.client.domain.SourceFee;
import th.co.truemoney.serviceinventory.engine.client.domain.services.InquiryOutstandingBillRequest;
import th.co.truemoney.serviceinventory.engine.client.domain.services.InquiryOutstandingBillResponse;
import th.co.truemoney.serviceinventory.engine.client.exception.SIEngineException;

public class TCG_Bill extends BillTemplate {

    @Override
    public boolean isMyBillBarcode(String taxID, String ref1, String ref2, String amount) {
        return "010554614953100".equals(taxID);
    }

    @Override
    public boolean isMyBillBillCode(String billCode) {
        return "tcg".equals(billCode);
    }

    @Override
    public InquiryOutstandingBillResponse inquiryOutstandingBill(InquiryOutstandingBillRequest inquiryOutstandingBillRequest, SIEngineResponse inquiryResponse) throws SIEngineException {
        inquiryResponse.setResultCode("0");
        inquiryResponse.setResultDesc("Success");
        inquiryResponse.setReqTransactionID(inquiryOutstandingBillRequest.getReqTransactionId());
        inquiryResponse.setTransactionID(randomTransactionID());
        inquiryResponse.setResponseMessage("Success");

        inquiryResponse.addParameterElement("customer_name", "Mr.Jeerapun");
        inquiryResponse.addParameterElement("address", "บางกะปิ");
        inquiryResponse.addParameterElement("reference1", "123456789");
        inquiryResponse.addParameterElement("reference2", "13311188899");
        inquiryResponse.addParameterElement("due_date", "20140618");
        inquiryResponse.addParameterElement("status", "0");
        inquiryResponse.addParameterElement("amount", "1500");
        inquiryResponse.addParameterElement("invoice_date", "20140603");
        inquiryResponse.addParameterElement("discount_amount", "1000");

        return new InquiryOutstandingBillResponse(inquiryResponse);
    }

    @Override
    protected void constructBillResponseTemplate(SIEngineResponse billResponse) {
        billResponse.setResultCode("0");
        billResponse.setResultDesc("Success");
        billResponse.setTransactionID(randomTransactionID());
        billResponse.setResponseMessage("Success");

        billResponse.addParameterElement("service_min_amount", "5000");
        billResponse.addParameterElement("service_max_amount", "3000000");

        billResponse.addParameterElement("target", "tcg");

        billResponse.addParameterElement("service_fee", "0");
        billResponse.addParameterElement("service_fee_type", "THB");
        billResponse.addParameterElement("title_th", "ทรูไลฟ์ ฟรีวิว");
        billResponse.addParameterElement("title_en", "True Life Freeview");

        billResponse.addParameterElement("ref1", "");
        billResponse.addParameterElement("ref2", "");

        billResponse.addParameterElement("ref1_title_th", "รหัสลูกค้า");
        billResponse.addParameterElement("ref1_title_en", "Account Number");

        billResponse.addParameterElement("ref2_title_th", "เลขที่ใบแจ้งค่าใช้บริการ");
        billResponse.addParameterElement("ref2_title_en", "Invoice Number");

        billResponse.addParameterElement("partial_payment", "Y");

        billResponse.addParameterElement("call_center", "1331");
        billResponse.addParameterElement("logo", "https://secure.truemoney-dev.com/m/tmn_webview/images/logo_bill/tcg@2x.png");

        billResponse.addParameterElement("duedate_bill", "");

        SourceFee source1 = createSourceFee("EW", "0", "THB", "10000", "30000000");
        SourceFee source2 = createSourceFee("MMCC", "700", "THB", "1000", "1000000");
        addSourceFee(billResponse, source1, source2);
    }


}
