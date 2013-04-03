package th.co.truemoney.serviceinventory.ewallet.repositories.impl;

public interface SourceOfFundPreference {

	DirectDebitPreference getBankPreference(String bankCode);

}
