package th.co.truemoney.serviceinventory.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import org.junit.Assert;
import org.junit.Test;

public class BasicEncryptUtilTest {

    @Test
    public void encryptPassword() {
        String encrypted = BasicEncryptUtil.encrypt("test1234");
        Assert.assertNotNull(encrypted);
    }

    @Test
    public void decryptPassword() {
        String decrypted = BasicEncryptUtil.decrypt("nxOjcKl2AdXeJz6wpzXtGnxc3y1+zEQ3");
        assertNotNull(decrypted);
    }
    
    @Test
    public void encryptAndDecryptUtil() {
    	String plainTxt = "hello world";
    	String encrypted = BasicEncryptUtil.encrypt(plainTxt);
    	String decryptedTxt = BasicEncryptUtil.decrypt(encrypted);
    	assertEquals(plainTxt, decryptedTxt);
    }

}
