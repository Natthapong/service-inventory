package th.co.truemoney.serviceinventory.controller;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
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
import th.co.truemoney.serviceinventory.ewallet.impl.ExtendAccessTokenAsynService;
import th.co.truemoney.serviceinventory.exception.ServiceInventoryException;
import th.co.truemoney.serviceinventory.exception.ServiceInventoryWebException;
import th.co.truemoney.serviceinventory.exception.ServiceInventoryWebException.Code;
import th.co.truemoney.serviceinventory.firsthop.config.SmsConfig;
import th.co.truemoney.serviceinventory.topup.TopUpMobileService;
import th.co.truemoney.serviceinventory.topup.domain.TopUpMobile;
import th.co.truemoney.serviceinventory.topup.domain.TopUpMobileConfirmationInfo;
import th.co.truemoney.serviceinventory.topup.domain.TopUpMobileDraft;
import th.co.truemoney.serviceinventory.topup.domain.TopUpMobileTransaction;

import com.fasterxml.jackson.databind.ObjectMapper;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(classes = { WebConfig.class, MemRepositoriesConfig.class, TestServiceInventoryConfig.class, TestRedisConfig.class, SmsConfig.class })
@ActiveProfiles(profiles={"local", "mem"})
public class TopupMobileControllerTest {

	private MockMvc mockMvc;
	
	private ObjectMapper mapper;

	@Autowired
	private WebApplicationContext wac;

	@Autowired
	private TopUpMobileService topUpMobileServiceMock;
	
	@Autowired
    private ExtendAccessTokenAsynService extendAccessTokenAsynServiceMock;
	
	@Before
	public void setup() {
		this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
		this.topUpMobileServiceMock = wac.getBean(TopUpMobileService.class);
		this.extendAccessTokenAsynServiceMock = wac.getBean(ExtendAccessTokenAsynService.class);
		this.mapper = new ObjectMapper();
	}

	@After
	public void tierDown() {
		reset(this.topUpMobileServiceMock);
		reset(this.extendAccessTokenAsynServiceMock);
	}
	
	@Test
	public void verifyAndCreateTopupMobileSuccess() throws Exception {
		TopUpMobile topUpMobile = new TopUpMobile();
		topUpMobile.setMobileNumber("08xxxxxxxx");
		topUpMobile.setAmount(BigDecimal.TEN);

		TopUpMobileDraft topUpMobileDraft = new TopUpMobileDraft();
		topUpMobileDraft.setTopUpMobileInfo(topUpMobile);
		topUpMobileDraft.setID("draftID");
		
		//given
		when(topUpMobileServiceMock.verifyAndCreateTopUpMobileDraft(anyString(), any(BigDecimal.class), anyString()))
			.thenReturn(topUpMobileDraft);		
		when(extendAccessTokenAsynServiceMock.setExpire(anyString())).thenReturn(new AsyncResult<Boolean>(true));

		//perform		
		this.mockMvc.perform(post("/top-up/mobile/draft?accessTokenID={accessTokenID}","TokenID")
				.contentType(MediaType.APPLICATION_JSON)
				.content(mapper.writeValueAsBytes(topUpMobileDraft)))
				.andExpect(status().isOk());
	}
	
	@Test
	public void verifyAndCreateTopupMobileFail() throws Exception {
		//given
		when(topUpMobileServiceMock.verifyAndCreateTopUpMobileDraft(anyString(), any(BigDecimal.class), anyString()))
			.thenThrow(new ServiceInventoryException(400,"Error Code","Error Description", "Error Namespace"));	
		when(extendAccessTokenAsynServiceMock.setExpire(anyString())).thenReturn(new AsyncResult<Boolean>(true));

		TopUpMobile topUpMobile = new TopUpMobile();
		topUpMobile.setMobileNumber("08xxxxxxxx");
		topUpMobile.setAmount(BigDecimal.TEN);

		TopUpMobileDraft topUpMobileDraft = new TopUpMobileDraft();
		topUpMobileDraft.setTopUpMobileInfo(topUpMobile);
		topUpMobileDraft.setID("draftID");		
		//perform		
		this.mockMvc.perform(post("/top-up/mobile/draft?accessTokenID={accessTokenID}","TokenID")
				.contentType(MediaType.APPLICATION_JSON)
				.content(mapper.writeValueAsBytes(topUpMobileDraft)))
				.andExpect(status().isBadRequest());
	}
	
	@Test
	public void getTopUpMobileDraftDetailSuccess() throws Exception {
		TopUpMobile topUpMobile = new TopUpMobile();
		topUpMobile.setMobileNumber("08xxxxxxxx");
		topUpMobile.setAmount(BigDecimal.TEN);

		TopUpMobileDraft topUpMobileDraft = new TopUpMobileDraft();
		topUpMobileDraft.setTopUpMobileInfo(topUpMobile);
		topUpMobileDraft.setID("draftID");
		
		//given
		when(topUpMobileServiceMock.getTopUpMobileDraftDetail(anyString(), anyString())).thenReturn(topUpMobileDraft);		
		when(extendAccessTokenAsynServiceMock.setExpire(anyString())).thenReturn(new AsyncResult<Boolean>(true));

		//perform		
		this.mockMvc.perform(get("/top-up/mobile/draft/{draftTransactionID}?accessTokenID={accessTokenID}","DraftID","TokenID")
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk());
	}
	
