package th.co.truemoney.serviceinventory.log;

import java.sql.Timestamp;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import th.co.truemoney.serviceinventory.engine.client.domain.services.SIEngineRequestWrapper;
import th.co.truemoney.serviceinventory.engine.client.domain.services.SIEngineResponseWrapper;
import th.co.truemoney.serviceinventory.engine.client.exception.SIEngineException;
import th.co.truemoney.serviceinventory.engine.client.exception.SIEngineUnExpectedException;
import th.co.truemoney.serviceinventory.ewallet.domain.DraftTransaction;
import th.co.truemoney.serviceinventory.ewallet.domain.OTP;
import th.co.truemoney.serviceinventory.ewallet.domain.Transaction;
import th.co.truemoney.serviceinventory.ewallet.exception.EwalletException;
import th.co.truemoney.serviceinventory.ewallet.exception.EwalletUnExpectedException;
import th.co.truemoney.serviceinventory.ewallet.proxy.message.EwalletRequest;
import th.co.truemoney.serviceinventory.ewallet.proxy.message.EwalletResponse;
import th.co.truemoney.serviceinventory.exception.ServiceInventoryException;
import th.co.truemoney.serviceinventory.firsthop.message.SmsRequest;
import th.co.truemoney.serviceinventory.firsthop.message.SmsResponse;
import th.co.truemoney.serviceinventory.log.dao.ActivityDAO;
import th.co.truemoney.serviceinventory.log.domain.ActivityLog;

@Component
public class AsyncJdbcLoggingProcessor {

    private static final String MDC_WORKFLOW_STATUS = "worflowStatus";
    private static final String MDC_DRAFT_TRANSACTION_ID = "draftTransactionID";
    private static final String MDC_TRANSACTION_ID = "transactionID";

    private static final String MDC_TRACKING_ID = "trackingID";

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private ActivityDAO activityDAO;

    public void writeLogController(String workerName, String activityName, List<Object> inputArgs, Object returnObj, ServiceInventoryException siWebException, long startTime, long stopTime)  {
        try {
            String trackingID = MDC.get(MDC_TRACKING_ID);

            if(trackingID==null) {
                return;
            }

            String loginID = MDC.get("loginID");
            String truemoneyID = MDC.get("truemoneyID");

            Short httpStatus = siWebException == null ? Short.valueOf("200") : siWebException.getHttpStatus().shortValue();
            String resultCode = siWebException != null ? siWebException.getErrorCode() : "0";
            String resultNamespace = siWebException != null ? siWebException.getErrorNamespace() : "TMN-SERVICE-INVENTORY";

            String processState = getWorkflowStatus(returnObj);
            String transactionID = getWorkflowTransactionID(returnObj);

	    logger.info("transaction ID : " + workerName + " " + activityName + ": " + transactionID);

            String refTransID = MDC.get("refTransID");
            String accessID = MDC.get("accessTokenID");

            Integer durationTime = getDurationTime(startTime, stopTime);

            String detailInput = getInputDetails(inputArgs);
            String detailOutput = getOutputDetails(returnObj);

            Long logID = activityDAO.createLogID();

            ActivityLog activityLog = new ActivityLog();
            activityLog.setLogID(logID);
            activityLog.setTrackingID(trackingID);
            activityLog.setWorkerTypeID(Short.valueOf("0"));
            activityLog.setAccessID(accessID);
            activityLog.setTruemoneyID(truemoneyID);
            activityLog.setLoginID(loginID);
            activityLog.setWorkerName(workerName);
            activityLog.setActivityName(activityName);
            activityLog.setHttpStatus(httpStatus);
            activityLog.setResultCode(resultCode);
            activityLog.setResultNamespace(resultNamespace);
            activityLog.setTransactionID(transactionID);
            activityLog.setProcessState(processState);
            activityLog.setRefTransID(refTransID);
            activityLog.setCreatedDate(new Timestamp(startTime));
            activityLog.setResponseDate(new Timestamp(stopTime));
            activityLog.setDurationTime(durationTime);
            activityLog.setDetailInput(detailInput);
            activityLog.setDetailOutput(detailOutput);

            logger.info("-----------------------------");
            logger.info("ActivityLog: " + activityLog.toString());
            logger.info("-----------------------------");

            activityDAO.createActivityLog(activityLog);
        } catch(ServiceInventoryException e) {
            logger.error("error writing user activity log", e);
        }
    }

    private String getOutputDetails(Object retObject) {
        return (retObject != null) ? retObject.toString() : null;
    }

    private String getInputDetails(List<Object> inputArgs) {
        StringBuilder builder = new StringBuilder();
        if( (inputArgs!=null)
                && (inputArgs.size()>0) ) {
            for (Object tmpObject : inputArgs) {
                builder.append(tmpObject.toString());
            }
        }

        return builder.toString();
    }

    private Integer getDurationTime(long startTime, long stopTime) {
        return (int) (stopTime - startTime);
    }

