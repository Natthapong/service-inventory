package th.co.truemoney.serviceinventory.log.dao;

import org.springframework.dao.DataAccessException;

import th.co.truemoney.serviceinventory.log.domain.ActivityLog;

public interface ActivityDAO {

	public void createActivityLog(ActivityLog activityLog) throws DataAccessException;
	
}
