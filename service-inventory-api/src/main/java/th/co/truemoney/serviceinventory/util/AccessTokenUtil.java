package th.co.truemoney.serviceinventory.util;

import java.util.UUID;

public class AccessTokenUtil {
	
	public static String generateToken(String username, Integer channelID, String deviceID, 
			String deviceType, String deviceVersion, String clientIP, String utibaSessionID) {		
		String salt = UUID.randomUUID().toString();		
		return EncryptUtil.buildHmacSignature(String.format("%s%d%s%s%s%s%s%s",
				username, channelID, salt, deviceID, deviceType, deviceVersion,
				clientIP, salt), utibaSessionID);
	}

	public static boolean isValidCheckSum(String data,String token){
		if(data.equals(token)){
			return true;
		}
		return false;
	}
	
}
