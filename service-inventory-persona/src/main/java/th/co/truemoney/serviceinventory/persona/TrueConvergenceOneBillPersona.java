package th.co.truemoney.serviceinventory.persona;

import java.util.ArrayList;
import java.util.List;

import th.co.truemoney.serviceinventory.engine.client.domain.ExtraXML;
import th.co.truemoney.serviceinventory.engine.client.domain.SIEngineResponse;
import th.co.truemoney.serviceinventory.engine.client.domain.SourceFee;
import th.co.truemoney.serviceinventory.engine.client.domain.services.ConfirmBillPayRequest;
import th.co.truemoney.serviceinventory.engine.client.domain.services.ConfirmBillPayResponse;
import th.co.truemoney.serviceinventory.engine.client.domain.services.ConfirmTopUpAirtimeRequest;
import th.co.truemoney.serviceinventory.engine.client.domain.services.ConfirmTopUpAirtimeResponse;
import th.co.truemoney.serviceinventory.engine.client.domain.services.GetBarcodeRequest;
import th.co.truemoney.serviceinventory.engine.client.domain.services.GetBarcodeResponse;
import th.co.truemoney.serviceinventory.engine.client.domain.services.GetBillRequest;
import th.co.truemoney.serviceinventory.engine.client.domain.services.GetBillResponse;
import th.co.truemoney.serviceinventory.engine.client.domain.services.InquiryOutstandingBillRequest;
import th.co.truemoney.serviceinventory.engine.client.domain.services.InquiryOutstandingBillResponse;
import th.co.truemoney.serviceinventory.engine.client.domain.services.VerifyBillPayRequest;
import th.co.truemoney.serviceinventory.engine.client.domain.services.VerifyBillPayResponse;
import th.co.truemoney.serviceinventory.engine.client.domain.services.VerifyTopUpAirtimeRequest;
import th.co.truemoney.serviceinventory.engine.client.domain.services.VerifyTopUpAirtimeResponse;
import th.co.truemoney.serviceinventory.engine.client.exception.SIEngineException;
import th.co.truemoney.serviceinventory.engine.client.proxy.impl.BillProxy;
import th.co.truemoney.serviceinventory.engine.client.proxy.impl.TopUpMobileProxy;

public class TrueConvergenceOneBillPersona implements BarcodePersona {

