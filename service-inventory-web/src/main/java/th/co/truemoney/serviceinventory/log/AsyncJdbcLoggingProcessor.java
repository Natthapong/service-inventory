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
import th.co.truemoney.serviceinventory.ewallet.domain.AccessToken;
import th.co.truemoney.serviceinventory.ewallet.domain.DraftTransaction;
import th.co.truemoney.serviceinventory.ewallet.domain.Transaction;
import th.co.truemoney.serviceinventory.ewallet.exception.EwalletException;
import th.co.truemoney.serviceinventory.ewallet.exception.EwalletUnExpectedException;
import th.co.truemoney.serviceinventory.ewallet.proxy.message.EwalletRequest;
import th.co.truemoney.serviceinventory.ewallet.proxy.message.EwalletResponse;
import th.co.truemoney.serviceinventory.ewallet.repositories.AccessTokenRepository;
import th.co.truemoney.serviceinventory.exception.ServiceInventoryException;
import th.co.truemoney.serviceinventory.firsthop.message.SmsRequest;
import th.co.truemoney.serviceinventory.firsthop.message.SmsResponse;
import th.co.truemoney.serviceinventory.log.dao.ActivityDAO;
import th.co.truemoney.serviceinventory.log.domain.ActivityLog;

@Component
public class AsyncJdbcLoggingProcessor {

	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	private ActivityDAO activityDAO;
	
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
		} catch (ServiceInventoryException e) {
		}
	}
	
	public void writeLogController(String workerName, String activityName, List<Object> objectList, Object retObject, ServiceInventoryException serviceInventoryErrorCase, long startTime, long stopTime)  {
		try {
			String trackingID = MDC.get("trackingID");
			if(trackingID==null) {
				return;
			}
			Timestamp createdDate = new Timestamp(startTime);
			Timestamp responseDate = new Timestamp(stopTime);
			
			String loginID = MDC.get("loginID");
			String truemoneyID = MDC.get("truemoneyID");
			Short httpStatus = serviceInventoryErrorCase == null ? Short.valueOf("200") : Short.valueOf(serviceInventoryErrorCase.getHttpStatus().toString()) ;
			String resultCode = serviceInventoryErrorCase != null ? serviceInventoryErrorCase.getErrorCode() : null;
			String resultNamespace = serviceInventoryErrorCase != null ? serviceInventoryErrorCase.getErrorNamespace() : null;
			String transactionID = null;
			String processState = null;
			if(retObject instanceof DraftTransaction) {
				transactionID = ((DraftTransaction) retObject).getID();
				processState = ((DraftTransaction) retObject).getStatus().getStatus();
			} else if(retObject instanceof Transaction) {
				transactionID = ((Transaction) retObject).getID();
				processState = ((Transaction) retObject).getStatus().getStatus();
			} else if(retObject instanceof Transaction.Status) {
				transactionID = null;
				processState = ((Transaction.Status) retObject).getStatus();
			} else if(retObject instanceof DraftTransaction.Status) {
				transactionID = null;
				processState = ((DraftTransaction.Status) retObject).getStatus();
			}
			if(transactionID==null) {
				transactionID = MDC.get("transactionID");
			}
			
			String refTransID = MDC.get("refTransID");
			Integer durationTime = (int) (stopTime - startTime);
			String accessID = MDC.get("accessTokenID");
			
			Long logID = activityDAO.createLogID();
			
			StringBuilder detailInput = new StringBuilder();
			if( (objectList!=null) 
					&& (objectList.size()>0) ) {
				for (Object tmpObject : objectList) {
					detailInput.append(tmpObject.toString());
				}
			}
			
			StringBuilder detailOutput = new StringBuilder();
			if(retObject!=null) {
				detailOutput = detailOutput.append(retObject.toString());
			}
			
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
			activityLog.setCreatedDate(createdDate);
			activityLog.setResponseDate(responseDate);
			activityLog.setDurationTime(durationTime);
			activityLog.setDetailInput(detailInput.toString());
			activityLog.setDetailOutput(detailOutput.toString());
			
			logger.info("-----------------------------");
			logger.info("ActivityLog: " + activityLog.toString());
			logger.info("-----------------------------");
			
			activityDAO.createActivityLog(activityLog);
		} catch(ServiceInventoryException e) {
			e.printStackTrace();
		}
	}
	
	public void writeLogSiEngineProxies(String workerName, String activityName,
			SIEngineRequestWrapper siEngineRequest, SIEngineResponseWrapper siEngineResponse,
			SIEngineException errorException, Long startTime, Long stopTime) {
		try {
			String trackingID = MDC.get("trackingID");
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
			String transactionID = siEngineRequest != null ? siEngineRequest.getReqTransactionId() : null;
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
			if(transactionID!=null) {
				MDC.put("transactionID", transactionID);
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
			activityLog.setTransactionID(transactionID);
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
		} catch (ServiceInventoryException e) {
			e.printStackTrace();
		}
	}
	
	public void writeLogEwalletProxies(String workerName, String activityName,
			EwalletRequest ewalletRequest, EwalletResponse ewalletResponse,
			EwalletException errorException, Long startTime, Long stopTime) {
		try {
			String trackingID = MDC.get("trackingID");
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
			String transactionID = ewalletRequest != null ? ewalletRequest.getRequestTransactionId() : null;
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
			if(transactionID!=null) {
				MDC.put("transactionID", transactionID);
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
			activityLog.setTransactionID(transactionID);
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
		} catch (ServiceInventoryException e) {
			e.printStackTrace();
		}
	}
	
	public void writeLogSmsProxies(String workerName, String activityName,
			SmsRequest smsRequest, SmsResponse smsResponse, Exception errorException, Long startTime, Long stopTime) {
		try {
			String trackingID = MDC.get("trackingID");
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
			String transactionID = smsRequest != null ? smsRequest.getReqTransactionId() : null;
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
			if(transactionID!=null) {
				MDC.put("transactionID", transactionID);
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
			activityLog.setTransactionID(transactionID);
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
		} catch (ServiceInventoryException e) {
			e.printStackTrace();
		}
	}
	
}
