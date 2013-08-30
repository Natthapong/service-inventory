package th.co.truemoney.serviceinventory.service;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;

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

import th.co.truemoney.serviceinventory.config.LocalAppleUserConfig;
import th.co.truemoney.serviceinventory.config.LocalEnvironmentConfig;
import th.co.truemoney.serviceinventory.config.MemRepositoriesConfig;
import th.co.truemoney.serviceinventory.config.ServiceInventoryConfig;
import th.co.truemoney.serviceinventory.config.TestEnvConfig;
import th.co.truemoney.serviceinventory.ewallet.domain.AccessToken;
import th.co.truemoney.serviceinventory.ewallet.domain.ClientCredential;
import th.co.truemoney.serviceinventory.ewallet.domain.EWalletOwnerCredential;
import th.co.truemoney.serviceinventory.ewallet.domain.TmnProfile;
import th.co.truemoney.serviceinventory.ewallet.impl.TmnProfileServiceImpl;
import th.co.truemoney.serviceinventory.ewallet.repositories.AccessTokenRepository;
import th.co.truemoney.serviceinventory.ewallet.repositories.impl.AccessTokenMemoryRepository;
import th.co.truemoney.serviceinventory.exception.ResourceNotFoundException;
import th.co.truemoney.serviceinventory.exception.SignonServiceException;
import th.co.truemoney.serviceinventory.legacyfacade.LegacyFacade;
import th.co.truemoney.serviceinventory.legacyfacade.handlers.UserProfileHandler;
import th.co.truemoney.serviceinventory.testutils.IntegrationTest;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { ServiceInventoryConfig.class, MemRepositoriesConfig.class, LocalEnvironmentConfig.class, LocalAppleUserConfig.class, TestEnvConfig.class })
@ActiveProfiles(profiles={"local", "mem"})
@Category(IntegrationTest.class)
public class TmnProfileServiceImplTest {

	private Integer CHANNEL_ID = 41;

	@Autowired
    private TmnProfileServiceImpl profileService;

    @Autowired
    private LegacyFacade legacyFacade;

    private UserProfileHandler mockProfileFacade;

    private AccessToken accessToken = new AccessToken("tokenID", "loginID", "sessionID", "tmnID", CHANNEL_ID);

    private AccessTokenRepository accessTokenRepo = new AccessTokenMemoryRepository();
    
    
    @Before
    public void setup() {
        mockProfileFacade = Mockito.mock(UserProfileHandler.class);
        legacyFacade.setProfileFacade(mockProfileFacade);
        profileService.setAccessTokenRepository(accessTokenRepo);
    }

    @Before
    public void setupAccessTokenRepo() {
    	accessTokenRepo.save(accessToken);
    }
    
    @Test
    public void shouldReturnAccessTokenWhenLoginSuccess() {

        //given
        when(mockProfileFacade.login(CHANNEL_ID, "user1.test.v1@gmail.com", "secret")).thenReturn(accessToken);

        //when
        String result = this.profileService.login(
                new EWalletOwnerCredential("user1.test.v1@gmail.com", "secret", CHANNEL_ID),
                new ClientCredential("appKey", "appUser", "appPassword"));

        //then
        assertNotNull(result);
        assertEquals("tokenID", result);

    }

    @Test
    public void shouldThrowExceptionWhenLoginFail() {

        //given
        when(mockProfileFacade.login(CHANNEL_ID, "bad.user@gmail.com", "secret")).thenThrow(new SignonServiceException("401", "bad login"));

        //when
        try {
            this.profileService.login(new EWalletOwnerCredential("bad.user@gmail.com", "secret", CHANNEL_ID),
                new ClientCredential("appKey", "appUser", "appPassword"));
            Assert.fail();
        } catch (SignonServiceException ex) {
            assertEquals("401", ex.getErrorCode());
            assertEquals("bad login", ex.getErrorDescription());
        }
    }

    @Test
    public void shouldLogoutSuccessWhenUserWasLogined() {

        //given
        when(mockProfileFacade.login(CHANNEL_ID, "user1.test.v1@gmail.com", "secret")).thenReturn(accessToken);

        String accessTokenID = this.profileService.login(
                new EWalletOwnerCredential("user1.test.v1@gmail.com", "secret", CHANNEL_ID),
                new ClientCredential("appKey", "appUser", "appPassword"));

        //when
        profileService.logout(accessTokenID);
    }

    @Test(expected=ResourceNotFoundException.class)
    public void shouldLogoutFailWhenUserWasNeverLogined() {
        profileService.logout("invalid-token-id");
    }
    
    @Test
    public void shouldChangeProfileImageStatusSuccess() {
    	
    	Boolean imageStatus = Boolean.FALSE;
    	TmnProfile returnedProfile = new TmnProfile();
    	
    	//given
    	Mockito.doNothing().when(mockProfileFacade).changeProfileImageStatus(eq(CHANNEL_ID), eq("sessionID"), eq("tmnID"), eq(imageStatus));
    	when(mockProfileFacade.getProfile(eq(CHANNEL_ID), eq("sessionID"), eq("tmnID"))).thenReturn(returnedProfile);
    	
    	TmnProfile profile = this.profileService.changeProfileImageStatus("tokenID", imageStatus);
    	assertThat(profile, equalTo(returnedProfile));

    	Mockito.verify(mockProfileFacade).changeProfileImageStatus(eq(CHANNEL_ID), eq("sessionID"), eq("tmnID"), eq(imageStatus));
    	Mockito.verify(mockProfileFacade).getProfile(eq(CHANNEL_ID), eq("sessionID"), eq("tmnID"));
    }
    
}
