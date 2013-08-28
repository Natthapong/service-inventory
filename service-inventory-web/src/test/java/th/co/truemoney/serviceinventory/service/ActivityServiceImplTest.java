package th.co.truemoney.serviceinventory.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
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
import th.co.truemoney.serviceinventory.config.TestEnvConfig;
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
@ContextConfiguration(classes = { ServiceInventoryConfig.class, MemRepositoriesConfig.class, LocalEnvironmentConfig.class, LocalAppleUserConfig.class, TestEnvConfig.class })
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
    	accessTokenRepo = new AccessTokenMemoryRepository();
    	service.setRestTemplate(restTemplate);
    	service.setAccessTokenRepository(accessTokenRepo);
    	mockServer = MockRestServiceServer.createServer(restTemplate);
    	accessTokenRepo.save(new AccessToken("9898989898989898989", "loginID", "sessionID", "54321", 40));
    }
    
    @Before
    public void setupLegacyFacade() {
    	userProfileBuilder = Mockito.mock(UserProfileBuilder.class);
    	when(userProfileBuilder.fromChannel(anyInt())).thenReturn(userProfileBuilder);
    	when(userProfileBuilder.withServiceCode(anyString())).thenReturn(userProfileBuilder);
    	when(userProfileBuilder.withRefernce1(anyString())).thenReturn(userProfileBuilder);
    	
    	legacyFacade = Mockito.mock(LegacyFacade.class);
    	when(legacyFacade.userProfile(anyString(), anyString())).thenReturn(userProfileBuilder);
    	service.setLegacyFacade(legacyFacade);
    }
    
    @Test
    public void getMobileAcitivityList() {
    	String accessToken = "9898989898989898989";
    	mockServer.expect(
            	requestTo("http://127.0.0.1:8787/core-report-web/transaction/history/54321")
            ).andExpect(method(HttpMethod.GET)
            ).andRespond(
            	withSuccess(new ClassPathResource("json/stub_all_activities.json"), MediaType.APPLICATION_JSON)
            );
    	
    	List<Activity> activityList = service.getActivities(accessToken);
    	
    	mockServer.verify();
    	assertNotNull(activityList);
    	assertEquals(2, activityList.size());
    }

    @Test
    public void getMobileActivityDetailSuccess(){
    	String accessToken = "9898989898989898989";
    	String truemoneyID = "54321";
    	Long reportID = 9999L;

    	mockServer.expect(
            	requestTo(String.format("http://127.0.0.1:8787/core-report-web/transaction/history/%s/detail/%d", truemoneyID, reportID))
            ).andExpect(method(HttpMethod.GET)
            ).andRespond(
            	withSuccess(new ClassPathResource("json/stub_specific_activities.json"), MediaType.APPLICATION_JSON)
            );
    	
        ActivityDetail activity = service.getActivityDetail(reportID, accessToken);

        mockServer.verify();
        assertNotNull(activity);

        assertEquals("Truemove H+", activity.getAction());
        assertEquals(new BigDecimal(2000), activity.getAmount());
        assertEquals("billpay", activity.getType());
        assertEquals("085-382-8482", activity.getRef1());
    }
    
    @Test
    public void getBuyEPinActivityDetail(){
    	String accessToken = "9898989898989898989";
    	String truemoneyID = "54321";
    	Long reportID = 9910L;

    	when(userProfileBuilder.isFavoritable()).thenReturn(Boolean.TRUE);
    	when(userProfileBuilder.isFavorited()).thenReturn(Boolean.TRUE);
    	
    	mockServer.expect(
            	requestTo(String.format("http://127.0.0.1:8787/core-report-web/transaction/history/%s/detail/%d", truemoneyID, reportID))
            ).andExpect(method(HttpMethod.GET)
            ).andRespond(
            	withSuccess(new ClassPathResource("json/stub_buyepin_activity.json"), MediaType.APPLICATION_JSON)
            );
    	
        ActivityDetail activity = service.getActivityDetail(reportID, accessToken);

        mockServer.verify();
        assertNotNull(activity);

        assertEquals("buy_cashcard", activity.getType());
        assertEquals("ecash", activity.getAction());
        assertEquals(new BigDecimal(100), activity.getAmount());
        assertEquals("0897665655", activity.getRef1());
        assertEquals("123456789012345678", activity.getRef2());
        assertEquals("12345678901234", activity.getAdditionalData());
        assertEquals(Boolean.TRUE, activity.isFavoritable());
        assertEquals(Boolean.TRUE, activity.isFavorited());
    }
    @Test
    public void badAccessTokenError() {
        try {
            service.getActivities("bad access token");
            fail("invalid access token");
        } catch (ResourceNotFoundException ex) {

        }
    }
    
    @Test
    public void testValidateRequest() {
    	UserProfileBuilder builder1 = Mockito.mock(UserProfileBuilder.class);
    	when(builder1.fromChannel(anyInt())).thenReturn(builder1);
    	when(builder1.withServiceCode(anyString())).thenReturn(builder1);
    	when(builder1.withRefernce1(anyString())).thenReturn(builder1);
    	when(builder1.isFavoritable()).thenReturn(Boolean.TRUE);
    	when(builder1.isFavorited()).thenReturn(Boolean.TRUE);
    	
    	when(userProfileBuilder.withServiceCode(eq("ecash"))).thenReturn(builder1);
    	String accessToken = "9898989898989898989";
    	String truemoneyID = "54321";
    	Long reportID = 9910L;

    	
    	mockServer.expect(
            	requestTo(String.format("http://127.0.0.1:8787/core-report-web/transaction/history/%s/detail/%d", truemoneyID, reportID))
            ).andExpect(method(HttpMethod.GET)
            ).andRespond(
            	withSuccess(new ClassPathResource("json/stub_buyepin_activity.json"), MediaType.APPLICATION_JSON)
            );
    	
        ActivityDetail activity = service.getActivityDetail(reportID, accessToken);

        mockServer.verify();
        assertNotNull(activity);
        
        assertEquals("buy_cashcard", activity.getType());
        assertEquals("ecash", activity.getAction());
        assertEquals(new BigDecimal(100), activity.getAmount());
        assertEquals("0897665655", activity.getRef1());
        assertEquals("123456789012345678", activity.getRef2());
        assertEquals("12345678901234", activity.getAdditionalData());
        assertEquals(Boolean.TRUE, activity.isFavoritable());
        assertEquals(Boolean.TRUE, activity.isFavorited());
    	
    }
    

}
