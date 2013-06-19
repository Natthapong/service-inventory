package th.co.truemoney.serviceinventory.authen.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

public class AppUserConfigTest {

    private AppleUserConfig appleUserConfig;

    @Before
    public void setup() throws JsonParseException, JsonMappingException, IOException {
    	appleUserConfig = new AppleUserConfig();
    }

    @Test
    public void getAppleUser() {
        AppleUser appleUser = appleUserConfig.getAppleUser("tmn.10000000020");
        assertNotNull(appleUser);
        assertEquals("123456", appleUser.getOtpString());
    }

}
