package th.co.truemoney.serviceinventory.ewallet.client;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.math.BigDecimal;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.client.RestTemplate;

import th.co.truemoney.serviceinventory.bill.BillPaymentService;
import th.co.truemoney.serviceinventory.bill.domain.Bill;
import th.co.truemoney.serviceinventory.ewallet.client.config.ServiceInventoryClientConfigTest;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { ServiceInventoryClientConfigTest.class })
@ActiveProfiles(profiles="dev")
public class TmnBillPaymentServiceClientCacheTest {
	
	@Autowired
	BillPaymentService billPaymentServiceClient;
	
	@Autowired
	RestTemplate restTemplate;
	
	@Test
	public void returnCacheValue() {
		billPaymentServiceClient.retrieveBillInformationWithKeyin("tmvh", "tokenID-0");
		billPaymentServiceClient.retrieveBillInformationWithKeyin("tmvh", "tokenID-1");
		billPaymentServiceClient.retrieveBillInformationWithKeyin("tmvh", "tokenID-2");
		
		verify(
				restTemplate, times(1)
		).exchange(
				anyString(), 
				eq(HttpMethod.GET), 
				any(HttpEntity.class), 
				eq(Bill.class),
				anyString(),
				anyString());
	}
	
	@Test
	public void returnNonCacheValue() {
		billPaymentServiceClient.retrieveBillInformationWithKeyin("tmvh", "tokenID-0");
		billPaymentServiceClient.retrieveBillInformationWithKeyin("trmv", "tokenID-0");
		billPaymentServiceClient.retrieveBillInformationWithKeyin("tr-c", "tokenID-0");
		
		verify(
				restTemplate, times(3)
		).exchange(
				anyString(), 
				eq(HttpMethod.GET), 
				any(HttpEntity.class), 
				eq(Bill.class),
				anyString(),
				anyString());
	}
}
