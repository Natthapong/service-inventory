package th.co.truemoney.serviceinventory.persona;


import org.junit.Assert;
import org.junit.Test;

import com.tmn.core.api.message.GetBasicProfileResponse;

public class AdamTest {

    @Test
    public void test() {
        Adam adam = new Adam();
        GetBasicProfileResponse basicProfile = adam.getTmnProfileClient().getBasicProfile(null);
        Assert.assertEquals("adam", basicProfile.getFullName());
    }

}
