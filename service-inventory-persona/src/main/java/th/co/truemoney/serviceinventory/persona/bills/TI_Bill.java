package th.co.truemoney.serviceinventory.persona.bills;

import th.co.truemoney.serviceinventory.engine.client.domain.SIEngineResponse;
import th.co.truemoney.serviceinventory.engine.client.domain.SourceFee;

public class TI_Bill extends BillTemplate {

    @Override
    public boolean isMyBillBarcode(String taxID, String ref1, String ref2,
            String amount) {
        return "010553811410300".equals(taxID);
    }

    @Override
    public boolean isMyBillBillCode(String billCode) {
        return "ti".equals(billCode);
    }

    @Override
    protected void constructBillResponseTemplate(SIEngineResponse billResponse) {

        billResponse.addParameterElement("service_min_amount", "5000");
        billResponse.addParameterElement("service_max_amount", "3000000");

        billResponse.addParameterElement("target", "ti");

        billResponse.addParameterElement("service_fee", "0");
        billResponse.addParameterElement("service_fee_type", "THB");
        billResponse.addParameterElement("title_th", "ทรูออนไลน์");
        billResponse.addParameterElement("title_en", "True Online");

        billResponse.addParameterElement("ref1", "");
        billResponse.addParameterElement("ref2", "");

        billResponse.addParameterElement("ref1_title_th", "รหัสลูกค้า/หมายเลขโทรศัพท์");
        billResponse.addParameterElement("ref1_title_en", "Account/Phone Number");

        billResponse.addParameterElement("ref2_title_th", "เลขที่ใบแจ้งค่าใช้บริการ");
        billResponse.addParameterElement("ref2_title_en", "Invoice Number");

        billResponse.addParameterElement("partial_payment", "Y");

        billResponse.addParameterElement("call_center", "");
        billResponse.addParameterElement("logo", "https://secure.truemoney-dev.com/m/tmn_webview/images/logo_bill/ti@2x.png");

        billResponse.addParameterElement("duedate_bill", "");

        SourceFee source1 = createSourceFee("EW", "0", "THB", "10000", "30000000");
        SourceFee source2 = createSourceFee("MMCC", "700", "THB", "1000", "1000000");
        addSourceFee(billResponse, source1, source2);
    }

}
