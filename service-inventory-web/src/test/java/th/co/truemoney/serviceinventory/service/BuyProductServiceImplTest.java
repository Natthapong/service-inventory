package th.co.truemoney.serviceinventory.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
import th.co.truemoney.serviceinventory.buy.domain.BuyProductTransaction;
import th.co.truemoney.serviceinventory.buy.domain.BuyProductTransaction.FailStatus;
import th.co.truemoney.serviceinventory.buy.impl.AsyncBuyProductProcessor;
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
import th.co.truemoney.serviceinventory.exception.ResourceNotFoundException;
import th.co.truemoney.serviceinventory.exception.ServiceInventoryWebException;
import th.co.truemoney.serviceinventory.exception.ServiceInventoryWebException.Code;
import th.co.truemoney.serviceinventory.exception.UnVerifiedOwnerTransactionException;
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
    
    private BuyProductDraft buyProductDraft;
    
	private AsyncBuyProductProcessor asyncBuyProductProcessor;

    @Before
    public void setup() {

        accessTokenRepo = new AccessTokenMemoryRepository();
        transactionRepo = new TransactionRepositoryImpl(new MemoryExpirableMap());

        accessToken = new AccessToken("12345", "1111", "5555", "4444", "0868185055", "tanathip.se@gmail.com", 41);
        accessToken.setClientCredential(new ClientCredential("appKey", "appUser", "appPassword", "channel", "channel detail"));
        accessTokenRepo.save(accessToken);

        BuyProduct buyProduct = new BuyProduct();
		buyProductDraft = new BuyProductDraft("DraftID", buyProduct, new BigDecimal("50"), "transactionID", "epin_c" , "08xxxxxxxx");
        transactionRepo.saveDraftTransaction(buyProductDraft, accessToken.getAccessTokenID());

        mockBuyProductFacade = Mockito.mock(BuyProductHandler.class);

        buyProductServiceImpl.setAccessTokenRepo(accessTokenRepo);
        buyProductServiceImpl.setTransactionRepo(transactionRepo);

        asyncBuyProductProcessor = Mockito.mock(AsyncBuyProductProcessor.class);
        buyProductServiceImpl.setAsyncBuyProductProcessor(asyncBuyProductProcessor);
        
        legacyFacade.setBuyProductFacade(mockBuyProductFacade);
    }

    @Test
    public void createAndVerifyBuyProductDraft() {
        when(mockBuyProductFacade.verifyBuyProduct(any(VerifyBuyRequest.class))).thenReturn(createBuyProduct());

        BuyProductDraft buyProductDraft = buyProductServiceImpl.createAndVerifyBuyProductDraft("epin_c", "08xxxxxxxx", new BigDecimal("50"), "12345");
        assertEquals("transactionID", buyProductDraft.getTransactionID());

        BuyProductDraft buyProductDraftFromRepo = transactionRepo.findDraftTransaction(buyProductDraft.getID(), "12345", BuyProductDraft.class);
        assertNotNull(buyProductDraftFromRepo);
        assertEquals("transactionID", buyProductDraftFromRepo.getTransactionID());
    }
    
    @Test
    public void shouldThrowUnVerifiedExceptionWhenUserSkipsOTPVerify() {

    	try {
    		buyProductServiceImpl.performBuyProduct(buyProductDraft.getID(), accessToken.getAccessTokenID());
    		fail();
    	} catch (UnVerifiedOwnerTransactionException ex) {
    		assertEquals(Code.OWNER_UNVERIFIED, ex.getErrorCode());
    	}

        verify(asyncBuyProductProcessor, Mockito.never()).buyProduct(any(BuyProductTransaction.class), any(AccessToken.class));
    }

    @Test
    public void shouldReturnCorrectStatusWhenGetTransactionStatusGivesGoodStatuses() {

        //given
    	buyProductDraft.setStatus(BuyProductDraft.Status.OTP_CONFIRMED);
    	BuyProductTransaction buyProductTransaction = new BuyProductTransaction(buyProductDraft);
        transactionRepo.saveTransaction(buyProductTransaction, accessToken.getAccessTokenID());

        //given status is verified
        buyProductTransaction.setStatus(BuyProductTransaction.Status.VERIFIED);
        transactionRepo.saveTransaction(buyProductTransaction, accessToken.getAccessTokenID());

        //when status is verified
        BuyProductTransaction.Status status =  buyProductServiceImpl.getBuyProductStatus(buyProductDraft.getID(), accessToken.getAccessTokenID());
        assertEquals(BuyProductTransaction.Status.VERIFIED, status);

        //given status is processing
        buyProductTransaction.setStatus(BuyProductTransaction.Status.PROCESSING);
        transactionRepo.saveTransaction(buyProductTransaction, accessToken.getAccessTokenID());

        //when status is processing
        status =  buyProductServiceImpl.getBuyProductStatus(buyProductDraft.getID(), accessToken.getAccessTokenID());
        assertEquals(BuyProductTransaction.Status.PROCESSING, status);

        //given status is success
        buyProductTransaction.setStatus(BuyProductTransaction.Status.SUCCESS);
        transactionRepo.saveTransaction(buyProductTransaction, accessToken.getAccessTokenID());

        //when status is processing
        status =  buyProductServiceImpl.getBuyProductStatus(buyProductDraft.getID(), accessToken.getAccessTokenID());
        assertEquals(BuyProductTransaction.Status.SUCCESS, status);
    }

    @Test
    public void shouldThrowExceptionWhenGetTransactionStatusGivesBadStatuses() {

        //given
    	buyProductDraft.setStatus(BuyProductDraft.Status.OTP_CONFIRMED);
    	BuyProductTransaction buyProductTransaction = new BuyProductTransaction(buyProductDraft);
        transactionRepo.saveTransaction(buyProductTransaction, accessToken.getAccessTokenID());

        //given status has failed because umarket
        buyProductTransaction.setStatus(BuyProductTransaction.Status.FAILED);
        buyProductTransaction.setFailStatus(BuyProductTransaction.FailStatus.UNKNOWN_FAILED);
        transactionRepo.saveTransaction(buyProductTransaction, accessToken.getAccessTokenID());

        //when
        try {
        	buyProductServiceImpl.getBuyProductStatus(buyProductDraft.getID(), accessToken.getAccessTokenID());
            fail();
        } catch (ServiceInventoryWebException ex) {
            assertEquals(Code.CONFIRM_FAILED, ex.getErrorCode());
        }

        //given status has failed because unknown failure
        buyProductTransaction.setStatus(BuyProductTransaction.Status.FAILED);
        buyProductTransaction.setFailStatus(FailStatus.UNKNOWN_FAILED);
        transactionRepo.saveTransaction(buyProductTransaction, accessToken.getAccessTokenID());

        //when
        try {
        	buyProductServiceImpl.getBuyProductStatus(buyProductDraft.getID(), accessToken.getAccessTokenID());
            fail();
        } catch (ServiceInventoryWebException ex) {
            assertEquals(Code.CONFIRM_FAILED, ex.getErrorCode());
        }
    }

    @Test
    public void shouldThrowResourceNotFoundExceptionWhenGetTransactionStatusWithBadKeys() {
        //given
    	buyProductDraft.setStatus(BuyProductDraft.Status.OTP_CONFIRMED);
        BuyProductTransaction buyProductTransaction = new BuyProductTransaction(buyProductDraft);
        transactionRepo.saveTransaction(buyProductTransaction, accessToken.getAccessTokenID());

        //given status has failed because umarket
        buyProductTransaction.setStatus(BuyProductTransaction.Status.SUCCESS);
        transactionRepo.saveTransaction(buyProductTransaction, accessToken.getAccessTokenID());

        //when using bad trans id
        try {
        	buyProductServiceImpl.getBuyProductStatus("bad trans id", accessToken.getAccessTokenID());
            fail();
        } catch (ResourceNotFoundException ex) {
            assertEquals(Code.TRANSACTION_NOT_FOUND, ex.getErrorCode());
        }

        //when using bad access token
        try {
        	buyProductServiceImpl.getBuyProductStatus(buyProductTransaction.getID(), "bad access token");
            fail();
        } catch (ResourceNotFoundException ex) {
            assertEquals(Code.ACCESS_TOKEN_NOT_FOUND, ex.getErrorCode());
        }
    }
    
	private BuyProduct createBuyProduct() {
		BuyProduct buyProduct = new BuyProduct("transactionID", "transRelation", new BigDecimal("50"));
        return buyProduct;
	}


}
