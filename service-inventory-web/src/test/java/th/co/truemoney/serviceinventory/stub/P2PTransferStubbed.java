package th.co.truemoney.serviceinventory.stub;

import java.math.BigDecimal;
import java.util.UUID;

import th.co.truemoney.serviceinventory.transfer.domain.P2PTransferDraft;

import com.tmn.core.api.message.StandardMoneyResponse;
import com.tmn.core.api.message.VerifyTransferResponse;

public class P2PTransferStubbed {

	public static StandardMoneyResponse createSuccessStubbedStandardMoneyResponse() {
        StandardMoneyResponse moneyResponse = new StandardMoneyResponse();
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        moneyResponse.setTransactionId("123456789");
        moneyResponse.setResultCode("0");
        moneyResponse.setResultNamespace("core");
        return moneyResponse;
	}

	public static VerifyTransferResponse createSuccessStubbedVerifyTransferResponse() {
        VerifyTransferResponse verifyTransferResponse = new VerifyTransferResponse();
        verifyTransferResponse.setTransactionId("100000001");
        verifyTransferResponse.setResultCode("0");
        verifyTransferResponse.setResultNamespace("core");
        verifyTransferResponse.setLoginId("stub@local.com");
        verifyTransferResponse.setRemainingBalance(new BigDecimal("100.00"));
        verifyTransferResponse.setTargetFullname("target Fullname");
        verifyTransferResponse.setTargetProfilePicture("target Profile Picture");
        return verifyTransferResponse;
    }

	public static P2PTransferDraft createP2PDraft(BigDecimal amount, String targetMobileNumber, String targetName, String imageFileName, String byAccessToken) {

		String draftID = UUID.randomUUID().toString();
		P2PTransferDraft draft = new P2PTransferDraft();
		draft.setID(draftID);
		draft.setAccessTokenID(byAccessToken);
		draft.setAmount(amount);
		draft.setMobileNumber(targetMobileNumber);
		draft.setFullname(targetName);
		draft.setImageFileName(imageFileName);
		draft.setStatus(P2PTransferDraft.Status.CREATED);

		return draft;
	}
}
