package th.co.truemoney.serviceinventory.ewallet.repositories.impl;

import java.util.HashMap;
import java.util.LinkedHashMap;

import th.co.truemoney.serviceinventory.bill.domain.Bill;
import th.co.truemoney.serviceinventory.ewallet.repositories.BillInformationRepository;
import th.co.truemoney.serviceinventory.exception.ResourceNotFoundException;
import th.co.truemoney.serviceinventory.exception.ServiceInventoryWebException.Code;

public class BillInformationMemoryRepository implements BillInformationRepository {

	public HashMap<String, Bill> map = new LinkedHashMap<String, Bill>();

	@Override
	public Bill findBill(String billID, String accessTokenID) {
		Bill billInfo = map.get(accessTokenID + ":" + billID);
		if(billInfo == null) {
			throw new ResourceNotFoundException(Code.BILL_NOT_FOUND, "access token not found.");
		}
		return billInfo;
	}

	@Override
	public void saveBill(Bill bill, String accessTokenID) {
		if (bill != null) {
			map.put(accessTokenID + ":" + bill.getID(), bill);
		}
	}

}
