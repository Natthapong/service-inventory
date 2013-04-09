package th.co.truemoney.serviceinventory.ewallet.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(Include.NON_NULL)
public class BillPaymentDraftTransaction extends DraftTransaction {

	private static final long serialVersionUID = 2049045334050859727L;
	
	private static final String DRAFT_TYPE = "billPaymentDraftTransaction";
	
	public BillPaymentDraftTransaction() {
		type = DRAFT_TYPE;
	}

}
