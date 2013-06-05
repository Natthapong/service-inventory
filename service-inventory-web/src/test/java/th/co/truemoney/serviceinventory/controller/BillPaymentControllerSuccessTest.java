package th.co.truemoney.serviceinventory.controller;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
import th.co.truemoney.serviceinventory.bill.domain.OutStandingBill;
import th.co.truemoney.serviceinventory.config.MemRepositoriesConfig;
import th.co.truemoney.serviceinventory.config.TestRedisConfig;
import th.co.truemoney.serviceinventory.config.TestServiceInventoryConfig;
import th.co.truemoney.serviceinventory.config.WebConfig;
import th.co.truemoney.serviceinventory.firsthop.config.SmsConfig;
import th.co.truemoney.serviceinventory.stub.BillPaymentStubbed;


@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(classes = { WebConfig.class, MemRepositoriesConfig.class, TestServiceInventoryConfig.class, TestRedisConfig.class, SmsConfig.class })
@ActiveProfiles(profiles={"local", "mem"})
public class BillPaymentControllerSuccessTest {

	@Autowired
	private WebApplicationContext wac;

	private MockMvc mockMvc;

	@Autowired
	private BillPaymentService billPaymentServiceMock;

	@Before
	public void setup() {
		this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
		this.billPaymentServiceMock = wac.getBean(BillPaymentService.class);
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
	public void getBillInformationWithKeyinSuccess() throws Exception {

		//given
		Bill stubbedBill = BillPaymentStubbed.createSuccessBillPaymentInfo();
		when(billPaymentServiceMock.retrieveBillInformationWithKeyin(anyString(), anyString())).thenReturn(stubbedBill);

		//perform
		this.mockMvc.perform(get("/bill-payment/information/tcg/?accessTokenID=12345")
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
}
