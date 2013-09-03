package th.co.truemoney.serviceinventory.controller;

import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import th.co.truemoney.serviceinventory.config.MemRepositoriesConfig;
import th.co.truemoney.serviceinventory.config.TestServiceInventoryConfig;
import th.co.truemoney.serviceinventory.config.WebConfig;
import th.co.truemoney.serviceinventory.ewallet.ActivityService;
import th.co.truemoney.serviceinventory.ewallet.domain.Activity;
import th.co.truemoney.serviceinventory.ewallet.domain.ActivityDetail;
import th.co.truemoney.serviceinventory.exception.ServiceInventoryWebException;
import th.co.truemoney.serviceinventory.exception.ServiceInventoryWebException.Code;
import th.co.truemoney.serviceinventory.firsthop.config.SmsConfig;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(classes = { WebConfig.class, MemRepositoriesConfig.class, TestServiceInventoryConfig.class, SmsConfig.class })
@ActiveProfiles(profiles={"local", "mem"})
public class TransactionActivityControllerTest {

	private MockMvc mockMvc;

	@Autowired
	private WebApplicationContext wac;
	
	@Autowired
	private ActivityService activityServiceMock;
	
	@Before
	public void setup() {
		this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
		this.activityServiceMock = wac.getBean(ActivityService.class);
	}

	@After
	public void tierDown() {
		reset(this.activityServiceMock);
	}
	
	@Test
	public void shouldGetActivitySuccess() throws Exception {
		Activity activity = new Activity();
		activity.setReportID(1000l);
		List<Activity> activities = new ArrayList<Activity>();
		activities.add(activity);
		
		when(this.activityServiceMock.getActivities(anyString())).thenReturn(activities);
		
		this.mockMvc.perform(get("/ewallet/activities/{accessTokenID}", "12345")
			.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())	
			.andExpect(jsonPath("$.[0].reportID").value(1000));
	}
	
	@Test
	public void shouldGetActivityFail() throws Exception {
		Activity activity = new Activity();
		activity.setReportID(1000l);
		List<Activity> activities = new ArrayList<Activity>();
		activities.add(activity);
		
		when(this.activityServiceMock.getActivities(anyString()))
			.thenThrow(new ServiceInventoryWebException(Code.GET_ACTIVITY_FAILED, "Error Description"));
		
		this.mockMvc.perform(get("/ewallet/activities/{accessTokenID}", "12345")
			.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.errorCode").value(Code.GET_ACTIVITY_FAILED))
			.andExpect(jsonPath("$.errorDescription").value("Error Description"))
			.andExpect(jsonPath("$.errorNamespace").value("TMN-SERVICE-INVENTORY"));
	}
	
	@Test
	public void shouldGetActivityDetailSuccess() throws Exception {
		ActivityDetail activity = new ActivityDetail();
		activity.setTransactionID("xxx");
		
		when(this.activityServiceMock.getActivityDetail(anyLong(), anyString())).thenReturn(activity);
		
		this.mockMvc.perform(get("/ewallet/activities/{accessTokenID}/detail/{reportID}", "12345", 1000l)
			.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())	
			.andExpect(jsonPath("$.transactionID").value("xxx"));
	}
	
	@Test
	public void shouldGetActivityDetailFail() throws Exception {
		ActivityDetail activity = new ActivityDetail();
		activity.setTransactionID("xxx");
		
		when(this.activityServiceMock.getActivityDetail(anyLong(), anyString()))
			.thenThrow(new ServiceInventoryWebException(Code.GET_ACTIVITY_DETAIL_FAILED, "Error Description"));
		
		this.mockMvc.perform(get("/ewallet/activities/{accessTokenID}/detail/{reportID}", "12345", 1000l)
			.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.errorCode").value(Code.GET_ACTIVITY_DETAIL_FAILED))
			.andExpect(jsonPath("$.errorDescription").value("Error Description"))
			.andExpect(jsonPath("$.errorNamespace").value("TMN-SERVICE-INVENTORY"));		
	}
	
}
