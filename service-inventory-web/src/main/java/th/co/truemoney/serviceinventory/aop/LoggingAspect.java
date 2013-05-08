package th.co.truemoney.serviceinventory.aop;


import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

@Aspect
@Component
public class LoggingAspect {

	private Logger logger = LoggerFactory.getLogger(this.getClass());
	private Logger fileLogger = LoggerFactory.getLogger("fileBillPay");
	private String pattern = "HH:mm:ss:SS";
	private DateFormat format = new SimpleDateFormat(pattern);
	
	@Around("execution(* th.co.truemoney.serviceinventory.controller.*.*(..))")
	public Object logTimeMethod(ProceedingJoinPoint joinPoint) throws Throwable {
		StopWatch stopWatch = new StopWatch();
		stopWatch.start();
		Object retVal = joinPoint.proceed();
		stopWatch.stop();
		StringBuffer logMessage = new StringBuffer();
		logMessage.append(joinPoint.getTarget().getClass().getSimpleName());
		logMessage.append(".");
		logMessage.append(joinPoint.getSignature().getName());
		logMessage.append(" execution time: ");
		logMessage.append(stopWatch.getTotalTimeMillis());
		logMessage.append(" ms");
		logger.info(logMessage.toString());
		return retVal;
	}
	
	@Around("execution(* th.co.truemoney.serviceinventory.controller.BillPaymentController.*(..)) || " +
			"execution(* th.co.truemoney.serviceinventory.bill.impl.AsyncBillPayProcessor.*(..)) || " +
			"execution(* th.co.truemoney.serviceinventory.engine.client.proxy.impl.BillProxyImpl.*(..))")
	public Object logTimeBillPayMethod(ProceedingJoinPoint joinPoint) throws Throwable {
		Long startTime = System.currentTimeMillis();
		Object retVal = joinPoint.proceed();
		Long stopTime = System.currentTimeMillis();
		StringBuffer logMessage = new StringBuffer();
		logMessage.append(joinPoint.getTarget().getClass().getSimpleName());
		logMessage.append(".");
		logMessage.append(joinPoint.getSignature().getName());
		logMessage.append(" - Start Time : " + format.format(new Date(startTime)) + " ms ,Stop Time: " + format.format(new Date(stopTime)) + " ms");
		logMessage.append(" execution time: ");
		logMessage.append( (stopTime - startTime) );
		logMessage.append(" ms");
		fileLogger.info(logMessage.toString());
		return retVal;
	}
	
}