    @Override
    public BillProxy getBillPayProxy() {

        return new BillProxy() {

			@Override
            public GetBarcodeResponse getBarcodeInformation(GetBarcodeRequest barcodeRequest) throws SIEngineException {
                SIEngineResponse billResponse = new SIEngineResponse();
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
                billResponse.addParameterElement("service_max_amount", "1000000");
                billResponse.addParameterElement("duedate_bill", "310153");

                ExtraXML extraXML = new ExtraXML();

                List<SourceFee> sourceFeeList = new ArrayList<SourceFee>();

                SourceFee source1 = new SourceFee();
                source1.setSource("EW");
                source1.setSourceFee("300");
                source1.setSourceFeeType("THB");
                source1.setMinAmount("1000");
                source1.setMaxAmount("1000000");

                SourceFee source2 = new SourceFee();
                source2.setSource("MMCC");
                source2.setSourceFee("700");
                source2.setSourceFeeType("THB");
                source2.setMinAmount("1000");
                source2.setMaxAmount("1000000");

                sourceFeeList.add(source1);
                sourceFeeList.add(source2);

                extraXML.setSourceFeeList(sourceFeeList);
                billResponse.setExtraXML(extraXML);

                return new GetBarcodeResponse(billResponse);
            }

            @Override
            public VerifyBillPayResponse verifyBillPay(VerifyBillPayRequest billPayRequest)
                    throws SIEngineException {

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
            public ConfirmBillPayResponse confirmBillPay(ConfirmBillPayRequest billPayRequest)
                    throws SIEngineException {

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

			@Override
			public GetBillResponse getBillCodeInformation(GetBillRequest request)
					throws SIEngineException {
				  SIEngineResponse billResponse = new SIEngineResponse();
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
	                billResponse.addParameterElement("target", "tcg");
	                billResponse.addParameterElement("service_max_amount", "1000000");

	                ExtraXML extraXML = new ExtraXML();

	                List<SourceFee> sourceFeeList = new ArrayList<SourceFee>();

	                SourceFee source1 = new SourceFee();
	                source1.setSource("EW");
	                source1.setSourceFee("300");
	                source1.setSourceFeeType("THB");
	                source1.setMinAmount("1000");
	                source1.setMaxAmount("1000000");

	                SourceFee source2 = new SourceFee();
	                source2.setSource("MMCC");
	                source2.setSourceFee("700");
	                source2.setSourceFeeType("THB");
	                source2.setMinAmount("1000");
	                source2.setMaxAmount("1000000");

	                sourceFeeList.add(source1);
	                sourceFeeList.add(source2);

	                extraXML.setSourceFeeList(sourceFeeList);
	                billResponse.setExtraXML(extraXML);

	            return new GetBillResponse(billResponse);
			}
            
			@Override
			public InquiryOutstandingBillResponse inquiryOutstandingBill(InquiryOutstandingBillRequest inquiryOutstandingBillRequest)
					throws SIEngineException {
				 SIEngineResponse billResponse = new SIEngineResponse();
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
        };
    }

    @Override
    public TopUpMobileProxy getTopUpMobileProxy() {
        return new TopUpMobileProxy() {

            @Override
            public VerifyTopUpAirtimeResponse verifyTopUpAirtime(
                    VerifyTopUpAirtimeRequest request) throws SIEngineException {

                SIEngineResponse response = new SIEngineResponse();
                response.setResultNamespace("core");
                response.setResultCode("0");
                response.setResultDesc("SUCCESS");
                response.setReqTransactionID("3310R0041");
                response.setTransactionID("130419013811");
                response.setResponseMessage("SUCCESS");

                response.addParameterElement("logo", "http://www3.truecorp.co.th/assets/layouts/truecorp/images/logo/logo_truemoveh.png");
                response.addParameterElement("service_min_amount", "0");
                response.addParameterElement("login_id", "user1.test.v1@gmail.com");
                response.addParameterElement("title_th", "ทรูมูฟ เอช แบบเติมเงิน");
                response.addParameterElement("amount", "10100");
                response.addParameterElement("msisdn", "0864041515");
                response.addParameterElement("service_max_amount", "200000");
                response.addParameterElement("title_en", "TrueMove-H Prepaid");
                response.addParameterElement("service_fee", "1400");
                response.addParameterElement("total_service_fee", "1400");
                response.addParameterElement("target", "TRMVH");
                response.addParameterElement("remaining_balance", "284800");
                response.addParameterElement("service_fee_type", "THB");

                ExtraXML extraXML = new ExtraXML();

                List<SourceFee> sourceFeeList = new ArrayList<SourceFee>();

                SourceFee source1 = new SourceFee();
                source1.setSource("EW");
                source1.setSourceFee("300");
                source1.setSourceFeeType("THB");
                source1.setMinAmount("1000");
                source1.setMaxAmount("1000000");

                sourceFeeList.add(source1);

                extraXML.setSourceFeeList(sourceFeeList);
                response.setExtraXML(extraXML);

                return new VerifyTopUpAirtimeResponse(response);
            }

            @Override
            public ConfirmTopUpAirtimeResponse confirmTopUpAirtime(ConfirmTopUpAirtimeRequest request) throws SIEngineException {
                SIEngineResponse response = new SIEngineResponse();
                response.setResultNamespace("ENGINE");
                response.setResultCode("0");
                response.setResultDesc("SUCCESS");
                response.setReqTransactionID("3310R0041");
                response.setTransactionID("20130419174529049074");
                response.setResponseMessage("SUCCESS");

                response.addParameterElement("trans_relation", "27e8c5d9e37746c7e");
                response.addParameterElement("remaining_balance", "284800");
                response.addParameterElement("amount", "10100");
                response.addParameterElement("msisdn", "0864041515");
                response.addParameterElement("approve_code", "35138505");

                return new ConfirmTopUpAirtimeResponse(response);
            }
        };
    }

}
