package th.co.truemoney.serviceinventory.stub;

import java.math.BigDecimal;
import java.util.UUID;

import th.co.truemoney.serviceinventory.ewallet.domain.DraftTransaction;
import th.co.truemoney.serviceinventory.ewallet.proxy.message.StandardMoneyResponse;
import th.co.truemoney.serviceinventory.ewallet.proxy.message.VerifyTransferResponse;
import th.co.truemoney.serviceinventory.transfer.domain.P2PDraftTransaction;

public class P2PTransferStubbed {

	public static StandardMoneyResponse createSuccessStubbedStandardMoneyResponse() {
		return new StandardMoneyResponse("1234", "0", "namespce", new String[] {"key"}, new String[] {"value"}, "stub@local.com", new BigDecimal(100.00));
	}

	public static VerifyTransferResponse createSuccessStubbedVerifyTransferResponse() {
		return new VerifyTransferResponse("1234", "0", "namespce", new String[] {""}, new String[] {""}, "stub@local.com", new BigDecimal(100.00), "target Fullname");
	}

	public static P2PDraftTransaction createP2PDraft(BigDecimal amount, String targetMobileNumber, String targetName, String byAccessToken) {

		String draftID = UUID.randomUUID().toString();
		P2PDraftTransaction draft = new P2PDraftTransaction();
		draft.setID(draftID);
		draft.setAccessTokenID(byAccessToken);
		draft.setAmount(amount);
		draft.setMobileNumber(targetMobileNumber);
		draft.setFullname(targetName);
		draft.setStatus(DraftTransaction.Status.CREATED);

		return draft;
	}
}
