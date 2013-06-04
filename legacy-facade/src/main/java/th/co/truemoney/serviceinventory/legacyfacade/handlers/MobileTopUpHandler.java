package th.co.truemoney.serviceinventory.legacyfacade.handlers;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import th.co.truemoney.serviceinventory.bill.domain.ServiceFeeInfo;
import th.co.truemoney.serviceinventory.bill.domain.SourceOfFund;
import th.co.truemoney.serviceinventory.engine.client.domain.SourceFee;
import th.co.truemoney.serviceinventory.engine.client.domain.services.ConfirmTopUpAirtimeRequest;
import th.co.truemoney.serviceinventory.engine.client.domain.services.ConfirmTopUpAirtimeResponse;
import th.co.truemoney.serviceinventory.engine.client.domain.services.VerifyTopUpAirtimeRequest;
import th.co.truemoney.serviceinventory.engine.client.domain.services.VerifyTopUpAirtimeResponse;
import th.co.truemoney.serviceinventory.engine.client.exception.FailResultCodeException;
import th.co.truemoney.serviceinventory.engine.client.exception.SIEngineException;
import th.co.truemoney.serviceinventory.engine.client.proxy.impl.TopUpMobileProxy;
import th.co.truemoney.serviceinventory.exception.ServiceInventoryException;
import th.co.truemoney.serviceinventory.topup.domain.TopUpMobile;
import th.co.truemoney.serviceinventory.topup.domain.TopUpMobileConfirmationInfo;

public class MobileTopUpHandler {

	@Autowired
	private TopUpMobileProxy topUpMobileProxy;
	
	public TopUpMobile verifyTopUpMobile(VerifyTopUpAirtimeRequest verifyTopUpAirtimeRequest){
		try {
			VerifyTopUpAirtimeResponse verifyTopUpAirtimeResponse = topUpMobileProxy.verifyTopUpAirtime(verifyTopUpAirtimeRequest);
			TopUpMobile topUpMobile = new TopUpMobile();
			topUpMobile.setID(verifyTopUpAirtimeResponse.getTransactionID());
			topUpMobile.setTarget(verifyTopUpAirtimeResponse.getTarget());
			topUpMobile.setLogo(verifyTopUpAirtimeResponse.getLogo());
			topUpMobile.setTitleTH(verifyTopUpAirtimeResponse.getTitleTH());
			topUpMobile.setTitleEN(verifyTopUpAirtimeResponse.getTitleEN());

			topUpMobile.setAmount(verifyTopUpAirtimeResponse.getAmount());
			topUpMobile.setMinAmount(verifyTopUpAirtimeResponse.getMinAmount());
			topUpMobile.setMaxAmount(verifyTopUpAirtimeResponse.getMaxAmount());

			topUpMobile.setMobileNumber(verifyTopUpAirtimeResponse.getMobileNumber());
			topUpMobile.setRemainBalance(verifyTopUpAirtimeResponse.getRemainBalance());
			
			ServiceFeeInfo serviceFee = createServiceFee(verifyTopUpAirtimeResponse);
			topUpMobile.setServiceFee(serviceFee);

			List<SourceOfFund> sourceOfFundFees = createSourceOfFundFeeList(verifyTopUpAirtimeResponse);
			topUpMobile.setSourceOfFundFees(sourceOfFundFees.toArray(new SourceOfFund[sourceOfFundFees.size()]));

			return topUpMobile;
		} catch(FailResultCodeException ex) {
			throw new VerifyTopUpAirtimeFailException(ex);
		}
	}
	
	public TopUpMobileConfirmationInfo topUpMobile(ConfirmTopUpAirtimeRequest confirmRequest) {
		try {
			SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm");
			ConfirmTopUpAirtimeResponse topUpResponse = topUpMobileProxy.confirmTopUpAirtime(confirmRequest);
			TopUpMobileConfirmationInfo confirmationInfo = new TopUpMobileConfirmationInfo();
			confirmationInfo.setTransactionID(topUpResponse.getApproveCode());
			confirmationInfo.setTransactionDate(df.format(new Date()));
			return confirmationInfo;
		} catch (FailResultCodeException ex) {
			// TODO map exception to corresponding throw
			throw new UnknownSystemTransactionFailException(ex);
		}
	}
	
	private List<SourceOfFund> createSourceOfFundFeeList(VerifyTopUpAirtimeResponse verifyTopUpAirtimeResponse) {
		List<SourceFee> sourceOfFundList = verifyTopUpAirtimeResponse.getExtraXML().getSourceFeeList();
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

	private ServiceFeeInfo createServiceFee(VerifyTopUpAirtimeResponse verifyTopUpAirtimeResponse) {
		ServiceFeeInfo serviceFee = new ServiceFeeInfo();
		serviceFee.setFeeRateType(verifyTopUpAirtimeResponse.getServiceFeeType());
		BigDecimal decimalServiceFee = BigDecimal.ZERO;
		if (serviceFee.getFeeRateType().equals("THB")) {
			// fee type = fix
			BigDecimal fee = verifyTopUpAirtimeResponse.getServiceFee() != null ? new BigDecimal(verifyTopUpAirtimeResponse.getServiceFee()) : BigDecimal.ZERO;
			decimalServiceFee = fee.divide(new BigDecimal("100"));
		} else {
			// fee type = percent
			decimalServiceFee = verifyTopUpAirtimeResponse.getServiceFee() != null ? new BigDecimal(verifyTopUpAirtimeResponse.getServiceFee()) : BigDecimal.ZERO;
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
		private static final long serialVersionUID = 420345162856639797L;

		public SIEngineTransactionFailException(SIEngineException ex) {
			super(500, ex.getCode(), "topUp airtime system fail with code: " + ex.getCode(), ex.getNamespace(), ex.getMessage());
		}
	}

	public static class UMarketSystemTransactionFailException extends ServiceInventoryException {
		private static final long serialVersionUID = -4144730969193929228L;

		public UMarketSystemTransactionFailException(SIEngineException ex) {
			super(500, ex.getCode(), "umarket system fail with code: " + ex.getCode(), ex.getNamespace(), ex.getMessage());
		}
	}

	public static class UnknownSystemTransactionFailException extends ServiceInventoryException {
		private static final long serialVersionUID = 7714167636585614404L;

		public UnknownSystemTransactionFailException(SIEngineException ex) {
			super(500, ex.getCode(),  "unknown system fail with code: " + ex.getCode(), ex.getNamespace(), ex.getMessage());
		}
	}

	public static class VerifyTopUpAirtimeFailException extends ServiceInventoryException{
		private static final long serialVersionUID = -5895975328287926690L;

		public VerifyTopUpAirtimeFailException(SIEngineException ex) {
			super(500,ex.getCode(),"Verify topUp airtime fail with code: " + ex.getCode(),ex.getNamespace(),ex.getMessage());
		}
	}

	public static class UnknownServiceFeeType extends ServiceInventoryException {
		private static final long serialVersionUID = 5313680069554085972L;
		private static final String UNKNOWN_SERVICE_FEE_TYPE = "xxxx";

		public UnknownServiceFeeType(String feeType) {
			super(500, UNKNOWN_SERVICE_FEE_TYPE,  "unknown fee type code: " + feeType, "BILL-PROXY", null);
		}
	}
	
}
