package th.co.truemoney.serviceinventory.util;

import static org.junit.Assert.assertEquals;

import java.math.BigDecimal;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class FeeUtilTest {

	private FeeUtil feeUtil;
	
	
	@Before
	public void setup() {
		this.feeUtil = new FeeUtil();	
	}

	@After
	public void tierDown() {
	}
	
	@Test
	public void calculateFeePercent() {
		BigDecimal amount = new BigDecimal("1000.00");
		BigDecimal feeValue = new BigDecimal("3.00");
		String feeType = "P";
		BigDecimal minTotalFee = new BigDecimal("0.00");
		BigDecimal maxTotalFee = new BigDecimal("200.00");
		
		BigDecimal fee = this.feeUtil.calculateFee(amount, feeValue, feeType, minTotalFee, maxTotalFee);
		
		assertEquals("30.000000", fee.toString());
	}
	
	@Test
	public void calculateFeePercentWhenTotalLessThanMinFee() {
		BigDecimal amount = new BigDecimal("1000.00");
		BigDecimal feeValue = new BigDecimal("3.00");
		String feeType = "P";
		BigDecimal minTotalFee = new BigDecimal("100.00");
		BigDecimal maxTotalFee = new BigDecimal("200.00");
		
		BigDecimal fee = this.feeUtil.calculateFee(amount, feeValue, feeType, minTotalFee, maxTotalFee);
		
		assertEquals(minTotalFee.setScale(6).toString(), fee.toString());
	}
	
	@Test
	public void calculateFeePercentWhenTotalMostThanMaxFee(){
		BigDecimal amount = new BigDecimal("10000.00");
		BigDecimal feeValue = new BigDecimal("3.00");
		String feeType = "P";
		BigDecimal minTotalFee = new BigDecimal("100.00");
		BigDecimal maxTotalFee = new BigDecimal("200.00");
		
		BigDecimal fee = this.feeUtil.calculateFee(amount, feeValue, feeType, minTotalFee, maxTotalFee);
		
		assertEquals(maxTotalFee.setScale(6).toString(), fee.toString());
	}
	
	@Test
	public void calculateFeeFixCost() {
		BigDecimal amount = new BigDecimal("1000.00");
		BigDecimal feeValue = new BigDecimal("3.00");
		String feeType = "F";
		BigDecimal minTotalFee = new BigDecimal("0.00");
		BigDecimal maxTotalFee = new BigDecimal("0.00");
		
		BigDecimal fee = this.feeUtil.calculateFee(amount, feeValue, feeType, minTotalFee, maxTotalFee);
		
		assertEquals(feeValue.setScale(6).toString(), fee.toString());
	}
	
}
