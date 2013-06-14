package th.co.truemoney.serviceinventory.stub;

import th.co.truemoney.serviceinventory.ewallet.domain.AccessToken;

public class AccessTokenRepositoryStubbed {
	public static AccessToken createSuccessAccessToken() {
		return new AccessToken("1234567890", "0987654321", "0987654321", "1111111111", "0866012345", "local@tmn.com", 41);
	}
}
