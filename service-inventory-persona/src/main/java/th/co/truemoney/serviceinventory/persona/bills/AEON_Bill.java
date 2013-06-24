package th.co.truemoney.serviceinventory.persona.bills;

import th.co.truemoney.serviceinventory.engine.client.domain.SIEngineResponse;
import th.co.truemoney.serviceinventory.engine.client.domain.SourceFee;

public class AEON_Bill extends BillTemplate {

    @Override
    public boolean isMyBillBarcode(String taxID, String ref1, String ref2, String amount) {
        return "010754400007800".equals(taxID);
    }

    @Override
    public boolean isMyBillBillCode(String billCode) {
        return "aeon".equals(billCode);
    }

    @Override
    protected void constructBillResponseTemplate(SIEngineResponse billResponse) {
        billResponse.setResultCode("0");
        billResponse.setResultDesc("Success");
        billResponse.setTransactionID(randomTransactionID());
        billResponse.setResponseMessage("Success");

        billResponse.addParameterElement("service_min_amount", "5000");
        billResponse.addParameterElement("service_max_amount", "3000000");

        billResponse.addParameterElement("target", "aeon");

        billResponse.addParameterElement("service_fee", "500");
        billResponse.addParameterElement("service_fee_type", "THB");
        billResponse.addParameterElement("title_th", "บัตรอิออน");
//        billResponse.addParameterElement("title_en", "");

//        billResponse.addParameterElement("ref1", "");
//        billResponse.addParameterElement("ref2", "");

        billResponse.addParameterElement("ref1_title_th", "เลขที่อ้างอิง1");
        billResponse.addParameterElement("ref1_title_en", "Ref. No. 1");

//        billResponse.addParameterElement("ref2_title_th", "");
//        billResponse.addParameterElement("ref2_title_en", "");

        billResponse.addParameterElement("partial_payment", "Y");

        billResponse.addParameterElement("call_center", "");
        billResponse.addParameterElement("logo", "https://secure.truemoney-dev.com/m/tmn_webview/images/logo_bill/aeon@2x.png");

        billResponse.addParameterElement("duedate_bill", "");

        SourceFee source1 = createSourceFee("EW", "0", "THB", "10000", "30000000");
        SourceFee source2 = createSourceFee("MMCC", "700", "THB", "1000", "1000000");
        addSourceFee(billResponse, source1, source2);
    }

}
