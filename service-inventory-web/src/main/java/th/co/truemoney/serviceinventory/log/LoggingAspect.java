package th.co.truemoney.serviceinventory.log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

import th.co.truemoney.serviceinventory.engine.client.domain.services.SIEngineRequestWrapper;
import th.co.truemoney.serviceinventory.engine.client.domain.services.SIEngineResponseWrapper;
import th.co.truemoney.serviceinventory.engine.client.exception.SIEngineException;
import th.co.truemoney.serviceinventory.ewallet.domain.AccessToken;
import th.co.truemoney.serviceinventory.ewallet.exception.EwalletException;
import th.co.truemoney.serviceinventory.ewallet.proxy.message.EwalletRequest;
import th.co.truemoney.serviceinventory.ewallet.proxy.message.EwalletResponse;
import th.co.truemoney.serviceinventory.ewallet.repositories.AccessTokenRepository;
import th.co.truemoney.serviceinventory.exception.ResourceNotFoundException;
import th.co.truemoney.serviceinventory.exception.ServiceInventoryException;
import th.co.truemoney.serviceinventory.firsthop.message.SmsRequest;
import th.co.truemoney.serviceinventory.firsthop.message.SmsResponse;

@Aspect
@Component
public class LoggingAspect {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private AsyncJdbcLoggingProcessor asyncJdbcLoggingProcessor;

    @Autowired
    private AccessTokenRepository accessTokenRepository;

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
        List<Object> controllerArgs = getArgs(joinPoint);
        Object returnValue = null;
        ServiceInventoryException serviceInventoryException = null;
        try {

            initLogin();

            returnValue = joinPoint.proceed();

            return returnValue;

        } catch (ServiceInventoryException se) {

            serviceInventoryException = se;
            throw se;

        } finally {

            Long stopTime = System.currentTimeMillis();
            asyncJdbcLoggingProcessor.writeLogController(
                    joinPoint.getTarget().getClass().getSimpleName(),
                    joinPoint.getSignature().getName(),
                    controllerArgs, returnValue, serviceInventoryException,
                    startTime, stopTime);
        }
    }

    private List<Object> getArgs(ProceedingJoinPoint joinPoint) {
        if (joinPoint.getArgs() != null) {
            return Arrays.asList(joinPoint.getArgs());
        } else {
            return new ArrayList<Object>();
        }
    }

    @Around("execution(* th.co.truemoney.serviceinventory.engine.client.proxy.impl.*.*(..))")
    public Object logSiEngine(ProceedingJoinPoint joinPoint) throws Throwable {

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

                asyncJdbcLoggingProcessor.writeLogSiEngineProxies(
                        joinPoint.getTarget().getClass().getSimpleName(),
                        joinPoint.getSignature().getName(),
                        siEngineRequest,siEngineResponse, errorException,
                        startTime, stopTime);
            }

        }
        return joinPoint.proceed();
    }

    @Around("execution(* th.co.truemoney.serviceinventory.ewallet.proxy.tmnprofile.*.*(..)) || "
            + "execution(* th.co.truemoney.serviceinventory.ewallet.proxy.tmnsecurity.*.*(..)) || "
            + "execution(* th.co.truemoney.serviceinventory.ewallet.proxy.tmnprofile.admin.*.*(..)) || "
            + "execution(* th.co.truemoney.serviceinventory.ewallet.proxy.ewalletsoap.*.*(..))")
    public Object logEwalletProxies(ProceedingJoinPoint joinPoint)
            throws Throwable {

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
                asyncJdbcLoggingProcessor.writeLogEwalletProxies(joinPoint
                        .getTarget().getClass().getSimpleName(), joinPoint
                        .getSignature().getName(), ewalletRequest,
                        ewalletResponse, errorException, startTime, stopTime);
            }

        }
        return joinPoint.proceed();
    }

    @Around("execution(* th.co.truemoney.serviceinventory.firsthop.proxy.*.*(..))")
    public Object logSmsProxies(ProceedingJoinPoint joinPoint) throws Throwable {

        Long startTime = System.currentTimeMillis();
        Object[] args = joinPoint.getArgs();

        if (args.length > 0 && args[0] instanceof EwalletRequest) {

            SmsRequest smsRequest = (SmsRequest) args[0];
            Exception errorException = null;
            SmsResponse smsResponse = null;
            try {
                smsResponse = (SmsResponse) joinPoint.proceed();
                return smsResponse;
            } catch (Exception ex) {
                errorException = ex;
                throw ex;
            } finally {
                Long stopTime = System.currentTimeMillis();
                asyncJdbcLoggingProcessor.writeLogSmsProxies(joinPoint
                        .getTarget().getClass().getSimpleName(), joinPoint
                        .getSignature().getName(), smsRequest, smsResponse,
                        errorException, startTime, stopTime);
            }

        }
        return joinPoint.proceed();
    }

    private void initLogin() {
        String accessTokenID = MDC.get("accessTokenID");

        if (accessTokenID != null) {

            try {
                AccessToken accessToken = accessTokenRepository.findAccessToken(accessTokenID);

                String loginID = accessToken.getEmail() != null ? accessToken.getEmail() : accessToken.getMobileNumber();
                String truemoneyID = accessToken.getTruemoneyID();

                MDC.put("loginID", loginID);
                MDC.put("truemoneyID", truemoneyID);

            } catch (ResourceNotFoundException ex) {
                MDC.put("loginID", "INVALID_LOGIN");
                MDC.put("truemoneyID", "INVALID_LOGIN");
            }
        }
    }

    /*
     * @Around(
     * "execution(* th.co.truemoney.serviceinventory.ewallet.impl.ActivityServiceImpl.*(..))"
     * ) public Object asyncLogCoreReportWeb(ProceedingJoinPoint joinPoint)
     * throws Throwable {
     *
     * Long startTime = System.currentTimeMillis(); Object[] args =
     * joinPoint.getArgs();
     *
     * if (args.length > 0 && args[0] instanceof SIEngineRequestWrapper) {
     *
     * SIEngineRequestWrapper siEngineRequest = (SIEngineRequestWrapper)
     * args[0]; SIEngineException errorException = null; SIEngineResponseWrapper
     * siEngineResponse = null; try { siEngineResponse =
     * (SIEngineResponseWrapper) joinPoint.proceed(); return siEngineResponse; }
     * catch (SIEngineException ex) { errorException = ex; throw ex; } finally {
     * Long stopTime = System.currentTimeMillis();
     * System.out.println("joinPoint.getTarget().getClass()=>" +
     * joinPoint.getTarget().getClass() + "<" ) ;
     * System.out.println("joinPoint.getTarget().getClass().getSimpleName()=>" +
     * joinPoint.getTarget().getClass().getSimpleName() + "<" ) ; //
     * System.out.println("joinPoint.getSignature()=>" +
     * joinPoint.getSignature() + "<" ) ; //
     * System.out.println("joinPoint.getSignature().getName()=>" +
     * joinPoint.getSignature().getName() + "<" );
     * asyncJdbcLoggingProcessor.writeLogSiEngineProxies
     * (joinPoint.getTarget().getClass().getSimpleName(),
     * joinPoint.getSignature().getName(), siEngineRequest, siEngineResponse,
     * errorException, startTime, stopTime); }
     *
     * } return joinPoint.proceed(); }
     */

}