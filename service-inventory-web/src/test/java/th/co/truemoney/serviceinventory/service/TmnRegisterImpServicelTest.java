package th.co.truemoney.serviceinventory.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.rmi.RemoteException;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import th.co.truemoney.serviceinventory.config.LocalEnvironmentConfig;
import th.co.truemoney.serviceinventory.config.MemRepositoriesConfig;
import th.co.truemoney.serviceinventory.config.ServiceInventoryConfig;
import th.co.truemoney.serviceinventory.email.EmailService;
import th.co.truemoney.serviceinventory.ewallet.domain.OTP;
import th.co.truemoney.serviceinventory.ewallet.domain.TmnProfile;
import th.co.truemoney.serviceinventory.ewallet.impl.TmnProfileServiceImpl;
import th.co.truemoney.serviceinventory.ewallet.repositories.RegisteringProfileRepository;
import th.co.truemoney.serviceinventory.exception.ServiceInventoryWebException;
import th.co.truemoney.serviceinventory.exception.ServiceInventoryWebException.Code;
import th.co.truemoney.serviceinventory.sms.OTPService;
import th.co.truemoney.serviceinventory.testutils.IntegrationTest;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { ServiceInventoryConfig.class, MemRepositoriesConfig.class, LocalEnvironmentConfig.class })
@ActiveProfiles(profiles={"local", "mem"})
@Category(IntegrationTest.class)
public class TmnRegisterImpServicelTest {

    @Autowired
    private TmnProfileServiceImpl tmnProfileServiceImpl;

    @Autowired
    private RegisteringProfileRepository profileRepo;


    private OTPService otpService;
    private EmailService emailService;

    @Before
    public void setup() {

        otpService = mock(OTPService.class);
        emailService = mock(EmailService.class);

        tmnProfileServiceImpl.setOtpService(otpService);
        tmnProfileServiceImpl.setEmailService(emailService);
    }

    @Test
    public void validateEmailSuccess() throws RemoteException {

        String email = "user1.test.v1@gmail.com";
        String result = tmnProfileServiceImpl.validateEmail(41, email);

        assertEquals(result , email);
    }

    @Test
    public void createRegisterProfileShouldSendOTP() throws Exception {

        TmnProfile registeringProfile = new TmnProfile();
        registeringProfile.setMobileNumber("0861234567");

        tmnProfileServiceImpl.createProfile(41, registeringProfile);

        verify(otpService).send("0861234567");
    }

    @SuppressWarnings("unchecked")
    @Test
    public void confirmProfileUsingCorrectOTPShouldCreateNewProfileAndSendWelcomeEmail() throws Exception {
        OTP otp = new OTP("0861234567", "refCode", "otpPin");

        TmnProfile registeringProfile = new TmnProfile();
        registeringProfile.setMobileNumber("0861234567");

        profileRepo.saveRegisteringProfile(registeringProfile);

        TmnProfile tmnProfile =  tmnProfileServiceImpl.confirmCreateProfile(41, otp);

        assertNotNull(tmnProfile);
        verify(otpService).isValidOTP(otp);
        verify(emailService).sendWelcomeEmail(anyString(), Mockito.anyMap());
    }

    @SuppressWarnings("unchecked")
    @Test
    public void confirmProfileUsingBadOTPShouldNotCreateNewProfileAndSendWelcomeEmail() throws Exception {
        OTP otp = new OTP("0861234567", "refCode", "otpPin");

        TmnProfile registeringProfile = new TmnProfile();
        registeringProfile.setMobileNumber("0861234567");

        profileRepo.saveRegisteringProfile(registeringProfile);

        Mockito.doThrow(new ServiceInventoryWebException(Code.INVALID_OTP, "invalid OTP."))
               .when(otpService).isValidOTP(otp);

        try {
            tmnProfileServiceImpl.confirmCreateProfile(41, otp);
            Assert.fail();
        } catch (ServiceInventoryWebException e) {
            Assert.assertEquals(Code.INVALID_OTP, e.getErrorCode());
        }

        verify(emailService, Mockito.never()).sendWelcomeEmail(anyString(), Mockito.anyMap());
    }
}