	@Test
	public void getTopUpMobileDraftDetailFail() throws Exception {
		//given
		when(topUpMobileServiceMock.getTopUpMobileDraftDetail(anyString(), anyString()))
			.thenThrow(new ServiceInventoryException(400,"Error Code","Error Description", "Error Namespace"));			
		when(extendAccessTokenAsynServiceMock.setExpire(anyString())).thenReturn(new AsyncResult<Boolean>(true));

		//perform		
		this.mockMvc.perform(get("/top-up/mobile/draft/{draftTransactionID}?accessTokenID={accessTokenID}","DraftID","TokenID")
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest());
	}
	
	@Test
	public void performTopupMobileSuccess() throws Exception {
		//given
		when(topUpMobileServiceMock.performTopUpMobile(anyString(), anyString())).thenReturn(TopUpMobileTransaction.Status.SUCCESS);		
		when(extendAccessTokenAsynServiceMock.setExpire(anyString())).thenReturn(new AsyncResult<Boolean>(true));

		//perform		
		this.mockMvc.perform(put("/top-up/mobile/transaction/{transactionID}?accessTokenID={accessTokenID}","TransactionID","TokenID")
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk());
	}
	
	@Test
	public void performTopupMobileFail() throws Exception {
		//given
		when(topUpMobileServiceMock.performTopUpMobile(anyString(), anyString()))
			.thenThrow(new ServiceInventoryWebException(Code.CONFIRM_FAILED, "confirmation processing fail."));		
		when(extendAccessTokenAsynServiceMock.setExpire(anyString())).thenReturn(new AsyncResult<Boolean>(true));

		//perform		
		this.mockMvc.perform(put("/top-up/mobile/transaction/{transactionID}?accessTokenID={accessTokenID}","TransactionID","TokenID")
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.errorCode").value(Code.CONFIRM_FAILED))
				.andExpect(jsonPath("$.errorDescription").value("confirmation processing fail."))
				.andExpect(jsonPath("$.errorNamespace").value("TMN-SERVICE-INVENTORY"));
	}
	
	@Test
	public void getTopupMobileStatusSuccess() throws Exception {
		//given
		when(topUpMobileServiceMock.getTopUpMobileStatus(anyString(), anyString())).thenReturn(TopUpMobileTransaction.Status.SUCCESS);		
		when(extendAccessTokenAsynServiceMock.setExpire(anyString())).thenReturn(new AsyncResult<Boolean>(true));

		//perform		
		this.mockMvc.perform(get("/top-up/mobile/transaction/{transactionID}/status?accessTokenID={accessTokenID}","TransactionID","TokenID")
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk());
	}
	
	@Test
	public void getTopupMobileStatusFail() throws Exception {
		//given
		when(topUpMobileServiceMock.getTopUpMobileStatus(anyString(), anyString())).thenReturn(TopUpMobileTransaction.Status.FAILED);		
		when(extendAccessTokenAsynServiceMock.setExpire(anyString())).thenReturn(new AsyncResult<Boolean>(true));

		//perform		
		this.mockMvc.perform(get("/top-up/mobile/transaction/{transactionID}/status?accessTokenID={accessTokenID}","TransactionID","TokenID")
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk());
	}
	
	@Test
	public void getTopupMobileInfoSuccess() throws Exception {
		TopUpMobileConfirmationInfo confirmationInfo = new TopUpMobileConfirmationInfo();		
		TopUpMobileTransaction topUpMobileTransaction = new TopUpMobileTransaction();
		topUpMobileTransaction.setConfirmationInfo(confirmationInfo);

		//given
		when(topUpMobileServiceMock.getTopUpMobileResult(anyString(), anyString())).thenReturn(topUpMobileTransaction);		
		when(extendAccessTokenAsynServiceMock.setExpire(anyString())).thenReturn(new AsyncResult<Boolean>(true));

		//perform		
		this.mockMvc.perform(get("/top-up/mobile/transaction/{transactionID}?accessTokenID={accessTokenID}","TransactionID","TokenID")
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$..transactionID").exists())
				.andExpect(jsonPath("$..transactionDate").exists());
	}
	
	@Test
	public void getTopupMobileInfoFail() throws Exception {
		//given
		when(topUpMobileServiceMock.getTopUpMobileResult(anyString(), anyString()))
			.thenThrow(new ServiceInventoryException(400,"Error Code","Error Description", "Error Namespace"));				
		when(extendAccessTokenAsynServiceMock.setExpire(anyString())).thenReturn(new AsyncResult<Boolean>(true));

		//perform		
		this.mockMvc.perform(get("/top-up/mobile/transaction/{transactionID}?accessTokenID={accessTokenID}","TransactionID","TokenID")
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.errorCode").value("Error Code"))
				.andExpect(jsonPath("$.errorDescription").value("Error Description"))
				.andExpect(jsonPath("$.errorNamespace").value("Error Namespace"));
	}

}
