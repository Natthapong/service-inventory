package th.co.truemoney.serviceinventory.exception;

import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import th.co.truemoney.serviceinventory.bean.ErrorBean;

public class ServiceInventoryException extends BaseException {
	
	private static final long serialVersionUID = 7328535407875381185L;
	
	private Map<String, Object> data;	
	
	public ServiceInventoryException(String code, String description) {
		super(code, description);
	}
	
	public ServiceInventoryException(String code, String description, String namespace) {
		super(code, description, namespace);
	}
	
	public void setData(Map<String, Object> data) {
		this.data = data;
	}

	@Override
	public ErrorBean handleExceptions(BaseException e, HttpServletResponse response) {
		response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		ErrorBean error = new ErrorBean(e.getCode(), e.getDescription());
		error.setErrorNamespace(e.getNamespace() != null ? e.getNamespace() : NAMESPACE);
		error.setData(this.data);
		return error;
	}

}
