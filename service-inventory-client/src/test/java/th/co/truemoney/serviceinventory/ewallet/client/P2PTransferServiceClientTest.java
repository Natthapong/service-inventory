package th.co.truemoney.serviceinventory.ewallet.client;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;

import java.math.BigDecimal;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import th.co.truemoney.serviceinventory.ewallet.client.config.ServiceInventoryClientConfig;
import th.co.truemoney.serviceinventory.ewallet.domain.OTP;
import th.co.truemoney.serviceinventory.ewallet.domain.P2PDraftRequest;
import th.co.truemoney.serviceinventory.ewallet.domain.P2PDraftTransaction;
import th.co.truemoney.serviceinventory.exception.ServiceInventoryException;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { ServiceInventoryClientConfig.class })
@ActiveProfiles(profiles = "local")
@Category(IntegrationTest.class)
public class P2PTransferServiceClientTest {

	@Autowired
	P2PTransferServiceClient p2pTransferServiceClient;
	
	@Test 
	public void createDraftTransactionSuccess(){
		P2PDraftTransaction p2pDraftTransaction = p2pTransferServiceClient.createDraftTransaction(new P2PDraftRequest("0868185055",new BigDecimal(2000)), "12345");
		assertEquals("fullName", p2pDraftTransaction.getFullname());
		assertNotNull(p2pDraftTransaction);	
	}
	
	@Test 
	public void getDraftTransactionDetailSuccess() {
		P2PDraftTransaction p2pDraftTransaction = p2pTransferServiceClient.getDraftTransactionDetails("1", "12345");
		assertNotNull(p2pDraftTransaction);		
	}
	
	@Test 
	public void getDraftTransactionDetailFail() {
		try {
			p2pTransferServiceClient.getDraftTransactionDetails("3", "12355");
		} catch (ServiceInventoryException serviceInventoryException) {
			assertNotSame("0", serviceInventoryException.getErrorCode());
		}			
	}
	
	@Test @Ignore
	public void sendOTPSuccess() {
		OTP otp = p2pTransferServiceClient.sendOTP("1", "12345");
		assertNotNull(otp);	
	}
	
	@Test @Ignore
	public void sendOTPFail() {
		try {
			p2pTransferServiceClient.sendOTP("3", "12355");
		} catch (ServiceInventoryException serviceInventoryException) {
			
		}
	}
}
