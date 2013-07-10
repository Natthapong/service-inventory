package th.co.truemoney.serviceinventory.controller;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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

import th.co.truemoney.serviceinventory.config.MemRepositoriesConfig;
import th.co.truemoney.serviceinventory.config.TestRedisConfig;
import th.co.truemoney.serviceinventory.config.TestServiceInventoryConfig;
import th.co.truemoney.serviceinventory.config.WebConfig;
import th.co.truemoney.serviceinventory.ewallet.ForgotPasswordService;
import th.co.truemoney.serviceinventory.ewallet.domain.OTP;
import th.co.truemoney.serviceinventory.ewallet.domain.ResetPassword;
import th.co.truemoney.serviceinventory.ewallet.domain.VerifyResetPassword;
import th.co.truemoney.serviceinventory.firsthop.config.SmsConfig;

import com.fasterxml.jackson.databind.ObjectMapper;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(classes = { WebConfig.class, MemRepositoriesConfig.class, TestServiceInventoryConfig.class, TestRedisConfig.class, SmsConfig.class })
@ActiveProfiles(profiles={"local", "mem"})
public class ForgotPasswordControllerTest {

	private MockMvc mockMvc;
	private ObjectMapper mapper;

	@Autowired
	private WebApplicationContext wac;
	
	@Autowired
	private ForgotPasswordService forgotPasswordServiceMock;
	
	@Before
	public void setup() {
		this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
		this.forgotPasswordServiceMock = wac.getBean(ForgotPasswordService.class);
		mapper = new ObjectMapper();
	}

	@After
	public void tierDown() {
		reset(this.forgotPasswordServiceMock);
	}
	
	@Test
	public void shouldVerifyResetPasswordSuccess() throws Exception {

		when(forgotPasswordServiceMock.verifyResetPassword(anyInt(), any(ResetPassword.class))).thenReturn(new VerifyResetPassword());

		ResetPassword resetPassword = new ResetPassword("token", "newPassword");
		
		this.mockMvc.perform(post("/ewallet/profile/password/verify-reset?channelID={channelID}", 40)
			.contentType(MediaType.APPLICATION_JSON)
			.content(mapper.writeValueAsBytes(resetPassword)))
			.andExpect(status().isOk());
		
	}
	
	@Test
	public void shouldVerifyOTPResetPasswordSuccess() throws Exception {

		when(forgotPasswordServiceMock.verifyOTP(anyInt(), any(VerifyResetPassword.class))).thenReturn("tokenID");

    	//stubbed
    	ResetPassword resetPassword = new ResetPassword("tokenID", "newPassword");
    	resetPassword.setMobileNumber("0866013468");
    	resetPassword.setLoginID("adam@tmn.com");
    	resetPassword.setTruemoneyID("tmn.0000000010");
    	
        OTP stubbedOTP = new OTP("0866013468", "abcd", "111111");
        VerifyResetPassword verifyResetPassword = new VerifyResetPassword();
        verifyResetPassword.setOtp(stubbedOTP);
        verifyResetPassword.setResetPasswordID(resetPassword.getToken());
		
		this.mockMvc.perform(post("/ewallet/profile/password/verify-otp?channelID={channelID}", 40)
			.contentType(MediaType.APPLICATION_JSON)
			.content(mapper.writeValueAsBytes(verifyResetPassword)))
			.andExpect(status().isOk());
		
	}
	
	@Test
	public void shouldConfirmResetPasswordSuccess() throws Exception {

    	//stubbed
    	ResetPassword resetPassword = new ResetPassword("tokenID", "newPassword");
    	resetPassword.setMobileNumber("0866013468");
    	resetPassword.setLoginID("adam@tmn.com");
    	resetPassword.setTruemoneyID("tmn.0000000010");
    	
        //given
        OTP stubbedOTP = new OTP("0866013468", "abcd", "111111");
        VerifyResetPassword verifyResetPassword = new VerifyResetPassword();
        verifyResetPassword.setOtp(stubbedOTP);
        verifyResetPassword.setResetPasswordID(resetPassword.getToken());
        
		when(forgotPasswordServiceMock.confirmResetPassword(anyInt(), anyString(), anyString())).thenReturn(resetPassword.getToken());

		this.mockMvc.perform(post("/ewallet/profile/password/confirm-reset/{resetPasswordID}?channelID={channelID}", verifyResetPassword.getResetPasswordID() ,40)
			.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk());
		
	}
	
}
