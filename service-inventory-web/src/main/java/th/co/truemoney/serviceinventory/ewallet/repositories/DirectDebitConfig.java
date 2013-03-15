package th.co.truemoney.serviceinventory.ewallet.repositories;

import th.co.truemoney.serviceinventory.bean.DirectDebitConfigBean;

public interface DirectDebitConfig {
	public DirectDebitConfigBean getBankDetail(String bankCode);
}
