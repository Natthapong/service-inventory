package th.co.truemoney.serviceinventory.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import th.co.truemoney.serviceinventory.ewallet.ActivityService;
import th.co.truemoney.serviceinventory.ewallet.domain.Activity;
import th.co.truemoney.serviceinventory.ewallet.domain.ActivityDetail;
import th.co.truemoney.serviceinventory.ewallet.impl.ExtendAccessTokenAsynService;
import th.co.truemoney.serviceinventory.exception.ServiceInventoryException;
import th.co.truemoney.serviceinventory.exception.ServiceInventoryWebException;
import th.co.truemoney.serviceinventory.exception.ServiceInventoryWebException.Code;

@Controller
@RequestMapping(value="/ewallet")
public class TransactionActivityController {

	@Autowired
	private ActivityService activityService;
	
	@Autowired
	private ExtendAccessTokenAsynService extendAccessTokenAsynService;
	
	@RequestMapping(value = "/activities/{accessTokenID}", method = RequestMethod.GET)
	public @ResponseBody List<Activity> getActivities(@PathVariable String accessTokenID) {
		extendExpireAccessToken(accessTokenID);
		try {
			List<Activity> activities = activityService.getActivities(accessTokenID);
			return activities;
		} catch (ServiceInventoryException e) {
			throw new ServiceInventoryWebException(Code.GET_ACTIVITY_FAILED, e.getErrorDescription());
		}
	}
	
	@RequestMapping(value = "/activities/{accessTokenID}/detail/{reportID}", method = RequestMethod.GET)
	public @ResponseBody ActivityDetail getActivityDetail(@PathVariable String accessTokenID, @PathVariable Long reportID) {
		extendExpireAccessToken(accessTokenID);
		try {	
			ActivityDetail activityDetail = activityService.getActivityDetail(reportID, accessTokenID);
			return activityDetail;
		} catch (ServiceInventoryException e) {
			throw new ServiceInventoryWebException(Code.GET_ACTIVITY_DETAIL_FAILED, e.getErrorDescription());
		}
	}
	
	@RequestMapping(value = "/activities/{accessTokenID}/resend/e-pin/{reportID}", method = RequestMethod.GET)
	public @ResponseBody Boolean resendEPIN(@PathVariable String accessTokenID, @PathVariable Long reportID) {
		extendExpireAccessToken(accessTokenID);
		try {	
			return activityService.resendEPIN(reportID, accessTokenID);
		} catch (ServiceInventoryException e) {
			throw new ServiceInventoryWebException(Code.GET_ACTIVITY_DETAIL_FAILED, e.getErrorDescription());
		}
	}
	
	private void extendExpireAccessToken(String accessTokenID) {
		extendAccessTokenAsynService.setExpire(accessTokenID);
	}
	
}
