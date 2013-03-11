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
import th.co.truemoney.serviceinventory.config.TestServiceConfig;
import th.co.truemoney.serviceinventory.config.TestWebConfig;
import th.co.truemoney.serviceinventory.ewallet.TmnProfileService;
import th.co.truemoney.serviceinventory.ewallet.domain.Login;
import th.co.truemoney.serviceinventory.exception.SignonServiceException;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(classes = { TestWebConfig.class, TestServiceConfig.class })
public class TmnProfileControllerLoginFailTest {

	@Autowired
	private WebApplicationContext wac;

	@Autowired
	private TmnProfileService tmnProfileServiceMock;
	
	@Test
	public void shouldLoginFail() throws Exception {
		
		MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();	
		
		ObjectMapper mapper = new ObjectMapper();
		
		Login login = new Login();
		login.setUsername("mali@hotmail.com");
		login.setPassword("0000");
		
		ServiceRequest<Login> serviceRequest = new ServiceRequest<Login>();
		serviceRequest.setRequestTransactionID(Long.toString(System.currentTimeMillis()));
		serviceRequest.setBody(login);

		this.tmnProfileServiceMock = wac.getBean(TmnProfileService.class);
		
		reset(this.tmnProfileServiceMock);
		
		when(this.tmnProfileServiceMock.login(any(Login.class))).thenThrow(
				new SignonServiceException(
						"1", 
						"error description",
						"error namespace"));
		
		mockMvc.perform(post("/login")
				.contentType(MediaType.APPLICATION_JSON)
				.content(mapper.writeValueAsBytes(serviceRequest)))
				.andExpect(status().isUnauthorized())
				.andExpect(jsonPath("$.responseCode").value("1"))
				.andExpect(jsonPath("$.responseDesc").value("error description"))
				.andExpect(jsonPath("$.responseNamespace").value("error namespace"))
				.andDo(print());	
		
	}
		
}