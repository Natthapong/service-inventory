package th.co.truemoney.serviceinventory.controller;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import th.co.truemoney.serviceinventory.config.MemRepositoriesConfig;
import th.co.truemoney.serviceinventory.config.TestRedisConfig;
import th.co.truemoney.serviceinventory.config.TestServiceInventoryConfig;
import th.co.truemoney.serviceinventory.config.WebConfig;
import th.co.truemoney.serviceinventory.ewallet.domain.TopUpOrder;
import th.co.truemoney.serviceinventory.ewallet.impl.ExtendAccessTokenAsynService;
import th.co.truemoney.serviceinventory.exception.ServiceInventoryException;
import th.co.truemoney.serviceinventory.exception.ServiceInventoryWebException;
import th.co.truemoney.serviceinventory.exception.ServiceInventoryWebException.Code;
import th.co.truemoney.serviceinventory.firsthop.config.SmsConfig;
import th.co.truemoney.serviceinventory.transfer.P2PTransferService;
import th.co.truemoney.serviceinventory.transfer.domain.P2PTransactionConfirmationInfo;
import th.co.truemoney.serviceinventory.transfer.domain.P2PTransferDraft;
import th.co.truemoney.serviceinventory.transfer.domain.P2PTransferTransaction;

import com.fasterxml.jackson.databind.ObjectMapper;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(classes = { WebConfig.class, MemRepositoriesConfig.class, TestServiceInventoryConfig.class, TestRedisConfig.class, SmsConfig.class })
@ActiveProfiles(profiles={"local", "mem"})
public class P2PTransferControllerTest {

	private MockMvc mockMvc;
	
	private ObjectMapper mapper;

	@Autowired
	private WebApplicationContext wac;

	@Autowired
	private P2PTransferService p2pTransferServiceMock;
	
	@Autowired
    private ExtendAccessTokenAsynService extendAccessTokenAsynServiceMock;

	@Before
	public void setup() {
		this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
		this.p2pTransferServiceMock = wac.getBean(P2PTransferService.class);
		this.extendAccessTokenAsynServiceMock = wac.getBean(ExtendAccessTokenAsynService.class);
		this.mapper = new ObjectMapper();
	}

	@After
	public void tierDown() {
		reset(this.p2pTransferServiceMock);
		reset(this.extendAccessTokenAsynServiceMock);
	}
	
	@Test
	public void createAndVerifyTransferSuccess() throws Exception {
		P2PTransferDraft p2pTransferDraft = new P2PTransferDraft("08xxxxxxxx", BigDecimal.TEN);
		p2pTransferDraft.setID("ID");
		
		//given
		when(p2pTransferServiceMock.createAndVerifyTransferDraft(anyString(), any(BigDecimal.class), anyString()))
			.thenReturn(p2pTransferDraft);
		when(extendAccessTokenAsynServiceMock.setExpire(anyString())).thenReturn(new AsyncResult<Boolean>(true));
		
		//perform
		this.mockMvc.perform(post("/transfer/draft?accessTokenID={accessTokenID}", "TokenID")
			.contentType(MediaType.APPLICATION_JSON)
			.content(mapper.writeValueAsBytes(p2pTransferDraft)))
			.andExpect(status().isOk());
	} 
	
	@Test
	public void createAndVerifyTransferFail() throws Exception {
		P2PTransferDraft p2pTransferDraft = new P2PTransferDraft("08xxxxxxxx", BigDecimal.TEN);
		p2pTransferDraft.setID("ID");
		
		//given
		when(p2pTransferServiceMock.createAndVerifyTransferDraft(anyString(), any(BigDecimal.class), anyString()))
			.thenThrow(new ServiceInventoryException(400,"Error Code","Error Description", "Error Namespace"));	
		when(extendAccessTokenAsynServiceMock.setExpire(anyString())).thenReturn(new AsyncResult<Boolean>(true));
		
		//perform
		this.mockMvc.perform(post("/transfer/draft?accessTokenID={accessTokenID}", "TokenID")
			.contentType(MediaType.APPLICATION_JSON)
			.content(mapper.writeValueAsBytes(p2pTransferDraft)))
			.andExpect(status().isBadRequest());
	}
	
	@Test
	public void setPersonalMessageSuccess() throws Exception {
		P2PTransferDraft p2pTransferDraft = new P2PTransferDraft("08xxxxxxxx", BigDecimal.TEN);
		p2pTransferDraft.setID("ID");
		
		//given
		doNothing().when(p2pTransferServiceMock).setPersonalMessage(anyString(), anyString(), anyString());
		when(extendAccessTokenAsynServiceMock.setExpire(anyString())).thenReturn(new AsyncResult<Boolean>(true));
		
		//perform
		this.mockMvc.perform(put("/transfer/draft/{draftTransactionID}/update?personalMessage={personalMessage}&accessTokenID={accessTokenID}", "ID", "Message", "TokenID")
			.contentType(MediaType.APPLICATION_JSON)
			.content(mapper.writeValueAsBytes(p2pTransferDraft)))
			.andExpect(status().isOk());
	} 
	
