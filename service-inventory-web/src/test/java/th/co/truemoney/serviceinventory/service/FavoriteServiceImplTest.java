package th.co.truemoney.serviceinventory.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;

import org.hibernate.validator.constraints.Length;
import org.junit.Before;
import org.junit.Ignore;
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
import th.co.truemoney.serviceinventory.ewallet.domain.Favorite;
import th.co.truemoney.serviceinventory.ewallet.exception.FailResultCodeException;
import th.co.truemoney.serviceinventory.ewallet.impl.FavoriteServiceImpl;
import th.co.truemoney.serviceinventory.ewallet.repositories.impl.AccessTokenMemoryRepository;
import th.co.truemoney.serviceinventory.exception.ServiceInventoryException;
import th.co.truemoney.serviceinventory.legacyfacade.facade.LegacyFacade;
import th.co.truemoney.serviceinventory.legacyfacade.facade.ProfileFacade;
import th.co.truemoney.serviceinventory.legacyfacade.facade.LegacyFacade.UserProfileBuilder;
import th.co.truemoney.serviceinventory.testutils.IntegrationTest;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { ServiceInventoryConfig.class, MemRepositoriesConfig.class, LocalEnvironmentConfig.class })
@ActiveProfiles(profiles={"local", "mem"})
@Category(IntegrationTest.class)
public class FavoriteServiceImplTest {
	
	private AccessTokenMemoryRepository accessTokenRepo;
	
	private AccessToken accessToken;
	
	@Autowired
	private LegacyFacade legacyFacade;
	
	@Autowired
	private FavoriteServiceImpl favoriteServiceImpl;
	
	private Favorite favorite;
	
	@Before
	public void setup() {
		accessTokenRepo = new AccessTokenMemoryRepository();
		accessToken = new AccessToken("12345", "5555", "AdamTmnMoneyId", "0868185055", "adam@tmn.com", 41);
		accessTokenRepo.save(accessToken);
		accessTokenRepo.save(new AccessToken("0000", "5555", "Not", "0868185055", "adam@tmn.com", 41));
		favoriteServiceImpl.setAccessTokenRepository(accessTokenRepo);
		favorite = new Favorite(2000L,"serviceType","101","102",new BigDecimal(2000));
	}
	
	
	@Test
	public void addFavorite(){
		Favorite favoriteResult = favoriteServiceImpl.addFavorite(favorite, "12345");
		assertNotNull(favoriteResult);
	}
	
	@Test
	public void addFavoriteFail(){
		try{
			Favorite favoriteResult = favoriteServiceImpl.addFavorite(favorite, "0000");
			fail("Add Favorite Fail");
		}catch(FailResultCodeException e){
			assertEquals("500", e.getCode());
		}
	}
	
	@Test
	public void isFavorite(){
		Boolean isFavorite = favoriteServiceImpl.isFavoritable("serviceType", "serviceCode", "ref1","12345");
		assertNotNull(isFavorite);
		assertEquals(true, isFavorite);
		
		isFavorite = favoriteServiceImpl.isFavoritable("serviceType", "serviceCode", "ref1","0000");
		assertNotNull(isFavorite);
		assertEquals(false, isFavorite);
	}
	
	@Test 
	public void getListFavorite(){
		List<Favorite> favorites = favoriteServiceImpl.getFavorites("12345");
		assertNotNull(favorites);
	}
}
