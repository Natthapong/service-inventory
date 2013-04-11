package th.co.truemoney.serviceinventory.legacyfacade.ewallet;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.springframework.security.crypto.codec.Hex;

import th.co.truemoney.serviceinventory.bill.domain.BillPaymentConfirmationInfo;
import th.co.truemoney.serviceinventory.bill.domain.BillPaymentInfo;
import th.co.truemoney.serviceinventory.bill.domain.ServiceFee;
import th.co.truemoney.serviceinventory.bill.domain.SourceOfFundFee;

public class BillPaymentFacade {
	
	public BillPaymentInfo getBarcodeInformation(String barcode) {		
		return new BillPaymentInfo();
	}
	
	public BillPaymentConfirmationInfo payBill(BillPaymentInfo billPaymentInfo, String sessionID, String mobileNumber, Integer channelID, String tmnID
			/*amount, String ref1, String ref2,
			String transRef, String transRelation, String target, String totalServiceFee,
			String sourceOfFund, BigDecimal totalSourceFee, String sourceFeeType, Integer channelID, 
			String paypointCode, String paypointName, String transType, String sessionID, String truemoneyID*/) {
		
		BillPaymentConfirmationInfo confirmationInfo = new BillPaymentConfirmationInfo();
		
		String key = "";
		String functionID = "910010003";
		String appUser = "";
		String appPassword = "";
		String reqTransID = "";
		
		BigDecimal amount = billPaymentInfo.getAmount();
		String ref1 = billPaymentInfo.getRef1();
		String ref2 = billPaymentInfo.getRef2();
		String commandAction = "";
		String fundName = "";
		String transRef = "";
		String transRelation ="";
		String target = billPaymentInfo.getTarget();
		ServiceFee serviceFee = billPaymentInfo.getServiceFee();
		BigDecimal totalServiceFee = serviceFee.getTotalFee();
		String serviceFeeType = serviceFee.getFeeType();
		SourceOfFundFee[] sourceOfFundFees = billPaymentInfo.getSourceOfFundFees();
		SourceOfFundFee sourceOfFundFee = null;
		String source = "EW";
		int i = 0;
		BigDecimal totalSourceFee = new BigDecimal(0);
		String sourceFeeType = "";
		String paypointCode = "Mobile";
		String paypointName = "Mobile";
		String transType = "01";
		String md5Hash = "";
		byte[] md5Bytes = null;
		String rawText = "";
		byte[] rawBytes = null;
		
		for (i = 0; i < sourceOfFundFees.length; i++) {
			if (sourceOfFundFees[i].getSourceType().equals(source)) {
				sourceOfFundFee = sourceOfFundFees[i];
			}
		}
		totalSourceFee = sourceOfFundFee.getTotalFee();
		sourceFeeType = sourceOfFundFee.getFeeType();
		
		rawText = key + appUser + functionID + reqTransID + amount.toString() + sessionID;
		MessageDigest md;
		try {
			rawBytes = rawText.getBytes("UTF-8");
			md = MessageDigest.getInstance("MD5");
			md5Bytes = md.digest(rawBytes);
			md5Hash = new String(Hex.encode(md5Bytes));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		
		return confirmationInfo;
	}
}