    private String getWorkflowTransactionID(Object retObject) {

	String mdcDraftTransactionID = MDC.get(MDC_DRAFT_TRANSACTION_ID);
	String mdcTransactionID = MDC.get(MDC_TRANSACTION_ID);

	return mdcDraftTransactionID != null ? mdcDraftTransactionID : mdcTransactionID;
    }

    private String getWorkflowStatus(Object retObject) {
        if(retObject instanceof DraftTransaction) {
            return ((DraftTransaction) retObject).getStatus().getStatus();
        } else if(retObject instanceof Transaction) {
            return ((Transaction) retObject).getStatus().getStatus();
        } else if(retObject instanceof Transaction.Status) {
            return ((Transaction.Status) retObject).getStatus();
        } else if(retObject instanceof DraftTransaction.Status) {
            return ((DraftTransaction.Status) retObject).getStatus();
	} else if(retObject instanceof OTP)  {
	    return MDC.get(MDC_WORKFLOW_STATUS);
        }

        return null;
    }

    public void writeLogSiEngineProxies(String workerName, String activityName,
            SIEngineRequestWrapper siEngineRequest, SIEngineResponseWrapper siEngineResponse,
            SIEngineException errorException, Long startTime, Long stopTime) {
        try {
            String trackingID = MDC.get(MDC_TRACKING_ID);
            if(trackingID==null) {
                return;
            }
            Timestamp createdDate = new Timestamp(startTime);
            Timestamp responseDate = new Timestamp(stopTime);
            String loginID = MDC.get("loginID");
            String truemoneyID = MDC.get("truemoneyID");
            Short httpStatus = (errorException instanceof SIEngineUnExpectedException) ? Short.valueOf("500") : Short.valueOf("200");
            String resultCode = siEngineResponse != null ? siEngineResponse.getResultCode() : errorException.getCode();
            String resultNamespace = siEngineResponse != null ? siEngineResponse.getResultNamespace() : errorException.getNamespace();
            String proxyTransactionID = siEngineRequest != null ? siEngineRequest.getReqTransactionId() : null;
            String processState = null;
            String refTransID = siEngineResponse != null ? siEngineResponse.getTransactionID() : null;
            Integer durationTime = (int) (stopTime - startTime);
            String accessID = MDC.get("accessTokenID");
            Long logID = activityDAO.createLogID();

            StringBuilder detailInput = new StringBuilder();
            if(siEngineRequest!=null) {
                detailInput = detailInput.append(siEngineRequest.toString());
            }
            StringBuilder detailOutput = new StringBuilder();
            if(siEngineResponse!=null) {
                detailOutput = detailOutput.append(siEngineResponse.toString());
            }
            if(refTransID!=null) {
                MDC.put("refTransID", refTransID);
            }
            if(proxyTransactionID!=null) {
                MDC.put("proxyTransactionID", proxyTransactionID);
            }

            ActivityLog activityLog = new ActivityLog();
            activityLog.setLogID(logID);
            activityLog.setTrackingID(trackingID);
            activityLog.setWorkerTypeID(Short.valueOf("1"));
            activityLog.setAccessID(accessID);
            activityLog.setTruemoneyID(truemoneyID);
            activityLog.setLoginID(loginID);
            activityLog.setWorkerName(workerName);
            activityLog.setActivityName(activityName);
            activityLog.setHttpStatus(httpStatus);
            activityLog.setResultCode(resultCode);
            activityLog.setResultNamespace(resultNamespace);
            activityLog.setTransactionID(proxyTransactionID);
            activityLog.setProcessState(processState);
            activityLog.setRefTransID(refTransID);
            activityLog.setCreatedDate(createdDate);
            activityLog.setResponseDate(responseDate);
            activityLog.setDurationTime(durationTime);
            activityLog.setDetailInput(detailInput.toString());
            activityLog.setDetailOutput(detailOutput.toString());

            logger.info("-----------------------------");
            logger.info("ActivityLog: " + activityLog.toString());
            logger.info("-----------------------------");

            activityDAO.createActivityLog(activityLog);
        } catch (Exception e) {
            logger.error("error writing user activity log", e);
        }
    }

