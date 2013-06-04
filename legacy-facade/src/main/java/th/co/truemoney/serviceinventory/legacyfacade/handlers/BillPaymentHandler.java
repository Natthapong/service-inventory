package th.co.truemoney.serviceinventory.legacyfacade.handlers;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import th.co.truemoney.serviceinventory.bill.domain.Bill;
import th.co.truemoney.serviceinventory.bill.domain.BillPaymentConfirmationInfo;
import th.co.truemoney.serviceinventory.bill.domain.ServiceFeeInfo;
import th.co.truemoney.serviceinventory.bill.domain.SourceOfFund;
import th.co.truemoney.serviceinventory.engine.client.domain.SourceFee;
import th.co.truemoney.serviceinventory.engine.client.domain.services.ConfirmBillPayRequest;
import th.co.truemoney.serviceinventory.engine.client.domain.services.ConfirmBillPayResponse;
import th.co.truemoney.serviceinventory.engine.client.domain.services.GetBarcodeRequest;
import th.co.truemoney.serviceinventory.engine.client.domain.services.GetBarcodeResponse;
import th.co.truemoney.serviceinventory.engine.client.domain.services.GetBillRequest;
import th.co.truemoney.serviceinventory.engine.client.domain.services.GetBillResponse;
import th.co.truemoney.serviceinventory.engine.client.domain.services.VerifyBillPayRequest;
import th.co.truemoney.serviceinventory.engine.client.domain.services.VerifyBillPayResponse;
import th.co.truemoney.serviceinventory.engine.client.exception.FailResultCodeException;
import th.co.truemoney.serviceinventory.engine.client.exception.SIEngineException;
import th.co.truemoney.serviceinventory.engine.client.proxy.impl.BillProxy;
import th.co.truemoney.serviceinventory.exception.ServiceInventoryException;

public class BillPaymentHandler {

    @Autowired
    private BillProxy billPayProxy;

    public String verify(VerifyBillPayRequest billPayRequest){
        try {
        	VerifyBillPayResponse verifyResponse = billPayProxy.verifyBillPay(billPayRequest);
            return verifyResponse.getTransactionID();
        } catch(FailResultCodeException ex) {
            throw new VerifyBillPayFailException(ex);
        }
    }

    public BillPaymentConfirmationInfo payBill(ConfirmBillPayRequest billRequest) {
        try {
            SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm");
            ConfirmBillPayResponse billPayResponse = billPayProxy.confirmBillPay(billRequest);
            BillPaymentConfirmationInfo confirmationInfo = new BillPaymentConfirmationInfo();
            confirmationInfo.setTransactionID(billPayResponse.getApproveCode());
            confirmationInfo.setTransactionDate(df.format(new Date()));
            return confirmationInfo;
        } catch (FailResultCodeException ex) {
            // TODO map exception to corresponding throw
            throw new UnknownSystemTransactionFailException(ex);
        }
    }

    public Bill getBarcodeInformation(GetBarcodeRequest request) {
        try {
            GetBarcodeResponse barcodeResponse = billPayProxy.getBarcodeInformation(request);

            Bill billInfo = new Bill();
            billInfo.setTarget(barcodeResponse.getTarget());
            billInfo.setLogoURL(barcodeResponse.getLogo());
            billInfo.setTitleTH(barcodeResponse.getTitleTH());
            billInfo.setTitleEN(barcodeResponse.getTitleEN());

            billInfo.setRef1TitleTH(barcodeResponse.getRef1TitleTH());
            billInfo.setRef1TitleEN(barcodeResponse.getRef1TitleEN());
            billInfo.setRef1(barcodeResponse.getRef1());

            billInfo.setRef2TitleTH(barcodeResponse.getRef2TitleTH());
            billInfo.setRef2TitleEN(barcodeResponse.getRef2TitleEN());
            billInfo.setRef2(barcodeResponse.getRef2());

            billInfo.setPartialPayment(barcodeResponse.getPartialPayment());
            billInfo.setCallCenterNumber(barcodeResponse.getCallCenterNumber());
            billInfo.setAmount(barcodeResponse.getAmount());
            billInfo.setMinAmount(barcodeResponse.getMinAmount());
            billInfo.setMaxAmount(barcodeResponse.getMaxAmount());

            ServiceFeeInfo serviceFee = createServiceFee(barcodeResponse);
            billInfo.setServiceFee(serviceFee);

            List<SourceOfFund> sourceOfFundFees = createSourceOfFundFeeList(barcodeResponse);
            billInfo.setSourceOfFundFees(sourceOfFundFees.toArray(new SourceOfFund[sourceOfFundFees.size()]));

            billInfo.setDueDate(barcodeResponse.getDueDate());

            return billInfo;

        } catch (FailResultCodeException ex) {
            String errorNamespace = ex.getNamespace();
            if ("SIENGINE".equals(errorNamespace)) {
                throw new SIEngineTransactionFailException(ex);
            } else if ("UMARKET".equalsIgnoreCase(errorNamespace)) {
                throw new UMarketSystemTransactionFailException(ex);
            } else {
                throw new UnknownSystemTransactionFailException(ex);
            }
        }
    }

