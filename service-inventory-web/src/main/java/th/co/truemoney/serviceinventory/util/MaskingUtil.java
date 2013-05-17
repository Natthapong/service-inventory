package th.co.truemoney.serviceinventory.util;

import org.springframework.util.StringUtils;

public class MaskingUtil {
	
	private static final String MASKSTR = "***";
	
	private MaskingUtil() {
	}
	
	private static String maskShortWord(String word) {
		return (word.length() < 3 ? word.substring(0, 1) : word.substring(0, 3)) + MASKSTR;
	} 
	
	private static String maskLongWord(String word) {
		return word.length() < 5 ? word : word.substring(0, 5) + MASKSTR;
	}
	
	public static String maskFullName(String fullName) {
		if (! StringUtils.hasText(fullName)) {
			return "-";
		}
		
		String[] words = fullName.trim().split("\\s+");
		if (words.length == 1) {
			return maskLongWord(words[0]);
		} else {
			String fName = words[0];
			String lName = words[1];
			return maskShortWord(fName) + " " + maskShortWord(lName);
		}
	}
	
}
