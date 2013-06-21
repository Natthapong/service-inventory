package th.co.truemoney.serviceinventory.bill;

import java.math.BigDecimal;

public interface InquiryStrategy {
    public BigDecimal getAmount(String billCode, String ref1, String ref2);
}
