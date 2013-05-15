package th.co.truemoney.serviceinventory.util;

import java.util.UUID;

public class AccessTokenUtil {

	private static final String SALT = "5dc77d2e2310519a97aae050d85bec6870b4651a63447f02dfc936814067dd45a2f90e3c662f016f20dad45a2760739860af7ae92b3de00c2fd557ecbc3cc0d5";
	
	public static String generateToken(String username, Integer channelId, String deviceID,
			String deviceType, String deviceVersion, String clientIP, String utibaSessionID) {
		String salt = UUID.randomUUID().toString();
		return EncryptUtil.buildHmacSignature(String.format("%s%d%s%s%s%s%s%s",
				username, channelId, salt, deviceID, deviceType, deviceVersion,
				clientIP, salt), utibaSessionID);
	}

	public static boolean isValidCheckSum(String checksum, String data, String accessTokenID){
		String localChecksum = EncryptUtil.buildHmacSignature(accessTokenID, data+SALT);
		return localChecksum.toLowerCase().equals(checksum);	
	}
	
	public static void main(String[] args) {
		String accessTokenID = "a27ab91a-1371-4c0f-a1f6-f422e271fa54";
		String id = "9f8a33b6-113c-4588-a38c-06f2158a35a9";
		String otp = "379864";
		String data = id+otp+accessTokenID;
		String result = EncryptUtil.buildHmacSignature(accessTokenID, data+SALT);
	}

}
