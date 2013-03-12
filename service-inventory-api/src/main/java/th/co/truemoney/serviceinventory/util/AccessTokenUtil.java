package th.co.truemoney.serviceinventory.util;

public class AccessTokenUtil {
	
	private static final String SALT = "L6or4enIp7su888m";

	public static String generateToken(String username, Integer channelID, String deviceID, 
			String deviceType, String deviceVersion, String clientIP, String utibaSessionID) {
		return EncryptUtil.buildHmacSignature(String.format("%s%d%s%s%s%s%s%s",
				username, channelID, SALT, deviceID, deviceType, deviceVersion,
				clientIP, SALT), utibaSessionID);
	}

	public static boolean isValidCheckSum(String data,String token){
		if(data.equals(token)){
			return true;
		}
		return false;
	}
	
}
