package th.co.truemoney.serviceinventory.service;

import java.util.concurrent.ExecutionException;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;


import th.co.truemoney.serviceinventory.ewallet.domain.AccessToken;
import th.co.truemoney.serviceinventory.ewallet.domain.OTP;
import th.co.truemoney.serviceinventory.ewallet.domain.TopUpOrder;
import th.co.truemoney.serviceinventory.ewallet.domain.TopUpStatus;
import th.co.truemoney.serviceinventory.ewallet.impl.AsyncService;
import th.co.truemoney.serviceinventory.ewallet.impl.TopUpServiceImpl;
import th.co.truemoney.serviceinventory.ewallet.repositories.AccessTokenRepository;
import th.co.truemoney.serviceinventory.ewallet.repositories.OrderRepository;
import th.co.truemoney.serviceinventory.util.EncryptUtil;

public class TopUpServiceImplTest {
	
	private String localChecksum;
	
	AsyncService asyncService;
	
	TopUpServiceImpl topUpService;
	
	@Before
	public void setup() {
		topUpService = new TopUpServiceImpl();
	}
	
	@Test 
	public void confirmPlaceOrder() throws InterruptedException, ExecutionException {
		AccessToken accessToken = new AccessToken();
		TopUpOrder topUpOrder = new TopUpOrder();
		topUpOrder.setID("1");
		OTP otp = new OTP();
		localChecksum = EncryptUtil.buildHmacSignature("accessToken", topUpOrder.toString()+"accessToken");
		otp.setChecksum(localChecksum);
		
		asyncService = mock(AsyncService.class);
		AccessTokenRepository accessTokenRepo = mock(AccessTokenRepository.class);
		OrderRepository orderRepo = mock(OrderRepository.class);
		
		when(orderRepo.getTopUpOrder(anyString())).thenReturn(topUpOrder);
		when(accessTokenRepo.getAccessToken(anyString())).thenReturn(accessToken);
		
		topUpService.setAsyncService(asyncService);
		topUpService.setOrderRepo(orderRepo);
		topUpService.setAccessTokenRepo(accessTokenRepo);
		
				
		TopUpOrder order = topUpService.confirmPlaceOrder(topUpOrder.getID(), otp, "accessToken");
		
		assertEquals(TopUpStatus.PROCESSING, order.getStatus());
		verify(asyncService).topUpUtibaEwallet(topUpOrder, accessToken);
		verify(orderRepo).saveTopUpOrder(topUpOrder);
	}
}
