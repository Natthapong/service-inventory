package th.co.truemoney.serviceinventory.exception;

import javax.servlet.http.HttpServletResponse;

import th.co.truemoney.serviceinventory.bean.ErrorBean;

public abstract class BaseException extends RuntimeException {

	private static final long serialVersionUID = -5871887802915224160L;
	
	public static final String NAMESPACE = "TMN-SERVICE-INVENTORY";
	
	public static class Code  {		
		//general error code
		public static final String GENERAL_ERROR = "9999";
		public static final String SEND_OTP_FAIL = "1000";
		public static final String OTP_NOT_MATCH = "1001";
		public static final String INVALID_CHECKSUM = "1002";
		public static final String OTP_NOT_FOUND = "1003";
		public static final String TOPUP_ORDER_NOT_FOUND = "1004";
				
		//profile
		public static final String INVALID_PROFILE_TYPE = "10000";
		public static final String ACCESS_TOKEN_NOT_FOUND = "10001";
		
		//list source of fund direct debit
		
	}
	
	private String code;
	private String description;
	private String namespace;
	
	public abstract ErrorBean handleExceptions(BaseException e, HttpServletResponse response);
	
	public BaseException() {
		super();
	}
	
	public BaseException(String code, String description) {
		this.code = code;
		this.description = description;
	}
	
	public BaseException(String code, String description, String namespace) {
		this.code = code;
		this.description = description;
		this.namespace = namespace;
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
	
}
