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
import th.co.truemoney.serviceinventory.ewallet.domain.TmnProfile;
import th.co.truemoney.serviceinventory.ewallet.impl.TmnProfileServiceImpl;
import th.co.truemoney.serviceinventory.ewallet.repositories.AccessTokenRepository;
import th.co.truemoney.serviceinventory.legacyfacade.LegacyFacade;
import th.co.truemoney.serviceinventory.legacyfacade.handlers.UserProfileHandler;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { ServiceInventoryConfig.class, MemRepositoriesConfig.class, LocalEnvironmentConfig.class, LocalAppleUserConfig.class })
@ActiveProfiles(profiles={"local", "mem"})
public class GetProfileServiceImplTest {

    @Autowired
    private TmnProfileServiceImpl tmnProfileService;

    @Autowired
    private LegacyFacade legacyFacade;

    private UserProfileHandler mockProfileFacade;
    
    private AccessTokenRepository mockAccessTokenRepo;
    
    @Before
    public void setup() {
    	mockProfileFacade = Mockito.mock(UserProfileHandler.class);
    	mockAccessTokenRepo = Mockito.mock(AccessTokenRepository.class);    	
        legacyFacade.setProfileFacade(mockProfileFacade);
        tmnProfileService.setAccessTokenRepository(mockAccessTokenRepo);
    }  
    
    @Test
    public void shouldGetProfileSuccess() {
    	//stubbed
		TmnProfile stubbedTmnProfile = new TmnProfile();
		stubbedTmnProfile.setFullname("fullname");
		stubbedTmnProfile.setMobileNumber("086xxxxxxx");
		stubbedTmnProfile.setThaiID("1212121212121");		
		stubbedTmnProfile.setHasPassword(Boolean.TRUE);
		stubbedTmnProfile.setHasPin(Boolean.FALSE);
		stubbedTmnProfile.setImageFileName("xxx.jsp");
		
		AccessToken accessToken = new AccessToken("tokenID", "loginID", "sessionID", "tmnID", 41);
		
        //given
        when(mockProfileFacade.getProfile(anyInt(), anyString(), anyString())).thenReturn(stubbedTmnProfile);
        when(mockAccessTokenRepo.findAccessToken(anyString())).thenReturn(accessToken);
        

        //when
        TmnProfile tmnProfile = this.tmnProfileService.getTruemoneyProfile(accessToken.getAccessTokenID());

        //then
        assertNotNull(tmnProfile);
        assertEquals("fullname", tmnProfile.getFullname());
        assertEquals("xxx.jsp", tmnProfile.getImageFileName());
    }
    
}
