package th.co.truemoney.serviceinventory.ewallet.repositories;

import th.co.truemoney.serviceinventory.bill.domain.Bill;

public interface BillInformationRepository {

	public Bill findBill(String billID, String accessTokenID);
	public void saveBill(Bill bill, String accessTokenID);

}
