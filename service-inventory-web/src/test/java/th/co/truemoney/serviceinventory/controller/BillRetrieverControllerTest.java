package th.co.truemoney.serviceinventory.controller;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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

import th.co.truemoney.serviceinventory.bill.BillRetriever;
import th.co.truemoney.serviceinventory.bill.domain.Bill;
import th.co.truemoney.serviceinventory.config.MemRepositoriesConfig;
import th.co.truemoney.serviceinventory.config.TestServiceInventoryConfig;
import th.co.truemoney.serviceinventory.config.WebConfig;
import th.co.truemoney.serviceinventory.firsthop.config.SmsConfig;
import th.co.truemoney.serviceinventory.stub.BillPaymentStubbed;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(classes = { WebConfig.class, MemRepositoriesConfig.class, TestServiceInventoryConfig.class, SmsConfig.class })
@ActiveProfiles(profiles={"local", "mem"})
public class BillRetrieverControllerTest {

    @Autowired
    private WebApplicationContext wac;

    private MockMvc mockMvc;

    @Autowired
    private BillRetriever billRetrieverMock;

    @Before
    public void setup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
        this.billRetrieverMock = wac.getBean(BillRetriever.class);

        Bill stubbedBill = BillPaymentStubbed.createSuccessBillPaymentInfo();

        when(billRetrieverMock.getOfflineBillInfoByScanningBarcode(anyString(), anyString())).thenReturn(stubbedBill);
        when(billRetrieverMock.getOfflineBillInfoByKeyInBillCode(anyString(), anyString(), anyString(), any(BigDecimal.class), anyString())).thenReturn(stubbedBill);
        when(billRetrieverMock.getOfflineBillInfoByKeyInBillCode(anyString(), anyString(), anyString(), any(BigDecimal.class), anyString())).thenReturn(stubbedBill);
        when(billRetrieverMock.getOfflineBillInfoFromUserFavorited(anyLong(), any(BigDecimal.class), anyString())).thenReturn(stubbedBill);
        when(billRetrieverMock.getOnlineBillInfoFromUserFavorited(anyLong(), anyString())).thenReturn(stubbedBill);

    }

    @After
    public void tierDown() {
        reset(this.billRetrieverMock);
    }

    @Test
    public void getBillInformationFromBarcodeSuccess() throws Exception {

        //perform
        this.mockMvc.perform(get("/bill-payment/bill/information/?barcode=|010004552 010520120200015601 10000&accessTokenID=12345")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ref1").value("010004552"))
                .andExpect(jsonPath("$.ref2").value("010520120200015601"))
                .andExpect(jsonPath("$.amount").value(10000.00));
    }

    @Test
    public void getBillInformationFromBillcodeOffline_Success() throws Exception {

        //perform
        this.mockMvc.perform(get("/bill-payment/bill/information/?billCode=tcg&ref1=myRef1&ref2=myRef2&amount=50.00&inquiry=offline&accessTokenID=12345")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void getBillInformationFromBillcodeOffline_SuccessWithNoInquiryParam() throws Exception {

        //perform
        this.mockMvc.perform(get("/bill-payment/bill/information/?billCode=tcg&amount=50.0&ref1=myRef1&ref2=myRef2&accessTokenID=12345")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void getBillInformationFromBillcodeOffline_FailMissingAmount() throws Exception {

        //perform
        this.mockMvc.perform(get("/bill-payment/bill/information/?billCode=tcg&inquiry=offline&accessTokenID=12345")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void getBillInformationFromBillcodeOffline_FailMissingRef1() throws Exception {

        //perform
        this.mockMvc.perform(get("/bill-payment/bill/information/?billCode=tcg&ref2=myRef2&amount=50.0&inquiry=offline&accessTokenID=12345")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void getBillInformationFromBillcodeOffline_FailMissingRef2() throws Exception {

        //perform
        this.mockMvc.perform(get("/bill-payment/bill/information/?billCode=tcg&ref1=myRef1&amount=50.0&inquiry=offline&accessTokenID=12345")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void getBillInformationFromBillcodeOnline_Success_NoNeedAmount() throws Exception {

        //perform
        this.mockMvc.perform(get("/bill-payment/bill/information/?billCode=tcg&ref1=myRef1&ref2=myRef2&inquiry=online&accessTokenID=12345")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void getBillInformationFromUserFavoriteOffline_SuccessWithNoInquiryParam() throws Exception {

        //perform
        this.mockMvc.perform(get("/bill-payment/bill/information/?favoriteID=2&amount=5.0&accessTokenID=12345")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void getBillInformationFromUserFavoriteOffline_FailMissingAmount() throws Exception {

        //perform
        this.mockMvc.perform(get("/bill-payment/bill/information/?favoriteID=2&inquiry=offline&accessTokenID=12345")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void getBillInformationFromUserFavoriteOnline_Success_NoNeedAmount() throws Exception {

        //perform
        this.mockMvc.perform(get("/bill-payment/bill/information/?favoriteID=2&inquiry=online&accessTokenID=12345")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

}
