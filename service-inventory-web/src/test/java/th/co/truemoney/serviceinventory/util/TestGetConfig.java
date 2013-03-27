package th.co.truemoney.serviceinventory.util;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import th.co.truemoney.serviceinventory.config.TestDevProxyConfig;
import th.co.truemoney.serviceinventory.config.TestTmnProfileConfig;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { TestTmnProfileConfig.class, TestDevProxyConfig.class })
public class TestGetConfig {

	@Autowired @Qualifier("tmnProfileInitiator")
	private String tmnProfileInitiator;
	
	@Autowired @Qualifier("tmnProfilePin")
	private String tmnProfilePin;
	
	@Test
	public void shouldGetConfigSuccess() {
		assertEquals("si.tmnprofile", tmnProfileInitiator);
		assertEquals("0000", tmnProfilePin);	
	}
	
}
