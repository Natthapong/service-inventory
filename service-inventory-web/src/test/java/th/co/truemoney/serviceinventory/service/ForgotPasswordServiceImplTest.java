package th.co.truemoney.serviceinventory.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import th.co.truemoney.serviceinventory.config.EnvConfig;
import th.co.truemoney.serviceinventory.config.LocalEnvironmentConfig;
import th.co.truemoney.serviceinventory.config.MemRepositoriesConfig;
import th.co.truemoney.serviceinventory.config.ServiceInventoryConfig;
import th.co.truemoney.serviceinventory.ewallet.domain.OTP;
import th.co.truemoney.serviceinventory.ewallet.domain.ResetPassword;
import th.co.truemoney.serviceinventory.ewallet.domain.VerifyResetPassword;
import th.co.truemoney.serviceinventory.ewallet.impl.ForgotPasswordServiceImpl;
import th.co.truemoney.serviceinventory.ewallet.repositories.ForgotPasswordRepository;
import th.co.truemoney.serviceinventory.ewallet.repositories.OTPRepository;
import th.co.truemoney.serviceinventory.legacyfacade.LegacyFacade;
import th.co.truemoney.serviceinventory.legacyfacade.handlers.ForgotPasswordHandler;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { ServiceInventoryConfig.class, MemRepositoriesConfig.class, LocalEnvironmentConfig.class, EnvConfig.class })
@ActiveProfiles(profiles={"local", "mem"})
public class ForgotPasswordServiceImplTest {

    @Autowired
    private ForgotPasswordServiceImpl forgotPasswordService;

    @Autowired
    private LegacyFacade legacyFacade;

    private ForgotPasswordHandler mockForgotPasswordFacade;
    
    private OTPRepository mockOTPRepository;
    
    private ForgotPasswordRepository mockForgotPasswordRepository;
    
    @Before
    public void setup() {
    	mockForgotPasswordFacade = Mockito.mock(ForgotPasswordHandler.class);
    	mockOTPRepository = Mockito.mock(OTPRepository.class);
    	mockForgotPasswordRepository = Mockito.mock(ForgotPasswordRepository.class);
        legacyFacade.setForgotPasswordFacade(mockForgotPasswordFacade);
        forgotPasswordService.setForgotPasswordRepository(mockForgotPasswordRepository);
    }

    @Test
    public void shouldVerifyResetPasswordSuccess() {
    	//stubbed
    	ResetPassword resetPassword = new ResetPassword("tokenID", "newPassword");
    	resetPassword.setMobileNumber("0866013468");
    	resetPassword.setLoginID("adam@tmn.com");
    	resetPassword.setTruemoneyID("tmn.0000000010");
    	
        //given
        when(mockForgotPasswordFacade.verifyResetPassword(anyInt(), anyString())).thenReturn(resetPassword);

        //when
        VerifyResetPassword result = this.forgotPasswordService.verifyResetPassword(40, resetPassword);

        //then
        assertNotNull(result);
        assertEquals("tokenID", result.getResetPasswordID());

    }
    
    @Test
    public void shouldConfirmResetPasswordSuccess() {
    	
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
        
        when(mockOTPRepository.findOTPByRefCode(anyString(), anyString())).thenReturn(stubbedOTP);
        when(mockForgotPasswordRepository.findResetPassword(anyString())).thenReturn(resetPassword);     
        
        //when
        this.forgotPasswordService.confirmResetPassword(40, resetPassword);

        verify(mockForgotPasswordFacade).confirmResetPassword(anyInt(), anyString(), anyString(), anyString());
    }

}
