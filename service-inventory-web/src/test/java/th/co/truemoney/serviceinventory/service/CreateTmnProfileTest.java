package th.co.truemoney.serviceinventory.service;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import th.co.truemoney.serviceinventory.ewallet.domain.TmnProfile;
import th.co.truemoney.serviceinventory.ewallet.impl.TmnProfileServiceImpl;
import th.co.truemoney.serviceinventory.ewallet.proxy.tmnprofile.admin.TmnProfileAdminProxy;
import th.co.truemoney.serviceinventory.ewallet.proxy.tmnprofile.admin.impl.TmnProfileAdminProxyImpl;

public class CreateTmnProfileTest {

	private TmnProfileServiceImpl tmnProfileServiceImpl;
	private TmnProfileAdminProxy tmnProfileAdminProxyMock;
	
	@Before
	public void setup() {
		this.tmnProfileServiceImpl = new TmnProfileServiceImpl();
		this.tmnProfileAdminProxyMock = Mockito.mock(TmnProfileAdminProxyImpl.class);

		this.tmnProfileServiceImpl.setTmnProfileAdminProxy(tmnProfileAdminProxyMock);
	}
	
	@Test
	public void createTmnProfile() {		
		Integer channelID = 40;
		TmnProfile tmnProfile = setTmnProfile();		
		String otpReferenceCode = this.tmnProfileServiceImpl.createProfile(channelID, tmnProfile);
		assertEquals("abcd", otpReferenceCode);		
	}

	private TmnProfile setTmnProfile() {
		TmnProfile tmnProfile = new TmnProfile();
		tmnProfile.setEmail("test@gmail.com");
		tmnProfile.setPassword("123456");
		tmnProfile.setFullname("UnitTest");
		tmnProfile.setThaiID("1212121212121");
		tmnProfile.setMobileno("0891234567");
		return tmnProfile;
	}
	
}
