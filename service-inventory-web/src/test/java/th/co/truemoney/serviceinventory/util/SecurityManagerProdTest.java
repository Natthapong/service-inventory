package th.co.truemoney.serviceinventory.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.security.Security;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.util.StringUtils;

import th.co.truemoney.serviceinventory.config.EnvConfig;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ActiveProfiles(profiles={"prod", "mem"})
@ContextConfiguration(classes = { EnvConfig.class })
public class SecurityManagerProdTest {
	
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
		String cipherText = "PptD236GI44pvxz2srbblT0UfMIuzmghjzj0IdydG4VajTwWsi1P87ku2Ev9pvc8lfvRIg1sydodC6xlR9bO7Kqvn7DDUB1Ko9YS7RMBz4ZYHAajkrowPH8MdEUqZaQ4JBye5OixwfS7jPID29vWqtVSagimYnf3wI5LSKax0lg=";
		String normalText = manager.decryptRSA(cipherText);
		assertEquals("12345678901234", normalText);
	}

	@Test
	public void testEncryptRSA() {
		String cipherText = manager.encryptRSA("12345678901234");
		assertTrue(StringUtils.hasLength(cipherText));
	}

}
