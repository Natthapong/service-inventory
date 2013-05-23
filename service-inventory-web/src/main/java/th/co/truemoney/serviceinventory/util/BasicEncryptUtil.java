package th.co.truemoney.serviceinventory.util;

import org.jasypt.util.text.BasicTextEncryptor;

public class BasicEncryptUtil {

    private static String salt = "e7bb960883743617455d87e7919e3bf8df5a6a88";

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