    public void writeLogEwalletProxies(String workerName, String activityName,
            EwalletRequest ewalletRequest, EwalletResponse ewalletResponse,
            EwalletException errorException, Long startTime, Long stopTime) {
        try {
            String trackingID = MDC.get(MDC_TRACKING_ID);
            if(trackingID==null) {
                return;
            }
            Timestamp createdDate = new Timestamp(startTime);
            Timestamp responseDate = new Timestamp(stopTime);
            String loginID = MDC.get("loginID");
            String truemoneyID = MDC.get("truemoneyID");
            Short httpStatus = (errorException instanceof EwalletUnExpectedException) ? Short.valueOf("500") : Short.valueOf("200");
            String resultCode = ewalletResponse != null ? ewalletResponse.getResultCode() : errorException.getCode();
            String resultNamespace = ewalletResponse != null ? ewalletResponse.getResultNamespace() : errorException.getNamespace();
            String proxyTransactionID = ewalletRequest != null ? ewalletRequest.getRequestTransactionId() : null;
            String processState = null;
            String refTransID = ewalletResponse != null ? ewalletResponse.getTransactionId() : null;
            Integer durationTime = (int) (stopTime - startTime);
            String accessID = MDC.get("accessTokenID");
            Long logID = activityDAO.createLogID();

            StringBuilder detailInput = new StringBuilder();
            if(ewalletRequest!=null) {
                detailInput = detailInput.append(ewalletRequest.toString());
            }
            StringBuilder detailOutput = new StringBuilder();
            if(ewalletResponse!=null) {
                detailOutput = detailOutput.append(ewalletResponse.toString());
            }
            if(refTransID!=null) {
                MDC.put("refTransID", refTransID);
            }
            if(proxyTransactionID!=null) {
                MDC.put("proxyTransactionID", proxyTransactionID);
            }

            ActivityLog activityLog = new ActivityLog();
            activityLog.setLogID(logID);
            activityLog.setTrackingID(trackingID);
            activityLog.setWorkerTypeID(Short.valueOf("1"));
            activityLog.setAccessID(accessID);
            activityLog.setTruemoneyID(truemoneyID);
            activityLog.setLoginID(loginID);
            activityLog.setWorkerName(workerName);
            activityLog.setActivityName(activityName);
            activityLog.setHttpStatus(httpStatus);
            activityLog.setResultCode(resultCode);
            activityLog.setResultNamespace(resultNamespace);
            activityLog.setTransactionID(proxyTransactionID);
            activityLog.setProcessState(processState);
            activityLog.setRefTransID(refTransID);
            activityLog.setCreatedDate(createdDate);
            activityLog.setResponseDate(responseDate);
            activityLog.setDurationTime(durationTime);
            activityLog.setDetailInput(detailInput.toString());
            activityLog.setDetailOutput(detailOutput.toString());

            logger.info("-----------------------------");
            logger.info("ActivityLog: " + activityLog.toString());
            logger.info("-----------------------------");

            activityDAO.createActivityLog(activityLog);
        } catch (Exception e) {
            logger.error("error writing user activity log", e);
        }
    }

    public void writeLogSmsProxies(String workerName, String activityName,
            SmsRequest smsRequest, SmsResponse smsResponse, Exception errorException, Long startTime, Long stopTime) {
        try {
            String trackingID = MDC.get(MDC_TRACKING_ID);
            if(trackingID==null) {
                return;
            }
            Timestamp createdDate = new Timestamp(startTime);
            Timestamp responseDate = new Timestamp(stopTime);
            String loginID = MDC.get("loginID");
            String truemoneyID = MDC.get("truemoneyID");
            Short httpStatus = errorException == null ? Short.valueOf("200") : Short.valueOf("500");
            String resultCode = smsResponse != null ? smsResponse.getResultCode() : null;
            String resultNamespace = smsResponse != null ? smsResponse.getResultNamespace() : null;
            String proxyTransactionID = smsRequest != null ? smsRequest.getReqTransactionId() : null;
            String processState = null;
            String refTransID = smsResponse != null ? smsResponse.getTransactionID() : null;
            Integer durationTime = (int) (stopTime - startTime);
            String accessID = MDC.get("accessTokenID");
            Long logID = activityDAO.createLogID();

            StringBuilder detailInput = new StringBuilder();
            if(smsRequest!=null) {
                detailInput = detailInput.append(smsRequest.toString());
            }
            StringBuilder detailOutput = new StringBuilder();
            if(smsResponse!=null) {
                detailOutput = detailOutput.append(smsResponse.toString());
            }
            if(refTransID!=null) {
                MDC.put("refTransID", refTransID);
            }
            if(proxyTransactionID!=null) {
                MDC.put("proxyTransactionID", proxyTransactionID);
            }

            ActivityLog activityLog = new ActivityLog();
            activityLog.setLogID(logID);
            activityLog.setTrackingID(trackingID);
            activityLog.setWorkerTypeID(Short.valueOf("1"));
            activityLog.setAccessID(accessID);
            activityLog.setTruemoneyID(truemoneyID);
            activityLog.setLoginID(loginID);
            activityLog.setWorkerName(workerName);
            activityLog.setActivityName(activityName);
            activityLog.setHttpStatus(httpStatus);
            activityLog.setResultCode(resultCode);
            activityLog.setResultNamespace(resultNamespace);
            activityLog.setTransactionID(proxyTransactionID);
            activityLog.setProcessState(processState);
            activityLog.setRefTransID(refTransID);
            activityLog.setCreatedDate(createdDate);
            activityLog.setResponseDate(responseDate);
            activityLog.setDurationTime(durationTime);
            activityLog.setDetailInput(detailInput.toString());
            activityLog.setDetailOutput(detailOutput.toString());

            logger.info("-----------------------------");
            logger.info("ActivityLog: " + activityLog.toString());
            logger.info("-----------------------------");

            activityDAO.createActivityLog(activityLog);
        } catch (Exception e) {
            logger.error("error writing user activity log", e);
        }
    }

}
