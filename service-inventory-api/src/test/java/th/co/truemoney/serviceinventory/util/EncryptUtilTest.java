package th.co.truemoney.serviceinventory.util;


import org.junit.Assert;
import org.junit.Test;

public class EncryptUtilTest {

    @Test
    public void encrypeSuccess() {
        String actual = EncryptUtil.buildHmacSignature("test private key", "value");
        Assert.assertEquals("c9b3e4e330290384ec71913e6bb69fc10d310f20", actual);
    }

}
