package th.co.truemoney.serviceinventory.log.dao;

import java.sql.Timestamp;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.web.WebAppConfiguration;

import th.co.truemoney.serviceinventory.config.LocalEnvironmentConfig;
import th.co.truemoney.serviceinventory.config.MemRepositoriesConfig;
import th.co.truemoney.serviceinventory.config.ServiceInventoryConfig;
import th.co.truemoney.serviceinventory.config.TestJdbcConfig;
import th.co.truemoney.serviceinventory.log.domain.ActivityLog;

import com.github.springtestdbunit.DbUnitTestExecutionListener;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(classes = { ServiceInventoryConfig.class, LocalEnvironmentConfig.class, MemRepositoriesConfig.class, TestJdbcConfig.class })
@ActiveProfiles(profiles = {"local", "mem"})
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class,
		DbUnitTestExecutionListener.class })
public class ActivityDAOTest {

	@Autowired
	private ActivityDAO activityDAO;

	@Test
	public void shouldCreateActivityLogSuccess() {

		Long logID = System.currentTimeMillis();
		String trackingID = "d80ec5cd-aae5-478c-a201-7e0f474c5027";
		Short workerTypeID = 0;
		String accessID = "d80ec5cd-aae5-478c-a201-7e1234567890";
		String truemoneyID = "tmn.10000000001";
		String loginID = "0838309928";
		String workerName = "billpaymentcontroller";
		String activityName = "scanandcreatebillpayment";
		Short httpStatus = 200;
		String resultCode = "0";
		String resultNamespace = "core";
		String transactionID = "d80ec5cd-aae5-478c-a201-7e0987654321";
		String processState = "success";
		String refTransID = "1234567890";
		Timestamp createdDate = new Timestamp(System.currentTimeMillis());
		Timestamp responseDate = new Timestamp(System.currentTimeMillis());
		Integer durationTime = 211;
		String detailInput = "input";
		String detailOutput = "output";

		ActivityLog activityLog = new ActivityLog();
		activityLog.setLogID(logID);
		activityLog.setAccessID(accessID);
		activityLog.setActivityName(activityName);
		activityLog.setCreatedDate(createdDate);
		activityLog.setDetailInput(detailInput);
		activityLog.setDetailOutput(detailOutput);
		activityLog.setDurationTime(durationTime);
		activityLog.setHttpStatus(httpStatus);
		activityLog.setLoginID(loginID);
		activityLog.setProcessState(processState);
		activityLog.setRefTransID(refTransID);
		activityLog.setResponseDate(responseDate);
		activityLog.setResultCode(resultCode);
		activityLog.setResultNamespace(resultNamespace);
		activityLog.setTrackingID(trackingID);
		activityLog.setTransactionID(transactionID);
		activityLog.setTruemoneyID(truemoneyID);
		activityLog.setWorkerName(workerName);
		activityLog.setWorkerTypeID(workerTypeID);

		activityDAO.createActivityLog(activityLog);
	}
}
