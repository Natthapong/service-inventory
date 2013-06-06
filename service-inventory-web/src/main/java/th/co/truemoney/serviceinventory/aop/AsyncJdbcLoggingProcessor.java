package th.co.truemoney.serviceinventory.aop;

import java.sql.Timestamp;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import th.co.truemoney.serviceinventory.bill.domain.BillPaymentDraft;
import th.co.truemoney.serviceinventory.bill.domain.BillPaymentTransaction;
import th.co.truemoney.serviceinventory.engine.client.domain.services.SIEngineRequestWrapper;
import th.co.truemoney.serviceinventory.engine.client.domain.services.SIEngineResponseWrapper;
import th.co.truemoney.serviceinventory.engine.client.exception.SIEngineException;
import th.co.truemoney.serviceinventory.engine.client.exception.SIEngineUnExpectedException;
import th.co.truemoney.serviceinventory.ewallet.domain.AccessToken;
import th.co.truemoney.serviceinventory.ewallet.exception.EwalletException;
import th.co.truemoney.serviceinventory.ewallet.exception.EwalletUnExpectedException;
import th.co.truemoney.serviceinventory.ewallet.proxy.message.EwalletRequest;
import th.co.truemoney.serviceinventory.ewallet.proxy.message.EwalletResponse;
import th.co.truemoney.serviceinventory.ewallet.repositories.AccessTokenRepository;
import th.co.truemoney.serviceinventory.exception.ServiceInventoryException;

@Component
public class AsyncJdbcLoggingProcessor {

    private Logger spikeLogger = LoggerFactory.getLogger("spikelog");

    @Autowired
    private AccessTokenRepository accessTokenRepository;

