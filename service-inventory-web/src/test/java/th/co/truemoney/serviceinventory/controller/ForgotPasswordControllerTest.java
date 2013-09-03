package th.co.truemoney.serviceinventory.controller;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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

import th.co.truemoney.serviceinventory.config.MemRepositoriesConfig;
import th.co.truemoney.serviceinventory.config.TestServiceInventoryConfig;
import th.co.truemoney.serviceinventory.config.WebConfig;
import th.co.truemoney.serviceinventory.ewallet.ForgotPasswordService;
import th.co.truemoney.serviceinventory.ewallet.domain.ForgotPassword;
import th.co.truemoney.serviceinventory.ewallet.domain.OTP;
import th.co.truemoney.serviceinventory.ewallet.domain.ResetPassword;
import th.co.truemoney.serviceinventory.ewallet.domain.VerifyResetPassword;
import th.co.truemoney.serviceinventory.exception.ServiceInventoryException;
import th.co.truemoney.serviceinventory.firsthop.config.SmsConfig;

import com.fasterxml.jackson.databind.ObjectMapper;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(classes = { WebConfig.class, MemRepositoriesConfig.class, TestServiceInventoryConfig.class, SmsConfig.class })
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
	public void shouldCreateForgotPasswordSuccess() throws Exception {
		when(forgotPasswordServiceMock.createForgotPassword(anyInt(), any(ForgotPassword.class))).thenReturn(new ForgotPassword());

		ForgotPassword forgotPassword = new ForgotPassword("email@gmail.com", "1212121212121");
		
		this.mockMvc.perform(post("/ewallet/profile/createforgotpassword?channelID={channelID}", 40)
			.contentType(MediaType.APPLICATION_JSON)
			.content(mapper.writeValueAsBytes(forgotPassword)))
			.andExpect(status().isOk());
	}
	
	@Test
	public void shouldCreateForgotPasswordFail() throws Exception {
		when(forgotPasswordServiceMock.createForgotPassword(anyInt(), any(ForgotPassword.class)))
			.thenThrow(new ServiceInventoryException(400,"Error Code","Error Description", "Error Namespace"));

		ForgotPassword forgotPassword = new ForgotPassword("email@gmail.com", "1212121212121");
		
		this.mockMvc.perform(post("/ewallet/profile/createforgotpassword?channelID={channelID}", 40)
			.contentType(MediaType.APPLICATION_JSON)
			.content(mapper.writeValueAsBytes(forgotPassword)))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.errorCode").value("Error Code"))
			.andExpect(jsonPath("$.errorDescription").value("Error Description"))
			.andExpect(jsonPath("$.errorNamespace").value("Error Namespace"));
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
	public void shouldVerifyResetPasswordFail() throws Exception {
		when(forgotPasswordServiceMock.verifyResetPassword(anyInt(), any(ResetPassword.class)))
			.thenThrow(new ServiceInventoryException(400,"Error Code","Error Description", "Error Namespace"));

		ResetPassword resetPassword = new ResetPassword("token", "newPassword");
		
		this.mockMvc.perform(post("/ewallet/profile/password/verify-reset?channelID={channelID}", 40)
			.contentType(MediaType.APPLICATION_JSON)
			.content(mapper.writeValueAsBytes(resetPassword)))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.errorCode").value("Error Code"))
			.andExpect(jsonPath("$.errorDescription").value("Error Description"))
			.andExpect(jsonPath("$.errorNamespace").value("Error Namespace"));
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
	public void shouldVerifyOTPResetPasswordFail() throws Exception {
		when(forgotPasswordServiceMock.verifyOTP(anyInt(), any(VerifyResetPassword.class)))
			.thenThrow(new ServiceInventoryException(400,"Error Code","Error Description", "Error Namespace"));

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
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.errorCode").value("Error Code"))
			.andExpect(jsonPath("$.errorDescription").value("Error Description"))
			.andExpect(jsonPath("$.errorNamespace").value("Error Namespace"));
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
        
		when(forgotPasswordServiceMock.confirmResetPassword(anyInt(), any(ResetPassword.class))).thenReturn(resetPassword.getToken());

		this.mockMvc.perform(post("/ewallet/profile/password/confirm-reset?channelID={channelID}", 40)
			.contentType(MediaType.APPLICATION_JSON)
			.content(mapper.writeValueAsBytes(resetPassword)))
			.andExpect(status().isOk());
	}
	
	@Test
	public void shouldConfirmResetPasswordFail() throws Exception {
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
        
		when(forgotPasswordServiceMock.confirmResetPassword(anyInt(), any(ResetPassword.class)))
			.thenThrow(new ServiceInventoryException(400,"Error Code","Error Description", "Error Namespace"));

		this.mockMvc.perform(post("/ewallet/profile/password/confirm-reset?channelID={channelID}", 40)
			.contentType(MediaType.APPLICATION_JSON)
			.content(mapper.writeValueAsBytes(resetPassword)))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.errorCode").value("Error Code"))
			.andExpect(jsonPath("$.errorDescription").value("Error Description"))
			.andExpect(jsonPath("$.errorNamespace").value("Error Namespace"));
	}
	
	@Test
	public void shouldResendOTPSuccess() throws Exception {
		when(this.forgotPasswordServiceMock.resendOTP(anyInt(), anyString())).thenReturn(new VerifyResetPassword());
		
    	//stubbed
    	ResetPassword resetPassword = new ResetPassword("tokenID", "newPassword");
    	resetPassword.setMobileNumber("0866013468");
    	resetPassword.setLoginID("adam@tmn.com");
    	resetPassword.setTruemoneyID("tmn.0000000010");
    	
        OTP stubbedOTP = new OTP("0866013468", "abcd", "111111");
        VerifyResetPassword verifyResetPassword = new VerifyResetPassword();
        verifyResetPassword.setOtp(stubbedOTP);
        verifyResetPassword.setResetPasswordID(resetPassword.getToken());
        
		this.mockMvc.perform(post("/ewallet/profile/password/resend-otp/{resetPasswordID}?channelID={channelID}", "resetPasswordID", 40)
			.contentType(MediaType.APPLICATION_JSON)
			.content(mapper.writeValueAsBytes(verifyResetPassword)))
			.andExpect(status().isOk());
	}
	
	@Test
	public void shouldResendOTPFailNotSendChannelID() throws Exception {
		when(this.forgotPasswordServiceMock.resendOTP(anyInt(), anyString())).thenReturn(new VerifyResetPassword());
		
    	//stubbed
    	ResetPassword resetPassword = new ResetPassword("tokenID", "newPassword");
    	resetPassword.setMobileNumber("0866013468");
    	resetPassword.setLoginID("adam@tmn.com");
    	resetPassword.setTruemoneyID("tmn.0000000010");
    	
        OTP stubbedOTP = new OTP("0866013468", "abcd", "111111");
        VerifyResetPassword verifyResetPassword = new VerifyResetPassword();
        verifyResetPassword.setOtp(stubbedOTP);
        verifyResetPassword.setResetPasswordID(resetPassword.getToken());
        
		this.mockMvc.perform(post("/ewallet/profile/password/resend-otp/{resetPasswordID}", "resetPasswordID")
			.contentType(MediaType.APPLICATION_JSON)
			.content(mapper.writeValueAsBytes(verifyResetPassword)))
			.andExpect(status().is(412));
	}
}
