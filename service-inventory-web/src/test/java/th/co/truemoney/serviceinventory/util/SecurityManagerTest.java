package th.co.truemoney.serviceinventory.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.security.Security;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.util.StringUtils;

import th.co.truemoney.serviceinventory.config.TestEnvConfig;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(classes = { TestEnvConfig.class })
public class SecurityManagerTest {
	
	@Autowired
	private SecurityManager manager;
	
	@BeforeClass
	public static void setup() {
		Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
	}
	
	@Test
	public void testGetPublicKey() {
		assertTrue(StringUtils.hasLength(manager.getPublicKey()));
	}

	@Test
	public void testGetPrivateKey() {
		assertTrue(StringUtils.hasLength(manager.getPrivateKey()));
	}

	@Test
	public void testDecryptRSA() {
		String cipherText = "ETVqWRx0DJX4FfyVrvoH2kuCpA8T7xVsnbdQ6GARQ+xDwX2g9x0CgCbYCnXXfW5H4hcOIVb8I0dhccPzGfagqwPQsq5Me2Y63B9sluj14WyoMebhZSX5rnEUZssZRCs7xPRjmpmztIB4msphf6U1R5XqWaZopr4G5Wq27nthvbg=";
		String normalText = manager.decryptRSA(cipherText);
		assertEquals("thisisapassword", normalText);
	}

	@Test
	public void testEncryptRSA() {
		String cipherText = manager.encryptRSA("thisisapassword");
		assertTrue(StringUtils.hasLength(cipherText));
	}

}
