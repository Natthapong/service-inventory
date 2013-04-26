package th.co.truemoney.serviceinventory.ewallet;

import java.util.List;

import th.co.truemoney.serviceinventory.ewallet.domain.Activity;
import th.co.truemoney.serviceinventory.ewallet.domain.ActivityDetail;
import th.co.truemoney.serviceinventory.exception.ServiceInventoryException;

public interface ActivityService {
	
	public List<Activity> getActivities(String accessTokenID)
		throws ServiceInventoryException;
	
	public ActivityDetail getActivityDetail(String reportID, String accessTokenID)
		throws ServiceInventoryException;
	
}
