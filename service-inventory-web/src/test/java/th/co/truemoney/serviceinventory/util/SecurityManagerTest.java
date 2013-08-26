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
		String cipherText = "Q+uMdAwX3Fg44RslIBAn4z9GjxJrQXUmRr45Wuwdd9ss/UXUa6gReHrLJwBhemWaliQEUh4ukCr8SXeFQ+0MDJdom5XHU84J0nihk6XolEaWFL6JPYFzmI7wRuJWYMjOlathY+Woq1uuNN1wYAtPgsTkuBPNJcxWY2WmNV1w9UU=";
		String normalText = manager.decryptRSA(cipherText);
		assertEquals("12345678901234", normalText);
	}

	@Test
	public void testEncryptRSA() {
		String cipherText = manager.encryptRSA("12345678901234");
		assertTrue(StringUtils.hasLength(cipherText));
	}

}
