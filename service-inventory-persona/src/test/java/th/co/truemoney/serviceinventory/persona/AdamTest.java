package th.co.truemoney.serviceinventory.persona;


import org.junit.Assert;
import org.junit.Test;

import th.co.truemoney.serviceinventory.ewallet.proxy.message.GetBasicProfileResponse;

public class AdamTest {

    @Test
    public void test() {
        Adam adam = new Adam();
        GetBasicProfileResponse basicProfile = adam.getTmnProfile().getBasicProfile(null);
        Assert.assertEquals("adam", basicProfile.getFullName());
    }

}
