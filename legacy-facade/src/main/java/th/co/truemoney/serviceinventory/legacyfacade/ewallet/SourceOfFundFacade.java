package th.co.truemoney.serviceinventory.legacyfacade.ewallet;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import th.co.truemoney.serviceinventory.ewallet.domain.AccessToken;
import th.co.truemoney.serviceinventory.ewallet.domain.DirectDebit;
import th.co.truemoney.serviceinventory.ewallet.proxy.message.ListSourceRequest;
import th.co.truemoney.serviceinventory.ewallet.proxy.message.ListSourceResponse;
import th.co.truemoney.serviceinventory.ewallet.proxy.message.SecurityContext;
import th.co.truemoney.serviceinventory.ewallet.proxy.message.SourceContext;
import th.co.truemoney.serviceinventory.ewallet.proxy.tmnprofile.TmnProfileProxy;

public class SourceOfFundFacade {

	private static final String DIRECT_DEBIT_SOURCE_TYPE = "debit";

	@Autowired
	private TmnProfileProxy tmnProfileProxy;

	public List<DirectDebit> getAllDirectDebitSourceOfFunds(AccessToken accessToken) {

		List<DirectDebit> directDebitList = new ArrayList<DirectDebit>();

		ListSourceRequest sourceRequest = createListSourceRequest(accessToken, DIRECT_DEBIT_SOURCE_TYPE);
		ListSourceResponse listSourceResponse = this.tmnProfileProxy.listSource(sourceRequest);
		SourceContext[] sourceContexts = listSourceResponse.getSourceList();

		if (sourceContexts != null && sourceContexts.length > 0) {
			for (int i = 0; i< sourceContexts.length; i++) {

				SourceContext sourceContext = sourceContexts[i];
				String[] sourceDetails = sourceContext.getSourceDetail();

				if (sourceDetails != null && sourceDetails.length > 0) {
					DirectDebit directDebit = createDirectDebitSource(sourceContext, sourceDetails);
					directDebitList.add(directDebit);
				}
			}
		}
		return directDebitList;
	}

	private DirectDebit createDirectDebitSource(SourceContext sourceContext, String[] sourceDetails) {
		DirectDebit directDebit = new DirectDebit();
		directDebit.setSourceOfFundID(sourceContext.getSourceId());
		directDebit.setSourceOfFundType(sourceContext.getSourceType());

		String bankCode = extractBankCode(sourceDetails);
		String accountNumber = extractAccountNumber(sourceDetails);

		directDebit.setBankCode(bankCode);
		directDebit.setBankAccountNumber(accountNumber);

		return directDebit;
	}

	private String extractAccountNumber(String[] sourceDetail) {
		return sourceDetail[0] != null && sourceDetail[1] != null ? sourceDetail[1].trim() : "";
	}

	private String extractBankCode(String[] sourceDetail) {
		return sourceDetail[0] != null ? sourceDetail[0].trim() : "";
	}

	private ListSourceRequest createListSourceRequest(AccessToken accessToken, String sourceOfFundType) {

		String sessionID = accessToken.getSessionID();
		String truemoneyID = accessToken.getTruemoneyID();
		Integer channelID = accessToken.getChannelID();

		SecurityContext securityContext = new SecurityContext(sessionID, truemoneyID);

		ListSourceRequest listSourceRequest = new ListSourceRequest();
		listSourceRequest.setSecurityContext(securityContext);
		listSourceRequest.setChannelId(channelID);
		listSourceRequest.setSourceType(sourceOfFundType);

		return listSourceRequest;
	}

	public void setTmnProfileProxy(TmnProfileProxy tmnProfileProxy) {
		this.tmnProfileProxy = tmnProfileProxy;
	}

}
