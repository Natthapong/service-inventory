package th.co.truemoney.serviceinventory.legacyfacade.handlers;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import th.co.truemoney.serviceinventory.ewallet.domain.DirectDebit;
import th.co.truemoney.serviceinventory.ewallet.proxy.TmnProfileProxyClient;

import com.tmn.core.api.message.ListSourceRequest;
import com.tmn.core.api.message.ListSourceResponse;
import com.tmn.core.api.message.SecurityContext;
import com.tmn.core.api.message.SourceContext;

public class TopUpSourceOfFundHandler {

	private static final String DIRECT_DEBIT_SOURCE_TYPE = "debit";

	@Autowired
	private TmnProfileProxyClient tmnProfileProxyClient;

	public List<DirectDebit> getAllDirectDebitSourceOfFunds(Integer channelID, String sessionID, String truemoneyID) {

		List<DirectDebit> directDebitList = new ArrayList<DirectDebit>();

		ListSourceRequest sourceRequest = createListSourceRequest(channelID, sessionID, truemoneyID, DIRECT_DEBIT_SOURCE_TYPE);
		ListSourceResponse listSourceResponse = this.tmnProfileProxyClient.listSource(sourceRequest);
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

	private ListSourceRequest createListSourceRequest(Integer channelID, String sessionID, String truemoneyID, String sourceOfFundType) {
		ListSourceRequest listSourceRequest = new ListSourceRequest();
		listSourceRequest.setSecurityContext(createSecurityContext(sessionID, truemoneyID));
		listSourceRequest.setChannelId(channelID);
		listSourceRequest.setSourceType(sourceOfFundType);
		return listSourceRequest;
	}

	private SecurityContext createSecurityContext(String sessionID,	String truemoneyID) {
		SecurityContext securityContext = new SecurityContext();
		securityContext.setSessionId(sessionID);
		securityContext.setTmnId(truemoneyID);
		return securityContext;
	}

	public void setTmnProfileProxy(TmnProfileProxyClient tmnProfileProxyClient) {
		this.tmnProfileProxyClient = tmnProfileProxyClient;
	}

}
