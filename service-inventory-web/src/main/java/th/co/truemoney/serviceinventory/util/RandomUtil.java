package th.co.truemoney.serviceinventory.util;

import java.util.Random;

public class RandomUtil {
	
	static String chars  = "BCDFGHJKLMNPQRSTVWXYZ";
	static String digits = "0123456789";
	
	public static String genRandomString(int length) {
		return random(length, chars);
	}
	
	public static String genRandomNumber(int length) {
		return random(length, digits);
	}
	
	private static String random(int length, String charSet) {
		int maxNum = charSet.length();
		Random random = new Random();
		StringBuffer buffer = new StringBuffer();
		for ( int i = 0; i < length; i++ ) {
			buffer.append(charSet.charAt(random.nextInt(maxNum)));
		}
		return buffer.toString();
	}
	
}