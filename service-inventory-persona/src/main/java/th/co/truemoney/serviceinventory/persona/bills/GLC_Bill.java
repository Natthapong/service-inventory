package th.co.truemoney.serviceinventory.persona.bills;

import th.co.truemoney.serviceinventory.engine.client.domain.SIEngineResponse;
import th.co.truemoney.serviceinventory.engine.client.domain.SourceFee;

public class GLC_Bill extends BillTemplate {

    @Override
    public boolean isMyBillBarcode(String taxID, String ref1, String ref2, String amount) {
        return "010753700032701".equals(taxID);
    }

    @Override
    public boolean isMyBillBillCode(String billCode) {
        return "glc".equals(billCode);
    }

    @Override
    protected void constructBillResponseTemplate(SIEngineResponse billResponse) {
        billResponse.setResultCode("0");
        billResponse.setResultDesc("Success");
        billResponse.setTransactionID(randomTransactionID());
        billResponse.setResponseMessage("Success");

        billResponse.addParameterElement("service_min_amount", "1000");
        billResponse.addParameterElement("service_max_amount", "3000000");

        billResponse.addParameterElement("target", "glc");

        billResponse.addParameterElement("service_fee", "500");
        billResponse.addParameterElement("service_fee_type", "THB");
        billResponse.addParameterElement("title_th", "สินเชื่อบริษัท กรุ๊ปลีส");
//        billResponse.addParameterElement("title_en", "");

        billResponse.addParameterElement("ref1", "");
        billResponse.addParameterElement("ref2", "");

        billResponse.addParameterElement("ref1_title_th", "รหัสลูกค้า");
        billResponse.addParameterElement("ref1_title_en", "Customer Number");

        billResponse.addParameterElement("ref2_title_th", "หมายเลขอ้างอิง");
        billResponse.addParameterElement("ref2_title_en", "Reference No");

        billResponse.addParameterElement("partial_payment", "Y");

//        billResponse.addParameterElement("call_center", "");
        billResponse.addParameterElement("logo", "https://secure.truemoney-dev.com/m/tmn_webview/images/logo_bill/glc@2x.png");

        billResponse.addParameterElement("duedate_bill", "");

        SourceFee source1 = createSourceFee("EW", "0", "THB", "10000", "30000000");
        SourceFee source2 = createSourceFee("MMCC", "700", "THB", "1000", "1000000");
        addSourceFee(billResponse, source1, source2);
    }

}
