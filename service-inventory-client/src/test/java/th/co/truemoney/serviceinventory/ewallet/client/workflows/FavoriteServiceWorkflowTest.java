package th.co.truemoney.serviceinventory.ewallet.client.workflows;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Assert;
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
import th.co.truemoney.serviceinventory.exception.ServiceInventoryException;

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
                TestData.createAdamSuccessLogin(),
                TestData.createSuccessClientLogin());

        Favorite favoriteBill = TestData.createFavoriteBill();

        Favorite favoriteResponse = client.addFavorite(favoriteBill, accessTokenID);
        assertNotNull(favoriteResponse);

        assertEquals("555", favoriteResponse.getRef1());
        //assertEquals(new Long(2000), favoriteResponse.getFavoriteID());
    }

    @Test
    public void shouldSuccessRemoveFavorite(){
        String accessTokenID = profileService.login(
                TestData.createAdamSuccessLogin(),
                TestData.createSuccessClientLogin());

        Favorite favoriteBill = TestData.createFavoriteBill();

        Favorite favoriteResponse = client.addFavorite(favoriteBill, accessTokenID);
        assertNotNull(favoriteResponse);

        assertEquals("555", favoriteResponse.getRef1());

        Boolean result = client.deleteFavorite(favoriteBill.getServiceCode(), favoriteBill.getRef1(), accessTokenID);

        assertEquals(true, result);
    }

    @Test
    public void ShouldFailRemoveFavorite(){
        String accessTokenID = profileService.login(
                TestData.createEveSuccessLogin(),
                TestData.createSuccessClientLogin());

        Favorite favoriteBill = TestData.createNotFavoriteBill();
        try{
            client.deleteFavorite(favoriteBill.getServiceCode(), favoriteBill.getRef1(), accessTokenID);
            Assert.fail();
        }catch(ServiceInventoryException e){
            assertEquals("xxx", e.getErrorCode());
        }
    }

    @Test
    public void getFavorites() {
        String accessTokenID = profileService.login(
                TestData.createSuccessUserLogin(),
                TestData.createSuccessClientLogin());

        assertNotNull(accessTokenID);

        List<Favorite> favorites = client.getFavorites(accessTokenID);
        assertNotNull(favorites);
        assertEquals(3, favorites.size());

        favorites = client.getFavorites(accessTokenID);
        assertNotNull(favorites);
        assertEquals(3, favorites.size());
    }

}
