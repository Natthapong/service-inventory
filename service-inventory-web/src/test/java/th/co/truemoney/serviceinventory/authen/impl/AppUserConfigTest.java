package th.co.truemoney.serviceinventory.authen.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import th.co.truemoney.serviceinventory.config.LocalAppleUserConfig;
import th.co.truemoney.serviceinventory.config.LocalEnvironmentConfig;
import th.co.truemoney.serviceinventory.config.MemRepositoriesConfig;
import th.co.truemoney.serviceinventory.config.ServiceInventoryConfig;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { ServiceInventoryConfig.class, LocalEnvironmentConfig.class, MemRepositoriesConfig.class, LocalAppleUserConfig.class })
@ActiveProfiles(profiles={"local", "mem"})
public class AppUserConfigTest {
    
    @Autowired
    private AppleUserMap appleUserMap;
    
    @Test
    public void getLocalAppleUser() {
        assertNotNull(appleUserMap);
        assertNotNull(appleUserMap.getAppleUsers().get("tmn.10000000020"));
        assertEquals("123456", appleUserMap.getAppleUsers().get("tmn.10000000020").getOtpString());
    }

}
