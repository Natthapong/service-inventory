package th.co.truemoney.serviceinventory.ewallet;

import th.co.truemoney.serviceinventory.ewallet.domain.DirectDebitOrder;
import th.co.truemoney.serviceinventory.ewallet.domain.DirectDebitOrderResult;


public interface TmnDirectDebitService {

	public DirectDebitOrderResult verify(Integer channelId, String accessToken, DirectDebitOrder directDebitOrder);
}
