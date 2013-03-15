package th.co.truemoney.serviceinventory.ewallet.repositories;

import th.co.truemoney.serviceinventory.ewallet.domain.DirectDebit;

public interface DirectDebitConfig {
	public DirectDebit getBankDetail(String bankCode);
}
