package th.co.truemoney.serviceinventory.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.stub;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.allOf;
import static org.junit.Assert.assertThat;

import org.codehaus.jackson.map.ObjectMapper;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.WebApplicationContext;

import th.co.truemoney.serviceinventory.config.TestServiceConfig;
import th.co.truemoney.serviceinventory.config.TestWebConfig;
import th.co.truemoney.serviceinventory.domain.DevConfigBean;
import th.co.truemoney.serviceinventory.domain.ResponseBean;
import th.co.truemoney.serviceinventory.domain.SigninBean;
import th.co.truemoney.serviceinventory.domain.proxy.BaseServiceRequestBean;
import th.co.truemoney.serviceinventory.domain.proxy.BaseServiceResponseBean;
import th.co.truemoney.serviceinventory.domain.proxy.SigninResponseBean;
import th.co.truemoney.serviceinventory.service.impl.LoginServiceImpl;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(classes = { TestWebConfig.class, TestServiceConfig.class })
public class TestLoginService {

	private @Autowired
	LoginService loginService;
	private @Autowired
	RestTemplate restTemplate;
	private @Autowired
	DevConfigBean devConfigBean;

	private MockRestServiceServer mockServer;

	@Before
	public void setUp() {
		mockServer = MockRestServiceServer.createServer(restTemplate);
	}

	@Test
	public void TestLogin() {

		RestTemplate restTemplate = new RestTemplate();

		MockRestServiceServer mockServer = MockRestServiceServer
				.createServer(restTemplate);

		mockServer.expect(requestTo("/login")).andRespond(
				withSuccess("Hello world", MediaType.APPLICATION_JSON));

		try {
			mockServer.verify();
		} catch (AssertionError error) {
			assertTrue(error.getMessage(),
					error.getMessage().contains("0 out of 1 were executed"));
		}

	}

	@Test
	public void TestReturnValueLogin() {

		LoginServiceImpl loginService = new LoginServiceImpl();
		RestTemplate restTemplate = mock(RestTemplate.class);
		
		String accessToken = "12345";

		ResponseEntity<String> responseBean = new ResponseEntity<String>(
				accessToken, HttpStatus.OK);

		when(
				restTemplate.exchange(
						eq(devConfigBean.getPathDevUrl("/login")),
						eq(HttpMethod.POST), any(HttpEntity.class),
						eq(String.class))).thenReturn(
				responseBean);

		loginService.setRestTemplate(restTemplate);

		assertEquals("12345", loginService.login(new SigninBean()));
		
	}
}
