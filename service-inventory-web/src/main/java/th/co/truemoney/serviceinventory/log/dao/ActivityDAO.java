package th.co.truemoney.serviceinventory.log.dao;

import org.springframework.dao.DataAccessException;

import th.co.truemoney.serviceinventory.log.domain.ActivityLog;

public interface ActivityDAO {

	public Long createLogID() throws DataAccessException;
	
	public void createActivityLog(ActivityLog activityLog) throws DataAccessException;
	
}
