package th.co.truemoney.serviceinventory.exception;

import javax.servlet.http.HttpServletResponse;

import th.co.truemoney.serviceinventory.bean.ErrorBean;

public class ServiceInventoryException extends BaseException {
	
	private static final long serialVersionUID = 7328535407875381185L;
	
	public static final String NAMESPACE = "TMN-SERVICE-INVENTORY";
	
	public static class Code  {
		public static final String SUCCESS = "0";		
		public static final String INVALID_PROFILE_TYPE = "10000";
		public static final String DATASOURCE_NOT_FOUND = "90000";
	}
	
	public ServiceInventoryException(String code, String description) {
		super(code, description);
	}
	
	public ServiceInventoryException(String code, String description, String namespace) {
		super(code, description, namespace);
	}
	
	@Override
	public ErrorBean handleExceptions(BaseException e, HttpServletResponse response) {
		response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		ErrorBean error = new ErrorBean(e.getCode(), e.getDescription());
		error.setErrorNamespace(e.getNamespace());
		return error;
	}

}
