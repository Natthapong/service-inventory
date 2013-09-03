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
import th.co.truemoney.serviceinventory.bill.domain.Bill;
import th.co.truemoney.serviceinventory.bill.domain.BillPaymentConfirmationInfo;
import th.co.truemoney.serviceinventory.bill.domain.BillPaymentDraft;
import th.co.truemoney.serviceinventory.bill.domain.BillPaymentTransaction;
import th.co.truemoney.serviceinventory.bill.domain.InquiryOutstandingBillType;
import th.co.truemoney.serviceinventory.bill.domain.OutStandingBill;
import th.co.truemoney.serviceinventory.config.MemRepositoriesConfig;
import th.co.truemoney.serviceinventory.config.TestServiceInventoryConfig;
import th.co.truemoney.serviceinventory.config.WebConfig;
import th.co.truemoney.serviceinventory.firsthop.config.SmsConfig;
import th.co.truemoney.serviceinventory.stub.BillPaymentStubbed;

import com.fasterxml.jackson.databind.ObjectMapper;


@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(classes = { WebConfig.class, MemRepositoriesConfig.class, TestServiceInventoryConfig.class, SmsConfig.class })
@ActiveProfiles(profiles={"local", "mem"})
public class BillPaymentControllerSuccessTest {

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
    public void getBillInformationSuccess() throws Exception {

        //given
        Bill stubbedBill = BillPaymentStubbed.createSuccessBillPaymentInfo();
        when(billPaymentServiceMock.retrieveBillInformationWithBarcode(anyString(), anyString())).thenReturn(stubbedBill);

        //perform
        //String expectedString = "{\"target\":\"tcg\",\"logoURL\":\"https://secure.truemoney-dev.com/m/tmn_webview/images/logo_bill/tmvh@2x.png\",\"titleTH\":\"ค่าใช้บริการบริษัทในกลุ่มทรู\",\"titleEN\":\"Convergence Postpay\",\"ref1TitleTH\":\"โทรศัพท์พื้นฐาน\",\"ref1TitleEN\":\"Fix Line\",\"ref1\":\"010004552\",\"ref2TitleTH\":\"รหัสลูกค้า\",\"ref2TitleEN\":\"Customer ID\",\"ref2\":\"010520120200015601\",\"amount\":10000,\"serviceFee\":{\"fee\":1000,\"feeType\":\"THB\",\"totalFee\":1000,\"minFeeAmount\":100,\"maxFeeAmount\":2500},\"sourceOfFundFees\":[{\"sourceType\":\"EW\",\"fee\":1000,\"totalFee\":1000,\"feeType\":\"THB\",\"minFeeAmount\":100,\"maxFeeAmount\":2500}]}";
        this.mockMvc.perform(get("/bill-payment/information/?barcode=|010554614953100 010004552 010520120200015601 85950&accessTokenID=12345")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ref1").exists());
    }

    @Test
    public void getBillInformationWithKeyinOnlineSuccess() throws Exception {

        //given
        Bill stubbedBill = BillPaymentStubbed.createSuccessBillPaymentInfo();
        when(billPaymentServiceMock.retrieveBillInformationWithKeyin(stubbedBill.getTarget(), stubbedBill.getRef1(), stubbedBill.getRef2(), BigDecimal.ZERO, InquiryOutstandingBillType.ONLINE, "12345")).thenReturn(stubbedBill);

        //perform
        this.mockMvc.perform(get("/bill-payment/information/?billCode=tcg&ref1=010004552&ref2=010520120200015601&inquiry=online&accessTokenID=12345")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.target").exists());

    }
    
    @Test
    public void getBillInformationWithKeyinOfflineSuccess() throws Exception {

        //given
        Bill stubbedBill = BillPaymentStubbed.createSuccessBillPaymentInfo();
        when(billPaymentServiceMock.retrieveBillInformationWithKeyin(stubbedBill.getTarget(), stubbedBill.getRef1(), stubbedBill.getRef2(), BigDecimal.ZERO, InquiryOutstandingBillType.OFFLINE, "12345")).thenReturn(stubbedBill);

        //perform
        this.mockMvc.perform(get("/bill-payment/information/?billCode=tcg&ref1=010004552&ref2=010520120200015601&inquiry=offline&accessTokenID=12345")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.target").exists());

    }

    @Test
    public void getFavouriteBillInformationOnlineSuccess() throws Exception {

        //given
        Bill stubbedBill = BillPaymentStubbed.createSuccessBillPaymentInfo();
        when(billPaymentServiceMock.retrieveBillInformationWithUserFavorite(stubbedBill.getTarget(), stubbedBill.getRef1(), stubbedBill.getRef2(), BigDecimal.ZERO, InquiryOutstandingBillType.ONLINE, "12345"))
        .thenReturn(stubbedBill);

        //perform
        this.mockMvc.perform(get("/bill-payment/information?billCode=tcg&ref1=010004552&ref2=010520120200015601&inquiry=online&favorite=true&accessTokenID=12345")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.target").exists());
    }

    @Test
    public void getFavouriteBillInformationOfflineSuccess() throws Exception {

        //given
        Bill stubbedBill = BillPaymentStubbed.createSuccessBillPaymentInfo();
        when(billPaymentServiceMock.retrieveBillInformationWithUserFavorite(stubbedBill.getTarget(), stubbedBill.getRef1(), stubbedBill.getRef2(), BigDecimal.ZERO, InquiryOutstandingBillType.OFFLINE, "12345"))
        .thenReturn(stubbedBill);

        //perform
        this.mockMvc.perform(get("/bill-payment/information?billCode=tcg&ref1=010004552&ref2=010520120200015601&inquiry=offline&favorite=true&accessTokenID=12345")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.target").exists());
    }
    
    @Test
    public void getBillOutStandingOnlineSuccess() throws Exception {

        //given
        OutStandingBill stubbedSuccessOutStandingBill = BillPaymentStubbed.createSuccessOutStandingBill();
        when(billPaymentServiceMock.retrieveBillOutStandingOnline(anyString(), anyString(), anyString(), anyString())).thenReturn(stubbedSuccessOutStandingBill);

        //perform
        this.mockMvc.perform(get("/bill-payment/information/outstanding/mea/123456789/?ref2=987654321&accessTokenID=12345")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.outStandingBalance").exists());

    }
    
    @Test
	public void createAndVerifyTransferSuccess() throws Exception {
		BillPaymentDraft billPaymentDraft = new BillPaymentDraft("ID", BillPaymentStubbed.createSuccessBillPaymentInfo());
		
		//given
		when(billPaymentServiceMock.verifyPaymentAbility(anyString(), any(BigDecimal.class), anyString()))
			.thenReturn(billPaymentDraft);
		
		//perform
		this.mockMvc.perform(post("/bill-payment/invoice/{billInfoID}?accessTokenID={accessTokenID}", "BillInfoID", "TokenID")
			.contentType(MediaType.APPLICATION_JSON)
			.content(mapper.writeValueAsBytes(billPaymentDraft)))
			.andExpect(status().isOk());
	} 
	
	@Test
	public void getBillPaymentDraftSuccess() throws Exception {
		BillPaymentDraft billPaymentDraft = new BillPaymentDraft("ID", BillPaymentStubbed.createSuccessBillPaymentInfo());
		
		//given
		when(billPaymentServiceMock.getBillPaymentDraftDetail(anyString(), anyString()))
			.thenReturn(billPaymentDraft);
		
		//perform
		this.mockMvc.perform(get("/bill-payment/invoice/{draftTransactionID}?accessTokenID={accessTokenID}", "ID", "TokenID")
			.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk());
	} 
	
	@Test
	public void performBillPaymentSuccess() throws Exception {
		//given
		when(billPaymentServiceMock.performPayment(anyString(), anyString())).thenReturn(BillPaymentTransaction.Status.SUCCESS);		

		//perform		
		this.mockMvc.perform(put("/bill-payment/transaction/{transactionID}?accessTokenID={accessTokenID}","TransactionID","TokenID")
			.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk());
	}
	
	@Test
	public void getBillPaymentStatusSuccess() throws Exception {
		//given
		when(billPaymentServiceMock.getBillPaymentStatus(anyString(), anyString())).thenReturn(BillPaymentTransaction.Status.SUCCESS);		

		//perform		
		this.mockMvc.perform(get("/bill-payment/transaction/{transactionID}/status?accessTokenID={accessTokenID}","TransactionID","TokenID")
			.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk());
	}
	
	@Test
	public void getTransferResultSuccess() throws Exception {
		BillPaymentConfirmationInfo confirmationInfo = new BillPaymentConfirmationInfo();		
		BillPaymentTransaction billPaymentTransaction = new BillPaymentTransaction();
		billPaymentTransaction.setConfirmationInfo(confirmationInfo);

		//given
		when(billPaymentServiceMock.getBillPaymentResult(anyString(), anyString())).thenReturn(billPaymentTransaction);		

		//perform		
		this.mockMvc.perform(get("/bill-payment/transaction/{transactionID}?accessTokenID={accessTokenID}","TransactionID","TokenID")
			.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$..transactionID").exists())
			.andExpect(jsonPath("$..transactionDate").exists());
	}
}
