package th.co.truemoney.serviceinventory.bill.validation;

import java.util.HashMap;

import th.co.truemoney.serviceinventory.bill.domain.Bill;
import th.co.truemoney.serviceinventory.exception.ServiceInventoryWebException;

public class BillDueDateMissingException extends ServiceInventoryWebException {

    private static final long serialVersionUID = -332603753696250941L;

    public BillDueDateMissingException(Bill bill) {
        super(Code.BILL_REQUIRED_DUEDATE, "bill required due date.");

        HashMap<String, Object> mapData = new HashMap<String, Object>();
        mapData.put("target", bill.getTarget());

        setData(mapData);
    }


}
