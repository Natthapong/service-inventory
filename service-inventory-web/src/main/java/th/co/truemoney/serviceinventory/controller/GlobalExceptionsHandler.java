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
import th.co.truemoney.serviceinventory.ewallet.exception.EwalletUnExpectedException;
import th.co.truemoney.serviceinventory.ewallet.exception.FailResultCodeException;
import th.co.truemoney.serviceinventory.exception.ServiceInventoryException;
import th.co.truemoney.serviceinventory.exception.ServiceInventoryWebException;

@ControllerAdvice
public class GlobalExceptionsHandler {

	private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionsHandler.class);

	@ExceptionHandler
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public @ResponseBody ErrorBean handleEwalletFailResultCodeExceptions(FailResultCodeException exception) {
		return new ErrorBean(400, exception.getCode() , "Ewallet return error code: " + exception.getCode(), exception.getNamespace(), exception.getMessage());
	}

	@ExceptionHandler
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	public @ResponseBody ErrorBean handleEwalletUnExpectedExceptions(EwalletUnExpectedException exception) {
		return new ErrorBean(503, "503", "Internal server error", exception.getNamespace(), exception.getMessage());
	}



	@ExceptionHandler
	@ResponseStatus(HttpStatus.PRECONDITION_FAILED)
	public @ResponseBody ErrorBean handleBeanValidationExceptions(MethodArgumentNotValidException exception) {

		FieldError fieldError = exception.getBindingResult().getFieldError();
		String developerMessage = fieldError.getDefaultMessage() + ": " + fieldError.getField();

		return new ErrorBean(412, "412", "Validation failed", ServiceInventoryWebException.NAMESPACE, developerMessage);
	}

	@ExceptionHandler
	@ResponseStatus(HttpStatus.PRECONDITION_FAILED)
	public @ResponseBody ErrorBean handleBeanBindingValidationExceptions(BindException exception) {

		FieldError fieldError = exception.getBindingResult().getFieldError();
		String developerMessage = fieldError.getDefaultMessage() + ": " + fieldError.getField();

		return new ErrorBean(412, "412", "Validation failed", ServiceInventoryWebException.NAMESPACE, developerMessage);
	}

	@ExceptionHandler
	public @ResponseBody ErrorBean handleSystemException(ServiceInventoryException exception, HttpServletResponse response) {
		Integer httpStatus = exception.getHttpStatus() != null ? exception.getHttpStatus() : 400;
		exception.setHttpStatus(httpStatus);
		response.setStatus(httpStatus);

		return new ErrorBean(exception);
	}


	@ExceptionHandler
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	public @ResponseBody ErrorBean handleAllExceptions(Exception exception) {

		logger.debug("=========================");
		logger.error("SI:WEB "+exception.getMessage(), exception);
		logger.debug("=========================");

		return new ErrorBean(Integer.toString(HttpServletResponse.SC_INTERNAL_SERVER_ERROR), "INTERNAL_SERVER_ERROR");
	}

}
