package th.co.truemoney.serviceinventory.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
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

import th.co.truemoney.serviceinventory.config.LocalEnvironmentConfig;
import th.co.truemoney.serviceinventory.config.MemRepositoriesConfig;
import th.co.truemoney.serviceinventory.config.ServiceInventoryConfig;
import th.co.truemoney.serviceinventory.ewallet.domain.AccessToken;
import th.co.truemoney.serviceinventory.ewallet.domain.ClientCredential;
import th.co.truemoney.serviceinventory.ewallet.domain.EWalletOwnerCredential;
import th.co.truemoney.serviceinventory.ewallet.impl.TmnProfileServiceImpl;
import th.co.truemoney.serviceinventory.ewallet.repositories.impl.AccessTokenMemoryRepository;
import th.co.truemoney.serviceinventory.exception.ResourceNotFoundException;
import th.co.truemoney.serviceinventory.exception.SignonServiceException;
import th.co.truemoney.serviceinventory.legacyfacade.facade.LegacyFacade;
import th.co.truemoney.serviceinventory.legacyfacade.facade.ProfileFacade;
import th.co.truemoney.serviceinventory.testutils.IntegrationTest;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { ServiceInventoryConfig.class, MemRepositoriesConfig.class, LocalEnvironmentConfig.class })
@ActiveProfiles(profiles={"local", "mem"})
@Category(IntegrationTest.class)
public class TmnProfileServiceImplTest {

    @Autowired
    private TmnProfileServiceImpl profileService;

    @Autowired
    private LegacyFacade legacyFacade;

    private ProfileFacade mockProfileFacade;

    private AccessToken accessToken = new AccessToken("tokenID", "sessionID", "tmnID", 41);

    @Before
    public void setup() {

        mockProfileFacade = Mockito.mock(ProfileFacade.class);
        legacyFacade.setProfileFacade(mockProfileFacade);

        profileService.setAccessTokenRepository(new AccessTokenMemoryRepository());
    }


    @Test
    public void shouldReturnAccessTokenWhenLoginSuccess() {

        //given
        when(mockProfileFacade.login(40, "user1.test.v1@gmail.com", "secret")).thenReturn(accessToken);

        //when
        String result = this.profileService.login(
                new EWalletOwnerCredential("user1.test.v1@gmail.com", "secret", 40),
                new ClientCredential("appKey", "appUser", "appPassword"));

        //then
        assertNotNull(result);
        assertEquals("tokenID", result);

    }

    @Test
    public void shouldThrowExceptionWhenLoginFail() {

        //given
        when(mockProfileFacade.login(40, "bad.user@gmail.com", "secret")).thenThrow(new SignonServiceException("401", "bad login"));

        //when
        try {
            this.profileService.login(new EWalletOwnerCredential("bad.user@gmail.com", "secret", 40),
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
        when(mockProfileFacade.login(40, "user1.test.v1@gmail.com", "secret")).thenReturn(accessToken);

        String accessTokenID = this.profileService.login(
                new EWalletOwnerCredential("user1.test.v1@gmail.com", "secret", 40),
                new ClientCredential("appKey", "appUser", "appPassword"));

        //when
        profileService.logout(accessTokenID);
    }

    @Test(expected=ResourceNotFoundException.class)
    public void shouldLogoutFailWhenUserWasNeverLogined() {
        profileService.logout(accessToken.getAccessTokenID());
    }

}
