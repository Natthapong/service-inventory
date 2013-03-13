package th.co.truemoney.serviceinventory.config;

import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import th.co.truemoney.serviceinventory.domain.DevConfigBean;
import th.co.truemoney.serviceinventory.service.LoginService;
import th.co.truemoney.serviceinventory.service.impl.LoginServiceImpl;


@Configuration
public class TestServiceConfig {
	
	@Bean 
	public DevConfigBean getDevConfigBean(){
		return new DevConfigBean();
	}
	
	@Bean
	public LoginService getVerifyService() {
		return Mockito.mock(LoginServiceImpl.class);
	}
	
	@Bean
	public RestTemplate getRestTemplate(){
		return new RestTemplate();
	}
}