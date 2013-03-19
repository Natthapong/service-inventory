package th.co.truemoney.serviceinventory.util;

public class RandomUtil {
	
	public static String genRandomString(int length) {
		StringBuffer buffer = new StringBuffer();
		java.util.Random random = new java.util.Random();
		char[] chars = new char[] { 'A', 'B',
				  'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M',
				  'N', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y',
				  'Z'};
		for ( int i = 0; i < length; i++ ) {
			buffer.append(chars[random.nextInt(chars.length)]);
		}
		return buffer.toString();
	}
	
	public static String genRandomNumber(int length) {
		StringBuffer buffer = new StringBuffer();
		java.util.Random random = new java.util.Random();
		char[] chars = new char[] { '1', '2', '3', '4', '5', '6', '7', '8', '9', '0'};
		for ( int i = 0; i < length; i++ ) {
			buffer.append(chars[random.nextInt(chars.length)]);
		}
		return buffer.toString();
	}
	
}