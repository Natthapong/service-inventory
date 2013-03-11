package th.co.truemoney.serviceinventory.exception;

import javax.servlet.http.HttpServletResponse;

import th.co.truemoney.serviceinventory.common.domain.ServiceResponse;

public class SignonServiceException extends BaseException {
	
	private static final long serialVersionUID = 7328535407875381185L;
	
	public static final String NAMESPACE = "TMN-SERVICE-INVENTORY";
	
	public static class Code  {
		public static final String SUCCESS = "0";		
	}
	
	public SignonServiceException(String code, String description) {
		super(code, description);
	}
	
	public SignonServiceException(String code, String description, String namespace) {
		super(code, description, namespace);
	}
	
	@Override
	public ServiceResponse<Object> handleExceptions(BaseException e, HttpServletResponse response) {
		response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		ServiceResponse<Object> error = new ServiceResponse<Object>();
		error.setResponseCode(e.getCode());
		error.setResponseDesc(e.getDescription());
		error.setResponseNamespace(e.getNamespace());
		return error;
	}

}
