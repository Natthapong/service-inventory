package th.co.truemoney.serviceinventory.ewallet.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.web.client.RestTemplate;

import th.co.truemoney.serviceinventory.ewallet.client.config.EnvironmentConfig;
import th.co.truemoney.serviceinventory.exception.ServiceInventoryException;

public class TmnTopupServiceClient {
	
	@Autowired
	RestTemplate restTemplate;

	@Autowired
	private EnvironmentConfig environmentConfig;

	@Autowired
	private HttpHeaders headers;

	public void verify(Integer channelId,String accesstoken, String checksum) throws ServiceInventoryException {
		
	}
	
	public void order(Integer channelId,String accesstoken, String checksum) throws ServiceInventoryException {

	}
	
}
