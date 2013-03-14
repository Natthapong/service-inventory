package th.co.truemoney.serviceinventory.ewallet.repositories;

import th.co.truemoney.serviceinventory.bean.AddMoneyBankDetail;


public interface AddMoneyDirectDebitConfig {
	public AddMoneyBankDetail getBankDetail(String bankCode);
}
