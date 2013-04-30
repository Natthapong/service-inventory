package th.co.truemoney.serviceinventory.service;

import static org.junit.Assert.*;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.*;

import java.math.BigDecimal;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

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

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { ServiceInventoryConfig.class, MemRepositoriesConfig.class, LocalEnvironmentConfig.class })
@ActiveProfiles(profiles={"local", "mem"})
public class ActivityServiceImplTest {
	
	@Autowired
	ActivityServiceImpl service;
	
	@Test
	public void correctURLGetDetailCalled(){
		
		RestTemplate restTemplate = new RestTemplate();
		service.setRestTemplate(restTemplate);
		
		AccessTokenRepository accessTokenRepo = new AccessTokenMemoryRepository();
		accessTokenRepo.save(new AccessToken("accessToken", "sessionID", "54321", 40));
		
		service.setAccessTokenRepository(accessTokenRepo);
		
		MockRestServiceServer mockServer = MockRestServiceServer.createServer(restTemplate);

		mockServer.expect(requestTo("http://127.0.0.1:8585/core-report-web/transaction/history/54321/detail/9999"))
	 	 .andExpect(method(HttpMethod.GET))
	     .andRespond(withSuccess(new ClassPathResource("json/stub_specific_activities.json"), MediaType.APPLICATION_JSON));
	 
		ActivityDetail activity = service.getActivityDetail(9999L, "accessToken");
	 
		mockServer.verify();
		assertNotNull(activity);
		
		assertEquals("Truemove H+", activity.getAction());
		assertEquals(new BigDecimal(2000), activity.getAmount());
		assertEquals("billpay", activity.getType());
		assertEquals("085-382-8482", activity.getRef1());
		
	}
	
	@Test
	public void correctURLGetCalled() {
		
		RestTemplate restTemplate = new RestTemplate();
		service.setRestTemplate(restTemplate);
		
		AccessTokenRepository accessTokenRepo = new AccessTokenMemoryRepository();
		accessTokenRepo.save(new AccessToken("accessToken", "sessionID", "54321", 40));
		
		service.setAccessTokenRepository(accessTokenRepo);
		
		MockRestServiceServer mockServer = MockRestServiceServer.createServer(restTemplate);

		mockServer.expect(requestTo("http://127.0.0.1:8585/core-report-web/transaction/history/54321"))
	 	 .andExpect(method(HttpMethod.GET))
	     .andRespond(withSuccess(new ClassPathResource("json/stub_all_activities.json"), MediaType.APPLICATION_JSON));
	 
		List<Activity> activities = service.getActivities("accessToken");
	 
		mockServer.verify();
		assertNotNull(activities);
			
		Activity activity = activities.get(0);
		assertNotNull(activity);
		assertEquals("Truemove H+", activity.getAction());
		assertEquals(new BigDecimal(2000), activity.getAmount());
		assertEquals("billpay", activity.getType());
		assertEquals("085-382-8482", activity.getRef1());
		assertNotNull(activity.getTransactionDate());
	}
	
	@Test
	public void badAccessToken() {
		
		RestTemplate restTemplate = new RestTemplate();
		service.setRestTemplate(restTemplate);
		
		AccessTokenRepository accessTokenRepo = new AccessTokenMemoryRepository();
		accessTokenRepo.save(new AccessToken("accessToken", "sessionID", "54321", 40));
		
		service.setAccessTokenRepository(accessTokenRepo);
		try {
			service.getActivities("bad access token");
			fail();
		} catch (ResourceNotFoundException ex) {
			
		}
	}
	
}