    public Bill getBillCodeInformation(GetBillRequest request) {
        try {
            GetBillResponse billResponse = billPayProxy.getBillCodeInformation(request);

            Bill billInfo = new Bill();
            billInfo.setTarget(billResponse.getTarget());
            billInfo.setLogoURL(billResponse.getLogo());
            billInfo.setTitleTH(billResponse.getTitleTH());
            billInfo.setTitleEN(billResponse.getTitleEN());

            billInfo.setRef1TitleTH(billResponse.getRef1TitleTH());
            billInfo.setRef1TitleEN(billResponse.getRef1TitleEN());
            billInfo.setRef1(billResponse.getRef1());

            billInfo.setRef2TitleTH(billResponse.getRef2TitleTH());
            billInfo.setRef2TitleEN(billResponse.getRef2TitleEN());
            billInfo.setRef2(billResponse.getRef2());

            billInfo.setPartialPayment(billResponse.getPartialPayment());
            billInfo.setCallCenterNumber(billResponse.getCallCenterNumber());
            billInfo.setAmount(billResponse.getAmount());
            billInfo.setMinAmount(billResponse.getMinAmount());
            billInfo.setMaxAmount(billResponse.getMaxAmount());

            ServiceFeeInfo serviceFee = createServiceFee(billResponse);
            billInfo.setServiceFee(serviceFee);

            List<SourceOfFund> sourceOfFundFees = createSourceOfFundFeeList(billResponse);
            billInfo.setSourceOfFundFees(sourceOfFundFees.toArray(new SourceOfFund[sourceOfFundFees.size()]));

            billInfo.setDueDate(billResponse.getDueDate());

            return billInfo;

        } catch (FailResultCodeException ex) {
            String errorNamespace = ex.getNamespace();
            if ("SIENGINE".equals(errorNamespace)) {
                throw new SIEngineTransactionFailException(ex);
            } else if ("UMARKET".equalsIgnoreCase(errorNamespace)) {
                throw new UMarketSystemTransactionFailException(ex);
            } else {
                throw new UnknownSystemTransactionFailException(ex);
            }
        }
    }
    
    private List<SourceOfFund> createSourceOfFundFeeList(GetBarcodeResponse barcodeResponse) {
        List<SourceFee> sourceOfFundList = barcodeResponse.getExtraXML().getSourceFeeList();
        List<SourceOfFund> sourceOfFundFees = new ArrayList<SourceOfFund>();
        for (SourceFee sourceFee : sourceOfFundList) {

            SourceOfFund sourceOfFundFee = new SourceOfFund();
            sourceOfFundFee.setSourceType(sourceFee.getSource());
            sourceOfFundFee.setFeeRateType(sourceFee.getSourceFeeType());

            BigDecimal calculatedSourceFee = calculateSourceFee(sourceFee, sourceOfFundFee);
            sourceOfFundFee.setFeeRate(calculatedSourceFee.setScale(2, RoundingMode.HALF_UP));
            sourceOfFundFee.setMinFeeAmount(convertStringToFraction(sourceFee.getMinAmount()));
            sourceOfFundFee.setMaxFeeAmount(convertStringToFraction(sourceFee.getMaxAmount()));

            sourceOfFundFees.add(sourceOfFundFee);
        }
        return sourceOfFundFees;
    }

    private ServiceFeeInfo createServiceFee(GetBarcodeResponse barcodeResponse) {
        ServiceFeeInfo serviceFee = new ServiceFeeInfo();
        serviceFee.setFeeRateType(barcodeResponse.getServiceFeeType());
        BigDecimal decimalServiceFee = BigDecimal.ZERO;
        if (serviceFee.getFeeRateType().equals("THB")) {
            // fee type = fix
            BigDecimal fee = barcodeResponse.getServiceFee() != null ? new BigDecimal(barcodeResponse.getServiceFee()) : BigDecimal.ZERO;
            decimalServiceFee = fee.divide(new BigDecimal("100"));
        } else {
            // fee type = percent
            decimalServiceFee = barcodeResponse.getServiceFee() != null ? new BigDecimal(barcodeResponse.getServiceFee()) : BigDecimal.ZERO;
        }
        serviceFee.setFeeRate(decimalServiceFee.setScale(2, RoundingMode.HALF_UP));
        return serviceFee;
    }
    