	@Test
	public void getTransferDraftSuccess() throws Exception {
		P2PTransferDraft p2pTransferDraft = new P2PTransferDraft("08xxxxxxxx", BigDecimal.TEN);
		p2pTransferDraft.setID("ID");
		
		//given
		when(p2pTransferServiceMock.getTransferDraftDetails(anyString(), anyString()))
			.thenReturn(p2pTransferDraft);
		when(extendAccessTokenAsynServiceMock.setExpire(anyString())).thenReturn(new AsyncResult<Boolean>(true));
		
		//perform
		this.mockMvc.perform(get("/transfer/draft/{draftTransactionID}?accessTokenID={accessTokenID}", "ID", "TokenID")
			.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk());
	} 
	
	@Test
	public void getTransferDraftFail() throws Exception {
		//given
		when(p2pTransferServiceMock.getTransferDraftDetails(anyString(), anyString()))
			.thenThrow(new ServiceInventoryException(400,"Error Code","Error Description", "Error Namespace"));	
		when(extendAccessTokenAsynServiceMock.setExpire(anyString())).thenReturn(new AsyncResult<Boolean>(true));
		
		//perform
		this.mockMvc.perform(get("/transfer/draft/{draftTransactionID}?accessTokenID={accessTokenID}", "ID", "TokenID")
			.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isBadRequest());
	}
	
	@Test
	public void performTransferSuccess() throws Exception {
		//given
		when(p2pTransferServiceMock.performTransfer(anyString(), anyString())).thenReturn(TopUpOrder.Status.SUCCESS);		
		when(extendAccessTokenAsynServiceMock.setExpire(anyString())).thenReturn(new AsyncResult<Boolean>(true));

		//perform		
		this.mockMvc.perform(put("/transfer/transaction/{transactionID}?accessTokenID={accessTokenID}","TransactionID","TokenID")
			.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk());
	}
	
	@Test
	public void performTransferFail() throws Exception {
		//given
		when(p2pTransferServiceMock.performTransfer(anyString(), anyString()))
			.thenThrow(new ServiceInventoryWebException(Code.CONFIRM_FAILED, "confirmation processing fail."));		
		when(extendAccessTokenAsynServiceMock.setExpire(anyString())).thenReturn(new AsyncResult<Boolean>(true));

		//perform		
		this.mockMvc.perform(put("/transfer/transaction/{transactionID}?accessTokenID={accessTokenID}","TransactionID","TokenID")
			.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.errorCode").value(Code.CONFIRM_FAILED))
			.andExpect(jsonPath("$.errorDescription").value("confirmation processing fail."))
			.andExpect(jsonPath("$.errorNamespace").value("TMN-SERVICE-INVENTORY"));
	}

	@Test
	public void getTransferStatusSuccess() throws Exception {
		//given
		when(p2pTransferServiceMock.getTransferringStatus(anyString(), anyString())).thenReturn(P2PTransferTransaction.Status.SUCCESS);		
		when(extendAccessTokenAsynServiceMock.setExpire(anyString())).thenReturn(new AsyncResult<Boolean>(true));

		//perform		
		this.mockMvc.perform(get("/transfer/transaction/{transactionID}/status?accessTokenID={accessTokenID}","TransactionID","TokenID")
			.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk());
	}
	
	@Test
	public void getTransferStatusFail() throws Exception {
		//given
		when(p2pTransferServiceMock.getTransferringStatus(anyString(), anyString())).thenReturn(P2PTransferTransaction.Status.FAILED);		
		when(extendAccessTokenAsynServiceMock.setExpire(anyString())).thenReturn(new AsyncResult<Boolean>(true));

		//perform		
		this.mockMvc.perform(get("/transfer/transaction/{transactionID}/status?accessTokenID={accessTokenID}","TransactionID","TokenID")
			.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk());
	}
	
	@Test
	public void getTransferResultSuccess() throws Exception {
		P2PTransactionConfirmationInfo confirmationInfo = new P2PTransactionConfirmationInfo();		
		P2PTransferTransaction p2pTransferTransaction = new P2PTransferTransaction();
		p2pTransferTransaction.setConfirmationInfo(confirmationInfo);

		//given
		when(p2pTransferServiceMock.getTransactionResult(anyString(), anyString())).thenReturn(p2pTransferTransaction);		
		when(extendAccessTokenAsynServiceMock.setExpire(anyString())).thenReturn(new AsyncResult<Boolean>(true));

		//perform		
		this.mockMvc.perform(get("/transfer/transaction/{transactionID}?accessTokenID={accessTokenID}","TransactionID","TokenID")
			.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$..transactionID").exists())
			.andExpect(jsonPath("$..transactionDate").exists());
	}
	
	@Test
	public void getTransferResultFail() throws Exception {
		//given
		when(p2pTransferServiceMock.getTransactionResult(anyString(), anyString()))
			.thenThrow(new ServiceInventoryException(400,"Error Code","Error Description", "Error Namespace"));	
		when(extendAccessTokenAsynServiceMock.setExpire(anyString())).thenReturn(new AsyncResult<Boolean>(true));

		//perform		
		this.mockMvc.perform(get("/transfer/transaction/{transactionID}?accessTokenID={accessTokenID}","TransactionID","TokenID")		
			.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.errorCode").value("Error Code"))
			.andExpect(jsonPath("$.errorDescription").value("Error Description"))
			.andExpect(jsonPath("$.errorNamespace").value("Error Namespace"));
	}
}
