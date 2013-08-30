package th.co.truemoney.serviceinventory.legacyfacade.handlers;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import th.co.truemoney.serviceinventory.ewallet.domain.TopUpConfirmationInfo;
import th.co.truemoney.serviceinventory.ewallet.proxy.WalletProxyClient;
import th.co.truemoney.serviceinventory.transfer.domain.P2PTransactionConfirmationInfo;
import th.co.truemoney.serviceinventory.transfer.domain.P2PTransfer;

import com.tmn.core.api.message.AddMoneyRequest;
import com.tmn.core.api.message.SecurityContext;
import com.tmn.core.api.message.StandardBizRequest;
import com.tmn.core.api.message.StandardMoneyResponse;
import com.tmn.core.api.message.TransferRequest;
import com.tmn.core.api.message.VerifyAddMoneyRequest;
import com.tmn.core.api.message.VerifyTransferRequest;
import com.tmn.core.api.message.VerifyTransferResponse;

public class EwalletBalanceHandler {

	@Autowired
	private WalletProxyClient walletProxyClient;
	private SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm");

	public BigDecimal getCurrentBalance(Integer channelID, String sessionID, String tmnID) {
		StandardBizRequest standardBizRequest = createStandardBizRequest(channelID, sessionID, tmnID);
		return this.walletProxyClient.getBalance(standardBizRequest).getCurrentBalance();
	}

	public void verifyTopupToMyWallet(BigDecimal amount,
			String sourceOfFundID, String sourceOfFundType, Integer channelID,
			String sessionID, String truemoneyID) {

		VerifyAddMoneyRequest request = createVerifyAddMoneyRequest(amount,
				sourceOfFundID, sourceOfFundType, channelID, sessionID,
				truemoneyID);
		
		this.walletProxyClient.verifyAddMoney(request);
	}

	public TopUpConfirmationInfo topupToMyWallet(BigDecimal amount,
			String sourceOfFundID, String sourceOfFundType, Integer channelID,
			String sessionID, String truemoneyID) {

		AddMoneyRequest addMoneyRequest = createAddMoneyRequest(amount,
				sourceOfFundID, sourceOfFundType, channelID, sessionID,
				truemoneyID);

		StandardMoneyResponse moneyResponse = this.walletProxyClient.addMoney(addMoneyRequest);

		TopUpConfirmationInfo confirmationInfo = new TopUpConfirmationInfo();
		confirmationInfo.setTransactionID(moneyResponse.getTransactionId());
		confirmationInfo.setTransactionDate(df.format(new Date()));

		return confirmationInfo;
	}
	
	public P2PTransfer verifyTransferFromPersonToPerson(BigDecimal amount,
			String targetMobileNumber, Integer channelID, String sessionID,
			String tmnID) {

		VerifyTransferRequest verifyRequest = createVerifyTransferRequest(
				amount, targetMobileNumber, channelID, sessionID, tmnID);

		VerifyTransferResponse verifyResponse = this.walletProxyClient.verifyTransfer(verifyRequest);
		
		if (isShowProfileImage(verifyResponse.getTargetProfilePictureFlag())) {
			return new P2PTransfer(verifyResponse.getTargetFullname(), verifyResponse.getTargetProfilePicture());
		} else {
			return new P2PTransfer(verifyResponse.getTargetFullname(), "");
		}
	}
	
	private boolean isShowProfileImage(String flag) {
		return "1".equals(flag);
	}

	public P2PTransactionConfirmationInfo transferFromPersonToPerson(BigDecimal amount,
			String targetMobileNumber, Integer channelID, String sessionID,
			String tmnID, String personalMessage) {

		TransferRequest transferRequest = createConfirmTransferRequest(amount,
				targetMobileNumber, channelID, sessionID, tmnID,
				personalMessage);

		com.tmn.core.api.message.StandardMoneyResponse standardMoneyResponse 
			= this.walletProxyClient.transfer(transferRequest);

		P2PTransactionConfirmationInfo info = new P2PTransactionConfirmationInfo();
		info.setTransactionID(standardMoneyResponse.getTransactionId());
		info.setTransactionDate(df.format(new Date()));

		return info;
			
	}
	
	private StandardBizRequest createStandardBizRequest(Integer channelID, String sessionID, String tmnID) {
		StandardBizRequest standardBizRequest = new StandardBizRequest();
		standardBizRequest.setChannelId(channelID);
		standardBizRequest.setSecurityContext(createSecurityContext(sessionID, tmnID));
		return standardBizRequest;
	}
	
	private VerifyAddMoneyRequest createVerifyAddMoneyRequest(
			BigDecimal amount, String sourceOfFundID, String sourceOfFundType,
			Integer channelID, String sessionID, String truemoneyID) {
		VerifyAddMoneyRequest request = new VerifyAddMoneyRequest();
		request.setAmount(amount);
		request.setSourceId(sourceOfFundID);
		request.setSourceType(sourceOfFundType);
		request.setChannelId(channelID);
		request.setSecurityContext(createSecurityContext(sessionID, truemoneyID));
		return request;
	}

	private AddMoneyRequest createAddMoneyRequest(BigDecimal amount,
			String sourceOfFundID, String sourceOfFundType, Integer channelID,
			String sessionID, String truemoneyID) {
		AddMoneyRequest addMoneyRequest = new AddMoneyRequest();
		addMoneyRequest.setAmount(amount);
		addMoneyRequest.setChannelId(channelID);
		addMoneyRequest.setSecurityContext(createSecurityContext(sessionID, truemoneyID));
		addMoneyRequest.setSourceId(sourceOfFundID);
		addMoneyRequest.setSourceType(sourceOfFundType);
		return addMoneyRequest;
	}
	
	private VerifyTransferRequest createVerifyTransferRequest(
			BigDecimal amount, String targetMobileNumber, Integer channelID,
			String sessionID, String tmnID) {
		VerifyTransferRequest verifyRequest = new VerifyTransferRequest();
		verifyRequest.setChannelId(channelID);
		verifyRequest.setAmount(amount);
		verifyRequest.setTarget(targetMobileNumber);
		verifyRequest.setSecurityContext(createSecurityContext(sessionID, tmnID));
		return verifyRequest;
	}

	private TransferRequest createConfirmTransferRequest(BigDecimal amount,
			String targetMobileNumber, Integer channelID, String sessionID,
			String tmnID, String personalMessage) {
		String tmpPersonalMessage = Base64.encodeBase64String(StringUtils.getBytesUtf8(personalMessage));

		TransferRequest transferRequest = new TransferRequest();
		transferRequest.setAmount(amount);
		transferRequest.setChannelId(channelID);
		transferRequest.setSecurityContext(createSecurityContext(sessionID, tmnID));
		transferRequest.setTarget(targetMobileNumber);
		transferRequest.setPersonalMessage(tmpPersonalMessage);
		return transferRequest;
	}
	
    private SecurityContext createSecurityContext(String sessionID, String truemoneyID) {
    	SecurityContext securityContext = new SecurityContext();
    	securityContext.setSessionId(sessionID);
    	securityContext.setTmnId(truemoneyID);
    	return securityContext;
	}

	public void setWalletProxyClient(WalletProxyClient walletProxyClient) {
		this.walletProxyClient = walletProxyClient;
	}

}
