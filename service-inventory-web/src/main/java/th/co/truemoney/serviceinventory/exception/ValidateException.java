package th.co.truemoney.serviceinventory.exception;

import java.util.HashMap;

import javax.servlet.http.HttpServletResponse;

import th.co.truemoney.serviceinventory.bean.ErrorBean;

public class ValidateException extends BaseException {

	private static final long serialVersionUID = 6974722292694736686L;

	public ValidateException(String code, String description) {
		super(code, description);
	}
	
	@Override
	public ErrorBean handleExceptions(BaseException e, 	HttpServletResponse response) {
		response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		ErrorBean error = new ErrorBean(e.getCode(), e.getDescription());
		error.setErrorNamespace(e.getNamespace() != null ? e.getNamespace() : NAMESPACE);
		error.setData(this.data != null ? this.data : new HashMap<String, Object>());
		return error;
	}

}
