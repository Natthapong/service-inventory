package th.co.truemoney.serviceinventory.controller;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.WebRequest;

import th.co.truemoney.serviceinventory.bean.ErrorBean;
import th.co.truemoney.serviceinventory.exception.BaseException;
import th.co.truemoney.serviceinventory.exception.ServiceInventoryException;

public class BaseController {

	private static Logger logger = LoggerFactory.getLogger(BaseController.class);

	@ExceptionHandler(BaseException.class)
	public @ResponseBody ErrorBean handleException(BaseException exception,
			WebRequest request, HttpServletResponse response) {

		logger.debug("==========================================");
		logger.debug(exception.getMessage(), exception);
		logger.debug("==========================================");

		return exception.handleExceptions(exception, response);

	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public @ResponseBody ErrorBean handleBeanValidationExceptions(MethodArgumentNotValidException exception,
		WebRequest request, HttpServletResponse response) {

		FieldError fieldError = exception.getBindingResult().getFieldError();
		String developerMessage = fieldError.getDefaultMessage() + ": " + fieldError.getField();

		response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		ErrorBean error = new ErrorBean("400", "Validation failed", ServiceInventoryException.NAMESPACE, developerMessage);
		return error;
	}

	@ExceptionHandler(BindException.class)
	public @ResponseBody ErrorBean handleBeanBindingValidationExceptions(BindException exception,
		WebRequest request, HttpServletResponse response) {

		FieldError fieldError = exception.getBindingResult().getFieldError();
		String developerMessage = fieldError.getDefaultMessage() + ": " + fieldError.getField();

		response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		ErrorBean error = new ErrorBean("400", "Validation failed", ServiceInventoryException.NAMESPACE, developerMessage);
		return error;
	}

	@ExceptionHandler(Exception.class)
	public @ResponseBody ErrorBean handleAllExceptions(Exception exception,
			WebRequest request, HttpServletResponse response) {

		logger.debug("==========================================");
		logger.debug(exception.getMessage(), exception);
		logger.debug("==========================================");

		response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		ErrorBean error = new ErrorBean(Integer.toString(HttpServletResponse.SC_INTERNAL_SERVER_ERROR), "INTERNAL_SERVER_ERROR");
		error.setErrorNamespace(ServiceInventoryException.NAMESPACE);
		return error;

	}

}
