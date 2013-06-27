package th.co.truemoney.serviceinventory.bill.validation;

import java.util.HashMap;

import th.co.truemoney.serviceinventory.bill.domain.Bill;
import th.co.truemoney.serviceinventory.exception.ServiceInventoryWebException;

public class DebtBillException extends ServiceInventoryWebException {

	private static final long serialVersionUID = 6482230326166973787L;
	
	public DebtBillException(Bill bill) {
		super(Code.DEBT_BILL, "bill has debt: " + bill);
		
        HashMap<String, Object> mapData = new HashMap<String, Object>();
        mapData.put("dueDate", bill.getDueDate());
        mapData.put("amount", bill.getAmount());
        mapData.put("target", bill.getTarget());

        setData(mapData);
	}

}
