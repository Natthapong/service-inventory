package th.co.truemoney.serviceinventory.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import th.co.truemoney.serviceinventory.config.LocalAppleUserConfig;
import th.co.truemoney.serviceinventory.config.LocalEnvironmentConfig;
import th.co.truemoney.serviceinventory.config.MemRepositoriesConfig;
import th.co.truemoney.serviceinventory.config.ServiceInventoryConfig;
import th.co.truemoney.serviceinventory.ewallet.domain.AccessToken;
import th.co.truemoney.serviceinventory.ewallet.domain.ChangePin;
import th.co.truemoney.serviceinventory.ewallet.impl.TmnProfileServiceImpl;
import th.co.truemoney.serviceinventory.ewallet.repositories.AccessTokenRepository;
import th.co.truemoney.serviceinventory.legacyfacade.LegacyFacade;
import th.co.truemoney.serviceinventory.legacyfacade.handlers.UserProfileHandler;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { ServiceInventoryConfig.class, MemRepositoriesConfig.class, LocalEnvironmentConfig.class, LocalAppleUserConfig.class })
@ActiveProfiles(profiles={"local", "mem"})
public class ChangeProfileServiceImplTest {

    @Autowired
    private TmnProfileServiceImpl tmnProfileService;

    @Autowired
    private LegacyFacade legacyFacade;

    private UserProfileHandler mockUserProfileFacade;
    
    private AccessTokenRepository mockAccessTokenRepo;
    
    @Before
    public void setup() {
    	mockUserProfileFacade = Mockito.mock(UserProfileHandler.class);
    	mockAccessTokenRepo = Mockito.mock(AccessTokenRepository.class);    	
        legacyFacade.setProfileFacade(mockUserProfileFacade);
        tmnProfileService.setAccessTokenRepository(mockAccessTokenRepo);
    }  
    
    @Test
    public void shouldChangePinSuccess() {
    	//stubbed
		String stubbedMobileNumber = "08xxxxxxxx";
		AccessToken accessToken = new AccessToken("tokenID", "loginID", "sessionID", "tmnID", 41);
		
        //given
        when(mockUserProfileFacade.changePin(anyInt(), anyString(), anyString(), anyString(), anyString())).thenReturn(stubbedMobileNumber);
        when(mockAccessTokenRepo.findAccessToken(anyString())).thenReturn(accessToken);
        
        //when
        String mobileNumber = this.tmnProfileService.changePin(accessToken.getChannelID(), new ChangePin("0000", "1111"), accessToken.getAccessTokenID());

        //then
        assertNotNull(mobileNumber);
        assertEquals("08xxxxxxxx", mobileNumber);
    }
    
}
