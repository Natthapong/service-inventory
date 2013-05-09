package th.co.truemoney.serviceinventory.ewallet.client.workflows;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import th.co.truemoney.serviceinventory.ewallet.client.FavoriteServicesClient;
import th.co.truemoney.serviceinventory.ewallet.client.TmnProfileServiceClient;
import th.co.truemoney.serviceinventory.ewallet.client.config.LocalEnvironmentConfig;
import th.co.truemoney.serviceinventory.ewallet.client.config.ServiceInventoryClientConfig;
import th.co.truemoney.serviceinventory.ewallet.client.testutils.IntegrationTest;
import th.co.truemoney.serviceinventory.ewallet.client.testutils.TestData;
import th.co.truemoney.serviceinventory.ewallet.domain.Favorite;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { ServiceInventoryClientConfig.class, LocalEnvironmentConfig.class })
@ActiveProfiles(profiles = "local")
@Category(IntegrationTest.class)
public class FavoriteServiceWorkflowTest {
	@Autowired
	FavoriteServicesClient client;
	
	@Autowired
	TmnProfileServiceClient profileService;
	
	@Test
	public void addFavorite() {
		String accessTokenID = profileService.login(
				TestData.createSuccessUserLogin(),
				TestData.createSuccessClientLogin());
		
		Favorite favorite = new Favorite();
		
		Favorite favoriteResponse = client.addFavorite(favorite, accessTokenID);
		assertNotNull(favoriteResponse);
		
		assertEquals(new Long(1000), favoriteResponse.getFavoriteID());
	}
	
	@Test
	public void getFavorites() {
		String accessTokenID = profileService.login(
				TestData.createSuccessUserLogin(),
				TestData.createSuccessClientLogin());

		assertNotNull(accessTokenID);
		
		List<Favorite> favorites = client.getFavorites("billpay", accessTokenID);
		assertNotNull(favorites);
		assertEquals(0, favorites.size());
		
		favorites = client.getFavorites(null, accessTokenID);
		assertNotNull(favorites);
		assertEquals(0, favorites.size());
	}
}
