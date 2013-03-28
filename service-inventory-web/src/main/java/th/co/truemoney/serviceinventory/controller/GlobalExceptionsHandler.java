package th.co.truemoney.serviceinventory.controller;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import th.co.truemoney.serviceinventory.bean.ErrorBean;
import th.co.truemoney.serviceinventory.exception.BaseException;
import th.co.truemoney.serviceinventory.exception.ServiceInventoryException;

@ControllerAdvice
public class GlobalExceptionsHandler {

	private static Logger logger = LoggerFactory.getLogger(GlobalExceptionsHandler.class);

	@ExceptionHandler
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public @ResponseBody ErrorBean handleBeanValidationExceptions(MethodArgumentNotValidException exception) {

		FieldError fieldError = exception.getBindingResult().getFieldError();
		String developerMessage = fieldError.getDefaultMessage() + ": " + fieldError.getField();

		return new ErrorBean("400", "Validation failed", ServiceInventoryException.NAMESPACE, developerMessage);
	}

	@ExceptionHandler
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public @ResponseBody ErrorBean handleBeanBindingValidationExceptions(BindException exception) {

		FieldError fieldError = exception.getBindingResult().getFieldError();
		String developerMessage = fieldError.getDefaultMessage() + ": " + fieldError.getField();

		return new ErrorBean("400", "Validation failed", ServiceInventoryException.NAMESPACE, developerMessage);
	}

	@ExceptionHandler
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public @ResponseBody ErrorBean handleSystemException(BaseException exception) {

		logger.debug("==========================================");
		logger.debug(exception.getMessage(), exception);
		logger.debug("==========================================");

		return exception.getErrorBean();
	}


	@ExceptionHandler
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	public @ResponseBody ErrorBean handleAllExceptions(Exception exception) {

		logger.debug("==========================================");
		logger.debug(exception.getMessage(), exception);
		logger.debug("==========================================");

		return new ErrorBean(Integer.toString(HttpServletResponse.SC_INTERNAL_SERVER_ERROR), "INTERNAL_SERVER_ERROR");
	}

}
