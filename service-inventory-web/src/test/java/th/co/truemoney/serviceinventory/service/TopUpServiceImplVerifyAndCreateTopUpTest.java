package th.co.truemoney.serviceinventory.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Arrays;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import th.co.truemoney.serviceinventory.config.LocalEnvironmentConfig;
import th.co.truemoney.serviceinventory.config.MemRepositoriesConfig;
import th.co.truemoney.serviceinventory.config.ServiceInventoryConfig;
import th.co.truemoney.serviceinventory.ewallet.domain.AccessToken;
import th.co.truemoney.serviceinventory.ewallet.domain.DirectDebit;
import th.co.truemoney.serviceinventory.ewallet.domain.TopUpQuote;
import th.co.truemoney.serviceinventory.ewallet.impl.TopUpServiceImpl;
import th.co.truemoney.serviceinventory.ewallet.repositories.AccessTokenRepository;
import th.co.truemoney.serviceinventory.exception.ServiceInventoryWebException;
import th.co.truemoney.serviceinventory.legacyfacade.ewallet.LegacyFacade;
import th.co.truemoney.serviceinventory.legacyfacade.ewallet.SourceOfFundFacade;
import th.co.truemoney.serviceinventory.testutils.IntegrationTest;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { ServiceInventoryConfig.class, MemRepositoriesConfig.class, LocalEnvironmentConfig.class })
@ActiveProfiles(profiles={"local", "mem"})
@Category(IntegrationTest.class)
public class TopUpServiceImplVerifyAndCreateTopUpTest {

    //unit under test
    @Autowired
    private TopUpServiceImpl topUpService;

    @Autowired
    private LegacyFacade legacyFacade;

    @Autowired
    private AccessTokenRepository accessTokenRepo;

    private SourceOfFundFacade sofFacadeMock;

    private AccessToken accessToken = new AccessToken("tokenID", "sessionID", "tmnID", 41);
    private DirectDebit userDirectDebit = new DirectDebit("sofID", "debit");

    @Before
    public void setup() {

        //given
        sofFacadeMock = mock(SourceOfFundFacade.class);
        legacyFacade.setSourceOfFundFacade(sofFacadeMock);

        accessTokenRepo.save(accessToken);

        when(sofFacadeMock.getAllDirectDebitSourceOfFunds(anyInt(), anyString(), anyString()))
            .thenReturn(Arrays.asList(userDirectDebit));

    }

    @Test
    public void verifyAndCreateTopUpQuoteSuccess() {

        //given
        BigDecimal amount = new BigDecimal(400);
        //when
        TopUpQuote topupQuote = this.topUpService.createAndVerifyTopUpQuote(userDirectDebit.getSourceOfFundID(), amount, accessToken.getAccessTokenID());

        //then
        assertNotNull(topupQuote);
    }

    @Test
    public void verifyAndCreateTopUpQuoteSuccessFailLessThanMinAmount() {

        //given
        BigDecimal topUpAmount = new BigDecimal(30);
        userDirectDebit.setMinAmount(new BigDecimal(300));

        //when
        try {
            this.topUpService.createAndVerifyTopUpQuote(userDirectDebit.getSourceOfFundID(), topUpAmount, accessToken.getAccessTokenID());
            Assert.fail();
        } catch (ServiceInventoryWebException e) {
            //then
            assertEquals("20001", e.getErrorCode());
        }
    }

    @Test
    public void verifyAndCreateTopUpQuoteSuccessFailMostThanMaxAmount() {

        //given
        BigDecimal topUpAmount = new BigDecimal(50000);
        userDirectDebit.setMaxAmount(new BigDecimal(30000));

        //when
        try {
            this.topUpService.createAndVerifyTopUpQuote(userDirectDebit.getSourceOfFundID(), topUpAmount, accessToken.getAccessTokenID());
            Assert.fail();
        } catch (ServiceInventoryWebException e) {
            //then
            assertEquals("20002", e.getErrorCode());
        }
    }

}
