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
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import th.co.truemoney.serviceinventory.bill.BillPaymentService;
import th.co.truemoney.serviceinventory.bill.domain.BillPaymentDraft;
import th.co.truemoney.serviceinventory.bill.domain.BillPaymentTransaction;
import th.co.truemoney.serviceinventory.config.MemRepositoriesConfig;
import th.co.truemoney.serviceinventory.config.TestRedisConfig;
import th.co.truemoney.serviceinventory.config.TestServiceInventoryConfig;
import th.co.truemoney.serviceinventory.config.WebConfig;
import th.co.truemoney.serviceinventory.exception.ServiceInventoryException;
import th.co.truemoney.serviceinventory.exception.ServiceInventoryWebException;
import th.co.truemoney.serviceinventory.exception.ServiceInventoryWebException.Code;
import th.co.truemoney.serviceinventory.firsthop.config.SmsConfig;
import th.co.truemoney.serviceinventory.stub.BillPaymentStubbed;

import com.fasterxml.jackson.databind.ObjectMapper;


@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(classes = { WebConfig.class, MemRepositoriesConfig.class, TestServiceInventoryConfig.class, TestRedisConfig.class, SmsConfig.class })
@ActiveProfiles(profiles={"local", "mem"})
public class BillPaymentControllerFailTest {

	@Autowired
	private WebApplicationContext wac;

	private MockMvc mockMvc;
	
	private ObjectMapper mapper;

	@Autowired
	private BillPaymentService billPaymentServiceMock;

	@Before
	public void setup() {
		this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
		this.billPaymentServiceMock = wac.getBean(BillPaymentService.class);
		this.mapper = new ObjectMapper();
	}

	@After
	public void tierDown() {
		reset(this.billPaymentServiceMock);
	}

	@Test
	public void getBillInformationFail() throws Exception {

		//given
		ServiceInventoryWebException exception = BillPaymentStubbed.createFailBillPaymentInfo();
		when(billPaymentServiceMock.retrieveBillInformationWithBarcode(anyString(), anyString())).thenThrow(exception);

		//perform
		this.mockMvc.perform(get("/bill-payment/information/?barcode=|010554614953100 010004552 010520120200015601 85950&accessTokenID=12345")
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest());
	}
	
	public void createAndVerifyTransferFail() throws Exception {
		BillPaymentDraft billPaymentDraft = new BillPaymentDraft("ID", BillPaymentStubbed.createSuccessBillPaymentInfo());
		
		//given
		when(billPaymentServiceMock.verifyPaymentAbility(anyString(), any(BigDecimal.class), anyString()))
			.thenThrow(new ServiceInventoryException(400,"Error Code","Error Description", "Error Namespace"));	
		
		//perform
		this.mockMvc.perform(post("/bill-payment/invoice/{billInfoID}?accessTokenID={accessTokenID}", "BillID", "TokenID")
			.contentType(MediaType.APPLICATION_JSON)
			.content(mapper.writeValueAsBytes(billPaymentDraft)))
			.andExpect(status().isBadRequest());
	} 
	
	@Test
	public void getBillPaymentDraftFail() throws Exception {
		//given
		when(billPaymentServiceMock.getBillPaymentDraftDetail(anyString(), anyString()))
			.thenThrow(new ServiceInventoryException(400,"Error Code","Error Description", "Error Namespace"));	
		
		//perform
		this.mockMvc.perform(get("/bill-payment/invoice/{draftTransactionID}?accessTokenID={accessTokenID}", "ID", "TokenID")
			.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isBadRequest());
	} 
	
	@Test
	public void performBillPaymentFail() throws Exception {
		//given
		when(billPaymentServiceMock.performPayment(anyString(), anyString()))
			.thenThrow(new ServiceInventoryWebException(Code.CONFIRM_FAILED, "confirmation processing fail."));			

		//perform		
		this.mockMvc.perform(put("/bill-payment/transaction/{transactionID}?accessTokenID={accessTokenID}","TransactionID","TokenID")
			.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.errorCode").value(Code.CONFIRM_FAILED))
			.andExpect(jsonPath("$.errorDescription").value("confirmation processing fail."))
			.andExpect(jsonPath("$.errorNamespace").value("TMN-SERVICE-INVENTORY"));
	}
	
	@Test
	public void getBillPaymentStatusFail() throws Exception {
		//given
		when(billPaymentServiceMock.getBillPaymentStatus(anyString(), anyString()))
			.thenReturn(BillPaymentTransaction.Status.FAILED);		

		//perform		
		this.mockMvc.perform(get("/bill-payment/transaction/{transactionID}/status?accessTokenID={accessTokenID}","TransactionID","TokenID")
			.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk());
	}
	
	@Test
	public void getTransferResultFail() throws Exception {
		//given
		when(billPaymentServiceMock.getBillPaymentResult(anyString(), anyString()))
			.thenThrow(new ServiceInventoryException(400,"Error Code","Error Description", "Error Namespace"));	

		//perform		
		this.mockMvc.perform(get("/bill-payment/transaction/{transactionID}?accessTokenID={accessTokenID}","TransactionID","TokenID")
			.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.errorCode").value("Error Code"))
			.andExpect(jsonPath("$.errorDescription").value("Error Description"))
			.andExpect(jsonPath("$.errorNamespace").value("Error Namespace"));
	}
	
}
