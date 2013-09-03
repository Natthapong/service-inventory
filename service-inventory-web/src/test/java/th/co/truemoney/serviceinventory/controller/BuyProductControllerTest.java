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

import th.co.truemoney.serviceinventory.buy.BuyProductService;
import th.co.truemoney.serviceinventory.buy.domain.BuyProduct;
import th.co.truemoney.serviceinventory.buy.domain.BuyProductConfirmationInfo;
import th.co.truemoney.serviceinventory.buy.domain.BuyProductDraft;
import th.co.truemoney.serviceinventory.buy.domain.BuyProductTransaction;
import th.co.truemoney.serviceinventory.config.MemRepositoriesConfig;
import th.co.truemoney.serviceinventory.config.TestServiceInventoryConfig;
import th.co.truemoney.serviceinventory.config.WebConfig;
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
public class BuyProductControllerTest {

	private MockMvc mockMvc;
	
	private ObjectMapper mapper;

	@Autowired
	private WebApplicationContext wac;

	@Autowired
	private BuyProductService buyProductServiceMock;
	
	@Autowired
    private ExtendAccessTokenAsynService extendAccessTokenAsynServiceMock;
	
	@Before
	public void setup() {
		this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
		this.buyProductServiceMock = wac.getBean(BuyProductService.class);
		this.extendAccessTokenAsynServiceMock = wac.getBean(ExtendAccessTokenAsynService.class);
		this.mapper = new ObjectMapper();
	}

	@After
	public void tierDown() {
		reset(this.buyProductServiceMock);
		reset(this.extendAccessTokenAsynServiceMock);
	}
	
	@Test
	public void verifyAndCreateSuccess() throws Exception {
		BuyProduct buyProduct = new BuyProduct("ecash_c", new BigDecimal("50"));
		BuyProductDraft buyProductDraft = new BuyProductDraft("draftID", buyProduct);
		buyProductDraft.setRecipientMobileNumber("08xxxxxxxx");
		
		//given
		when(buyProductServiceMock.createAndVerifyBuyProductDraft(anyString(), anyString(), any(BigDecimal.class), anyString()))
			.thenReturn(buyProductDraft);		
		when(extendAccessTokenAsynServiceMock.setExpire(anyString())).thenReturn(new AsyncResult<Boolean>(true));

		//perform		
		this.mockMvc.perform(post("/buy/product/draft?accessTokenID={accessTokenID}","TokenID")
				.contentType(MediaType.APPLICATION_JSON)
				.content(mapper.writeValueAsBytes(buyProductDraft)))
				.andExpect(status().isOk());
	}
	
	@Test
	public void verifyAndCreateFail() throws Exception {
		BuyProduct buyProduct = new BuyProduct("ecash_c", new BigDecimal("50"));
		BuyProductDraft buyProductDraft = new BuyProductDraft("draftID", buyProduct);
		buyProductDraft.setRecipientMobileNumber("08xxxxxxxx");
		
		//given
		when(buyProductServiceMock.createAndVerifyBuyProductDraft(anyString(), anyString(), any(BigDecimal.class), anyString()))
			.thenThrow(new ServiceInventoryException(400,"Error Code","Error Description", "Error Namespace"));	
		when(extendAccessTokenAsynServiceMock.setExpire(anyString())).thenReturn(new AsyncResult<Boolean>(true));

		//perform		
		this.mockMvc.perform(post("/buy/product/draft?accessTokenID={accessTokenID}","TokenID")
				.contentType(MediaType.APPLICATION_JSON)
				.content(mapper.writeValueAsBytes(buyProductDraft)))
				.andExpect(status().isBadRequest());
	}
	
	@Test
	public void getBuyProductDraftDetailSuccess() throws Exception {
		BuyProduct buyProduct = new BuyProduct("ecash_c", new BigDecimal("50"));
		BuyProductDraft buyProductDraft = new BuyProductDraft("draftID", buyProduct);
		buyProductDraft.setRecipientMobileNumber("08xxxxxxxx");
		
		//given
		when(buyProductServiceMock.getBuyProductDraftDetails(anyString(), anyString())).thenReturn(buyProductDraft);		
		when(extendAccessTokenAsynServiceMock.setExpire(anyString())).thenReturn(new AsyncResult<Boolean>(true));

		//perform		
		this.mockMvc.perform(get("/buy/product/draft/{draftTransactionID}?accessTokenID={accessTokenID}","DraftID","TokenID")
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk());
	}
	
