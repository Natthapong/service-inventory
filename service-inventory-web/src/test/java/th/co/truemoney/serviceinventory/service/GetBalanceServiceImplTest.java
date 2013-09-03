package th.co.truemoney.serviceinventory.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import th.co.truemoney.serviceinventory.config.EnvConfig;
import th.co.truemoney.serviceinventory.config.LocalEnvironmentConfig;
import th.co.truemoney.serviceinventory.config.MemRepositoriesConfig;
import th.co.truemoney.serviceinventory.config.ServiceInventoryConfig;
import th.co.truemoney.serviceinventory.ewallet.domain.AccessToken;
import th.co.truemoney.serviceinventory.ewallet.impl.TmnProfileServiceImpl;
import th.co.truemoney.serviceinventory.ewallet.repositories.AccessTokenRepository;
import th.co.truemoney.serviceinventory.legacyfacade.LegacyFacade;
import th.co.truemoney.serviceinventory.legacyfacade.handlers.EwalletBalanceHandler;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { ServiceInventoryConfig.class, MemRepositoriesConfig.class, LocalEnvironmentConfig.class, EnvConfig.class })
@ActiveProfiles(profiles={"local", "mem"})
public class GetBalanceServiceImplTest {

    @Autowired
    private TmnProfileServiceImpl tmnProfileService;

    @Autowired
    private LegacyFacade legacyFacade;

    private EwalletBalanceHandler mockEwalletBalanceFacade;
    
    private AccessTokenRepository mockAccessTokenRepo;
    
    @Before
    public void setup() {
    	mockEwalletBalanceFacade = Mockito.mock(EwalletBalanceHandler.class);
    	mockAccessTokenRepo = Mockito.mock(AccessTokenRepository.class);    	
        legacyFacade.setBalanceFacade(mockEwalletBalanceFacade);
        tmnProfileService.setAccessTokenRepository(mockAccessTokenRepo);
    }  
    
    @Test
    public void shouldGetBalanceSuccess() {
    	//stubbed
		BigDecimal stubbedBalance = BigDecimal.TEN;

		AccessToken accessToken = new AccessToken("tokenID", "loginID", "sessionID", "tmnID", 41);
		
        //given
        when(mockEwalletBalanceFacade.getCurrentBalance(anyInt(), anyString(), anyString())).thenReturn(stubbedBalance);
        when(mockAccessTokenRepo.findAccessToken(anyString())).thenReturn(accessToken);
        

        //when
        BigDecimal balance = this.tmnProfileService.getEwalletBalance(accessToken.getAccessTokenID());

        //then
        assertNotNull(balance);
        assertEquals(BigDecimal.TEN, balance);        
    }
    
}
