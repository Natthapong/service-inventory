package th.co.truemoney.serviceinventory.log.dao.impl;

import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import th.co.truemoney.serviceinventory.log.dao.ActivityDAO;
import th.co.truemoney.serviceinventory.log.domain.ActivityLog;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Repository
public class ActivityDAOImpl implements ActivityDAO {

    @Transactional
    public void createActivityLog(ActivityLog activityLog) throws DataAccessException {
        //String sql1 = "";
        //jdbcTemplate.update(sql1, activityLog.getAccessTokenID(),activityLog.getActivityName(), activityLog.getCreatedDate(), activityLog.getDetails(), activityLog.getLoginID(), activityLog.getReferenceTransactionID(), activityLog.getResponseDate(), activityLog.getTmnID(), activityLog.getTransactionID());
        try {
            System.out.println(new ObjectMapper().writeValueAsString(activityLog));
        } catch (JsonProcessingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

}