	@Test
	public void getBuyProductDraftDetailFail() throws Exception {
		//given
		when(buyProductServiceMock.getBuyProductDraftDetails(anyString(), anyString()))
			.thenThrow(new ServiceInventoryException(400,"Error Code","Error Description", "Error Namespace"));			
		when(extendAccessTokenAsynServiceMock.setExpire(anyString())).thenReturn(new AsyncResult<Boolean>(true));

		//perform		
		this.mockMvc.perform(get("/buy/product/draft/{draftTransactionID}?accessTokenID={accessTokenID}","DraftID","TokenID")
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest());
	}
	
	@Test
	public void performBuyProductSuccess() throws Exception {
		//given
		when(buyProductServiceMock.performBuyProduct(anyString(), anyString())).thenReturn(BuyProductTransaction.Status.SUCCESS);		
		when(extendAccessTokenAsynServiceMock.setExpire(anyString())).thenReturn(new AsyncResult<Boolean>(true));

		//perform		
		this.mockMvc.perform(put("/buy/product/transaction/{transactionID}?accessTokenID={accessTokenID}","TransactionID","TokenID")
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk());
	}
	
	@Test
	public void performBuyProductFail() throws Exception {
		//given
		when(buyProductServiceMock.performBuyProduct(anyString(), anyString()))
			.thenThrow(new ServiceInventoryWebException(Code.CONFIRM_FAILED, "confirmation processing fail."));		
		when(extendAccessTokenAsynServiceMock.setExpire(anyString())).thenReturn(new AsyncResult<Boolean>(true));

		//perform		
		this.mockMvc.perform(put("/buy/product/transaction/{transactionID}?accessTokenID={accessTokenID}","TransactionID","TokenID")
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.errorCode").value(Code.CONFIRM_FAILED))
				.andExpect(jsonPath("$.errorDescription").value("confirmation processing fail."))
				.andExpect(jsonPath("$.errorNamespace").value("TMN-SERVICE-INVENTORY"));
	}
	
	@Test
	public void getBuyProductStatusSuccess() throws Exception {
		//given
		when(buyProductServiceMock.getBuyProductStatus(anyString(), anyString())).thenReturn(BuyProductTransaction.Status.SUCCESS);		
		when(extendAccessTokenAsynServiceMock.setExpire(anyString())).thenReturn(new AsyncResult<Boolean>(true));

		//perform		
		this.mockMvc.perform(get("/buy/product/transaction/{transactionID}/status?accessTokenID={accessTokenID}","TransactionID","TokenID")
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk());
	}
	
	@Test
	public void getBuyProductStatusFail() throws Exception {
		//given
		when(buyProductServiceMock.getBuyProductStatus(anyString(), anyString())).thenReturn(BuyProductTransaction.Status.FAILED);		
		when(extendAccessTokenAsynServiceMock.setExpire(anyString())).thenReturn(new AsyncResult<Boolean>(true));

		//perform		
		this.mockMvc.perform(get("/buy/product/transaction/{transactionID}/status?accessTokenID={accessTokenID}","TransactionID","TokenID")
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk());
	}
	
	@Test
	public void getBuyProductResultSuccess() throws Exception {
		BuyProductConfirmationInfo confirmationInfo = new BuyProductConfirmationInfo();		
		BuyProductTransaction buyProductTransaction = new BuyProductTransaction();
		buyProductTransaction.setConfirmationInfo(confirmationInfo);

		//given
		when(buyProductServiceMock.getBuyProductResult(anyString(), anyString())).thenReturn(buyProductTransaction);		
		when(extendAccessTokenAsynServiceMock.setExpire(anyString())).thenReturn(new AsyncResult<Boolean>(true));

		//perform		
		this.mockMvc.perform(get("/buy/product/transaction/{transactionID}?accessTokenID={accessTokenID}","TransactionID","TokenID")
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$..transactionID").exists())
				.andExpect(jsonPath("$..transactionDate").exists());
	}
	
	@Test
	public void getBuyProductResultFail() throws Exception {
		//given
		when(buyProductServiceMock.getBuyProductResult(anyString(), anyString()))
			.thenThrow(new ServiceInventoryException(400,"Error Code","Error Description", "Error Namespace"));				
		when(extendAccessTokenAsynServiceMock.setExpire(anyString())).thenReturn(new AsyncResult<Boolean>(true));

		//perform		
		this.mockMvc.perform(get("/buy/product/transaction/{transactionID}?accessTokenID={accessTokenID}","TransactionID","TokenID")
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.errorCode").value("Error Code"))
				.andExpect(jsonPath("$.errorDescription").value("Error Description"))
				.andExpect(jsonPath("$.errorNamespace").value("Error Namespace"));
	}
	
	
}
