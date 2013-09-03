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
import th.co.truemoney.serviceinventory.config.TestServiceInventoryConfig;
import th.co.truemoney.serviceinventory.config.WebConfig;
import th.co.truemoney.serviceinventory.ewallet.TopUpService;
import th.co.truemoney.serviceinventory.ewallet.domain.DirectDebit;
import th.co.truemoney.serviceinventory.ewallet.domain.TopUpConfirmationInfo;
import th.co.truemoney.serviceinventory.ewallet.domain.TopUpOrder;
import th.co.truemoney.serviceinventory.ewallet.domain.TopUpQuote;
import th.co.truemoney.serviceinventory.ewallet.impl.ExtendAccessTokenAsynService;
import th.co.truemoney.serviceinventory.exception.ServiceInventoryException;
import th.co.truemoney.serviceinventory.exception.ServiceInventoryWebException;
import th.co.truemoney.serviceinventory.exception.ServiceInventoryWebException.Code;
import th.co.truemoney.serviceinventory.firsthop.config.SmsConfig;

import com.fasterxml.jackson.databind.ObjectMapper;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(classes = { WebConfig.class, MemRepositoriesConfig.class, TestServiceInventoryConfig.class, SmsConfig.class })
@ActiveProfiles(profiles={"local", "mem"})
public class TopupEwalletControllerTest {

	private MockMvc mockMvc;
	
	private ObjectMapper mapper;

	@Autowired
	private WebApplicationContext wac;

	@Autowired
	private TopUpService topUpEwalletServiceMock;
	
	@Autowired
    private ExtendAccessTokenAsynService extendAccessTokenAsynServiceMock;
	
	@Before
	public void setup() {
		this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
		this.topUpEwalletServiceMock = wac.getBean(TopUpService.class);
		this.extendAccessTokenAsynServiceMock = wac.getBean(ExtendAccessTokenAsynService.class);
		this.mapper = new ObjectMapper();
	}

	@After
	public void tierDown() {
		reset(this.topUpEwalletServiceMock);
		reset(this.extendAccessTokenAsynServiceMock);
	}
	
	@Test
	public void createAndVerifyTopupSuccess() throws Exception {
		DirectDebit sourceOfFund = new DirectDebit("SofID", "SofType");
		TopUpQuote topUpQuote = new TopUpQuote("", sourceOfFund, "accessTokenID", BigDecimal.TEN, BigDecimal.ONE);
		topUpQuote.setID("ID");
		
		//given
		when(topUpEwalletServiceMock.createAndVerifyTopUpQuote(anyString(), any(BigDecimal.class), anyString()))
			.thenReturn(topUpQuote);
		when(extendAccessTokenAsynServiceMock.setExpire(anyString())).thenReturn(new AsyncResult<Boolean>(true));
		
		//perform
		this.mockMvc.perform(post("/directdebit/{sourceOfFundID}/quote?accessTokenID={accessTokenID}", "SofID", "TokenID")
			.contentType(MediaType.APPLICATION_JSON)
			.content(mapper.writeValueAsBytes(topUpQuote)))
			.andExpect(status().isOk());
	}
	
	@Test
	public void createAndVerifyTopupFail() throws Exception {
		DirectDebit sourceOfFund = new DirectDebit("SofID", "SofType");
		TopUpQuote topUpQuote = new TopUpQuote("", sourceOfFund, "accessTokenID", BigDecimal.TEN, BigDecimal.ONE);
		topUpQuote.setID("ID");
		
		//given
		when(topUpEwalletServiceMock.createAndVerifyTopUpQuote(anyString(), any(BigDecimal.class), anyString()))
			.thenThrow(new ServiceInventoryException(400,"Error Code","Error Description", "Error Namespace"));	
		when(extendAccessTokenAsynServiceMock.setExpire(anyString())).thenReturn(new AsyncResult<Boolean>(true));
		
		//perform
		this.mockMvc.perform(post("/directdebit/{sourceOfFundID}/quote?accessTokenID={accessTokenID}", "SofID", "TokenID")
			.contentType(MediaType.APPLICATION_JSON)
			.content(mapper.writeValueAsBytes(topUpQuote)))
			.andExpect(status().isBadRequest());
	}
	
	@Test
	public void getTopupDetailSuccess() throws Exception {
		DirectDebit sourceOfFund = new DirectDebit("SofID", "SofType");
		TopUpQuote topUpQuote = new TopUpQuote("", sourceOfFund, "accessTokenID", BigDecimal.TEN, BigDecimal.ONE);
		topUpQuote.setID("ID");
		
		//given
		when(topUpEwalletServiceMock.getTopUpQuoteDetails(anyString(), anyString())).thenReturn(topUpQuote);
		when(extendAccessTokenAsynServiceMock.setExpire(anyString())).thenReturn(new AsyncResult<Boolean>(true));
		
		//perform
		this.mockMvc.perform(get("/top-up/quote/{draftTransactionID}?accessTokenID={accessTokenID}", "TransID", "TokenID")
			.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk());
	}
	
