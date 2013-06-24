package th.co.truemoney.serviceinventory.persona.bills;

import th.co.truemoney.serviceinventory.engine.client.domain.SIEngineResponse;
import th.co.truemoney.serviceinventory.engine.client.domain.SourceFee;

public class TLI_Bill extends BillTemplate {

    @Override
    public boolean isMyBillBarcode(String taxID, String ref1, String ref2, String amount) {
        return "010755500010401".equals(taxID);
    }

    @Override
    public boolean isMyBillBillCode(String billCode) {
        return "tli".equals(billCode);
    }

    @Override
    protected void constructBillResponseTemplate(SIEngineResponse billResponse) {

        billResponse.addParameterElement("service_min_amount", "1000");
        billResponse.addParameterElement("service_max_amount", "3000000");

        billResponse.addParameterElement("target", "tli");

        billResponse.addParameterElement("service_fee", "0");
        billResponse.addParameterElement("service_fee_type", "THB");
        billResponse.addParameterElement("title_th", "ไทยประกันชีวิต");

        billResponse.addParameterElement("ref1", "");
        billResponse.addParameterElement("ref2", "");

        billResponse.addParameterElement("ref1_title_th", "Ref. No. 1/Cust. No.");
        billResponse.addParameterElement("ref1_title_en", "Ref. No. 1/Cust. No.");

        billResponse.addParameterElement("ref2_title_th", "Ref. No. 2");
        billResponse.addParameterElement("ref2_title_en", "Ref. No. 2");

        billResponse.addParameterElement("partial_payment", "N");

        billResponse.addParameterElement("call_center", "");
        billResponse.addParameterElement("logo", "https://secure.truemoney-dev.com/m/tmn_webview/images/logo_bill/tli@2x.png");

        billResponse.addParameterElement("duedate_bill", "");

        SourceFee source1 = createSourceFee("EW", "0", "THB", "10000", "30000000");
        SourceFee source2 = createSourceFee("MMCC", "700", "THB", "1000", "1000000");
        addSourceFee(billResponse, source1, source2);
    }

}
