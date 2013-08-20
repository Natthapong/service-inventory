package th.co.truemoney.serviceinventory.controller;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
import th.co.truemoney.serviceinventory.buy.domain.BuyProductDraft;
import th.co.truemoney.serviceinventory.config.MemRepositoriesConfig;
import th.co.truemoney.serviceinventory.config.TestRedisConfig;
import th.co.truemoney.serviceinventory.config.TestServiceInventoryConfig;
import th.co.truemoney.serviceinventory.config.WebConfig;
import th.co.truemoney.serviceinventory.ewallet.impl.ExtendAccessTokenAsynService;
import th.co.truemoney.serviceinventory.exception.ServiceInventoryException;
import th.co.truemoney.serviceinventory.firsthop.config.SmsConfig;

import com.fasterxml.jackson.databind.ObjectMapper;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(classes = { WebConfig.class, MemRepositoriesConfig.class, TestServiceInventoryConfig.class, TestRedisConfig.class, SmsConfig.class })
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
		BuyProduct buyProduct = new BuyProduct("epin_c", new BigDecimal("50"));
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
		BuyProduct buyProduct = new BuyProduct("epin_c", new BigDecimal("50"));
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
	
}
