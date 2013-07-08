package th.co.truemoney.serviceinventory.exception;

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
        public static final String OWNER_ALREADY_CONFIRMED = "1014";
        public static final String CONFIRM_TPP_FAILED = "1015";
        public static final String CONFIRM_PCS_FAILED = "1016";
        public static final String FAVORITE_PAYMENT_UNVERIFIED = "1017";
        public static final String FAVORITE_SERVICE_CODE_NOT_INLIST = "1018";
        public static final String INVALID_MOBILE_NUMBER = "1019";
        public static final String BILL_REQUIRED_DUEDATE  = "1020";
        public static final String DEBT_BILL  	= "1021";
		public static final String BILL_REQUIRED_DEBT_STATUS = "1022";

        //profile
        public static final String INVALID_PROFILE_TYPE 	= "10000";
        public static final String ACCESS_TOKEN_NOT_FOUND 	= "10001";
        public static final String INVALID_PROFILE_STATUS	= "10002";
        public static final String RESET_PASSWORD_TOKEN_NOT_FOUND = "10003";

        //topup eWallet
        public static final String INVALID_AMOUNT_LESS ="20001";
        public static final String INVALID_AMOUNT_MORE ="20002";

        //billpay
        public static final String INVALID_BILL_PAYMENT_AMOUNT ="20003";
        public static final String UNKNOWN_SERVICE_FEE_TYPE = "20004";
        public static final String UNKNOWN_BILL_READER_TYPE = "20005";

        //transfer
        public static final String INVALID_TARGET_MOBILE_NUMBER = "30000";

        //core report
        public static final String GET_ACTIVITY_FAILED = "40000";
        public static final String GET_ACTIVITY_DETAIL_FAILED = "40001";

    }

    @SuppressWarnings("unchecked")
    public void marshallToData(Object object) {
        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> hashMap = mapper.convertValue(object, Map.class);
        this.setData(hashMap);
    }
}
