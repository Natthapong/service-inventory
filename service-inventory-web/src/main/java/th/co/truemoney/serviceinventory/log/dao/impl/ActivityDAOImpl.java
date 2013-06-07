package th.co.truemoney.serviceinventory.log.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import th.co.truemoney.serviceinventory.log.dao.ActivityDAO;
import th.co.truemoney.serviceinventory.log.domain.ActivityLog;

@Repository
public class ActivityDAOImpl implements ActivityDAO {

	private JdbcTemplate jdbcTemplate;

	@Autowired
	public void setDataSource(DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}
	
	@Override
	public Long createLogID() throws DataAccessException {
		String sql = "SELECT SI_ACTIVITY_LOG_SEQ.NEXTVAL AS LOG_ID FROM DUAL";
		
		return (Long) jdbcTemplate.queryForObject(sql, new Object[] { }, 
			new RowMapper<Object>() {
				public Object mapRow(ResultSet resultSet, int rowNum) throws SQLException {
					return getLong(resultSet, "LOG_ID");
				}
			});
	}

    @Transactional
    public void createActivityLog(ActivityLog activityLog) throws DataAccessException {
        String sql1 = "insert into SI_ACTIVITY_LOG (LOG_ID, TRACKING_ID, WORKER_TYPE_ID, " +
        		"ACCESS_ID, TMNID, LOGIN_ID, WORKER_NAME, ACTIVITY_NAME, HTTP_STATUS, " +
        		"RESULT_CODE, RESULT_NAMESPACE, TRANS_ID, PROCESS_STATE, REF_TRANS_ID, CREATED_DATE, " +
        		"RESPONSE_DATE, DURATION_TIME, DETAIL_INPUT, DETAIL_OUTPUT) " +
        				"values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        jdbcTemplate.update(sql1, new Object[] { activityLog.getLogID(), activityLog.getTrackingID(), activityLog.getWorkerTypeID(), activityLog.getAccessID(), 
        		activityLog.getTruemoneyID(), activityLog.getLoginID(),activityLog.getWorkerName(), activityLog.getActivityName(), activityLog.getHttpStatus(), 
        		activityLog.getResultCode(), activityLog.getResultNamespace(), activityLog.getTransactionID(), activityLog.getProcessState(), activityLog.getRefTransID(), activityLog.getCreatedDate(), 
        		activityLog.getResponseDate(), activityLog.getDurationTime(), activityLog.getDetailInput(), activityLog.getDetailOutput()});
    }
    
    private Long getLong(ResultSet rs, String key) throws SQLException {
		if (rs != null) {
			return (Long)rs.getLong(key);
		}
		return null;
	}	


}
