package th.co.truemoney.serviceinventory.exception;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;


public class ServiceInventoryWebException extends ServiceInventoryException {

	private static final long serialVersionUID = 7328535407875381185L;

	public static final String NAMESPACE = "TMN-SERVICE-INVENTORY";

	public ServiceInventoryWebException(String code, String description) {
		super(400, code, description, ServiceInventoryWebException.NAMESPACE);
	}

	public ServiceInventoryWebException(Integer httpStatus, String code, String description) {
		super(httpStatus, code, description, ServiceInventoryWebException.NAMESPACE);
	}

	public ServiceInventoryWebException(Integer httpStatus, String code, String description, String developerMessage) {
		super(httpStatus, code, description, ServiceInventoryWebException.NAMESPACE, developerMessage);
	}

	public static class Code  {
		//general error code
		public static final String GENERAL_ERROR 	= "9999";
		public static final String SEND_OTP_FAIL 	= "1000";
		public static final String OTP_NOT_MATCH 	= "1001";
		public static final String INVALID_CHECKSUM = "1002";
		public static final String OTP_NOT_FOUND = "1003";
		public static final String DRAFT_TRANSACTION_NOT_FOUND = "1004";
		public static final String TRANSACTION_NOT_FOUND = "1004";
		public static final String CONFIRM_BANK_FAILED = "1005";
		public static final String CONFIRM_UMARKET_FAILED = "1006";
		public static final String CONFIRM_FAILED = "1007";
		public static final String PROFILE_NOT_FOUND = "1008";
		public static final String SEND_EMAIL_FAIL	 = "1009";
		public static final String INVALID_OTP		 = "1010";
		public static final String BILL_NOT_FOUND = "1011";
		public static final String BILL_OVER_DUE  = "1012";
		public static final String OWNER_UNVERIFIED = "1013";
		public static final String CONFIRM_TPP_FAILED = "1014";
		public static final String CONFIRM_PCS_FAILED = "1015";
		
		//profile
		public static final String INVALID_PROFILE_TYPE 	= "10000";
		public static final String ACCESS_TOKEN_NOT_FOUND 	= "10001";
		public static final String INVALID_PROFILE_STATUS	= "10002";

		//topup eWallet
		public static final String INVALID_AMOUNT_LESS ="20001";
		public static final String INVALID_AMOUNT_MORE ="20002";

		//transfer
		public static final String INVALID_TARGET_MOBILE_NUMBER = "30000";
	}

	@SuppressWarnings("unchecked")
	public void marshallToData(Object object) {
		ObjectMapper mapper = new ObjectMapper();
		Map<String, Object> hashMap = mapper.convertValue(object, HashMap.class);
		this.setData(hashMap);
	}
}
