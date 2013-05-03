package th.co.truemoney.serviceinventory.util;

import static org.junit.Assert.assertNotNull;
import org.junit.Assert;
import org.junit.Test;

public class BasicEncryptUtilTest {

    @Test
    public void encryptPassword() {
        String encrypted = BasicEncryptUtil.encrypt("test1234");
        System.out.println(encrypted);
        Assert.assertNotNull(encrypted);
    }

    @Test
    public void decryptPassword() {
        String decrypted = BasicEncryptUtil.decrypt("nxOjcKl2AdXeJz6wpzXtGnxc3y1+zEQ3");
        System.out.println(decrypted);
        assertNotNull(decrypted);
    }

}
