package th.co.truemoney.serviceinventory.util;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class FeeUtil {

	public BigDecimal calculateFee(BigDecimal amount, BigDecimal feeValue,
			String feeType, BigDecimal minTotalFee, BigDecimal maxTotalFee) {

		BigDecimal totalFee = new BigDecimal(feeValue.toString());

		// Type "P" = percent type
		if ("P".equals(feeType)) {
			totalFee = amount.multiply(feeValue).divide(new BigDecimal(100.00));

			if (totalFee.compareTo(minTotalFee) == -1)
				totalFee = new BigDecimal(minTotalFee.toString());
			else if (totalFee.compareTo(maxTotalFee) == 1)
				totalFee = new BigDecimal(maxTotalFee.toString());
		}

		return totalFee.setScale(6, RoundingMode.HALF_UP);
	}
}
