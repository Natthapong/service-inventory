package th.co.truemoney.serviceinventory.persona.bills;

import th.co.truemoney.serviceinventory.engine.client.domain.SIEngineResponse;
import th.co.truemoney.serviceinventory.engine.client.domain.SourceFee;

/**
 * Testing bill for case confirmation failed
 *
 */
public class UmbrellaCorp_Bill extends BillTemplate {
	
	static final String BILLCODE = "umbrella_corporation";
	
	@Override
	public boolean isMyBillBarcode(String taxID, String ref1, String ref2,
			String amount) {
		return "666666666666666".equals(taxID);
	}

	@Override
	public boolean isMyBillBillCode(String billCode) {
		return BILLCODE.equals(billCode);
	}

	@Override
	protected void constructBillResponseTemplate(SIEngineResponse billResponse) {
        billResponse.addParameterElement("service_min_amount", "5000");
        billResponse.addParameterElement("service_max_amount", "3000000");

        billResponse.addParameterElement("target", BILLCODE);

        billResponse.addParameterElement("service_fee", "500");
        billResponse.addParameterElement("service_fee_type", "THB");
        billResponse.addParameterElement("title_th", "บัตรเครดิตเรซิเดนท์");

        billResponse.addParameterElement("ref1_title_th", "เลขที่อ้างอิง1");
        billResponse.addParameterElement("ref1_title_en", "Ref. No. 1");

        billResponse.addParameterElement("partial_payment", "Y");

        billResponse.addParameterElement("call_center", "");
        billResponse.addParameterElement("logo", "https://secure.truemoney-dev.com/m/tmn_webview/images/logo_bill/umbrella_corporation@2x.png");

        billResponse.addParameterElement("duedate_bill", "");

        SourceFee source1 = createSourceFee("EW", "0", "THB", "10000", "30000000");
        SourceFee source2 = createSourceFee("MMCC", "700", "THB", "1000", "1000000");
        addSourceFee(billResponse, source1, source2);
		
	}

}
