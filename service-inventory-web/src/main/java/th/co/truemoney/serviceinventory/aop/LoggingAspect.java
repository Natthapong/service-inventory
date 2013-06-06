package th.co.truemoney.serviceinventory.aop;


import java.util.Arrays;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

import th.co.truemoney.serviceinventory.engine.client.domain.services.SIEngineRequestWrapper;
import th.co.truemoney.serviceinventory.engine.client.domain.services.SIEngineResponseWrapper;
import th.co.truemoney.serviceinventory.engine.client.exception.SIEngineException;
import th.co.truemoney.serviceinventory.ewallet.exception.EwalletException;
import th.co.truemoney.serviceinventory.ewallet.proxy.message.EwalletRequest;
import th.co.truemoney.serviceinventory.ewallet.proxy.message.EwalletResponse;
import th.co.truemoney.serviceinventory.exception.ServiceInventoryException;

@Aspect
@Component
public class LoggingAspect {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private AsyncJdbcLoggingProcessor asyncJdbcLoggingProcessor;


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

    @Around("execution(* th.co.truemoney.serviceinventory.controller.*.*(..))")
    public Object logController(ProceedingJoinPoint joinPoint) throws Throwable {

        Long startTime = System.currentTimeMillis();
        Object retVal = null;
        ServiceInventoryException serviceInventoryErrorCase = null;
        try {
            asyncJdbcLoggingProcessor.setLogin();
            retVal = joinPoint.proceed();
            return retVal;
        } catch (ServiceInventoryException se) {
            serviceInventoryErrorCase = se;
            throw se;
        } finally {
            Long stopTime = System.currentTimeMillis();
            System.out.println(joinPoint.getTarget().getClass().getSimpleName() + " ========");
            asyncJdbcLoggingProcessor.writeLogController(joinPoint.getTarget().getClass().getSimpleName(), joinPoint.getSignature().getName(), Arrays.asList(joinPoint.getArgs()), retVal, serviceInventoryErrorCase, startTime, stopTime);
        }
    }

    @Around("execution(* th.co.truemoney.serviceinventory.engine.client.proxy.impl.*.*(..))")
    public Object asyncLogSiEngine(ProceedingJoinPoint joinPoint) throws Throwable  {

        Long startTime = System.currentTimeMillis();
        Object[] args = joinPoint.getArgs();

        if (args.length > 0 && args[0] instanceof SIEngineRequestWrapper) {

            SIEngineRequestWrapper siEngineRequest = (SIEngineRequestWrapper) args[0];
            SIEngineException errorException = null;
            SIEngineResponseWrapper siEngineResponse = null;
            try {
                siEngineResponse = (SIEngineResponseWrapper) joinPoint.proceed();
                return siEngineResponse;
            } catch (SIEngineException ex) {
                errorException = ex;
                throw ex;
            } finally {
                Long stopTime = System.currentTimeMillis();
                System.out.println("joinPoint.getTarget().getClass()=>" + joinPoint.getTarget().getClass() + "<" ) ;
                System.out.println("joinPoint.getTarget().getClass().getSimpleName()=>" + joinPoint.getTarget().getClass().getSimpleName() + "<" ) ;
                asyncJdbcLoggingProcessor.writeLogSiEngineProxies(joinPoint.getTarget().getClass().getSimpleName(), joinPoint.getSignature().getName(), siEngineRequest, siEngineResponse, errorException, startTime, stopTime);
            }

        }
        return joinPoint.proceed();
    }

    @Around("execution(* th.co.truemoney.serviceinventory.ewallet.proxy.tmnprofile.*.*(..)) || " +
            "execution(* th.co.truemoney.serviceinventory.ewallet.proxy.tmnsecurity.*.*(..)) || " +
            "execution(* th.co.truemoney.serviceinventory.ewallet.proxy.tmnprofile.admin.*.*(..)) || " +
            "execution(* th.co.truemoney.serviceinventory.ewallet.proxy.ewalletsoap.*.*(..))")
    public Object asyncLogEwalletProxies(ProceedingJoinPoint joinPoint) throws Throwable  {

                Long startTime = System.currentTimeMillis();
                Object[] args = joinPoint.getArgs();

        if (args.length > 0 && args[0] instanceof EwalletRequest) {

            EwalletRequest ewalletRequest = (EwalletRequest) args[0];
            EwalletException errorException = null;
            EwalletResponse ewalletResponse = null;
            try {
                ewalletResponse = (EwalletResponse) joinPoint.proceed();
                return ewalletResponse;
            } catch (EwalletException ex) {
                errorException = ex;
                throw ex;
            } finally {
                Long stopTime = System.currentTimeMillis();
                asyncJdbcLoggingProcessor.writeLogEwalletProxies(joinPoint.getTarget().getClass().getSimpleName(), joinPoint.getSignature().getName(), ewalletRequest, ewalletResponse, errorException, startTime, stopTime);
            }

        }
        return joinPoint.proceed();
    }

    @Around("execution(* th.co.truemoney.serviceinventory.ewallet.impl.ActivityServiceImpl.*(..))")
    public Object asyncLogCoreReportWeb(ProceedingJoinPoint joinPoint) throws Throwable  {

                Long startTime = System.currentTimeMillis();
                Object[] args = joinPoint.getArgs();

        if (args.length > 0 && args[0] instanceof SIEngineRequestWrapper) {

                SIEngineRequestWrapper siEngineRequest = (SIEngineRequestWrapper) args[0];
                SIEngineException errorException = null;
                SIEngineResponseWrapper siEngineResponse = null;
            try {
                siEngineResponse = (SIEngineResponseWrapper) joinPoint.proceed();
                return siEngineResponse;
            } catch (SIEngineException ex) {
                errorException = ex;
                throw ex;
            } finally {
                Long stopTime = System.currentTimeMillis();
                System.out.println("joinPoint.getTarget().getClass()=>" + joinPoint.getTarget().getClass() + "<" ) ;
                System.out.println("joinPoint.getTarget().getClass().getSimpleName()=>" + joinPoint.getTarget().getClass().getSimpleName() + "<" ) ;
//              System.out.println("joinPoint.getSignature()=>" + joinPoint.getSignature() + "<" ) ;
//              System.out.println("joinPoint.getSignature().getName()=>" + joinPoint.getSignature().getName() + "<" );
                asyncJdbcLoggingProcessor.writeLogSiEngineProxies(joinPoint.getTarget().getClass().getSimpleName(), joinPoint.getSignature().getName(), siEngineRequest, siEngineResponse, errorException, startTime, stopTime);
            }

        }
        return joinPoint.proceed();
    }

}