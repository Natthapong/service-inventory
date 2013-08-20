package th.co.truemoney.serviceinventory.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;

import java.math.BigDecimal;

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import th.co.truemoney.serviceinventory.buy.domain.BuyProduct;
import th.co.truemoney.serviceinventory.buy.domain.BuyProductDraft;
import th.co.truemoney.serviceinventory.buy.impl.BuyProductServiceImpl;
import th.co.truemoney.serviceinventory.config.LocalAppleUserConfig;
import th.co.truemoney.serviceinventory.config.LocalEnvironmentConfig;
import th.co.truemoney.serviceinventory.config.MemRepositoriesConfig;
import th.co.truemoney.serviceinventory.config.ServiceInventoryConfig;
import th.co.truemoney.serviceinventory.dao.impl.MemoryExpirableMap;
import th.co.truemoney.serviceinventory.engine.client.domain.services.VerifyBuyRequest;
import th.co.truemoney.serviceinventory.ewallet.domain.AccessToken;
import th.co.truemoney.serviceinventory.ewallet.domain.ClientCredential;
import th.co.truemoney.serviceinventory.ewallet.repositories.impl.AccessTokenMemoryRepository;
import th.co.truemoney.serviceinventory.ewallet.repositories.impl.TransactionRepositoryImpl;
import th.co.truemoney.serviceinventory.legacyfacade.LegacyFacade;
import th.co.truemoney.serviceinventory.legacyfacade.handlers.BuyProductHandler;
import th.co.truemoney.serviceinventory.testutils.IntegrationTest;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { ServiceInventoryConfig.class, MemRepositoriesConfig.class, LocalEnvironmentConfig.class, LocalAppleUserConfig.class })
@ActiveProfiles(profiles={"local", "mem"})
@Category(IntegrationTest.class)
public class BuyProductServiceImplTest {

    private AccessTokenMemoryRepository accessTokenRepo;

    private TransactionRepositoryImpl transactionRepo;

    private AccessToken accessToken;

    @Autowired
    private BuyProductServiceImpl buyProductServiceImpl;

    @Autowired
    private LegacyFacade legacyFacade;

    private BuyProductHandler mockBuyProductFacade;

    @Before
    public void setup() {

        accessTokenRepo = new AccessTokenMemoryRepository();
        transactionRepo = new TransactionRepositoryImpl(new MemoryExpirableMap());

        accessToken = new AccessToken("12345", "1111", "5555", "4444", "0868185055", "tanathip.se@gmail.com", 41);
        accessToken.setClientCredential(new ClientCredential("appKey", "appUser", "appPassword", "channel", "channel detail"));
        accessTokenRepo.save(accessToken);

        BuyProduct buyProduct = new BuyProduct();
		BuyProductDraft buyProductDraft = new BuyProductDraft("DraftID", buyProduct, new BigDecimal("50"), "transactionID", "epin_c" , "08xxxxxxxx");
        transactionRepo.saveDraftTransaction(buyProductDraft, accessToken.getAccessTokenID());

        mockBuyProductFacade = Mockito.mock(BuyProductHandler.class);

        buyProductServiceImpl.setAccessTokenRepo(accessTokenRepo);
        buyProductServiceImpl.setTransactionRepo(transactionRepo);

        legacyFacade.setBuyProductFacade(mockBuyProductFacade);
    }

    @Test
    public void createAndVerifyBuyProductDraft() {
        Mockito.when(mockBuyProductFacade.verifyBuyProduct(any(VerifyBuyRequest.class))).thenReturn(createBuyProduct());

        BuyProductDraft buyProductDraft = buyProductServiceImpl.createAndVerifyBuyProductDraft("epin_c", "08xxxxxxxx", new BigDecimal("50"), "12345");
        assertEquals("transactionID", buyProductDraft.getTransactionID());

        BuyProductDraft buyProductDraftFromRepo = transactionRepo.findDraftTransaction(buyProductDraft.getID(), "12345", BuyProductDraft.class);
        assertNotNull(buyProductDraftFromRepo);
        assertEquals("transactionID", buyProductDraftFromRepo.getTransactionID());
    }

	private BuyProduct createBuyProduct() {
		BuyProduct buyProduct = new BuyProduct("transactionID", "transRelation", new BigDecimal("50"));
        return buyProduct;
	}


}
