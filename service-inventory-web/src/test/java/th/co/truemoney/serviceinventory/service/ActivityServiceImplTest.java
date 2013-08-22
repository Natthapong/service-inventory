package th.co.truemoney.serviceinventory.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import java.math.BigDecimal;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

import th.co.truemoney.serviceinventory.config.LocalAppleUserConfig;
import th.co.truemoney.serviceinventory.config.LocalEnvironmentConfig;
import th.co.truemoney.serviceinventory.config.MemRepositoriesConfig;
import th.co.truemoney.serviceinventory.config.ServiceInventoryConfig;
import th.co.truemoney.serviceinventory.ewallet.domain.AccessToken;
import th.co.truemoney.serviceinventory.ewallet.domain.Activity;
import th.co.truemoney.serviceinventory.ewallet.domain.ActivityDetail;
import th.co.truemoney.serviceinventory.ewallet.impl.ActivityServiceImpl;
import th.co.truemoney.serviceinventory.ewallet.repositories.AccessTokenRepository;
import th.co.truemoney.serviceinventory.ewallet.repositories.impl.AccessTokenMemoryRepository;
import th.co.truemoney.serviceinventory.exception.ResourceNotFoundException;
import th.co.truemoney.serviceinventory.legacyfacade.LegacyFacade;
import th.co.truemoney.serviceinventory.legacyfacade.facade.builders.UserProfileBuilder;
import th.co.truemoney.serviceinventory.testutils.IntegrationTest;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { ServiceInventoryConfig.class, MemRepositoriesConfig.class, LocalEnvironmentConfig.class, LocalAppleUserConfig.class })
@ActiveProfiles(profiles={"local", "mem"})
@Category(IntegrationTest.class)
public class ActivityServiceImplTest {

	static final int CHANNEL_ID = 40;

	@Autowired
    ActivityServiceImpl service;
    
    MockRestServiceServer mockServer;
    
    RestTemplate restTemplate;
    
    LegacyFacade legacyFacade;
    
    UserProfileBuilder userProfileBuilder;
    
    AccessTokenRepository accessTokenRepo;

    @Before
    public void setup() {
    	restTemplate = new RestTemplate();
    	accessTokenRepo  = new AccessTokenMemoryRepository();
    	
    	accessTokenRepo.save(new AccessToken("accessToken", "loginID", "sessionID", "54321", 40));
    	
    	userProfileBuilder = Mockito.mock(UserProfileBuilder.class);
    	when(userProfileBuilder.fromChannel(anyInt())).thenReturn(userProfileBuilder);
    	when(userProfileBuilder.withServiceCode(anyString())).thenReturn(userProfileBuilder);
    	when(userProfileBuilder.withRefernce1(anyString())).thenReturn(userProfileBuilder);
    	
    	legacyFacade = Mockito.mock(LegacyFacade.class);
    	when(legacyFacade.userProfile(anyString(), anyString())).thenReturn(userProfileBuilder);
    	
    	service.setRestTemplate(restTemplate);
    	service.setAccessTokenRepository(accessTokenRepo);
    	service.setLegacyFacade(legacyFacade);
    	
    	mockServer = MockRestServiceServer.createServer(restTemplate);
    }
    
    @Test
    public void getMobileAcitivityList() {
    	mockServer.expect(
            	requestTo("http://127.0.0.1:8787/core-report-web/transaction/history/54321")
            ).andExpect(method(HttpMethod.GET)
            ).andRespond(
            	withSuccess(new ClassPathResource("json/stub_all_activities.json"), MediaType.APPLICATION_JSON)
            );
    	
    	List<Activity> activityList = service.getActivities("accessToken");
    	
    	mockServer.verify();
    	assertNotNull(activityList);
    	assertEquals(1, activityList.size());
    }

    @Test
    public void getMobileActivityDetailSuccess(){
    	String truemoneyID = "54321";
    	String reportID = "9999";

    	when(userProfileBuilder.isFavoritable()).thenReturn(Boolean.TRUE);
    	
    	mockServer.expect(
            	requestTo(String.format("http://127.0.0.1:8787/core-report-web/transaction/history/%s/detail/%s", truemoneyID, reportID))
            ).andExpect(method(HttpMethod.GET)
            ).andRespond(
            	withSuccess(new ClassPathResource("json/stub_specific_activities.json"), MediaType.APPLICATION_JSON)
            );
    	
        ActivityDetail activity = service.getActivityDetail(9999L, "accessToken");

        mockServer.verify();
        assertNotNull(activity);

        assertEquals("Truemove H+", activity.getAction());
        assertEquals(new BigDecimal(2000), activity.getAmount());
        assertEquals("billpay", activity.getType());
        assertEquals("085-382-8482", activity.getRef1());
        assertEquals(Boolean.TRUE, activity.isFavoritable());
    }

    @Test
    public void badAccessTokenError() {
        try {
            service.getActivities("bad access token");
            fail("invalid access token");
        } catch (ResourceNotFoundException ex) {

        }
    }

}