	@Test
	public void getTopupDetailFail() throws Exception {
		//given
		when(topUpEwalletServiceMock.getTopUpQuoteDetails(anyString(), anyString()))
			.thenThrow(new ServiceInventoryException(400,"Error Code","Error Description", "Error Namespace"));	
		when(extendAccessTokenAsynServiceMock.setExpire(anyString())).thenReturn(new AsyncResult<Boolean>(true));
		
		//perform
		this.mockMvc.perform(get("/top-up/quote/{draftTransactionID}?accessTokenID={accessTokenID}", "TransID", "TokenID")
			.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isBadRequest());
	}

	@Test
	public void performTopupSuccess() throws Exception {
		//given
		when(topUpEwalletServiceMock.performTopUp(anyString(), anyString())).thenReturn(TopUpOrder.Status.SUCCESS);		
		when(extendAccessTokenAsynServiceMock.setExpire(anyString())).thenReturn(new AsyncResult<Boolean>(true));

		//perform		
		this.mockMvc.perform(put("/top-up/order/{transactionID}?accessTokenID={accessTokenID}","TransactionID","TokenID")
			.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk());
	}
	
	@Test
	public void performTopupFail() throws Exception {
		//given
		when(topUpEwalletServiceMock.performTopUp(anyString(), anyString()))
			.thenThrow(new ServiceInventoryWebException(Code.CONFIRM_FAILED, "confirmation processing fail."));		
		when(extendAccessTokenAsynServiceMock.setExpire(anyString())).thenReturn(new AsyncResult<Boolean>(true));

		//perform		
		this.mockMvc.perform(put("/top-up/order/{transactionID}?accessTokenID={accessTokenID}","TransactionID","TokenID")
			.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.errorCode").value(Code.CONFIRM_FAILED))
			.andExpect(jsonPath("$.errorDescription").value("confirmation processing fail."))
			.andExpect(jsonPath("$.errorNamespace").value("TMN-SERVICE-INVENTORY"));
	}
	
	@Test
	public void getTopupStatusSuccess() throws Exception {
		//given
		when(topUpEwalletServiceMock.getTopUpProcessingStatus(anyString(), anyString())).thenReturn(TopUpOrder.Status.SUCCESS);		
		when(extendAccessTokenAsynServiceMock.setExpire(anyString())).thenReturn(new AsyncResult<Boolean>(true));

		//perform		
		this.mockMvc.perform(get("/top-up/order/{transactionID}/status?accessTokenID={accessTokenID}","TransactionID","TokenID")
			.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk());
	}
	
	@Test
	public void getTopupStatusFail() throws Exception {
		//given
		when(topUpEwalletServiceMock.getTopUpProcessingStatus(anyString(), anyString())).thenReturn(TopUpOrder.Status.FAILED);		
		when(extendAccessTokenAsynServiceMock.setExpire(anyString())).thenReturn(new AsyncResult<Boolean>(true));

		//perform		
		this.mockMvc.perform(get("/top-up/order/{transactionID}/status?accessTokenID={accessTokenID}","TransactionID","TokenID")
			.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk());
	}

	@Test
	public void getTopupResultSuccess() throws Exception {
		TopUpConfirmationInfo confirmationInfo = new TopUpConfirmationInfo();		
		TopUpOrder topUpOrder = new TopUpOrder();
		topUpOrder.setConfirmationInfo(confirmationInfo);

		//given
		when(topUpEwalletServiceMock.getTopUpOrderResults(anyString(), anyString())).thenReturn(topUpOrder);		
		when(extendAccessTokenAsynServiceMock.setExpire(anyString())).thenReturn(new AsyncResult<Boolean>(true));

		//perform		
		this.mockMvc.perform(get("/top-up/order/{transactionID}?accessTokenID={accessTokenID}","TransactionID","TokenID")
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$..transactionID").exists())
				.andExpect(jsonPath("$..transactionDate").exists());
	}
	
	@Test
	public void getTopupResultFail() throws Exception {
		//given
		when(topUpEwalletServiceMock.getTopUpOrderResults(anyString(), anyString()))
			.thenThrow(new ServiceInventoryException(400,"Error Code","Error Description", "Error Namespace"));	
		when(extendAccessTokenAsynServiceMock.setExpire(anyString())).thenReturn(new AsyncResult<Boolean>(true));

		//perform		
		this.mockMvc.perform(get("/top-up/order/{transactionID}?accessTokenID={accessTokenID}","TransactionID","TokenID")
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.errorCode").value("Error Code"))
				.andExpect(jsonPath("$.errorDescription").value("Error Description"))
				.andExpect(jsonPath("$.errorNamespace").value("Error Namespace"));
	}
	
}
