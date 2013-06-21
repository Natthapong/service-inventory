package th.co.truemoney.serviceinventory.bill.validation;

import java.util.HashMap;

import th.co.truemoney.serviceinventory.bill.domain.Bill;
import th.co.truemoney.serviceinventory.exception.ServiceInventoryWebException;

public class OverDueBillException extends ServiceInventoryWebException {

    private static final long serialVersionUID = -3685335893031075989L;

    public OverDueBillException(Bill bill) {
        super(Code.BILL_OVER_DUE, "bill over due date.");

        HashMap<String, Object> mapData = new HashMap<String, Object>();
        mapData.put("dueDate", bill.getDueDate());
        mapData.put("amount", bill.getAmount());
        mapData.put("target", bill.getTarget());

        setData(mapData);
    }

}
