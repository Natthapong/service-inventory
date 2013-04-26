package th.co.truemoney.serviceinventory.ewallet.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import th.co.truemoney.serviceinventory.ewallet.ActivityService;
import th.co.truemoney.serviceinventory.ewallet.domain.Activity;
import th.co.truemoney.serviceinventory.ewallet.domain.ActivityDetail;
import th.co.truemoney.serviceinventory.exception.ServiceInventoryException;

@Service
public class ActivityServiceImpl implements ActivityService {

	@Override
	public List<Activity> getActivities(String accessTokenID) throws ServiceInventoryException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ActivityDetail getActivityDetail(Long reportID, String accessTokenID) throws ServiceInventoryException {
		// TODO Auto-generated method stub
		return null;
	}

}
