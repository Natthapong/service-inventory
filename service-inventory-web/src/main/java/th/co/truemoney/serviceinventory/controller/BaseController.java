package th.co.truemoney.serviceinventory.controller;

import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.WebRequest;

import th.co.truemoney.serviceinventory.common.domain.ServiceResponse;
import th.co.truemoney.serviceinventory.exception.BaseException;
import th.co.truemoney.serviceinventory.exception.ServiceInventoryException;

public class BaseController {
	
	private static Logger logger = Logger.getLogger(BaseController.class);
	
	@ExceptionHandler(BaseException.class)
	public @ResponseBody ServiceResponse<Object> handleException(BaseException exception, 
			WebRequest request, HttpServletResponse response) {
		
		logger.debug("==========================================");
		logger.debug(exception.getMessage(), exception);
		logger.debug("==========================================");
		
		return exception.handleExceptions(exception, response);
		
	}
	
	@ExceptionHandler(Exception.class)
	public @ResponseBody ServiceResponse<Object> handleAllExceptions(Exception exception, 
			WebRequest request, HttpServletResponse response) {	
		
		response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		ServiceResponse<Object> error = new ServiceResponse<Object>();
		error.setResponseCode(Integer.toString(HttpServletResponse.SC_INTERNAL_SERVER_ERROR));
		error.setResponseDesc("INTERNAL_SERVER_ERROR");
		error.setResponseNamespace(ServiceInventoryException.NAMESPACE);
		return error;
		
	}
	
}
