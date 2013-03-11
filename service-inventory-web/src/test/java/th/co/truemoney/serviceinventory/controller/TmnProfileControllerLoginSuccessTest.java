package th.co.truemoney.serviceinventory.controller;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import th.co.truemoney.serviceinventory.common.domain.ServiceRequest;
import th.co.truemoney.serviceinventory.common.domain.ServiceResponse;
import th.co.truemoney.serviceinventory.config.TestServiceConfig;
import th.co.truemoney.serviceinventory.config.TestWebConfig;
import th.co.truemoney.serviceinventory.ewallet.TmnProfileService;
import th.co.truemoney.serviceinventory.ewallet.domain.Login;
import th.co.truemoney.serviceinventory.ewallet.domain.TmnProfile;
import th.co.truemoney.serviceinventory.exception.ServiceInventoryException;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(classes = { TestWebConfig.class, TestServiceConfig.class })
public class TmnProfileControllerLoginSuccessTest {

	@Autowired
	private WebApplicationContext wac;

	@Autowired
	private TmnProfileService tmnProfileServiceMock;

	@Test
	public void shouldLoginSuccess() throws Exception {	
		
		MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();	
		
		ObjectMapper mapper = new ObjectMapper();
		
		Login login = new Login();
		login.setUsername("mali@hotmail.com");
		login.setPassword("0000");
		
		ServiceRequest<Login> serviceRequest = new ServiceRequest<Login>();
		serviceRequest.setRequestTransactionID(Long.toString(System.currentTimeMillis()));
		serviceRequest.setBody(login);
		
		TmnProfile tmnProfile = new TmnProfile("SjdfgkIDF", "tmnid0001");
		tmnProfile.setFullname("Mali Colt");
		tmnProfile.setEwalletBalance("30000.00");

		ServiceResponse<TmnProfile> serviceResponse = new ServiceResponse<TmnProfile>(
				ServiceInventoryException.NAMESPACE, 
				ServiceInventoryException.Code.SUCCESS, 
				"Success");
		serviceResponse.setBody(tmnProfile);
		
		this.tmnProfileServiceMock = wac.getBean(TmnProfileService.class);	
				
		reset(this.tmnProfileServiceMock);
		
		when(this.tmnProfileServiceMock.login(any(Login.class))).thenReturn(serviceResponse);
		
		mockMvc.perform(post("/login")
			.contentType(MediaType.APPLICATION_JSON)
			.content(mapper.writeValueAsBytes(serviceRequest)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.responseCode").value("0"))
			.andExpect(jsonPath("$.responseDesc").value("Success"))
			.andExpect(jsonPath("$.responseNamespace").value("TMN-SERVICE-INVENTORY"))
			.andExpect(jsonPath("$.body.sessionID").value("SjdfgkIDF"))
			.andExpect(jsonPath("$.body.truemoneyID").value("tmnid0001"))
			.andExpect(jsonPath("$.body.fullname").value("Mali Colt"))
			.andExpect(jsonPath("$.body.ewalletBalance").value("30000.00"))
			.andDo(print());		
		
	}
}