    private List<SourceOfFund> createSourceOfFundFeeList(GetBillResponse barcodeResponse) {
        List<SourceFee> sourceOfFundList = barcodeResponse.getExtraXML().getSourceFeeList();
        List<SourceOfFund> sourceOfFundFees = new ArrayList<SourceOfFund>();
        for (SourceFee sourceFee : sourceOfFundList) {

            SourceOfFund sourceOfFundFee = new SourceOfFund();
            sourceOfFundFee.setSourceType(sourceFee.getSource());
            sourceOfFundFee.setFeeRateType(sourceFee.getSourceFeeType());

            BigDecimal calculatedSourceFee = calculateSourceFee(sourceFee, sourceOfFundFee);
            sourceOfFundFee.setFeeRate(calculatedSourceFee.setScale(2, RoundingMode.HALF_UP));
            sourceOfFundFee.setMinFeeAmount(convertStringToFraction(sourceFee.getMinAmount()));
            sourceOfFundFee.setMaxFeeAmount(convertStringToFraction(sourceFee.getMaxAmount()));

            sourceOfFundFees.add(sourceOfFundFee);
        }
        return sourceOfFundFees;
    }

    private ServiceFeeInfo createServiceFee(GetBillResponse barcodeResponse) {
        ServiceFeeInfo serviceFee = new ServiceFeeInfo();
        serviceFee.setFeeRateType(barcodeResponse.getServiceFeeType());
        BigDecimal decimalServiceFee = BigDecimal.ZERO;
        if (serviceFee.getFeeRateType().equals("THB")) {
            // fee type = fix
            BigDecimal fee = barcodeResponse.getServiceFee() != null ? new BigDecimal(barcodeResponse.getServiceFee()) : BigDecimal.ZERO;
            decimalServiceFee = fee.divide(new BigDecimal("100"));
        } else {
            // fee type = percent
            decimalServiceFee = barcodeResponse.getServiceFee() != null ? new BigDecimal(barcodeResponse.getServiceFee()) : BigDecimal.ZERO;
        }
        serviceFee.setFeeRate(decimalServiceFee.setScale(2, RoundingMode.HALF_UP));
        return serviceFee;
    }

    private BigDecimal calculateSourceFee(SourceFee sourceFee,
            SourceOfFund sourceOfFundFee) {
        BigDecimal decimalSourceFee = BigDecimal.ZERO;
        if (sourceOfFundFee.getFeeRateType().equals("THB")) {
            // fee type = fix
            BigDecimal fee = sourceFee.getSourceFee() != null ? new BigDecimal(sourceFee.getSourceFee()) : BigDecimal.ZERO;
            decimalSourceFee = fee.divide(new BigDecimal("100"));
        } else {
            // fee type = percent
            decimalSourceFee = sourceFee.getSourceFee() != null ? new BigDecimal(sourceFee.getSourceFee()) : BigDecimal.ZERO;;
        }
        return decimalSourceFee;
    }

    private BigDecimal convertStringToFraction(String value) {
        BigDecimal decimal = (value != null) ? new BigDecimal(value) : BigDecimal.ZERO;
        return decimal.divide(new BigDecimal("100"));
    }

    public static class SIEngineTransactionFailException extends ServiceInventoryException {
        private static final long serialVersionUID = 5955708376116171195L;

        public SIEngineTransactionFailException(SIEngineException ex) {
            super(500, ex.getCode(), "bill system fail with code: " + ex.getCode(), ex.getNamespace(), ex.getMessage());
        }
    }

    public static class UMarketSystemTransactionFailException extends ServiceInventoryException {
        private static final long serialVersionUID = 3748885497125818864L;

        public UMarketSystemTransactionFailException(SIEngineException ex) {
            super(500, ex.getCode(), "umarket system fail with code: " + ex.getCode(), ex.getNamespace(), ex.getMessage());
        }
    }

    public static class UnknownSystemTransactionFailException extends ServiceInventoryException {
        private static final long serialVersionUID = 5899038317339162588L;

        public UnknownSystemTransactionFailException(SIEngineException ex) {
            super(500, ex.getCode(),  "unknown system fail with code: " + ex.getCode(), ex.getNamespace(), ex.getMessage());
        }
    }

    public static class VerifyBillPayFailException extends ServiceInventoryException{
        private static final long serialVersionUID = 3029606083785530229L;

        public VerifyBillPayFailException(SIEngineException ex) {
            super(500,ex.getCode(),"Verify Bill Pay fail with code: " + ex.getCode(),ex.getNamespace(),ex.getMessage());
        }
    }

    public static class UnknownServiceFeeType extends ServiceInventoryException {

        private static final long serialVersionUID = 5313680069554085972L;
        private static final String UNKNOWN_SERVICE_FEE_TYPE = "xxxx";

        public UnknownServiceFeeType(String feeType) {
            super(500, UNKNOWN_SERVICE_FEE_TYPE,  "unknown fee type code: " + feeType, "BILL-PROXY", null);
        }
    }

    public void setBillPayProxy(BillProxy billPayProxy) {
        this.billPayProxy = billPayProxy;
    }

}
