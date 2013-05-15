package th.co.truemoney.serviceinventory.util;

import static org.junit.Assert.*;

import org.junit.Test;

public class AccessTokenUtilTest {
	
	private static final String SALT = "5dc77d2e2310519a97aae050d85bec6870b4651a63447f02dfc936814067dd45a2f90e3c662f016f20dad45a2760739860af7ae92b3de00c2fd557ecbc3cc0d5";
	
	@Test
	public void test() {
		String accessTokenID = "a27ab91a-1371-4c0f-a1f6-f422e271fa54";
		String id = "9f8a33b6-113c-4588-a38c-06f2158a35a9";
		String otp = "379864";
		String data = id+otp+accessTokenID;
		String result = EncryptUtil.buildHmacSignature(accessTokenID, data+SALT);
		assertEquals("26510346efaab8be95fd752655844b9af14d3469", result);
	}
}