    public void setLogin() {
        try {
            String accessTokenID = MDC.get("accessTokenID");
            AccessToken accessToken = accessTokenRepository.findAccessToken(accessTokenID);
            String loginID = accessToken==null?null:(accessToken.getEmail()==null?accessToken.getMobileNumber():accessToken.getEmail());
            String truemoneyID = accessToken==null?null:(accessToken.getTruemoneyID());
            if(loginID!=null) {
                MDC.put("loginID", loginID);
            }
            if(truemoneyID!=null) {
                MDC.put("truemoneyID", truemoneyID);
            }
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (ServiceInventoryException e) {
            e.printStackTrace();
        }
    }

    public void writeLogController(String workerName, String activityName, List<Object> objectList, Object retObject, ServiceInventoryException serviceInventoryErrorCase, long startTime, long stopTime)  {
        try {
            String trackingID = MDC.get("trackingID");
            Timestamp createdDate = new Timestamp(startTime);
            Timestamp responseDate = new Timestamp(stopTime);

            String loginID = MDC.get("loginID");
            String truemoneyID = MDC.get("truemoneyID");
            String httpStatus = serviceInventoryErrorCase == null ? "200" : Integer.toString(serviceInventoryErrorCase.getHttpStatus());
            String resultCode = serviceInventoryErrorCase != null ? serviceInventoryErrorCase.getErrorCode() : null;
            String resultNamespace = serviceInventoryErrorCase != null ? serviceInventoryErrorCase.getErrorNamespace() : null;
            String transactionID = null;
            String processState = null;
            if(retObject instanceof BillPaymentDraft) {
                transactionID = ((BillPaymentDraft) retObject).getID();
                processState = ((BillPaymentDraft) retObject).getStatus().getStatus();
            }
            if(retObject instanceof BillPaymentTransaction) {
                transactionID = ((BillPaymentTransaction) retObject).getID();
                processState = ((BillPaymentTransaction) retObject).getStatus().getStatus();
            }
            String refTransID = null;
            Integer durationTime = (int) (stopTime - startTime);

            spikeLogger.info("-----------------------------");
            spikeLogger.info("trackingID: " + trackingID);
            spikeLogger.info("createdDate: " + createdDate);
            spikeLogger.info("responseDate: " + responseDate);
            spikeLogger.info("loginID: " + loginID);
            spikeLogger.info("truemoneyID: " + truemoneyID);
            spikeLogger.info("httpStatus: " + httpStatus);
            spikeLogger.info("resultCode: " + resultCode);
            spikeLogger.info("resultNamespace: " + resultNamespace);
            spikeLogger.info("transactionID: " + transactionID);
            spikeLogger.info("processState: " + processState);
            spikeLogger.info("refTransID: " + refTransID);
            spikeLogger.info("durationTime: " + durationTime);
            spikeLogger.info("WorkerName: " + workerName);
            spikeLogger.info("ActivityName: " + activityName);
            for (Object tmpObject : objectList) {
                spikeLogger.info("Request Param: " + tmpObject);
            }
            spikeLogger.info("Response: " + retObject);
            spikeLogger.info("-----------------------------");
        } catch(ServiceInventoryException e) {
            e.printStackTrace();
        }
    }

    public void writeLogEwalletProxies(String workerName, String activityName,
            EwalletRequest ewalletRequest, EwalletResponse ewalletResponse,
            EwalletException errorException, Long startTime, Long stopTime) {
        try {
            String trackingID = MDC.get("trackingID");
            Timestamp createdDate = new Timestamp(startTime);
            Timestamp responseDate = new Timestamp(stopTime);
            String loginID = MDC.get("loginID");
            String truemoneyID = MDC.get("truemoneyID");
            String httpStatus = (errorException instanceof EwalletUnExpectedException) ? "500" : "200";
            String resultCode = ewalletResponse != null ? ewalletResponse.getResultCode() : errorException.getCode();
            String resultNamespace = ewalletResponse != null ? ewalletResponse.getResultNamespace() : errorException.getNamespace();
            String transactionID = ewalletRequest.getRequestTransactionId();
            String processState = null;
            String refTransID = ewalletResponse != null ? ewalletResponse.getTransactionId() : null;
            Integer durationTime = (int) (stopTime - startTime);

            spikeLogger.info("-----------------------------");
            spikeLogger.info("trackingID: " + trackingID);
            spikeLogger.info("createdDate: " + createdDate);
            spikeLogger.info("responseDate: " + responseDate);
            spikeLogger.info("loginID: " + loginID);
            spikeLogger.info("truemoneyID: " + truemoneyID);
            spikeLogger.info("httpStatus: " + httpStatus);
            spikeLogger.info("resultCode: " + resultCode);
            spikeLogger.info("resultNamespace: " + resultNamespace);
            spikeLogger.info("transactionID: " + transactionID);
            spikeLogger.info("processState: " + processState);
            spikeLogger.info("refTransID: " + refTransID);
            spikeLogger.info("durationTime: " + durationTime);
            spikeLogger.info("WorkerName: " + workerName);
            spikeLogger.info("ActivityName: " + activityName);
            spikeLogger.info("Request: " + ewalletRequest);
            spikeLogger.info("Response: " + ewalletResponse);
            spikeLogger.info("-----------------------------");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void writeLogSiEngineProxies(String workerName, String activityName,
            SIEngineRequestWrapper siEngineRequest, SIEngineResponseWrapper siEngineResponse,
            SIEngineException errorException, Long startTime, Long stopTime) {
        try {
            String trackingID = MDC.get("trackingID");
            Timestamp createdDate = new Timestamp(startTime);
            Timestamp responseDate = new Timestamp(stopTime);
            String loginID = MDC.get("loginID");
            String truemoneyID = MDC.get("truemoneyID");
            String httpStatus = (errorException instanceof SIEngineUnExpectedException) ? "500" : "200";
            String resultCode = siEngineResponse != null ? siEngineResponse.getResultCode() : errorException.getCode();
            String resultNamespace = siEngineResponse != null ? siEngineResponse.getResultNamespace() : errorException.getNamespace();
            String transactionID = siEngineRequest.getReqTransactionId();
            String processState = null;
            String refTransID = siEngineResponse != null ? siEngineResponse.getTransactionID() : null;
            Integer durationTime = (int) (stopTime - startTime);

            spikeLogger.info("-----------------------------");
            spikeLogger.info("trackingID: " + trackingID);
            spikeLogger.info("createdDate: " + createdDate);
            spikeLogger.info("responseDate: " + responseDate);
            spikeLogger.info("loginID: " + loginID);
            spikeLogger.info("truemoneyID: " + truemoneyID);
            spikeLogger.info("httpStatus: " + httpStatus);
            spikeLogger.info("resultCode: " + resultCode);
            spikeLogger.info("resultNamespace: " + resultNamespace);
            spikeLogger.info("transactionID: " + transactionID);
            spikeLogger.info("processState: " + processState);
            spikeLogger.info("refTransID: " + refTransID);
            spikeLogger.info("durationTime: " + durationTime);
            spikeLogger.info("WorkerName: " + workerName);
            spikeLogger.info("ActivityName: " + activityName);
            spikeLogger.info("Request: " + siEngineRequest);
            spikeLogger.info("Response: " + siEngineResponse);
            spikeLogger.info("-----------------------------");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

