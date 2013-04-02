package th.co.truemoney.serviceinventory.util;

import org.jasypt.util.text.BasicTextEncryptor;

public class EncryptUtil {
	
	private static String salt = "basic";
	
	public static String encrypt(String plainText) {
		BasicTextEncryptor basicTextEncryptor = new BasicTextEncryptor();
		basicTextEncryptor.setPassword(salt);
		return basicTextEncryptor.encrypt(plainText);
	}
	
	public static String decrypt(String encryptedText) {
		BasicTextEncryptor basicTextEncryptor = new BasicTextEncryptor();
		basicTextEncryptor.setPassword(salt);
		return basicTextEncryptor.decrypt(encryptedText);
	}
	
}
