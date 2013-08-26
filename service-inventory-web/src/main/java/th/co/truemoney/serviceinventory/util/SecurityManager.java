package th.co.truemoney.serviceinventory.util;

import java.security.KeyFactory;
import java.security.Security;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.Cipher;

import org.apache.axis.encoding.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;

public class SecurityManager {

	@Autowired
	private Environment env;
	
	static {
		Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
	}
	
	public String getPublicKey() {
		return env.getProperty("publicKey");
	}

	public String getPrivateKey() {
		return env.getProperty("privateKey");
	}
	
	public String decryptRSA(String encryptedTxt) {
		try {
			X509EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(Base64.decode(getPublicKey()));
	        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
	        RSAPublicKey publicKey = (RSAPublicKey)keyFactory.generatePublic(publicKeySpec);

			Cipher cipher = Cipher.getInstance("RSA/None/PKCS1Padding", "BC");

			cipher.init(Cipher.DECRYPT_MODE, publicKey);
			byte[] plainText = cipher.doFinal(Base64.decode(encryptedTxt));

			return new String(plainText, "UTF-8");
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}
	
	public String encryptRSA(String password) {
		try {
			byte[] input = password.getBytes("UTF-8");

			PKCS8EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(Base64.decode(getPrivateKey()));			
			KeyFactory keyFactory = KeyFactory.getInstance("RSA", "BC");
	        RSAPrivateKey privateKey = (RSAPrivateKey)keyFactory.generatePrivate(privateKeySpec);

			Cipher cipher = Cipher.getInstance("RSA/None/PKCS1Padding", "BC");

			cipher.init(Cipher.ENCRYPT_MODE, privateKey);
			byte[] cipherText = cipher.doFinal(input);
			return Base64.encode(cipherText);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

}