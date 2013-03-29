package th.co.truemoney.serviceinventory.exception;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;

import th.co.truemoney.serviceinventory.bean.ErrorBean;

public abstract class BaseException extends RuntimeException {

	private static final long serialVersionUID = -5871887802915224160L;

	public static final String NAMESPACE = "TMN-SERVICE-INVENTORY";

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

		//profile
		public static final String INVALID_PROFILE_TYPE 	= "10000";
		public static final String ACCESS_TOKEN_NOT_FOUND 	= "10001";
		public static final String INVALID_PROFILE_STATUS	= "10002";

		//topup eWallet
		public static final String INVALID_AMOUNT_LESS ="20001";
		public static final String INVALID_AMOUNT_MORE ="20002";

	}

	private String code;
	private String description;
	private String developerMessage;
	private String namespace;
	public Map<String, Object> data;

	public BaseException() {
		super();
	}

	public BaseException(String code, String description) {
		super(description);
		this.code = code;
		this.description = description;
	}

	public BaseException(String code, String description, String namespace) {
		super(description);
		this.code = code;
		this.description = description;
		this.namespace = namespace;
	}

	public BaseException(String code, String description, String namespace, String developerMessage) {
		super(description);
		this.code = code;
		this.description = description;
		this.namespace = namespace;
		this.developerMessage = developerMessage;
	}


	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getNamespace() {
		return namespace;
	}

	public void setNamespace(String namespace) {
		this.namespace = namespace;
	}

	public String getDeveloperMessage() {
		return developerMessage;
	}

	public void setDeveloperMessage(String developerMessage) {
		this.developerMessage = developerMessage;
	}

	public Map<String, Object> getData() {
		return data;
	}

	public void setData(Map<String, Object> data) {
		this.data = data;
	}

	public void marshallToData(Object object) {
		ObjectMapper mapper = new ObjectMapper();
		Map<String, Object> hashMap = mapper.convertValue(object, HashMap.class);
		this.setData(hashMap);
	}

	public ErrorBean getErrorBean() {
		return new ErrorBean(code, description, namespace, developerMessage, data);
	}

}
