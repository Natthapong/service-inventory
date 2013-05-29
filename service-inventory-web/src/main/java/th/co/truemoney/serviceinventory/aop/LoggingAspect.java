package th.co.truemoney.serviceinventory.aop;


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
	
}