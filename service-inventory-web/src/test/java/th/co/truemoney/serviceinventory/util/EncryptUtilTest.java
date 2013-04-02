package th.co.truemoney.serviceinventory.util;

import static org.junit.Assert.assertNotNull;
import org.junit.Assert;
import org.junit.Test;

public class EncryptUtilTest {

	@Test
	public void encryptPassword() {
		String encrypted = EncryptUtil.encrypt("test1234"); 
		System.out.println(encrypted);
		Assert.assertNotNull(encrypted);		
	}
	
	@Test
	public void decryptPassword() {
		String decrypted = EncryptUtil.decrypt("nxOjcKl2AdXeJz6wpzXtGnxc3y1+zEQ3"); 
		System.out.println(decrypted);
		assertNotNull(decrypted);		
	}
	
}
