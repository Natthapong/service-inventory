package th.co.truemoney.serviceinventory.util;

import org.junit.Test;

import static org.junit.Assert.*;

public class RandomUtilTest {

	@Test
	public void randomStringSuccess() {
		assertEquals(4, RandomUtil.genRandomString(4).length());
	}
	
	@Test
	public void randomNumberSuccess() {
		assertEquals(6, RandomUtil.genRandomNumber(6).length());
	}

}
