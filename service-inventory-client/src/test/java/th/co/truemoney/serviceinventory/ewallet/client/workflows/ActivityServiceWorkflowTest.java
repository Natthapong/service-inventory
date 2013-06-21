package th.co.truemoney.serviceinventory.ewallet.client.workflows;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import th.co.truemoney.serviceinventory.ewallet.client.ActivityServicesClient;
import th.co.truemoney.serviceinventory.ewallet.client.TmnProfileServiceClient;
import th.co.truemoney.serviceinventory.ewallet.client.config.LocalEnvironmentConfig;
import th.co.truemoney.serviceinventory.ewallet.client.config.ServiceInventoryClientConfig;
import th.co.truemoney.serviceinventory.ewallet.client.testutils.IntegrationTest;
import th.co.truemoney.serviceinventory.ewallet.client.testutils.TestData;
import th.co.truemoney.serviceinventory.ewallet.domain.Activity;
import th.co.truemoney.serviceinventory.ewallet.domain.ActivityDetail;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { ServiceInventoryClientConfig.class, LocalEnvironmentConfig.class })
@ActiveProfiles(profiles = "local")
@Category(IntegrationTest.class)
public class ActivityServiceWorkflowTest {

    @Autowired
    ActivityServicesClient client;

    @Autowired
    TmnProfileServiceClient profileService;

    @Test @Ignore
    public void getActivitySuccess(){
	String accessToken = profileService.login(
		TestData.createSuccessUserLogin(),
		TestData.createSuccessClientLogin());

	assertNotNull(accessToken);

	List<Activity> activities = client.getActivities(accessToken);
	assertNotNull(activities);
	assertTrue(activities.size() > 0);

	ActivityDetail activityDetail = client.getActivityDetail(1000L, accessToken);
	assertNotNull(activityDetail);
	assertEquals(-200, activityDetail.getAmount().intValue());

    }

}
