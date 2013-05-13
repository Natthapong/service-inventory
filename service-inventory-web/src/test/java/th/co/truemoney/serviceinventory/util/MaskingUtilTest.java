package th.co.truemoney.serviceinventory.util;

import static org.junit.Assert.*;

import org.junit.Test;

public class MaskingUtilTest {

	@Test
	public void test() {
		assertEquals("abcde***", MaskingUtil.maskFullName("abcdefghijklmno"));
		assertEquals("Mic*** Boo***", MaskingUtil.maskFullName("Michael    Booooooolay"));
		assertEquals("Mic*** Boo***", MaskingUtil.maskFullName("Michael Booooooolay"));
		assertEquals("Sun*** M***", MaskingUtil.maskFullName("Sun Mi Dong"));
		assertEquals("M*** B***", MaskingUtil.maskFullName("Mi Bu"));
		assertEquals("John", MaskingUtil.maskFullName("John"));
		assertEquals("-", MaskingUtil.maskFullName("   "));
		assertEquals("-", MaskingUtil.maskFullName(null));
	}

}
