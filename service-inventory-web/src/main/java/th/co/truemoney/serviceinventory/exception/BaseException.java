package th.co.truemoney.serviceinventory.exception;

import javax.servlet.http.HttpServletResponse;

import th.co.truemoney.serviceinventory.bean.ErrorBean;

public abstract class BaseException extends RuntimeException {

	private static final long serialVersionUID = -5871887802915224160L;
	
	public static final String NAMESPACE = "TMN-SERVICE-INVENTORY";
	
	public static class Code  {		
		public static final String SUCCESS = "0";			
		//profile
		public static final String INVALID_PROFILE_TYPE = "10000";
		public static final String ACCESS_TOKEN_NOT_FOUND = "10001";	
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
