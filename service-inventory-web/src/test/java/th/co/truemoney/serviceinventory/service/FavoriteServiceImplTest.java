package th.co.truemoney.serviceinventory.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.math.BigDecimal;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import th.co.truemoney.serviceinventory.config.LegacyFacadeConfig;
import th.co.truemoney.serviceinventory.config.LocalAppleUserConfig;
import th.co.truemoney.serviceinventory.config.LocalEnvironmentConfig;
import th.co.truemoney.serviceinventory.config.MemRepositoriesConfig;
import th.co.truemoney.serviceinventory.config.ServiceInventoryConfig;
import th.co.truemoney.serviceinventory.config.TestEnvConfig;
import th.co.truemoney.serviceinventory.ewallet.domain.AccessToken;
import th.co.truemoney.serviceinventory.ewallet.domain.Favorite;
import th.co.truemoney.serviceinventory.ewallet.exception.FailResultCodeException;
import th.co.truemoney.serviceinventory.ewallet.impl.FavoriteServiceImpl;
import th.co.truemoney.serviceinventory.ewallet.repositories.impl.AccessTokenMemoryRepository;
import th.co.truemoney.serviceinventory.exception.ServiceInventoryException;
import th.co.truemoney.serviceinventory.testutils.IntegrationTest;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { ServiceInventoryConfig.class, LegacyFacadeConfig.class, MemRepositoriesConfig.class, LocalEnvironmentConfig.class, LocalAppleUserConfig.class, TestEnvConfig.class })
@ActiveProfiles(profiles={"local", "mem"})
@Category(IntegrationTest.class)
public class FavoriteServiceImplTest {

    private AccessTokenMemoryRepository accessTokenRepo;

    private AccessToken accessToken;

    @Autowired
    private FavoriteServiceImpl favoriteServiceImpl;

    private Favorite favorite;

    @Before
    public void setup() {

        accessTokenRepo = new AccessTokenMemoryRepository();
        accessToken = new AccessToken("12345", "1111", "5555", "AdamTmnMoneyId", "0868185055", "adam@tmn.com", 41);
        accessTokenRepo.save(accessToken);
        accessTokenRepo.save(new AccessToken("0000", "1111", "5555", "EveTmnMoneyId", "0868185055", "eve@tmn.com", 41));
        accessTokenRepo.save(new AccessToken("0001", "1111", "5555", "failUser", "0868185055", "fail@tmn.com", 41));
        favoriteServiceImpl.setAccessTokenRepository(accessTokenRepo);
        favorite = new Favorite(2000L,"billpay","tr","0811234567", "", new BigDecimal(2000));
        // tr, trmv, tmvh, tlp, tic, ti, tcg
    }

    @After
    public void tearDown() {

    }

    @Test
    public void addFavorite(){
        Favorite favoriteResult = favoriteServiceImpl.addFavorite(favorite, "12345");
        assertNotNull(favoriteResult);
    }

    @Test
    public void addFavoriteFailWithWrongTmnIDUser(){
        try{
            favoriteServiceImpl.addFavorite(favorite, "0001");
            fail("Add Favorite Fail with wrong tmnID user");
        }catch(FailResultCodeException e){
            assertEquals("500", e.getCode());
        }
    }

    @Test
    public void addFavoriteFailWithWrongFavoriteServiceCode(){
        try{
            Favorite wrongFavorite = new Favorite(2000L,"billpay","tx","0811234567", "", new BigDecimal(2000));
            favoriteServiceImpl.addFavorite(wrongFavorite, "12345");
            fail("Add Favorite Fail with wrong service code");
        } catch(ServiceInventoryException se) {
            assertEquals("1018", se.getErrorCode());
        }
    }

    @Test
    public void isFavoritable(){
        Boolean isFavorite = favoriteServiceImpl.isFavoritable("serviceType", "trmv", "ref1","12345");
        assertNotNull(isFavorite);
        assertEquals(true, isFavorite);

        isFavorite = favoriteServiceImpl.isFavoritable("serviceType", "tcg", "ref1", "0000");
        assertNotNull(isFavorite);
        assertEquals(false, isFavorite);
    }

    @Test
    public void getListFavorite(){
        List<Favorite> favorites = favoriteServiceImpl.getFavorites("12345");
        assertNotNull(favorites);
        assertEquals(4, favorites.size());
    }
}
