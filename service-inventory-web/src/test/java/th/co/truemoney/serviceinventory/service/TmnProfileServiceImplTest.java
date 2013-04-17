package th.co.truemoney.serviceinventory.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.when;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
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
import th.co.truemoney.serviceinventory.ewallet.domain.ChannelInfo;
import th.co.truemoney.serviceinventory.ewallet.domain.ClientLogin;
import th.co.truemoney.serviceinventory.ewallet.domain.EWalletOwnerLogin;
import th.co.truemoney.serviceinventory.ewallet.impl.TmnProfileServiceImpl;
import th.co.truemoney.serviceinventory.ewallet.repositories.impl.AccessTokenMemoryRepository;
import th.co.truemoney.serviceinventory.exception.ResourceNotFoundException;
import th.co.truemoney.serviceinventory.exception.SignonServiceException;
import th.co.truemoney.serviceinventory.legacyfacade.ewallet.LegacyFacade;
import th.co.truemoney.serviceinventory.legacyfacade.ewallet.ProfileFacade;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { ServiceInventoryConfig.class, MemRepositoriesConfig.class, LocalEnvironmentConfig.class })
@ActiveProfiles(profiles={"local", "mem"})
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
				new EWalletOwnerLogin("user1.test.v1@gmail.com", "secret"),
				new ClientLogin("appKey", "appUser", "appPassword"),
				new ChannelInfo(40, "iphone", "iphone"));

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
			this.profileService.login(new EWalletOwnerLogin("bad.user@gmail.com", "secret"),
				new ClientLogin("appKey", "appUser", "appPassword"),
				new ChannelInfo(40, "iphone", "iphone"));
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
				new EWalletOwnerLogin("user1.test.v1@gmail.com", "secret"),
				new ClientLogin("appKey", "appUser", "appPassword"),
				new ChannelInfo(40, "iphone", "iphone"));

		//when
		profileService.logout(accessTokenID);
	}

	@Test(expected=ResourceNotFoundException.class)
	public void shouldLogoutFailWhenUserWasNeverLogined() {
		profileService.logout(accessToken.getAccessTokenID());
	}

}
