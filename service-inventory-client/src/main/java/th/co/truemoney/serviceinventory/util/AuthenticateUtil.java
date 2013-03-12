package th.co.truemoney.serviceinventory.util;

public class AuthenticateUtil {
	private static final String SALT = "L6or4enIp7su888m";
	private static final String APPNAME = "True Money";

	public static String generateToken(String username, String remoteIP,
			String session, String deviceID, String deviceType,
			String deviceVersion, String appVersion) {
		return EncryptUtil.buildHmacSignature(String.format("%s%s%s%s%s%s%s%s",
				username, remoteIP, SALT, deviceID, deviceType, deviceVersion,
				appVersion, APPNAME), session);
	}

	public static boolean isValidCheckSum(String data,String token){
		if(data.equals(token)){
			return true;
		}
		return false;
	}
}
