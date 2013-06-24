package th.co.truemoney.serviceinventory.persona.bills;

import th.co.truemoney.serviceinventory.engine.client.domain.services.InquiryOutstandingBillRequest;
import th.co.truemoney.serviceinventory.exception.ServiceInventoryException;

public class OnlineInquiryNotSupportException extends ServiceInventoryException {

    public OnlineInquiryNotSupportException(InquiryOutstandingBillRequest inquiryRequest) {
        super(500, "xxx", "online inquiry not supported: " + inquiryRequest.getBillCode(), "LOCAL-SI-ENGINE");
    }

    private static final long serialVersionUID = 2218895395206933213L;


}
